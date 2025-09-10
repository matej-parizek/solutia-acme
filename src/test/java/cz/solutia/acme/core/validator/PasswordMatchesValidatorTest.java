package cz.solutia.acme.core.validator;

import cz.solutia.acme.core.dto.ChangePasswordForm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordMatchesValidatorTest {

    @Test
    void passwordMatches_ok() {
        var v = new PasswordMatchesValidator();
        var form = new ChangePasswordForm();
        form.setCurrentPassword("OldPass1!");
        form.setNewPassword("NewPass1!");
        form.setConfirmNewPassword("NewPass1!");
        assertTrue(v.isValid(form, null));
    }

    @Test
    void passwordMatches_confirmDiffers() {
        var v = new PasswordMatchesValidator();
        var form = new ChangePasswordForm();
        form.setCurrentPassword("OldPass1!");
        form.setNewPassword("NewPass1!");
        form.setConfirmNewPassword("NewPass2!");
        assertFalse(v.isValid(form, null));
    }

    @Test
    void passwordMatches_nullSafety() {
        var v = new PasswordMatchesValidator();
        assertFalse(v.isValid(null, null));
        var form = new ChangePasswordForm();
        assertFalse(v.isValid(form, null));
    }
}
