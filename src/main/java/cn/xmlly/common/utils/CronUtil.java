package cn.xmlly.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * CRON表达式工具类
 *
 * @author: yanglf
 * @version: 1.0
 * @date: 2019/8/5 18:33
 */
public class CronUtil {

    public static String parseTime(Long dateTime) {
        Date date = new Date(dateTime);
        SimpleDateFormat sdf = new SimpleDateFormat("ss-mm-HH-dd-MM-yyyy");
        String dateStrs = sdf.format(date);
        StringBuilder sb = new StringBuilder();
        String[] dateStr = dateStrs.split("-");
        sb.append(dateStr[0]).append(" ").append(dateStr[1]).append(" ").append(dateStr[2]).append(" ")
                .append(dateStr[3]).append(" ").append(dateStr[4]).append(" ").append("?").append(" ").append(dateStr[5]);
        return sb.toString();
    }
}
