package com.chung.lifusic.musicservice.common.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateUtil {
    public static long localDateTimeToMilliseconds(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
