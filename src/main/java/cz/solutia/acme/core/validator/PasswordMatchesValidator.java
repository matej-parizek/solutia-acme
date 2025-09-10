package cz.solutia.acme.core.validator;

import cz.solutia.acme.core.dto.ChangePasswordForm;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public boolean isValid(Object object, ConstraintValidatorContext ctx) {
        var changePasswordForm = (ChangePasswordForm) object;

        return Optional.ofNullable(changePasswordForm).map(form ->
                form.getNewPassword() != null
                        && form.getConfirmNewPassword() != null
                        && form.getNewPassword().equals(form.getConfirmNewPassword())
        ).orElse(false);
    }
}
