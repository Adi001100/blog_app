package blog.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordConstraintValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void test_validPassword() {
        String validPassword = "ValidPassword123!";
        assertTrue(validator.validate(new SampleObject(validPassword)).isEmpty());
    }

    @Test
    void test_invalidPassword() {
        String invalidPassword = "INVALIDPASSWORD123!";
        assertFalse(validator.validate(new SampleObject(invalidPassword)).isEmpty());
    }

    private static class SampleObject {

        @ValidPassword
        private final String password;

        public SampleObject(String password) {
            this.password = password;
        }
    }
}

