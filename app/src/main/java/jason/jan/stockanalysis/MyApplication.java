package jason.jan.stockanalysis;

import android.app.Application;

import com.github.anrwatchdog.ANRWatchDog;

import jason.jan.stockanalysis.data.prefs.PreferenceImpl;
import jason.jan.stockanalysis.utils.LogUtils;
import me.yokeyword.fragmentation.Fragmentation;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2020/4/2 20:18
 */
public class MyApplication extends Application {

    private static MyApplication context;

    private PreferenceImpl preference;

    public static MyApplication getInstance(){
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        initDebug();
        initFragment();
        initData();
    }

    private void initDebug(){
        //ANR检测
        new ANRWatchDog().start();
        new ANRWatchDog().setIgnoreDebugger(true).start();
    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        Fragmentation.builder()
                // 设置 栈视图 模式为 （默认）悬浮球模式   SHAKE: 摇一摇唤出  NONE：隐藏， 仅在Debug环境生效
                .stackViewMode(Fragmentation.BUBBLE)
                .debug(BuildConfig.DEBUG) // 实际场景建议.debug(BuildConfig.DEBUG)
                /**
                 * 可以获取到{@link AfterSaveStateTransactionWarning}
                 * 在遇到After onSaveInstanceState时，不会抛出异常，会回调到下面的ExceptionHandler
                 */
                .handleException(e -> {
                    // 以Bugtags为例子: 把捕获到的 Exception 传到 Bugtags 后台。
                    // Bugtags.sendException(e);
                    LogUtils.e("", "##" + "Fragment catch Error:" + e.getMessage());
                })
                .install();
    }

    private void initData(){

        preference = new PreferenceImpl(this);
        preference.getRecordAllNameFirst();
    }

    public PreferenceImpl getPreference(){
        if (preference == null) {
            preference = new PreferenceImpl(this);
        }

        return preference;
    }

}
