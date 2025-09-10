package cz.solutia.acme.core.controller;

import cz.solutia.acme.core.model.User;
import cz.solutia.acme.core.repository.GeneralSettingsRepository;
import cz.solutia.acme.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class SettingsController {
    private GeneralSettingsRepository generalSettingsRepository;

    private UserRepository userRepository;

    @Autowired
    public SettingsController(GeneralSettingsRepository generalSettingsRepository, UserRepository userRepository) {
        this.generalSettingsRepository = generalSettingsRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("menu", "settings");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String firstname = "";
        String lastname = "";
        String email = authentication.getName();
        Optional<User> user = userRepository.findByUsername(authentication.getName());
        if (user.isPresent()) {
            firstname = user.get().getFirstname();
            lastname = user.get().getLastname();
        }

        model.addAttribute("firstname", firstname);
        model.addAttribute("lastname", lastname);
        model.addAttribute("email", email);
        return "settings";
    }
}
