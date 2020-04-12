package jason.jan.stockanalysis.mvvm.viewmodel;

import android.app.Application;
import android.text.TextUtils;
import android.util.ArrayMap;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jason.jan.stockanalysis.base.BaseViewModel;
import jason.jan.stockanalysis.data.DataSource;
import jason.jan.stockanalysis.data.http.RepositoryImpl;
import jason.jan.stockanalysis.entity.AnalysisStock;
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

    public float TOMORROW_BUY_UP_OFFSET = 4.0f;//明天开盘比今天收盘高3%

    public float PROXIMITY = 90;//相似度80%就可以放在条件里面了

    public float getTOMORROW_BUY_UP_OFFSET() {
        return TOMORROW_BUY_UP_OFFSET;
    }

    public void setTOMORROW_BUY_UP_OFFSET(float TOMORROW_BUY_UP_OFFSET) {
        this.TOMORROW_BUY_UP_OFFSET = TOMORROW_BUY_UP_OFFSET;
    }

    public float getPROXIMITY() {
        return PROXIMITY;
    }

    public void setPROXIMITY(float PROXIMITY) {
        this.PROXIMITY = PROXIMITY;
    }

    /**
     * 当前时间，就是最近的条件，这样不用重新查找数据库了
     */
    private List<Condition2> currentTimeCondition = new ArrayList<>();

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

    /**
     * 开始分析数据库
     */
    public void doAnalysis2Days(String code, String date, String pro, AnalysisCallback callback) {

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
                        doAnalysisInner2Days(code, date, pro, callback);
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
     * 明天收盘买，后台涨
     */
    public void doAnalysisTomorrowBuy(AnalysisCallback callback) {

        Observable.just(1)
                .observeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        doAnalysisTomorrowBuyInner(callback);
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
     * 预测明天涨停
     */
    public void doAnalysisUp(AnalysisCallback callback) {

        Observable.just(1)
                .observeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        doAnalysisTomorrowUpInner(callback);
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
     * 预测明天跌停
     */
    public void doAnalysisDown(AnalysisCallback callback) {

        Observable.just(1)
                .observeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        doAnalysisTomorrowDownInner(callback);
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
     * 分析明天收盘买，后天开盘能够比收盘大的
     *
     * @param callback
     */
    private void doAnalysisTomorrowBuyInner(AnalysisCallback callback) {

        try {
            //遍历股票，一只股票一只股票来
            List<String> codes = getRepository().getDatabase().stockDao().getDistinctCode();
            if (codes == null || codes.size() == 0) {
                d(TAG, "sorry, the database has no stock's code...Please add some data.");
                callback.failedAnalysis(new Throwable("sorry, the database has no stock's code...Please add some data."));
                return;
            }

            d(TAG, "数据库股票代码长度为：" + codes.size());
            if (currentTimeCondition.size() != 0) {
                currentTimeCondition.clear();
            }

            //保存最终结果
            if (DataSource.getInstance().getAnalysisTomorrowBuy() == null) {
                DataSource.getInstance().setAnalysisTomorrowBuy(new ArrayList<>());
            }
            DataSource.getInstance().getAnalysisTomorrowBuy().clear();

            long maxCurrentTime = getRepository().getDatabase().stockDao().getMaxCurrentTime();
            d(TAG, "当前最大时间为：" + maxCurrentTime);

            List<Condition2> listCondition = new ArrayList<>();//明天买，后天卖的所有可能性
            for (int i = 0; i < codes.size(); i++) {
                //这是某一只股票了，然后分析这只股票明天买后天卖能赚钱的所有类型，返回一个Condition2
                List<Condition2> currentCodeConditions = findConditionForTomorrowBuy(codes.get(i), maxCurrentTime);
                if (currentCodeConditions.size() != 0) {
                    listCondition.addAll(currentCodeConditions);
                }
            }

            //此时条件长度为0，就不用分析了
            if (listCondition.size() == 0) {
                d(TAG, "sorry, there is no possible occured.");
                callback.failedAnalysis(new Throwable("sorry, there is no possible occured."));
                return;
            } else {
                d(TAG, "当前总共有" + listCondition.size() + " 次发生了这种情况，接下来遍历今天所有股票，查找有没有相似的...");
                //d(TAG," is null ? "+listCondition.get(0).getTodayStock().toString());
                //d(TAG," is null ? "+listCondition.get(0).getYesStock().toString());
                //d(TAG," is null ? "+listCondition.get(0).getBeforeYesStock().toString());
            }

            //分析所有今天的股票，看看有没有符合条件的
            //搜索数据库，最近一天的股票时间，因为有可能节假日

            if (currentTimeCondition.size() == 0) {
                d(TAG, "sorry, there is no current day record");
                callback.failedAnalysis(new Throwable("sorry, there is no possible occured."));
                return;
            }

            d(TAG, "今天的记录条数为：" + currentTimeCondition.size());
            for (int i = 0; i < currentTimeCondition.size(); i++) {
                boolean isMatched = isMatched(0,currentTimeCondition.get(i), listCondition);
                if (isMatched) {
                    callback.finishOneStock(currentTimeCondition.get(i).getTodayStock().getCode(), 1);
                }
            }

            callback.finishAllStock();
        } catch (Throwable e) {
            d("Error", "##" + e.getMessage());
            callback.failedAnalysis(e);
        }
    }

    /**
     * 分析明天会涨停
     *
     * @param callback
     */
    private void doAnalysisTomorrowUpInner(AnalysisCallback callback) {

        try {
            //遍历股票，一只股票一只股票来
            List<String> codes = getRepository().getDatabase().stockDao().getDistinctCode();
            if (codes == null || codes.size() == 0) {
                d(TAG, "sorry, the database has no stock's code...Please add some data.");
                callback.failedAnalysis(new Throwable("sorry, the database has no stock's code...Please add some data."));
                return;
            }

            d(TAG, "数据库股票代码长度为：" + codes.size());
            if (currentTimeCondition.size() != 0) {
                currentTimeCondition.clear();
            }

            //保存最终结果
            if (DataSource.getInstance().getAnalysisTomorrowUp() == null) {
                DataSource.getInstance().setAnalysisTomorrowUp(new ArrayList<>());
            }
            DataSource.getInstance().getAnalysisTomorrowUp().clear();

            long maxCurrentTime = getRepository().getDatabase().stockDao().getMaxCurrentTime();
            d(TAG, "当前最大时间为：" + maxCurrentTime);

            List<Condition2> listCondition = new ArrayList<>();//明天涨停的所有类型
            for (int i = 0; i < codes.size(); i++) {
                //这是某一只股票了，然后分析这只股票明天会涨停类型，返回一个Condition2
                List<Condition2> currentCodeConditions = findConditionForTomorrowUp(codes.get(i), maxCurrentTime);
                if (currentCodeConditions.size() != 0) {
                    listCondition.addAll(currentCodeConditions);
                }
            }

            //此时条件长度为0，就不用分析了
            if (listCondition.size() == 0) {
                d(TAG, "sorry, there is no possible occured.");
                callback.failedAnalysis(new Throwable("sorry, there is no possible occured."));
                return;
            } else {
                d(TAG, "当前总共有" + listCondition.size() + " 次发生了这种情况，接下来遍历今天所有股票，查找有没有相似的...");
                //d(TAG," is null ? "+listCondition.get(0).getTodayStock().toString());
                //d(TAG," is null ? "+listCondition.get(0).getYesStock().toString());
                //d(TAG," is null ? "+listCondition.get(0).getBeforeYesStock().toString());
            }

            //分析所有今天的股票，看看有没有符合条件的
            //搜索数据库，最近一天的股票时间，因为有可能节假日

            if (currentTimeCondition.size() == 0) {
                d(TAG, "sorry, there is no current day record");
                callback.failedAnalysis(new Throwable("sorry, there is no possible occured."));
                return;
            }

            d(TAG, "今天的记录条数为：" + currentTimeCondition.size());
            for (int i = 0; i < currentTimeCondition.size(); i++) {
                boolean isMatched = isMatched(1, currentTimeCondition.get(i), listCondition);
                if (isMatched) {
                    callback.finishOneStock(currentTimeCondition.get(i).getTodayStock().getCode(), 1);
                }
            }

            callback.finishAllStock();
        } catch (Throwable e) {
            d("Error", "##" + e.getMessage());
            callback.failedAnalysis(e);
        }
    }

    /**
     * 分析明天会涨停
     *
     * @param callback
     */
    private void doAnalysisTomorrowDownInner(AnalysisCallback callback) {

        try {
            //遍历股票，一只股票一只股票来
            List<String> codes = getRepository().getDatabase().stockDao().getDistinctCode();
            if (codes == null || codes.size() == 0) {
                d(TAG, "sorry, the database has no stock's code...Please add some data.");
                callback.failedAnalysis(new Throwable("sorry, the database has no stock's code...Please add some data."));
                return;
            }

            d(TAG, "数据库股票代码长度为：" + codes.size());
            if (currentTimeCondition.size() != 0) {
                currentTimeCondition.clear();
            }

            //保存最终结果
            if (DataSource.getInstance().getAnalysisTomorrowBuy() == null) {
                DataSource.getInstance().setAnalysisTomorrowBuy(new ArrayList<>());
            }
            DataSource.getInstance().getAnalysisTomorrowBuy().clear();

            long maxCurrentTime = getRepository().getDatabase().stockDao().getMaxCurrentTime();
            d(TAG, "当前最大时间为：" + maxCurrentTime);

            List<Condition2> listCondition = new ArrayList<>();//明天买，后天卖的所有可能性
            for (int i = 0; i < codes.size(); i++) {
                //这是某一只股票了，然后分析这只股票明天买后天卖能赚钱的所有类型，返回一个Condition2
                List<Condition2> currentCodeConditions = findConditionForTomorrowDown(codes.get(i), maxCurrentTime);
                if (currentCodeConditions.size() != 0) {
                    listCondition.addAll(currentCodeConditions);
                }
            }

            //此时条件长度为0，就不用分析了
            if (listCondition.size() == 0) {
                d(TAG, "sorry, there is no possible occured.");
                callback.failedAnalysis(new Throwable("sorry, there is no possible occured."));
                return;
            } else {
                d(TAG, "当前总共有" + listCondition.size() + " 次发生了这种情况，接下来遍历今天所有股票，查找有没有相似的...");
                //d(TAG," is null ? "+listCondition.get(0).getTodayStock().toString());
                //d(TAG," is null ? "+listCondition.get(0).getYesStock().toString());
                //d(TAG," is null ? "+listCondition.get(0).getBeforeYesStock().toString());
            }

            //分析所有今天的股票，看看有没有符合条件的
            //搜索数据库，最近一天的股票时间，因为有可能节假日

            if (currentTimeCondition.size() == 0) {
                d(TAG, "sorry, there is no current day record");
                callback.failedAnalysis(new Throwable("sorry, there is no possible occured."));
                return;
            }

            d(TAG, "今天的记录条数为：" + currentTimeCondition.size());
            for (int i = 0; i < currentTimeCondition.size(); i++) {
                boolean isMatched = isMatched(2, currentTimeCondition.get(i), listCondition);
                if (isMatched) {
                    callback.finishOneStock(currentTimeCondition.get(i).getTodayStock().getCode(), 1);
                }
            }

            callback.finishAllStock();
        } catch (Throwable e) {
            d("Error", "##" + e.getMessage());
            callback.failedAnalysis(e);
        }
    }

    /**
     * 分析这一天的股票是否符合条件
     *
     * @param condition2s
     * @return
     */
    private boolean isMatched(int type, Condition2 targetDay, List<Condition2> condition2s) {


        //首先得查找到这只股票最近数据
        if (targetDay == null || condition2s == null || condition2s.size() == 0) return false;
        Stock todayStock = targetDay.getTodayStock();
        Stock yesStock = targetDay.getYesStock();
        Stock beforYesStock = targetDay.getBeforeYesStock();
        float maxVolume = targetDay.getMaxVolume();

        if (condition2s.get(0).getTodayStock() == null || condition2s.get(0).getYesStock() == null || condition2s.get(0).getBeforeYesStock() == null) {
            d(TAG, " the list is null!!!");
            return false;
        }

        if (todayStock == null || yesStock == null || beforYesStock == null) return false;

        for (int i = 0; i < condition2s.size(); i++) {

            float proximity1 = condition2s.get(i).getProximity() / 100.0f;//0.8
            float proximity2 = 2 - proximity1;//1.2

            Condition2 condition2 = condition2s.get(i);

            boolean isSelf = false;


            //分析今天涨幅是否符合 -0.8 -0.64
            float ratioTarget = (todayStock.getClosePrice() - yesStock.getClosePrice()) / yesStock.getClosePrice();
            float ratioCondition = (condition2.getTodayStock().getClosePrice() - condition2.getYesStock().getClosePrice()) / condition2.getYesStock().getClosePrice();
            float ratioRange1 = ratioCondition * proximity1;//0.64 -0.64
            float ratioRange2 = ratioCondition * proximity2;//0.96 -0.96
            float max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
            float min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;
            if (ratioCondition > 0) {
                //上涨
                if (ratioCondition >= 0.08) {
                    max = 0.12f;//最大可以
                }

            } else {
                //下跌
                if (ratioCondition <= -0.08) {
                    min = -0.12f;
                }
            }

            if (ratioTarget > max || ratioTarget < min) {
                if (isSelf) {
                    d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                    d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
                }

                continue;
            }
//        if (isSelf) {
//            LogUtils.d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
//            LogUtils.d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
//        }

            //分析昨天涨幅
            ratioTarget = (yesStock.getClosePrice() - beforYesStock.getClosePrice()) / beforYesStock.getClosePrice();
            ratioCondition = (condition2.getYesStock().getClosePrice() - condition2.getBeforeYesStock().getClosePrice()) / condition2.getBeforeYesStock().getClosePrice();
            ratioRange1 = ratioCondition * proximity1;
            ratioRange2 = ratioCondition * proximity2;
            max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
            min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;
            if (ratioCondition > 0) {
                //上涨
                if (ratioCondition >= 0.08) {
                    max = 0.12f;//最大可以
                }

            } else {
                //下跌
                if (ratioCondition <= -0.08) {
                    min = -0.12f;
                }
            }

            if (ratioTarget > max || ratioTarget < min) {
                if (isSelf) {
                    d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                    d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
                }
                continue;
            }
//        if (isSelf) {
//            LogUtils.d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
//            LogUtils.d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
//        }

            //分析今天成交量占历史成交量的多少
            ratioTarget = todayStock.getVolume() / maxVolume;
            ratioCondition = condition2.getTodayStock().getVolume() / condition2.getMaxVolume();
            ratioRange1 = ratioCondition * proximity1;
            ratioRange2 = ratioCondition * proximity2;
            max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
            min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;

            if (ratioTarget > max || ratioTarget < min) {
                if (isSelf) {
                    d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                    d("ratioRange = " + ratioTarget + " max=" + max + " min=" + min);
                }
                continue;
            }
//        if (isSelf) {
//            LogUtils.d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
//            LogUtils.d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
//        }

            //分析昨天成交量占历史成交量的多少
            ratioTarget = yesStock.getVolume() / maxVolume;
            ratioCondition = condition2.getYesStock().getVolume() / condition2.getMaxVolume();
            ratioRange1 = ratioCondition * proximity1;
            ratioRange2 = ratioCondition * proximity2;
            max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
            min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;

            if (ratioTarget > max || ratioTarget < min) {
                if (isSelf) {
                    d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                    d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
                }
                continue;
            }
//        if (isSelf) {
//            LogUtils.d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
//            LogUtils.d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
//        }

            //分析前日成交量占历史成交量的多少
            ratioTarget = beforYesStock.getVolume() / maxVolume;
            ratioCondition = condition2.getBeforeYesStock().getVolume() / condition2.getMaxVolume();
            ratioRange1 = ratioCondition * proximity1;
            ratioRange2 = ratioCondition * proximity2;
            max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
            min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;

            if (ratioTarget > max || ratioTarget < min) {
                if (isSelf) {
                    d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                    d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
                }
                continue;
            }
//        if (isSelf) {
//            LogUtils.d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
//            LogUtils.d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
//        }

            //发现都符合哦
            //d(TAG, " 经过匹对，此股符合条件...加入结果集合");
            //d(TAG, "发现都符合：targetStock=" + todayStock.toString());


            AnalysisStock stock = new AnalysisStock();
            stock.setCode(targetDay.getTodayStock().getCode());
            stock.setDate(targetDay.getTodayStock().getDate());
            stock.setVolume(targetDay.getTodayStock().getVolume());
            stock.setOpenPrice(targetDay.getTodayStock().getOpenPrice());
            stock.setClosePrice(targetDay.getTodayStock().getClosePrice());
            stock.setMinPrice(targetDay.getTodayStock().getMinPrice());
            stock.setMaxPrice(targetDay.getTodayStock().getMaxPrice());
            stock.setNextStock(condition2.getTodayStock());
            stock.setName(targetDay.getTodayStock().getName());

            if (type == 0) {
                if (DataSource.getInstance().getAnalysisTomorrowBuy() == null) {
                    DataSource.getInstance().setAnalysisTomorrowBuy(new ArrayList<>());
                }
                DataSource.getInstance().getAnalysisTomorrowBuy().add(stock);
            } else if (type == 1) {
                if (DataSource.getInstance().getAnalysisTomorrowUp() == null) {
                    DataSource.getInstance().setAnalysisTomorrowUp(new ArrayList<>());
                }
                DataSource.getInstance().getAnalysisTomorrowUp().add(stock);
            } else if (type == 2) {
                if (DataSource.getInstance().getAnalysisTomorrowDown() == null) {
                    DataSource.getInstance().setAnalysisTomorrowDown(new ArrayList<>());
                }
                DataSource.getInstance().getAnalysisTomorrowDown().add(stock);
            }

            return true;

        }

        return false;
    }

    /**
     * 检测明天买后天卖的条件
     *
     * @return
     */
    private List<Condition2> findConditionForTomorrowBuy(String code, long currentTime) {
        List<Condition2> condition2s = new ArrayList<>();
        List<Stock> theCodeStocks = getRepository().getDatabase().stockDao().getStocksByCode(code);

        if (theCodeStocks == null || theCodeStocks.size() <= 3) {
            d(TAG, "这个股票没有历史记录哦，继续分析下一只吧...");
            return condition2s;
        }

        //每天附近最大成交量
        List<Float> listVolumeMaxs = new ArrayList<>();
        for (int i = 0; i < theCodeStocks.size(); i++) {
            float maxVolume = findRecentlyMax(theCodeStocks, i);

            //d(TAG, "maxVolume = " + maxVolume);
            listVolumeMaxs.add(maxVolume);
        }

        //可以遍历分析这只股票了
        int size = theCodeStocks.size();
        for (int i = 0; i < size; i++) {
            if (i == size - 1 && i >= 3) {
                if (theCodeStocks.get(i).getCurrentTime() == currentTime) {
                    Condition2 currentConditon = new Condition2();
                    currentConditon.setTodayStock(theCodeStocks.get(i));
                    currentConditon.setYesStock(theCodeStocks.get(i - 1));
                    currentConditon.setBeforeYesStock(theCodeStocks.get(i - 2));
                    currentConditon.setMaxVolume(listVolumeMaxs.get(i));
                    currentTimeCondition.add(currentConditon);
                }
            }
            if (i >= 3 && i < size - 2) {
                Stock yesStock = theCodeStocks.get(i - 1);
                Stock beforeYesStock = theCodeStocks.get(i - 2);
                Stock todayStock = theCodeStocks.get(i);
                Stock tomorrowStock = theCodeStocks.get(i + 1);
                Stock tomorrow2Stock = theCodeStocks.get(i + 2);
                boolean isTarget = isTomorrowBuyType(tomorrowStock, tomorrow2Stock);
                if (isTarget) {
                    Condition2 condition2 = new Condition2();
                    condition2.setProximity(PROXIMITY);
                    condition2.setMaxVolume(listVolumeMaxs.get(i));
                    condition2.setTodayStock(todayStock);
                    condition2.setYesStock(yesStock);
                    condition2.setBeforeYesStock(beforeYesStock);
                    condition2s.add(condition2);
                    //d(TAG," is null ? "+todayStock.toString());
                }
            }
        }

        return condition2s;
    }

    /**
     * 检测明天涨停的条件
     *
     * @return
     */
    private List<Condition2> findConditionForTomorrowUp(String code, long currentTime) {
        List<Condition2> condition2s = new ArrayList<>();
        List<Stock> theCodeStocks = getRepository().getDatabase().stockDao().getStocksByCode(code);

        if (theCodeStocks == null || theCodeStocks.size() <= 3) {
            d(TAG, "这个股票没有历史记录哦，继续分析下一只吧...");
            return condition2s;
        }

        //每天附近最大成交量
        List<Float> listVolumeMaxs = new ArrayList<>();
        for (int i = 0; i < theCodeStocks.size(); i++) {
            float maxVolume = findRecentlyMax(theCodeStocks, i);

            //d(TAG, "maxVolume = " + maxVolume);
            listVolumeMaxs.add(maxVolume);
        }

        //可以遍历分析这只股票了
        int size = theCodeStocks.size();
        for (int i = 0; i < size; i++) {
            if (i == size - 1 && i >= 3) {
                if (theCodeStocks.get(i).getCurrentTime() == currentTime) {
                    Condition2 currentConditon = new Condition2();
                    currentConditon.setTodayStock(theCodeStocks.get(i));
                    currentConditon.setYesStock(theCodeStocks.get(i - 1));
                    currentConditon.setBeforeYesStock(theCodeStocks.get(i - 2));
                    currentConditon.setMaxVolume(listVolumeMaxs.get(i));
                    currentTimeCondition.add(currentConditon);
                }
            }
            if (i >= 3 && i < size - 2) {
                Stock yesStock = theCodeStocks.get(i - 1);
                Stock beforeYesStock = theCodeStocks.get(i - 2);
                Stock todayStock = theCodeStocks.get(i);
                Stock tomorrowStock = theCodeStocks.get(i + 1);
                Stock tomorrow2Stock = theCodeStocks.get(i + 2);
                boolean isTarget = isTomorrowUpType(todayStock, tomorrowStock);
                if (isTarget) {
                    Condition2 condition2 = new Condition2();
                    condition2.setProximity(PROXIMITY);
                    condition2.setMaxVolume(listVolumeMaxs.get(i));
                    condition2.setTodayStock(todayStock);
                    condition2.setYesStock(yesStock);
                    condition2.setBeforeYesStock(beforeYesStock);
                    condition2s.add(condition2);
                    //d(TAG," is null ? "+todayStock.toString());
                }
            }
        }

        return condition2s;
    }

    /**
     * 检测明天跌停的条件
     *
     * @return
     */
    private List<Condition2> findConditionForTomorrowDown(String code, long currentTime) {
        List<Condition2> condition2s = new ArrayList<>();
        List<Stock> theCodeStocks = getRepository().getDatabase().stockDao().getStocksByCode(code);

        if (theCodeStocks == null || theCodeStocks.size() <= 3) {
            d(TAG, "这个股票没有历史记录哦，继续分析下一只吧...");
            return condition2s;
        }

        //每天附近最大成交量
        List<Float> listVolumeMaxs = new ArrayList<>();
        for (int i = 0; i < theCodeStocks.size(); i++) {
            float maxVolume = findRecentlyMax(theCodeStocks, i);

            //d(TAG, "maxVolume = " + maxVolume);
            listVolumeMaxs.add(maxVolume);
        }

        //可以遍历分析这只股票了
        int size = theCodeStocks.size();
        for (int i = 0; i < size; i++) {
            if (i == size - 1 && i >= 3) {
                if (theCodeStocks.get(i).getCurrentTime() == currentTime) {
                    Condition2 currentConditon = new Condition2();
                    currentConditon.setTodayStock(theCodeStocks.get(i));
                    currentConditon.setYesStock(theCodeStocks.get(i - 1));
                    currentConditon.setBeforeYesStock(theCodeStocks.get(i - 2));
                    currentConditon.setMaxVolume(listVolumeMaxs.get(i));
                    currentTimeCondition.add(currentConditon);
                }
            }
            if (i >= 3 && i < size - 2) {
                Stock yesStock = theCodeStocks.get(i - 1);
                Stock beforeYesStock = theCodeStocks.get(i - 2);
                Stock todayStock = theCodeStocks.get(i);
                Stock tomorrowStock = theCodeStocks.get(i + 1);
                Stock tomorrow2Stock = theCodeStocks.get(i + 2);
                boolean isTarget = isTomorrowDownType(todayStock, tomorrowStock);
                if (isTarget) {
                    Condition2 condition2 = new Condition2();
                    condition2.setProximity(PROXIMITY);
                    condition2.setMaxVolume(listVolumeMaxs.get(i));
                    condition2.setTodayStock(todayStock);
                    condition2.setYesStock(yesStock);
                    condition2.setBeforeYesStock(beforeYesStock);
                    condition2s.add(condition2);
                    //d(TAG," is null ? "+todayStock.toString());
                }
            }
        }

        return condition2s;
    }

    /**
     * 明天开盘肯定比今天大一些的，记录收集起来
     *
     * @return
     */
    private boolean isTomorrowBuyType(Stock tomorrow, Stock tomorrow2Stock) {

        if (tomorrow == null || tomorrow2Stock == null) return false;

        if (tomorrow2Stock.getOpenPrice() <= tomorrow.getClosePrice()) return false;

        float upRatio = (tomorrow2Stock.getOpenPrice() - tomorrow.getClosePrice()) / tomorrow.getClosePrice();

        //d(TAG, "upRatio=" + upRatio);
        if (upRatio > (TOMORROW_BUY_UP_OFFSET / 100.0f)) {
            return true;
        }

        return false;
    }

    /**
     * 明天涨停的，记录收集起来
     *
     * @return
     */
    private boolean isTomorrowUpType(Stock todayStock, Stock tomorrow) {

        if (tomorrow == null || todayStock == null) return false;

        if (tomorrow.getClosePrice() <= todayStock.getClosePrice()) return false;

        float upRatio = (tomorrow.getClosePrice() - todayStock.getClosePrice()) / todayStock.getClosePrice();

        //d(TAG, "upRatio=" + upRatio);
        if (upRatio >= 0.095f) {
            return true;
        }

        return false;
    }

    /**
     * 明天跌停的，记录收集起来
     *
     * @return
     */
    private boolean isTomorrowDownType(Stock todayStock, Stock tomorrow) {

        if (tomorrow == null || todayStock == null) return false;

        if (tomorrow.getClosePrice() >= todayStock.getClosePrice()) return false;

        float upRatio = (tomorrow.getClosePrice() - todayStock.getClosePrice()) / todayStock.getClosePrice();

        //d(TAG, "upRatio=" + upRatio);
        if (upRatio <= -0.095f) {
            return true;
        }

        return false;
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
                float maxVolume = findRecentlyMax(theCodeStocks, i);
                condition.setTodayStock(theCodeStocks.get(i));
                if (i >= 3) {
                    condition.setYesStock(theCodeStocks.get(i - 1));
                    condition.setBeforeYesStock(theCodeStocks.get(i - 2));
                    condition.setBefore2YesStock(theCodeStocks.get(i - 3));
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
            getTheCodeStockAndAnalysis(0, codes.get(i), condition, callback);
        }

        callback.finishAllStock();
    }

    private void doAnalysisInner2Days(String code, String date, String pro, AnalysisCallback callback) {

        //获取查询参数
        Condition2 condition = new Condition2();
        condition.setProximity(Float.parseFloat(pro));
        List<Stock> theCodeStocks = getRepository().getDatabase().stockDao().getStocksByCode(code);
        d(TAG, "code=" + code + " date=" + date + " pro=" + pro);
        for (int i = 0; i < theCodeStocks.size(); i++) {
            if (theCodeStocks.get(i).getDate().equals(date)) {
                //limit限制长度，offset从哪里开始
                float maxVolume = findRecentlyMax(theCodeStocks, i);
                condition.setTodayStock(theCodeStocks.get(i));
                if (i >= 2) {
                    condition.setYesStock(theCodeStocks.get(i - 1));
                    condition.setBeforeYesStock(theCodeStocks.get(i - 2));
                    d(TAG, "找到目标信息：" + condition.getTodayStock().toString());
                    d(TAG, "找到目标昨日信息：" + condition.getYesStock().toString());
                    d(TAG, "maxVolume = " + maxVolume);
                    condition.setMaxVolume(maxVolume);
                }
            }
        }

        if (condition.getYesStock() == null || condition.getTodayStock() == null) {
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
            getTheCodeStockAndAnalysis(1, codes.get(i), condition, callback);
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
    private synchronized void getTheCodeStockAndAnalysis(int type, String code, Condition2 condition2, AnalysisCallback callback) {

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
            for (int i = 0; i < theCodeStocks.size(); i++) {
                float maxVolume = findRecentlyMax(theCodeStocks, i);

                //d(TAG, "maxVolume = " + maxVolume);
                listVolumeMaxs.add(maxVolume);
            }

            //遍历分析这个股票
            int resultSize = 0;
            int beging = type == 0 ? 3 : 2;
            for (int i = 0; i < theCodeStocks.size(); i++) {
                if (i >= beging) {
                    Stock nextStock = (i == theCodeStocks.size() - 1) ? null : theCodeStocks.get(i + 1);
                    boolean isTarget = false;
                    if (type == 0) {
                        isTarget = doAnalaysisTheCodeTheStock(theCodeStocks.get(i), theCodeStocks.get(i - 1), theCodeStocks.get(i - 2), theCodeStocks.get(i - 3), nextStock, listVolumeMaxs.get(i), condition2);
                    } else {
                        isTarget = doAnalaysisTheCodeTheStock2Days(theCodeStocks.get(i), theCodeStocks.get(i - 1), nextStock, listVolumeMaxs.get(i), condition2);
                    }
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
    private boolean doAnalaysisTheCodeTheStock(Stock todayStock, Stock yesStock, Stock beforYesStock, Stock before2YesStock, Stock nextStock, float maxVolume, Condition2 condition2) {

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
        float ratioTarget = (todayStock.getClosePrice() - yesStock.getClosePrice()) / yesStock.getClosePrice();
        float ratioCondition = (condition2.getTodayStock().getClosePrice() - condition2.getYesStock().getClosePrice()) / condition2.getYesStock().getClosePrice();
        float ratioRange1 = ratioCondition * proximity1;//0.64 -0.64
        float ratioRange2 = ratioCondition * proximity2;//0.96 -0.96
        float max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
        float min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;
        if (ratioCondition > 0) {
            //上涨
            if (ratioCondition >= 0.08) {
                max = 0.12f;//最大可以
            }

        } else {
            //下跌
            if (ratioCondition <= -0.08) {
                min = -0.12f;
            }
        }

        if (ratioTarget > max || ratioTarget < min) {
            if (isSelf) {
                d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
            }
            return false;
        }
//        if (isSelf) {
//            LogUtils.d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
//            LogUtils.d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
//        }

        //分析昨天涨幅
        ratioTarget = (yesStock.getClosePrice() - beforYesStock.getClosePrice()) / beforYesStock.getClosePrice();
        ratioCondition = (condition2.getYesStock().getClosePrice() - condition2.getBeforeYesStock().getClosePrice()) / condition2.getBeforeYesStock().getClosePrice();
        ratioRange1 = ratioCondition * proximity1;
        ratioRange2 = ratioCondition * proximity2;
        max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
        min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;
        if (ratioCondition > 0) {
            //上涨
            if (ratioCondition >= 0.08) {
                max = 0.12f;//最大可以
            }

        } else {
            //下跌
            if (ratioCondition <= -0.08) {
                min = -0.12f;
            }
        }

        if (ratioTarget > max || ratioTarget < min) {
            if (isSelf) {
                d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
            }
            return false;
        }
//        if (isSelf) {
//            LogUtils.d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
//            LogUtils.d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
//        }

        //分析前天涨幅
        ratioTarget = (beforYesStock.getClosePrice() - before2YesStock.getClosePrice()) / before2YesStock.getClosePrice();
        ratioCondition = (condition2.getBeforeYesStock().getClosePrice() - condition2.getBefore2YesStock().getClosePrice()) / condition2.getBefore2YesStock().getClosePrice();
        ratioRange1 = ratioCondition * proximity1;
        ratioRange2 = ratioCondition * proximity2;
        max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
        min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;
        if (ratioCondition > 0) {
            //上涨
            if (ratioCondition >= 0.08) {
                max = 0.12f;//最大可以
            }

        } else {
            //下跌
            if (ratioCondition <= -0.08) {
                min = -0.12f;
            }
        }

        if (ratioTarget > max || ratioTarget < min) {
            if (isSelf) {
                d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
            }
            return false;
        }
//        if (isSelf) {
//            LogUtils.d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
//            LogUtils.d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
//        }

        //分析今天成交量占历史成交量的多少
        ratioTarget = todayStock.getVolume() / maxVolume;
        ratioCondition = condition2.getTodayStock().getVolume() / condition2.getMaxVolume();
        ratioRange1 = ratioCondition * proximity1;
        ratioRange2 = ratioCondition * proximity2;
        max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
        min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;

        if (ratioTarget > max || ratioTarget < min) {
            if (isSelf) {
                d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                d("ratioRange = " + ratioTarget + " max=" + max + " min=" + min);
            }
            return false;
        }
//        if (isSelf) {
//            LogUtils.d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
//            LogUtils.d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
//        }

        //分析昨天成交量占历史成交量的多少
        ratioTarget = yesStock.getVolume() / maxVolume;
        ratioCondition = condition2.getYesStock().getVolume() / condition2.getMaxVolume();
        ratioRange1 = ratioCondition * proximity1;
        ratioRange2 = ratioCondition * proximity2;
        max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
        min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;

        if (ratioTarget > max || ratioTarget < min) {
            if (isSelf) {
                d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
            }
            return false;
        }
//        if (isSelf) {
//            LogUtils.d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
//            LogUtils.d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
//        }

        //分析前日成交量占历史成交量的多少
        ratioTarget = beforYesStock.getVolume() / maxVolume;
        ratioCondition = condition2.getBeforeYesStock().getVolume() / condition2.getMaxVolume();
        ratioRange1 = ratioCondition * proximity1;
        ratioRange2 = ratioCondition * proximity2;
        max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
        min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;

        if (ratioTarget > max || ratioTarget < min) {
            if (isSelf) {
                d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
            }
            return false;
        }
//        if (isSelf) {
//            LogUtils.d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
//            LogUtils.d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
//        }


        //发现都符合哦
        d(TAG, "发现都符合：targetStock=" + todayStock.toString());
        if (DataSource.getInstance().getAnalysisList2() == null) {
            DataSource.getInstance().setAnalysisList2(new ArrayList<>());
        }
        //d(TAG, " 经过匹对，此股符合条件...加入结果集合");
        DataSource.getInstance().getAnalysisList2().add(DataUtils.convertByStock(todayStock, nextStock));

        return true;
    }

    /**
     * 具体分析
     *
     * @param todayStock
     * @param yesStock
     * @param maxVolume
     * @param condition2
     * @return
     */
    private boolean doAnalaysisTheCodeTheStock2Days(Stock todayStock, Stock yesStock, Stock nextStock, float maxVolume, Condition2 condition2) {

        if (condition2 == null) return false;
        float proximity1 = condition2.getProximity() / 100.0f;//0.8
        float proximity2 = 2 - proximity1;//1.2

        if (todayStock == null || yesStock == null) return false;

        boolean isSelf = false;
        if (todayStock.getCode().equals(condition2.getTodayStock().getCode()) && todayStock.getDate().equals(condition2.getTodayStock().getDate())) {
            isSelf = true;
            d(TAG, " 此时正在分析自己...");
        }

        //分析今天涨幅是否符合 -0.8 -0.64
        float ratioTarget = (todayStock.getClosePrice() - yesStock.getClosePrice()) / yesStock.getClosePrice();
        float ratioCondition = (condition2.getTodayStock().getClosePrice() - condition2.getYesStock().getClosePrice()) / condition2.getYesStock().getClosePrice();
        float ratioRange1 = ratioCondition * proximity1;//0.64 -0.64
        float ratioRange2 = ratioCondition * proximity2;//0.96 -0.96
        float max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
        float min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;
        if (ratioCondition > 0) {
            //上涨
            if (ratioCondition >= 0.08) {
                max = 0.12f;//最大可以
            }

        } else {
            //下跌
            if (ratioCondition <= -0.08) {
                min = -0.12f;
            }
        }

        if (ratioTarget > max || ratioTarget < min) {
            if (isSelf) {
                d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
            }
            return false;
        }
//        if (isSelf) {
//            LogUtils.d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
//            LogUtils.d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
//        }


        //分析今天成交量占历史成交量的多少
        ratioTarget = todayStock.getVolume() / maxVolume;
        ratioCondition = condition2.getTodayStock().getVolume() / condition2.getMaxVolume();
        ratioRange1 = ratioCondition * proximity1;
        ratioRange2 = ratioCondition * proximity2;
        max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
        min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;

        if (ratioTarget > max || ratioTarget < min) {
            if (isSelf) {
                d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                d("ratioRange = " + ratioTarget + " max=" + max + " min=" + min);
            }
            return false;
        }
//        if (isSelf) {
//            LogUtils.d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
//            LogUtils.d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
//        }

        //分析昨天成交量占历史成交量的多少
        ratioTarget = yesStock.getVolume() / maxVolume;
        ratioCondition = condition2.getYesStock().getVolume() / condition2.getMaxVolume();
        ratioRange1 = ratioCondition * proximity1;
        ratioRange2 = ratioCondition * proximity2;
        max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
        min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;

        if (ratioTarget > max || ratioTarget < min) {
            if (isSelf) {
                d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
            }
            return false;
        }
//        if (isSelf) {
//            LogUtils.d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
//            LogUtils.d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
//        }

        //分析今天最高价在今天收盘价的什么位置
        ratioTarget = (todayStock.getMaxPrice() - todayStock.getClosePrice()) / todayStock.getClosePrice();
        ratioCondition = (condition2.getTodayStock().getMaxPrice() - condition2.getTodayStock().getClosePrice()) / condition2.getTodayStock().getClosePrice();
        ratioRange1 = ratioCondition * proximity1;//0.64 -0.64
        ratioRange2 = ratioCondition * proximity2;//0.96 -0.96
        max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
        min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;
        if (ratioCondition > 0) {
            //上涨
            if (ratioCondition >= 0.08) {
                max = 0.12f;//最大可以
            }

        } else {
            //下跌
            if (ratioCondition <= -0.08) {
                min = -0.12f;
            }
        }

        if (ratioTarget > max || ratioTarget < min) {
            if (isSelf) {
                d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
            }
            return false;
        }


        //分析今天最低价在今天收盘价的什么位置
        ratioTarget = (todayStock.getMinPrice() - todayStock.getClosePrice()) / todayStock.getClosePrice();
        ratioCondition = (condition2.getTodayStock().getMinPrice() - condition2.getTodayStock().getClosePrice()) / condition2.getTodayStock().getClosePrice();
        ratioRange1 = ratioCondition * proximity1;//0.64 -0.64
        ratioRange2 = ratioCondition * proximity2;//0.96 -0.96
        max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
        min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;
        if (ratioCondition > 0) {
            //上涨
            if (ratioCondition >= 0.08) {
                max = 0.12f;//最大可以
            }

        } else {
            //下跌
            if (ratioCondition <= -0.08) {
                min = -0.12f;
            }
        }

        if (ratioTarget > max || ratioTarget < min) {
            if (isSelf) {
                d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
            }
            return false;
        }

        //分析昨天最高价在昨天收盘价什么位置
        ratioTarget = (yesStock.getMaxPrice() - yesStock.getClosePrice()) / yesStock.getClosePrice();
        ratioCondition = (condition2.getYesStock().getMaxPrice() - condition2.getYesStock().getClosePrice()) / condition2.getYesStock().getClosePrice();
        ratioRange1 = ratioCondition * proximity1;//0.64 -0.64
        ratioRange2 = ratioCondition * proximity2;//0.96 -0.96
        max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
        min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;
        if (ratioCondition > 0) {
            //上涨
            if (ratioCondition >= 0.08) {
                max = 0.12f;//最大可以
            }

        } else {
            //下跌
            if (ratioCondition <= -0.08) {
                min = -0.12f;
            }
        }

        if (ratioTarget > max || ratioTarget < min) {
            if (isSelf) {
                d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
            }
            return false;
        }

        //分析昨天最低价在昨天收盘价什么位置
        ratioTarget = (yesStock.getMinPrice() - yesStock.getClosePrice()) / yesStock.getClosePrice();
        ratioCondition = (condition2.getYesStock().getMinPrice() - condition2.getYesStock().getClosePrice()) / condition2.getYesStock().getClosePrice();
        ratioRange1 = ratioCondition * proximity1;//0.64 -0.64
        ratioRange2 = ratioCondition * proximity2;//0.96 -0.96
        max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
        min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;
        if (ratioCondition > 0) {
            //上涨
            if (ratioCondition >= 0.08) {
                max = 0.12f;//最大可以
            }

        } else {
            //下跌
            if (ratioCondition <= -0.08) {
                min = -0.12f;
            }
        }

        if (ratioTarget > max || ratioTarget < min) {
            if (isSelf) {
                d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
                d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
            }
            return false;
        }


        //发现都符合哦
        d(TAG, "发现都符合：targetStock=" + todayStock.toString());
        if (DataSource.getInstance().getAnalysisList2() == null) {
            DataSource.getInstance().setAnalysisList2(new ArrayList<>());
        }
        //d(TAG, " 经过匹对，此股符合条件...加入结果集合");
        DataSource.getInstance().getAnalysisList2().add(DataUtils.convertByStock(todayStock, nextStock));

        return true;
    }

}
