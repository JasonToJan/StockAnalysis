package jason.jan.stockanalysis.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static jason.jan.stockanalysis.utils.LogUtils.d;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2020/4/5 15:45
 */
public class MyTimeUtils {

    public static Date stringToDate(String strTime, SimpleDateFormat format)
            throws ParseException {
        Date date = null;
        date = format.parse(strTime);
        return date;
    }

    public static long stringToLong(String strTime, SimpleDateFormat format) {

        try {
            Date date = stringToDate(strTime, format); // String类型转成date类型
            if (date == null) {
                return 0;
            } else {
                long currentTime = dateToLong(date); // date类型转成long类型
                return currentTime;
            }
        } catch (Throwable e) {
            d("Error", "##" + e.getMessage());
        }

        return 0;
    }

    /*
     * 将时间转换为时间戳
     */
    public static long dateToStamp(String s) {
        long result = 0;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse(s);
            result = date.getTime();
        } catch (Throwable e) {
            d("Error", "##" + e.getMessage());
        }
        return result;
    }

    /*
     * 将时间转换为时间戳
     */
    public static long dateToStamp(String s,SimpleDateFormat format) {
        long result = 0;
        try {
            Date date = format.parse(s);
            result = date.getTime();
        } catch (Throwable e) {
            d("Error", "##" + e.getMessage());
        }
        return result;
    }

    public static long dateToLong(Date date) {
        return date.getTime();
    }
}
