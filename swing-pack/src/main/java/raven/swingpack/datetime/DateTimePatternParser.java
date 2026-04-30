package raven.swingpack.datetime;

import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Raven
 */
public class DateTimePatternParser {

    private static String[] monthsLong;
    private static String[] monthsShort;
    private static String[] ampmString;

    public static List<DateTimePart> parsePattern(String pattern) {
        List<DateTimePart> parts = new ArrayList<>();
        Matcher m = Pattern.compile("('(?:''|[^'])*'|[a-zA-Z]+|[^a-zA-Z']+)").matcher(pattern);
        while (m.find()) {
            String token = m.group(1);
            boolean isLiteral = token.startsWith("'") && token.endsWith("'");
            DateTimePart part;
            if (isLiteral) {
                // remove quotes for literal
                token = token.substring(1, token.length() - 1).replace("''", "'");
                part = new DateTimePart(DateTimePart.Type.SEPARATOR, token, token.length());
            } else {
                part = identifyPart(token);
            }
            parts.add(part);
        }
        return parts;
    }

    public static int getDefaultValue(DateTimePart.Type type) {
        LocalDateTime now = LocalDateTime.now();
        return getDefaultValue(type, now);
    }

    public static int getDefaultValue(DateTimePart.Type type, LocalDateTime date) {
        switch (type) {
            case DAY:
                return date.getDayOfMonth();
            case MONTH:
                return date.getMonthValue();
            case YEAR:
                return date.getYear();
            case HOUR_24:
                return date.getHour();
            case HOUR_12:
                return (date.getHour() % 12 == 0) ? 12 : date.getHour() % 12;
            case MINUTE:
                return date.getMinute();
            case SECOND:
                return date.getSecond();
            case AM_PM:
                return date.getHour() < 12 ? 1 : 2;
            default:
                return 0;
        }
    }

    private static DateTimePart identifyPart(String token) {
        char ch = token.charAt(0);
        DateTimePart.Type type;
        int length;
        switch (ch) {
            case 'D':
            case 'd':
                type = DateTimePart.Type.DAY;
                length = 2;
                break;
            case 'M':
                type = DateTimePart.Type.MONTH;
                length = 2;
                break;
            case 'Y':
            case 'y':
                type = DateTimePart.Type.YEAR;
                length = token.length();
                break;
            case 'H':
                type = DateTimePart.Type.HOUR_24;
                length = 2;
                break;
            case 'h':
                type = DateTimePart.Type.HOUR_12;
                length = 2;
                break;
            case 'm':
                type = DateTimePart.Type.MINUTE;
                length = 2;
                break;
            case 's':
                type = DateTimePart.Type.SECOND;
                length = 2;
                break;
            case 'A':
            case 'a':
                type = DateTimePart.Type.AM_PM;
                length = 1;
                break;
            default:
                type = DateTimePart.Type.SEPARATOR;
                length = token.length();
                break;
        }
        return new DateTimePart(type, token, length);
    }

    public static String getMonthsLong(int month) {
        if (monthsLong == null) {
            monthsLong = Arrays.copyOf(new DateFormatSymbols().getMonths(), 12);
        }
        return monthsLong[adjustValue(month, 1, monthsLong.length) - 1];
    }

    public static String getMonthsShort(int month) {
        if (monthsShort == null) {
            monthsShort = Arrays.copyOf(new DateFormatSymbols().getShortMonths(), 12);
        }
        return monthsShort[adjustValue(month, 1, monthsShort.length) - 1];
    }

    public static String getAmpmString(int value) {
        if (ampmString == null) {
            ampmString = new DateFormatSymbols().getAmPmStrings();
        }
        return ampmString[adjustValue(value, 1, ampmString.length) - 1];
    }

    public static int adjustValue(int value, int min, int max) {
        if (value < min) {
            value = min;
        } else if (value > max) {
            value = max;
        }
        return value;
    }
}
