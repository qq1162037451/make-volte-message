package com.jhhc.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author xiaojiang
 * @date 2021/1/8 17:47
 */
public class DateUtils {

    private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");

    public static String formatYYYYMMDD(Date date) {
        return sdf1.format(date);
    }

    public static String formatYYYYMMDDYYYYMMDD(Date date) {
        return sdf2.format(date);
    }

    public static Date parseYYYYMMDD(String date) throws ParseException {
        return sdf1.parse(date);
    }

}
