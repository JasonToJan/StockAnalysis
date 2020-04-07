package jason.jan.stockanalysis.mvvm.viewmodel;

import android.app.Application;
import android.text.TextUtils;
import android.util.ArrayMap;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jason.jan.stockanalysis.base.BaseViewModel;
import jason.jan.stockanalysis.data.DataSource;
import jason.jan.stockanalysis.data.http.RepositoryImpl;
import jason.jan.stockanalysis.entity.Condition2;
import jason.jan.stockanalysis.entity.Stock;
import jason.jan.stockanalysis.utils.DataUtils;
import jason.jan.stockanalysis.utils.LogUtils;

import static jason.jan.stockanalysis.utils.LogUtils.d;

/**
 * desc:
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2020/4/7 12:51
 **/
public class AnalysisF2ViewModel extends BaseViewModel<RepositoryImpl> {

    private static final String TAG = "AnalysisF2ViewModel";

    public AnalysisF2ViewModel(@NonNull Application application) {
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
    public void doAnalysis(String code, String date, String pro, AnalysisCallback callback) {

        if (TextUtils.isEmpty(code) || TextUtils.isEmpty(date) || TextUtils.isEmpty(pro)) {
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
                        doAnalysisInner(code, date, pro, callback);
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

    private void doAnalysisInner(String code, String date, String pro, AnalysisCallback callback) {

        //获取查询参数
        Condition2 condition = new Condition2();
        condition.setProximity(Float.parseFloat(pro));
        List<Stock> theCodeStocks = getRepository().getDatabase().stockDao().getStocksByCode(code);
        d(TAG, "code=" + code + " date=" + date + " pro=" + pro);
        for (int i = 0; i < theCodeStocks.size(); i++) {
            if (theCodeStocks.get(i).getDate().equals(date)) {
                //limit限制长度，offset从哪里开始
                float maxVolume = findRecentlyMax(theCodeStocks,i);
                condition.setTodayStock(theCodeStocks.get(i));
                if (i >= 2) {
                    condition.setYesStock(theCodeStocks.get(i - 1));
                    condition.setBeforeYesStock(theCodeStocks.get(i - 2));
                    d(TAG, "找到目标信息：" + condition.getTodayStock().toString());
                    d(TAG, "找到目标昨日信息：" + condition.getYesStock().toString());
                    d(TAG, "找到目标前日信息：" + condition.getBeforeYesStock().toString());
                    d(TAG, "maxVolume = " + maxVolume);
                    condition.setMaxVolume(maxVolume);
                }
            }

        }

        if (condition.getYesStock() == null || condition.getBeforeYesStock() == null || condition.getTodayStock() == null) {
            d(TAG, "sorry, can't find target stock's info...");
            callback.failedAnalysis(new Throwable("sorry, can't find target stock's info..."));
            return;
        }

        List<String> codes = getRepository().getDatabase().stockDao().getDistinctCode();
        if (codes == null || codes.size() == 0) {
            d(TAG, "sorry, the database has no stock's code...Please add some data.");
            callback.failedAnalysis(new Throwable("sorry, the database has no stock's code...Please add some data."));
            return;
        }

        d(TAG, "数据库股票代码长度为：" + codes.size());
        //保存最终结果
        if (DataSource.getInstance().getAnalysisList2() == null) {
            DataSource.getInstance().setAnalysisList2(new ArrayList<>());
        }
        DataSource.getInstance().getAnalysisList2().clear();

        for (int i = 0; i < codes.size(); i++) {
            getTheCodeStockAndAnalysis(codes.get(i), condition, callback);
        }

        callback.finishAllStock();
    }

    private float findRecentlyMax(List<Stock> allList, int position) {
        if (allList == null || allList.size() == 0) return 0;
        float result = allList.get(0).getVolume();
        int from = position > 30 ? position - 30 : 0;
        for (int i = 0; i < allList.size(); i++) {
            if (i == from) {
                result = allList.get(i).getVolume();
            }
            if (i < from + 60) {
                result = result > allList.get(i).getVolume() ? result : allList.get(i).getVolume();
            }
        }
        return result;
    }

    /**
     * 获取数据库中的股票信息然后分析
     *
     * @param callback
     * @param condition2
     */
    private synchronized void getTheCodeStockAndAnalysis(String code, Condition2 condition2, AnalysisCallback callback) {

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

            List<Float> listVolumeMaxs = new ArrayList<>();
            long range = 24 * 3600 * 1000 * 30L;
            for (int i = 0; i < theCodeStocks.size(); i++) {
                float maxVolume = getRepository().getDatabase().stockDao().getMaxVolumeByCode(code, range);

                //d(TAG, "maxVolume = " + maxVolume);
                listVolumeMaxs.add(maxVolume);
            }

            //遍历分析这个股票
            int resultSize = 0;
            for (int i = 0; i < theCodeStocks.size(); i++) {
                if (i >= 2) {
                    Stock nextStock = (i == theCodeStocks.size() - 1) ? null : theCodeStocks.get(i + 1);

                    boolean isTarget = doAnalaysisTheCodeTheStock(theCodeStocks.get(i), theCodeStocks.get(i - 1), theCodeStocks.get(i - 2), nextStock, listVolumeMaxs.get(i), condition2);
                    if (isTarget) {
                        resultSize++;
                    }
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
     * 具体分析
     *
     * @param todayStock
     * @param yesStock
     * @param beforYesStock
     * @param maxVolume
     * @param condition2
     * @return
     */
    private boolean doAnalaysisTheCodeTheStock(Stock todayStock, Stock yesStock, Stock beforYesStock, Stock nextStock, float maxVolume, Condition2 condition2) {

        if (condition2 == null) return false;
        float proximity1 = condition2.getProximity() / 100.0f;//0.8
        float proximity2 = 2 - proximity1;//1.2

        if (todayStock == null || yesStock == null || beforYesStock == null) return false;

        boolean isSelf = false;
        if (todayStock.getCode().equals(condition2.getTodayStock().getCode()) && todayStock.getDate().equals(condition2.getTodayStock().getDate())) {
            isSelf = true;
            d(TAG, " 此时正在分析自己...");
        }

        //分析今天涨幅是否符合 -0.8 -0.64
        float ratioTarget = (todayStock.getClosePrice() - todayStock.getOpenPrice()) / todayStock.getOpenPrice();
        float ratioCondition = (condition2.getTodayStock().getClosePrice() - condition2.getTodayStock().getOpenPrice()) / condition2.getTodayStock().getOpenPrice();
        float ratioRange1 = ratioCondition * proximity1;//0.64 -0.64
        float ratioRange2 = ratioCondition * proximity2;//0.96 -0.96
        float max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
        float min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;

        if (ratioTarget > max || ratioTarget < min) {
            if (isSelf) {
                LogUtils.d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                LogUtils.d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
            }
            return false;
        }

        //分析昨天涨幅
        ratioTarget = (yesStock.getClosePrice() - yesStock.getOpenPrice()) / yesStock.getOpenPrice();
        ratioCondition = (condition2.getYesStock().getClosePrice() - condition2.getYesStock().getOpenPrice()) / condition2.getYesStock().getOpenPrice();
        ratioRange1 = ratioCondition * proximity1;
        ratioRange2 = ratioCondition * proximity2;
        max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
        min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;

        if (ratioTarget > max || ratioTarget < min) {
            if (isSelf) {
                LogUtils.d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                LogUtils.d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
            }
            return false;
        }

        //分析前天涨幅
        ratioTarget = (beforYesStock.getClosePrice() - beforYesStock.getOpenPrice()) / beforYesStock.getOpenPrice();
        ratioCondition = (condition2.getBeforeYesStock().getClosePrice() - condition2.getBeforeYesStock().getOpenPrice()) / condition2.getBeforeYesStock().getOpenPrice();
        ratioRange1 = ratioCondition * proximity1;
        ratioRange2 = ratioCondition * proximity2;
        max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
        min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;

        if (ratioTarget > max || ratioTarget < min) {
            if (isSelf) {
                LogUtils.d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                LogUtils.d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
            }
            return false;
        }

        //分析今天成交量占历史成交量的多少
        ratioTarget = (todayStock.getVolume() - maxVolume) / maxVolume;
        ratioCondition = (condition2.getTodayStock().getVolume() - condition2.getMaxVolume()) / condition2.getMaxVolume();
        ratioRange1 = ratioCondition * proximity1;
        ratioRange2 = ratioCondition * proximity2;
        max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
        min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;

        if (ratioTarget > max || ratioTarget < min) {
            if (isSelf) {
                LogUtils.d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                LogUtils.d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
            }
            return false;
        }

        //分析昨天成交量占历史成交量的多少
        ratioTarget = (yesStock.getVolume() - maxVolume) / maxVolume;
        ratioCondition = (condition2.getYesStock().getVolume() - condition2.getMaxVolume()) / condition2.getMaxVolume();
        ratioRange1 = ratioCondition * proximity1;
        ratioRange2 = ratioCondition * proximity2;
        max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
        min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;

        if (ratioTarget > max || ratioTarget < min) {
            if (isSelf) {
                LogUtils.d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                LogUtils.d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
            }
            return false;
        }

        //分析前日成交量占历史成交量的多少
        ratioTarget = (beforYesStock.getVolume() - maxVolume) / maxVolume;
        ratioCondition = (condition2.getBeforeYesStock().getVolume() - condition2.getMaxVolume()) / condition2.getMaxVolume();
        ratioRange1 = ratioCondition * proximity1;
        ratioRange2 = ratioCondition * proximity2;
        max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
        min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;

        if (ratioTarget > max || ratioTarget < min) {
            if (isSelf) {
                LogUtils.d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                LogUtils.d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
            }
            return false;
        }

        //发现都符合哦
        d(TAG, "发现都符合：targetStock=" + todayStock.toString());
        if (DataSource.getInstance().getAnalysisList() == null) {
            DataSource.getInstance().setAnalysisList(new ArrayList<>());
        }
        //d(TAG, " 经过匹对，此股符合条件...加入结果集合");
        DataSource.getInstance().getAnalysisList2().add(DataUtils.convertByStock(todayStock, nextStock));

        return true;
    }

}
