package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidFileValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFile {
    String message() default "File does not exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
