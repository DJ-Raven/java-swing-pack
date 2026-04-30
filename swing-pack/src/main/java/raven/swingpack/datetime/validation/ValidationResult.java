package raven.swingpack.datetime.validation;

import raven.swingpack.datetime.DateTimePart;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Raven
 */
public class ValidationResult {

    public List<Violation> getViolations() {
        return violations;
    }

    public void addViolation(Violation violation) {
        violations.add(violation);
    }

    public void addViolation(String message) {
        addViolation(Severity.ERROR, message);
    }

    public void addViolation(Severity severity, String message) {
        addViolation(null, severity, message);
    }

    public void addViolation(DateTimePart.Type type, String message) {
        addViolation(type, Severity.ERROR, message);
    }

    public void addViolation(DateTimePart.Type type, Severity severity, String message) {
        addViolation(new Violation(type, severity, message));
    }

    public Violation check(DateTimePart part) {
        for (Violation violation : violations) {
            if (violation.getType() == part.getType()) {
                return violation;
            }
        }
        return null;
    }

    public boolean isValid() {
        return violations.isEmpty() || !isError();
    }

    public boolean isSuccess() {
        return checkSeverity(Severity.SUCCESS);
    }

    public boolean isError() {
        return checkSeverity(Severity.ERROR);
    }

    public boolean isWarning() {
        return checkSeverity(Severity.WARNING);
    }

    private boolean checkSeverity(Severity severity) {
        for (Violation violation : violations) {
            if (violation.getSeverity() == severity) {
                return true;
            }
        }
        return false;
    }

    private final List<Violation> violations = new ArrayList<>();

    public static class Violation {

        public DateTimePart.Type getType() {
            return type;
        }

        public String getMessage() {
            return message;
        }

        public Severity getSeverity() {
            return severity;
        }

        public Violation(DateTimePart.Type type, Severity severity, String message) {
            this.type = type;
            this.severity = severity;
            this.message = message;
        }

        private final DateTimePart.Type type;
        private final Severity severity;
        private final String message;
    }

    public enum Severity {
        SUCCESS,
        ERROR,
        WARNING
    }
}
