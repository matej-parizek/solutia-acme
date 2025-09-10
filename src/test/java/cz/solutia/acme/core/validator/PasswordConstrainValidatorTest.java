package cz.solutia.acme.core.validator;

import cz.solutia.acme.core.dto.ChangePasswordForm;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class PasswordConstrainValidatorTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    private static ChangePasswordForm formWithNew(String newPwd) {
        ChangePasswordForm f = new ChangePasswordForm();
        f.setCurrentPassword("Current1!");
        f.setNewPassword(newPwd);
        f.setConfirmNewPassword(newPwd);
        return f;
    }

    @Test
    void validPassword_example() {
        assertTrue(validator.validate(formWithNew("GoodPass1!")).isEmpty());
    }

    @Test
    void validPassword_smallestLimitValues() {
        assertFalse(validator.validate(formWithNew("Aa1!aaa")).isEmpty());
        assertTrue(validator.validate(formWithNew("Aa1!aaaa")).isEmpty());
    }

    @Test
    void validPassword_highestLimitValue() {
        String thirty    = "A1!" + "a".repeat(27);
        String thirtyOne = "A1!" + "a".repeat(28);
        assertTrue(validator.validate(formWithNew(thirty)).isEmpty());
        assertFalse(validator.validate(formWithNew(thirtyOne)).isEmpty());
    }

    @Test
    void validPassword_requiresAllCharClasses_andNoWhitespace() {
        assertFalse(validator.validate(formWithNew("goodpass1!")).isEmpty());
        assertFalse(validator.validate(formWithNew("GOODPASS1!")).isEmpty());
        assertFalse(validator.validate(formWithNew("GoodPass!!")).isEmpty());
        assertFalse(validator.validate(formWithNew("GoodPass1")).isEmpty());
        assertFalse(validator.validate(formWithNew("Good Pass1!")).isEmpty());
    }

    @Test
    void validPassword_nullOrEmpty_fail() {
        ChangePasswordForm f1 = new ChangePasswordForm();
        f1.setCurrentPassword("Current1!");
        f1.setNewPassword(null);
        f1.setConfirmNewPassword(null);
        assertFalse(validator.validate(f1).isEmpty());

        ChangePasswordForm f2 = new ChangePasswordForm();
        f2.setCurrentPassword("Current1!");
        f2.setNewPassword("");
        f2.setConfirmNewPassword("");
        assertFalse(validator.validate(f2).isEmpty());
    }
}
