package com.seosa.seosa.global.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CursorUtils {

    private static final int DATE_LENGTH = 14;  // yyyyMMddHHmmss
    private static final int ID_LENGTH = 10;

    // 커서 문자열 생성
    public static String generateCustomCursor(LocalDateTime createdAt, Long id) {
        if (createdAt == null || id == null) return null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String dateStr = createdAt.format(formatter);

        String paddedId = String.format("%010d", id);

        return dateStr + paddedId;
    }
}