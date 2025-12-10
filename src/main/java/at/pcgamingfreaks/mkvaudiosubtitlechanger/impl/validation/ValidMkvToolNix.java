package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidMkvToolNixValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMkvToolNix {
    String message() default "MkvToolNix does not exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
