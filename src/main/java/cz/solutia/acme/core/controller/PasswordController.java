package cz.solutia.acme.core.controller;

import cz.solutia.acme.core.dto.ChangePasswordForm;
import cz.solutia.acme.core.service.IPasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/settings/password")
public class PasswordController {

    private final IPasswordService passwordService;

    @GetMapping
    public String form(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new ChangePasswordForm());
        }
        model.addAttribute("menu", "settings");
        return "change-password";
    }

    @PostMapping
    public String change(@Valid @ModelAttribute("form") ChangePasswordForm form,
                         BindingResult bindingResult,
                         Authentication auth,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("menu", "settings");
            bindingResult.getAllErrors();
            return "change-password";
        }
        passwordService.changePassword(auth.getName(), form.getCurrentPassword(), form.getNewPassword());
        return "redirect:/settings/password";
    }
}
