package raven.swingpack.datetime;

/**
 * @author Raven
 */
public class DateTimePart {

    private final Type type;
    private final String pattern;
    private final int maxLength;
    private final int patternLength;

    private final StringBuilder input;
    private boolean reset;

    protected DateTimePart(Type type, String pattern, int maxLength) {
        this.type = type;
        this.pattern = pattern;
        this.maxLength = maxLength;
        this.patternLength = pattern.length();
        this.input = new StringBuilder();
    }

    protected void append(char digit) {
        input.append(digit);
    }

    protected int createTemp(char digit) {
        return Integer.parseInt(String.valueOf(input) + digit);
    }

    protected void clear() {
        input.setLength(0);
    }

    protected boolean remove() {
        if (isEmpty()) {
            return false;
        }
        if (isNotAllowZeroValue()) {
            clear();
        } else {
            input.setLength(input.length() - 1);
        }
        reset = false;
        return true;
    }

    protected boolean isReset() {
        return reset;
    }

    protected void setReset(boolean reset) {
        this.reset = reset;
    }

    protected void format() {
        if (!isEmpty()) {
            int value = getValue();
            if (type.isValid(value)) {
                String formatted = String.format("%0" + patternLength + "d", value);
                input.setLength(0);
                input.append(formatted);
            } else {
                clear();
            }
        }
    }

    protected int getMaxLength() {
        return maxLength;
    }

    protected int getPatternLength() {
        return patternLength;
    }

    protected boolean isNotAllowZeroValue() {
        return getType() == Type.MONTH && patternLength > 2;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return pattern;
        }
        int value = getValue();
        if (type == Type.AM_PM) {
            return DateTimePatternParser.getAmpmString(value);
        } else if (type == Type.MONTH) {
            if (patternLength > 3) {
                return DateTimePatternParser.getMonthsLong(value);
            } else if (patternLength > 2) {
                return DateTimePatternParser.getMonthsShort(value);
            }
        }
        return input.toString();
    }

    public boolean isSeparator() {
        return type == Type.SEPARATOR;
    }

    public boolean isEmpty() {
        return input.length() == 0;
    }

    public int getLength() {
        return input.length();
    }

    public Type getType() {
        return type;
    }

    public String getPattern() {
        return pattern;
    }

    public int getValue() {
        if (isEmpty()) {
            return 0;
        }
        return Integer.parseInt(input.toString());
    }

    public void setValue(int value) {
        if (isEmpty() || getValue() != value) {
            String formatted = String.format("%0" + patternLength + "d", value);
            input.setLength(0);
            input.append(formatted);
            reset = true;
        }
    }

    public enum Type {
        DAY(1, 31),
        MONTH(1, 12),
        YEAR(1, 9999),
        HOUR_24(0, 23),
        HOUR_12(1, 12),
        MINUTE(0, 59),
        SECOND(0, 59),
        AM_PM(1, 2),
        SEPARATOR(1, 1);

        private final int minValue;
        private final int maxValue;

        Type(int minValue, int maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public int getMinValue() {
            return minValue;
        }

        public boolean isValid(int value) {
            return value >= minValue && value <= maxValue;
        }

        public int getMaxValue() {
            return maxValue;
        }
    }
}
