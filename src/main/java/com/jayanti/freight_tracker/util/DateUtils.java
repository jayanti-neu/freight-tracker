package com.jayanti.freight_tracker.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static String format(LocalDateTime time) {
        return time.format(FORMATTER);
    }
}
