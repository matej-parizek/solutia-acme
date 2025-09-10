package cz.solutia.acme.core.dto;

import cz.solutia.acme.core.validator.PasswordMatches;
import cz.solutia.acme.core.validator.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@PasswordMatches
public class ChangePasswordForm {
    @NotBlank(message = "{password.current.required}")
    private String currentPassword;

    @NotBlank(message = "{password.new.required}")
    @ValidPassword
    private String newPassword;

    @NotBlank(message = "{password.confirm.required}")
    private String confirmNewPassword;
}
