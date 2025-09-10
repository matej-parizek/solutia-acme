package cz.solutia.acme.core.controller;

import cz.solutia.acme.core.dto.ChangePasswordForm;
import cz.solutia.acme.exception.InvalidCurrentPasswordException;
import cz.solutia.acme.exception.SamePasswordException;
import cz.solutia.acme.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

@RequiredArgsConstructor
@ControllerAdvice(assignableTypes = PasswordController.class)
public class PasswordExceptionHandlerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(InvalidCurrentPasswordException.class)
    public ModelAndView handleInvalidCurrent(InvalidCurrentPasswordException ex, HttpServletRequest req) {
        ChangePasswordForm form = blankForm();
        BindingResult br = new BeanPropertyBindingResult(form, "form");
        br.rejectValue(
                "currentPassword",
                "password.current.invalid",
                Objects.requireNonNull(messageSource.getMessage("password.current.invalid", null,
                        "Current password is incorrect.", LocaleContextHolder.getLocale()))
        );

        ModelAndView mav = new ModelAndView("change-password");
        mav.addObject("menu", "settings");
        mav.addObject("form", form);
        mav.addObject(BindingResult.MODEL_KEY_PREFIX + "form", br);
        return mav;
    }

    @ExceptionHandler(SamePasswordException.class)
    public ModelAndView handleSamePassword(SamePasswordException ex, HttpServletRequest req) {
        ChangePasswordForm form = blankForm();
        BindingResult br = new BeanPropertyBindingResult(form, "form");
        br.rejectValue(
                "newPassword",
                "password.same.as.old",
                Objects.requireNonNull(messageSource.getMessage("password.same.as.old", null,
                        "New password must differ from the current one.", LocaleContextHolder.getLocale()))
        );

        ModelAndView mav = new ModelAndView("change-password");
        mav.addObject("menu", "settings");
        mav.addObject("form", form);
        mav.addObject(BindingResult.MODEL_KEY_PREFIX + "form", br);
        return mav;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleUserNotFound(UserNotFoundException ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("message", "User not found.");
        return mav;
    }

    private ChangePasswordForm blankForm() {
        ChangePasswordForm form = new ChangePasswordForm();
        form.setCurrentPassword(null);
        form.setNewPassword(null);
        form.setConfirmNewPassword(null);
        return form;
    }
}
