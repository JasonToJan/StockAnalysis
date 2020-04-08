package jason.jan.stockanalysis;

import android.app.Application;

import com.github.anrwatchdog.ANRWatchDog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

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

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((mContext, layout) -> {
//            layout.setPrimaryColorsId(R.color.white, R.color.status_background);//全局设置主题颜色
            return new ClassicsHeader(mContext).setPrimaryColorId(R.color.colorWhite).setAccentColorId(R.color.colorAccent);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((mContext, layout) -> {
            return new ClassicsFooter(mContext).setPrimaryColorId(R.color.colorWhite).setAccentColorId(R.color.colorAccent);
        });

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
