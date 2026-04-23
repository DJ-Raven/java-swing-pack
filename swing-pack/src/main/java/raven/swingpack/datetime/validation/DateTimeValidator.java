package raven.swingpack.datetime.validation;

import java.time.LocalDateTime;

/**
 * @author Raven
 */
public interface DateTimeValidator {

    ValidationResult validate(LocalDateTime dateTime);
}
