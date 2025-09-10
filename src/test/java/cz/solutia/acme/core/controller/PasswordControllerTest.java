package cz.solutia.acme.core.controller;

import cz.solutia.acme.MyTestContainersConfiguration;
import cz.solutia.acme.core.model.User;
import cz.solutia.acme.core.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Import(MyTestContainersConfiguration.class)
class PasswordControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void beforeAll() {
        userRepository.deleteAll();
        User user = new User();
        user.setUsername("user");
        user.setEmail("john.doe@email.com");
        user.setFirstname("John");
        user.setLastname("Doe");
        // Password is "correctOldPass"
        user.setPassword("$2a$10$vrGsGKz5PLz6dccZ0kVE7eO4qO4aqu3zw1KTlnrS7Q9sfcFLKOzLG");
        userRepository.save(user);
    }

    @AfterEach
    void  clean(){
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /settings/password")
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void getForm_shouldRenderViewWithNewFormAndMenu() throws Exception {
        mvc.perform(get("/settings/password"))
                .andExpect(status().isOk())
                .andExpect(view().name("change-password"))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("menu", "settings"));
    }

    @Test
    @DisplayName("GET /settings/password → form stays in model if already present")
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void getForm_shouldKeepExistingForm() throws Exception {
        var prefilled = new cz.solutia.acme.core.dto.ChangePasswordForm();
        prefilled.setCurrentPassword("old");
        prefilled.setNewPassword("newValid#1");
        prefilled.setConfirmNewPassword("newValid#1");

        mvc.perform(get("/settings/password").flashAttr("form", prefilled))
                .andExpect(status().isOk())
                .andExpect(view().name("change-password"))
                .andExpect(model().attribute("menu", "settings"))
                .andExpect(model().attribute("form", prefilled));
    }

    @Test
    @DisplayName("POST /settings/password with validation errors → view with errors")
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void postChange_withValidationErrors_ShouldReturnViewWithErrors() throws Exception {
        mvc.perform(post("/settings/password")
                        .with(csrf())
                        .param("currentPassword", "")
                        .param("newPassword", "abc")
                        .param("confirmNewPassword", "xyz"))
                .andExpect(status().isOk())
                .andExpect(view().name("change-password"))
                .andExpect(model().attribute("menu", "settings"))
                .andExpect(model().attributeHasErrors("form"));
    }

    @Test
    @DisplayName("POST /settings/password (success) ")
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void postChange_success_ShouldRedirect() throws Exception {
        mvc.perform(post("/settings/password")
                        .with(csrf())
                        .param("currentPassword", "correctOldPass")
                        .param("newPassword", "NewPass#2024")
                        .param("confirmNewPassword", "NewPass#2024"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/password"));
    }


    @Test
    @DisplayName("POST → InvalidCurrentPasswordException ")
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void postChange_invalidCurrent_ShouldRenderFieldError() throws Exception {
        mvc.perform(post("/settings/password")
                        .with(csrf())
                        .param("currentPassword", "wrongOldPass")
                        .param("newPassword", "NewPass#2024")
                        .param("confirmNewPassword", "NewPass#2024"))
                .andExpect(status().isOk())
                .andExpect(view().name("change-password"))
                .andExpect(model().attribute("menu", "settings"))
                .andExpect(model().attributeHasFieldErrors("form", "currentPassword"));
    }

    @Test
    @DisplayName("POST → SamePasswordException")
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void postChange_samePassword_ShouldRenderFieldError() throws Exception {
        mvc.perform(post("/settings/password")
                        .with(csrf())
                        .param("currentPassword", "CorrectOldPass")
                        .param("newPassword", "CorrectOldPass")
                        .param("confirmNewPassword", "CorrectOldPass"))
                .andExpect(status().isOk())
                .andExpect(view().name("change-password"))
                .andExpect(model().attribute("menu", "settings"))
                .andExpect(model().attributeHasFieldErrors("form", "newPassword"));
    }

    @Test
    @DisplayName("POST → UserNotFoundException")
    @WithMockUser(username = "nonexistent_user", roles = {"ADMIN"})
    void postChange_userNotFound_ShouldRenderErrorView() throws Exception {
        mvc.perform(post("/settings/password")
                        .with(csrf())
                        .param("currentPassword", "anything")
                        .param("newPassword", "Some#New1")
                        .param("confirmNewPassword", "Some#New1"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("message", "User not found."));
    }
}
