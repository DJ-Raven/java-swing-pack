package raven.swingpack.datetime;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Raven
 */
class DateTimeValue {

    public Value[] getValues() {
        return values;
    }

    public DateTimeValue(Value[] values) {
        this.values = values;
    }

    private final Value[] values;

    public LocalDateTime toLocalDateTimeOrNull() {
        if (isValid() && isValidDay()) {
            return toLocalDateTime();
        }
        return null;
    }

    protected LocalDateTime toLocalDateTime() {
        int day = 1;
        int month = 1;
        int year = 0;
        int hour = 0;
        int minute = 0;
        int second = 0;
        int ampm = 0;
        boolean hour12 = false;
        for (Value v : values) {
            if (v.getType() == DateTimePart.Type.DAY) {
                day = v.getValue();
            } else if (v.getType() == DateTimePart.Type.MONTH) {
                month = v.getValue();
            } else if (v.getType() == DateTimePart.Type.YEAR) {
                year = v.getValue();
            } else if (v.getType() == DateTimePart.Type.HOUR_12) {
                hour = v.getValue();
                hour12 = true;
            } else if (v.getType() == DateTimePart.Type.HOUR_24) {
                hour = v.getValue();
            } else if (v.getType() == DateTimePart.Type.MINUTE) {
                minute = v.getValue();
            } else if (v.getType() == DateTimePart.Type.SECOND) {
                second = v.getValue();
            } else if (v.getType() == DateTimePart.Type.AM_PM) {
                ampm = v.getValue();
            }
        }
        if (hour12 && ampm != 0) {
            hour %= 12;
            if (ampm == 2) {
                hour += 12;
            }
        }
        return LocalDateTime.of(year, month, day, hour, minute, second);
    }

    public boolean isValid() {
        for (Value v : values) {
            if (!v.isValid()) {
                return false;
            }
        }
        return true;
    }

    public boolean isNull() {
        for (Value v : values) {
            if (v.getValue() != null) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidDay() {
        Integer day = getValue(DateTimePart.Type.DAY);
        if (day != null) {
            Integer month = getValue(DateTimePart.Type.MONTH);
            Integer year = getValue(DateTimePart.Type.YEAR);
            if (month != null && year != null) {
                int maxDay = YearMonth.of(year, month).lengthOfMonth();
                if (day > maxDay) {
                    return false;
                }
            }
        }
        return true;
    }

    public Integer getValue(DateTimePart.Type type) {
        for (DateTimeValue.Value v : values) {
            if (v.getType() == type) {
                return v.getValue();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "DateTimeValue{" + Arrays.toString(values) + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DateTimeValue that = (DateTimeValue) o;
        return Objects.deepEquals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    public static class Value {

        public Integer getValue() {
            return value;
        }

        public DateTimePart.Type getType() {
            return type;
        }

        public Value(Integer value, DateTimePart.Type type) {
            this.value = value;
            this.type = type;
        }

        private final Integer value;
        private final DateTimePart.Type type;

        public boolean isValid() {
            return value != null && type.isValid(value);
        }

        @Override
        public String toString() {
            return type + "=" + value;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Value value1 = (Value) o;
            return Objects.equals(value, value1.value) && type == value1.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, type);
        }
    }
}
