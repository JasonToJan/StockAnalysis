package jason.jan.stockanalysis.data.prefs;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import jason.jan.stockanalysis.utils.LogUtils;


/**
 * Description: 本地sp文件接口实现类
 * *
 * Creator: Wang
 * Date: 2019/4/5 8:07
 */
public class PreferenceImpl implements IPreference {

    private static final String TAG = "PreferenceImpl";

    private final SharedPreferences mPreferences;

    /**
     * 设置 需要用的文件名
     */
    public static final String SETTING_PREFERENCE_FILE_NAME = "StockAnalysis_settings";

    /**
     * 是否是第一次启动 以此决定是否弹出弹框
     */
    public boolean isRecordAllName = true;
    private static final String IS_RECORD_ALL_NAME = "is_record_all_name";

    public PreferenceImpl(Application application) {
        LogUtils.d(TAG, "开始初始化PreferenceImpl");
        mPreferences = application.getSharedPreferences(SETTING_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public boolean hasRecordAllName() {
        return isRecordAllName;
    }

    @Override
    public void setHasRecordName(boolean isRecord) {
        this.isRecordAllName = isRecord;
        mPreferences.edit().putBoolean(IS_RECORD_ALL_NAME, isRecord).apply();
    }

    @Override
    public void getRecordAllNameFirst() {
        isRecordAllName = mPreferences.getBoolean(IS_RECORD_ALL_NAME, false);
    }
}
