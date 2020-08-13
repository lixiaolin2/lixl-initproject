package cn.xmlly.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lixiaolin
 * @date 2020/7/11 11:10
 */
@Slf4j
public class DateUtil {

    public static final String yMdHmPattern = "yyyyMMddHHmm";

    public static final String yMdPattern = "yyyyMMdd";

    public static final String SPECIFIC_DATE = "yyyyMMddHHmmss";
    /**
     * 时间加减法,并转成相应格式字符串
     *
     * @param date
     * @param format
     * @param sj
     * @return
     */
    public static String timeSub(Date date, String format, int sj) {
        date.setMinutes(date.getMinutes() - sj);
        String dateString = "";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            dateString = sdf.format(date);
        } catch (Exception ex) {
            log.error("【timeSub error = {}】", ex.getMessage());
        }
        return dateString;
    }


    public static String toString(Date date, String pattern)
    {
        if (date == null)
        {
            return "";
        }
        if (pattern == null)
        {
            pattern = "yyyy-MM-dd";
        }
        String dateString = "";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try
        {
            dateString = sdf.format(date);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return dateString;
    }


//    public static void main(String[] args) {
////        String string = DateUtil.toString(new Date(), DateUtil.SPECIFIC_DATE);
//        int anInt = new Random().nextInt(9999);
//        System.out.println();
//    }

}
