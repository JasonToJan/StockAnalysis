package jason.jan.stockanalysis.mvvm.viewmodel;

import android.app.Application;
import android.text.TextUtils;
import android.util.ArrayMap;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jason.jan.stockanalysis.base.BaseViewModel;
import jason.jan.stockanalysis.data.DataSource;
import jason.jan.stockanalysis.data.http.RepositoryImpl;
import jason.jan.stockanalysis.entity.BannerBean;
import jason.jan.stockanalysis.entity.Condition;
import jason.jan.stockanalysis.entity.Resource;
import jason.jan.stockanalysis.entity.Stock;
import jason.jan.stockanalysis.utils.DataUtils;
import jason.jan.stockanalysis.utils.LogUtils;
import jason.jan.stockanalysis.utils.MyTimeUtils;

import static jason.jan.stockanalysis.utils.LogUtils.d;
import static jason.jan.stockanalysis.utils.LogUtils.w;

/**
 * desc:
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2020/4/3 13:16
 **/
public class AnalysisFViewModel extends BaseViewModel<RepositoryImpl> {

    private static final String TAG = "AnalysisFViewModel";

    public AnalysisFViewModel(@NonNull Application application) {
        super(application);
    }

    public interface AnalysisCallback {

        /**
         * 完成某一只股票分析，size表示多少股票符合要求
         *
         * @param size
         */
        void finishOneStock(String code, int size);

        /**
         * 完成所有股票分析
         */
        void finishAllStock();

        /**
         * 分析失败
         */
        void failedAnalysis(Throwable e);
    }

    /**
     * 开始分析数据库
     */
    public void doAnalysis(AnalysisCallback callback) {

        List<Condition> params = DataSource.getInstance().getAnalysisParams();

        if (params == null || params.size() == 0) {
            callback.failedAnalysis(new Throwable("Params is null~"));
            return;
        }

        Observable.just(1)
                .observeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        doAnalysisInner(callback, params);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failedAnalysis(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 分析内核
     *
     * @param callback
     */
    private synchronized void doAnalysisInner(AnalysisCallback callback, List<Condition> params) {
        if (params == null || params.size() == 0) {
            callback.failedAnalysis(new Throwable("params is null~"));
            return;
        }

        List<String> codes = getRepository().getDatabase().stockDao().getDistinctCode();
        if (codes == null || codes.size() == 0) {
            d(TAG, "sorry, the database has no stock's code...Please add some data.");
            return;
        }

        d(TAG, "数据库股票代码长度为：" + codes.size());
        //保存最终结果
        if (DataSource.getInstance().getAnalysisList() == null) {
            DataSource.getInstance().setAnalysisList(new ArrayList<>());
        }
        DataSource.getInstance().getAnalysisList().clear();

        for (int i = 0; i < codes.size(); i++) {
            getTheCodeStockAndAnalysis(codes.get(i), params, callback);
        }

        callback.finishAllStock();
    }

    /**
     * 获取数据库中的股票信息然后分析
     *
     * @param callback
     * @param params
     */
    private synchronized void getTheCodeStockAndAnalysis(String code, List<Condition> params, AnalysisCallback callback) {

        if (TextUtils.isEmpty(code)) {
            callback.failedAnalysis(new Throwable("the code is null~"));
            return;
        }

        try {
            List<Stock> theCodeStocks = getRepository().getDatabase().stockDao().getStocksByCode(code);

            if (theCodeStocks == null || theCodeStocks.size() <= 2) {
                d(TAG, "这个股票没有历史记录哦，继续分析下一只吧...");
                return;
            }

            //先插入ArrayMap，然后再分析,顺便找到最近三十天
            ArrayMap<Long, Stock> arrayMap = new ArrayMap<>();
            List<Float> listVolumeMaxs = new ArrayList<>();
            long range = 24 * 3600 * 1000 * 30L;
            for (int i = 0; i < theCodeStocks.size(); i++) {
                arrayMap.put(theCodeStocks.get(i).getCurrentTime(), theCodeStocks.get(i));
                float maxVolume = getRepository().getDatabase().stockDao().getMaxVolumeByCode(code, range);

                //d(TAG, "maxVolume = " + maxVolume);
                listVolumeMaxs.add(maxVolume);
            }

            //遍历分析这个股票
            int resultSize = 0;
            for (int i = 0; i < theCodeStocks.size(); i++) {

                boolean isTarget = doAnalaysisTheCodeTheStock(arrayMap, listVolumeMaxs.get(i), theCodeStocks.get(i), params);
                if (isTarget) {
                    resultSize++;
                }
            }

            //此股票分析完毕
            callback.finishOneStock(code, resultSize);

        } catch (Throwable e) {
            d("Error", "##" + e.getMessage());
            callback.failedAnalysis(e);
        }
    }

    /**
     * 分析次股票
     *
     * @param targetStock
     * @param params
     * @return
     */
    private boolean doAnalaysisTheCodeTheStock(ArrayMap<Long, Stock> arrayMap, float maxVolume, Stock targetStock, List<Condition> params) {

        long currentTime = targetStock.getCurrentTime();
        if (arrayMap == null || arrayMap.size() <= 1) return false;

        long dayMills = 24 * 3600 * 1000;
        Stock yesterDayStock = arrayMap.get(currentTime - dayMills);
        Stock beforeYesterDayStock = arrayMap.get(currentTime - 2 * dayMills);
        Stock nextStock = arrayMap.get(currentTime + dayMills);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;//股票一般时间为星期1到星期5
        if (week == 1) {//当前是星期一的话
            yesterDayStock = arrayMap.get(currentTime - 3 * dayMills);
            beforeYesterDayStock = arrayMap.get(currentTime - 4 * dayMills);
        } else if (week == 2) {
            beforeYesterDayStock = arrayMap.get(currentTime - 4 * dayMills);
        } else if (week == 5) {
            nextStock = arrayMap.get(currentTime + 3 * dayMills);
        }

        for (Condition condition : params) {

            Stock target1 = null;
            switch (condition.getDay1()) {
                case 0://今天
                    target1 = targetStock;
                    break;

                case 1://昨天
                    target1 = yesterDayStock;
                    break;

                case 2://前天
                    target1 = beforeYesterDayStock;
                    break;
            }

            Stock target2 = null;
            switch (condition.getDay2()) {
                case 0://今天
                    target2 = targetStock;
                    break;

                case 1://昨天
                    target2 = yesterDayStock;
                    break;

                case 2://前天
                    target2 = beforeYesterDayStock;
                    break;
            }

            float attr1 = 0;//0表示开盘，1表示收盘，2表示最高价，3表示最低价，4表示成交量
            switch (condition.getAttr1()) {
                case 0:
                    if (target1 == null) return false;

                    attr1 = target1.getOpenPrice();
                    break;

                case 1:
                    if (target1 == null) return false;

                    attr1 = target1.getClosePrice();
                    break;

                case 2:
                    if (target1 == null) return false;

                    attr1 = target1.getMaxPrice();
                    break;

                case 3:
                    if (target1 == null) return false;

                    attr1 = target1.getMinPrice();
                    break;

                case 4:
                    if (target1 == null) return false;

                    attr1 = target1.getVolume();
                    break;
            }

            float attr2 = 0;//0表示开盘，1表示收盘，2表示最高价，3表示最低价，4表示成交量
            switch (condition.getAttr2()) {
                case 0:
                    if (target2 == null) return false;

                    attr2 = target2.getOpenPrice();
                    break;

                case 1:
                    if (target2 == null) return false;

                    attr2 = target2.getClosePrice();
                    break;

                case 2:
                    if (target2 == null) return false;

                    attr2 = target2.getMaxPrice();
                    break;

                case 3:
                    if (target2 == null) return false;

                    attr2 = target2.getMinPrice();
                    break;

                case 4:
                    if (target2 == null) return false;

                    attr2 = target2.getVolume();
                    break;
            }

            int isHighOrLow = condition.getFeature();//0表示高，1表示低
            float value = condition.getFeatureValue() / 100.0f;

            //LogUtils.d(TAG, "value=" + value + " attr1=" + attr1 + " attr2=" + attr2);
            if (condition.isOnlyOneAttr()) {
                if (target1 == null) return false;
                //只比较成交量
                if (isHighOrLow == 1) {
                    if (target1.getVolume() / maxVolume > value) {
                        return false;//本来是低，这里是高了，所以过滤掉
                    }
                } else {
                    if (target1.getVolume() / maxVolume < value) {
                        return false;//本来是高，这里是低了，所以过滤掉
                    }
                }

            } else if (condition.isHasTwoValue()) {//如果表示的是区间

                float value2 = condition.getFeatureValue2() / 100.0f;
                boolean isValue2Bigger = value2 > value;
                float radioOffset = (attr2 - attr1) / attr1;

                //比如 开盘价 是前一天的 5% - 10% 注意，可以为负
                if ( radioOffset > (isValue2Bigger ? value2 : value) || radioOffset < (isValue2Bigger ? value : value2)) {
                    return false;
                }

            } else {

                if (isHighOrLow == 1) {//今天比昨天 低 20%

                    if ((attr2 - attr1) / attr2 > value || attr1 > attr2) {
                        return false;//本来要求低，这里却高了
                    }

                } else {//今天比昨天 高 20%

                    if ((attr1 - attr2) / attr2 < value || attr1 < attr2) {
                        return false;//本来要求高的，这里却低了
                    }
                }

            }
        }

//        d(TAG, " 今天数据：" + targetStock.toString());
//        d(TAG, " 昨天数据：" + yesterDayStock.toString());
//        d(TAG, " 前天数据：" + beforeYesterDayStock.toString());
//        d(TAG, " maxVolume=" + maxVolume);

        if (DataSource.getInstance().getAnalysisList() == null) {
            DataSource.getInstance().setAnalysisList(new ArrayList<>());
        }
        //d(TAG, " 经过匹对，此股符合条件...加入结果集合");
        DataSource.getInstance().getAnalysisList().add(DataUtils.convertByStock(targetStock,nextStock));

        return true;
    }

}
