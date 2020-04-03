package jason.jan.stockanalysis.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;

/**
 * desc:
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2020/4/3 13:50
 **/
public class CommonUtils {

    /**
     * 上次点击的 时间戳
     */
    private static long lastClickTime;
    /**
     * 两次点击 时间间隔 必须超过2000ms 才有效，否则会被屏蔽
     */
    private static final int MIN_CLICK_DELAY_TIME_2000 = 2000;
    /**
     * 两次点击 时间间隔 必须超过500ms 才有效，否则会被屏蔽
     */
    private static final int MIN_CLICK_DELAY_TIME_500 = 500;
    /**
     * 两次点击 时间间隔 必须超过200ms 才有效，否则会被屏蔽
     */
    private static final int MIN_CLICK_DELAY_TIME_200 = 200;

    /**
     * 不超过200ms 的 重复点击 会被屏蔽
     *
     * @return
     */
    public static boolean doFirstClick200() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME_200) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    /**
     * 不超过500ms 的 重复点击 会被屏蔽
     *
     * @return
     */
    public static boolean doFirstClick500() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME_500) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    /**
     * 不超过2000ms 的 重复点击 会被屏蔽
     *
     * @return
     */
    public static boolean doFirstClick2000() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME_2000) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    /**
     * 设置Activity 是否固定方向
     * 解决Android8.0系统问题：
     * https://www.jianshu.com/p/1ed07ddbbbe5
     */
    public static void setRequestedOrientationForAndroidO(Activity activity) {
        try {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } catch (Throwable e) {
            LogUtils.d("", "Error##" + e.getMessage());
        }
    }
}
