package com.seosa.seosa.global.utils;

import java.time.LocalDateTime;

public class CursorUtils {

    private static final int DATE_LENGTH = 20;
    private static final int ID_LENGTH = 10;

    // 커서 문자열
    public static String generateCustomCursor(LocalDateTime createdAt, Long id) {
        if (createdAt == null || id == null) return null;

        String dateStr = createdAt.toString()
                .replaceAll("T", "")
                .replaceAll("-", "")
                .replaceAll(":", "") + "00";

        String paddedDate = String.format("%1$" + DATE_LENGTH + "s", dateStr).replace(' ', '0');
        String paddedId = String.format("%1$" + ID_LENGTH + "s", id).replace(' ', '0');

        return paddedDate + paddedId;
    }
}
