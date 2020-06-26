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
import jason.jan.stockanalysis.entity.Condition5;
import jason.jan.stockanalysis.entity.ConditionAverage;
import jason.jan.stockanalysis.entity.Stock;
import jason.jan.stockanalysis.utils.DataUtils;
import jason.jan.stockanalysis.utils.LogUtils;
import jason.jan.stockanalysis.utils.MyTimeUtils;

import static jason.jan.stockanalysis.utils.LogUtils.d;

/**
 * desc:
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2020/4/7 12:51
 **/
public class AnalysisF2ViewModel extends BaseViewModel<RepositoryImpl> {

    private static final String TAG = "AnalysisF2ViewModel";

    private Calendar calendar;

    public float TOMORROW_BUY_UP_OFFSET = 4.0f;//明天开盘比今天收盘高3%

    public float FIVE_DAY_UP_OFFSET = 110;//五天上涨百分比

    public float PROXIMITY = 99;//相似度80%就可以放在条件里面了

    public static final float PRO_DEFAULT = 99;//默认明天涨停的均线相似度

    public float AVERAGE_PROXIMITY = 90;//平均价格相似度90%就可以放在条件里面了

    public float JUMP_WATER_RATIO = 3.0f;//跳水百分比

    public float getTOMORROW_BUY_UP_OFFSET() {
        return TOMORROW_BUY_UP_OFFSET;
    }

    public float getJUMP_WATER_RATIO() {
        return JUMP_WATER_RATIO;
    }

    public void setJUMP_WATER_RATIO(float JUMP_WATER_RATIO) {
        this.JUMP_WATER_RATIO = JUMP_WATER_RATIO;
    }

    public float getFIVE_DAY_UP_OFFSET() {
        return FIVE_DAY_UP_OFFSET;
    }

    public void setFIVE_DAY_UP_OFFSET(float FIVE_DAY_UP_OFFSET) {
        this.FIVE_DAY_UP_OFFSET = FIVE_DAY_UP_OFFSET;
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

    /**
     * 当前时间，就是最近的条件，这样不用重新查找数据库了
     */
    private List<ConditionAverage> currentTimeConditionAverage = new ArrayList<>();

    /**
     * 当前时间，就是最近的条件，这样不用重新查找数据库了
     */
    private List<Condition5> currentTimeCondition5 = new ArrayList<>();

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
    public void doAnalysisUp(int begin, int end, AnalysisCallback callback) {

        Observable.just(1)
                .observeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        //doAnalysisTomorrowUpInner(callback);
                        doAnalysisTomorrowUpProInner(begin, end, callback);
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
     * 预测5日涨停
     */
    public void doAnalysis5DayUp(AnalysisCallback callback) {

        Observable.just(1)
                .observeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        doAnalysis5DayUpInner(callback);
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
     * 预测5日跌停
     */
    public void doAnalysis5DayDown(AnalysisCallback callback) {

        Observable.just(1)
                .observeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        doAnalysis5DayDownInner(callback);
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
     * 解析昨天跳水的
     */
    public void doAnalysisJumpWater(AnalysisCallback callback) {

        Observable.just(1)
                .observeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        doAnalysisJumpWaterInner(callback);
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
     * 解析昨天跳水的
     */
    public void doAnalysisGoUp(AnalysisCallback callback) {

        Observable.just(1)
                .observeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        doAnalysisGoUpInner(callback);
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
     * 跳水
     *
     * @param callback
     */
    private void doAnalysisJumpWaterInner(AnalysisCallback callback) {

        List<String> codes = getRepository().getDatabase().stockDao().getDistinctCode();
        if (codes == null || codes.size() == 0) {
            d(TAG, "sorry, the database has no stock's code...Please add some data.");
            callback.failedAnalysis(new Throwable("sorry, the database has no stock's code...Please add some data."));
            return;
        }

        d(TAG, "数据库股票代码长度为：" + codes.size());
        //保存最终结果
        if (DataSource.getInstance().getAnalysisJumpWater() == null) {
            DataSource.getInstance().setAnalysisJumpWater(new ArrayList<>());
        }
        DataSource.getInstance().getAnalysisJumpWater().clear();

        for (int i = 0; i < codes.size(); i++) {
            getTheCodeStockAndAnalysisIsJumpOrUp(0, codes.get(i), callback);
        }

        callback.finishAllStock();


    }

    /**
     * 冲高
     *
     * @param callback
     */
    private void doAnalysisGoUpInner(AnalysisCallback callback) {

        List<String> codes = getRepository().getDatabase().stockDao().getDistinctCode();
        if (codes == null || codes.size() == 0) {
            d(TAG, "sorry, the database has no stock's code...Please add some data.");
            callback.failedAnalysis(new Throwable("sorry, the database has no stock's code...Please add some data."));
            return;
        }

        d(TAG, "数据库股票代码长度为：" + codes.size());
        //保存最终结果
        if (DataSource.getInstance().getAnalysisJumpWater() == null) {
            DataSource.getInstance().setAnalysisJumpWater(new ArrayList<>());
        }
        DataSource.getInstance().getAnalysisJumpWater().clear();

        for (int i = 0; i < codes.size(); i++) {
            getTheCodeStockAndAnalysisIsJumpOrUp(1, codes.get(i), callback);
        }

        callback.finishAllStock();
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
                boolean isMatched = isMatched(0, currentTimeCondition.get(i), listCondition);
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

    private void doAnalysisTomorrowUpProInner(int begin, int end, AnalysisCallback callback) {

        //遍历今天所有股票
        List<String> codes = getRepository().getDatabase().stockDao().getDistinctCode();
        if (codes == null || codes.size() == 0) {
            d(TAG, "sorry, the database has no stock's code...Please add some data.");
            callback.failedAnalysis(new Throwable("sorry, the database has no stock's code...Please add some data."));
            return;
        }

        d(TAG, "数据库股票代码长度为：" + codes.size());
        if (currentTimeConditionAverage.size() != 0) {
            currentTimeConditionAverage.clear();
        }

        //保存最终结果
        if (DataSource.getInstance().getAnalysisTomorrowUp() == null) {
            DataSource.getInstance().setAnalysisTomorrowUp(new ArrayList<>());
        }
        DataSource.getInstance().getAnalysisTomorrowUp().clear();

        //long maxCurrentTime = getRepository().getDatabase().stockDao().getMaxCurrentTime();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DATE);
        long currentMonthMills = MyTimeUtils.dateToStamp(year + "-" + (currentMonth < 10 ? "0" + currentMonth : currentMonth) + "-" + (currentDay < 10 ? "0" + currentDay : currentDay));
        //d(TAG, "当前时间为：" + currentMonthMills);

        List<Stock> list = getRepository().getDatabase().stockDao().getStocksByCurrentTime(currentMonthMills);
        if (list == null || list.size() == 0) {
            d(TAG, "最大时间有问题哦，需要减一天");
            currentMonthMills -= 24 * 3600 * 1000;
            list = getRepository().getDatabase().stockDao().getStocksByCurrentTime(currentMonthMills);

            if (list == null || list.size() == 0) {
                d(TAG, "最大时间有问题哦，需要减一天");
                currentMonthMills -= 24 * 3600 * 1000;
                list = getRepository().getDatabase().stockDao().getStocksByCurrentTime(currentMonthMills);

                if (list == null || list.size() == 0) {
                    d(TAG, "最大时间有问题哦，需要减一天");
                    currentMonthMills -= 24 * 3600 * 1000;
                }
            }
        }


        for (int i = 0; i < codes.size(); i++) {
            //这是某一只股票了，然后分析这只股票明天会涨停类型，返回一个Condition2
            findConditionForTomorrowUp(codes.get(i), currentMonthMills);
        }

        if (currentTimeConditionAverage.size() == 0) {
            d(TAG, "sorry, there is no current day record");
            callback.failedAnalysis(new Throwable("sorry, there is no possible occured."));
            return;
        }

        try {
            d(TAG, "今天的记录条数为：" + currentTimeConditionAverage.size());
            for (int i = begin; i < end; i++) {

                final int position = i;
                doAnalysisInnerForTommorrowUp(currentTimeConditionAverage.get(i), new AnalysisCallback() {

                    @Override
                    public void finishOneStock(String code, int size) {

                    }

                    @Override
                    public void finishAllStock() {

                        Stock currentStock = currentTimeConditionAverage.get(position).getTodayStock();

                        if (DataSource.getInstance().getAnalysisList2() != null && DataSource.getInstance().getAnalysisList2().size() != 0) {
                            d(TAG, "已完成这只股票分析，name="+currentStock.getName()+" 相似长度为" + DataSource.getInstance().getAnalysisList2().size());

                            List<AnalysisStock> list = DataSource.getInstance().getAnalysisList2();
                            float proSum = 0;
                            int hasNullNextNum = 0;
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getNextStock() == null) {
                                    hasNullNextNum ++;
                                } else {
                                    proSum += (list.get(i).getNextStock().getClosePrice() - list.get(i).getClosePrice()) / list.get(i).getClosePrice();
                                }
                            }
                            if (list.size() - hasNullNextNum != 0) {
                                float upPro = proSum / (list.size() - hasNullNextNum);
                                LogUtils.d("发现该股票："+currentStock.getName()+" 明日上涨概率为："+ upPro);

                                DataSource.getInstance().getAnalysisTomorrowUp().add(DataUtils.convertByUpProStock(currentStock,upPro));
                            }

                        }

                        callback.finishOneStock(currentStock.getCode(), 1);
                    }

                    @Override
                    public void failedAnalysis(Throwable e) {

                    }
                });

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
            if (currentTimeConditionAverage.size() != 0) {
                currentTimeConditionAverage.clear();
            }

            //保存最终结果
            if (DataSource.getInstance().getAnalysisTomorrowUp() == null) {
                DataSource.getInstance().setAnalysisTomorrowUp(new ArrayList<>());
            }
            DataSource.getInstance().getAnalysisTomorrowUp().clear();

            //long maxCurrentTime = getRepository().getDatabase().stockDao().getMaxCurrentTime();
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int currentMonth = calendar.get(Calendar.MONTH) + 1;
            int currentDay = calendar.get(Calendar.DATE);
            long currentMonthMills = MyTimeUtils.dateToStamp(year + "-" + (currentMonth < 10 ? "0" + currentMonth : currentMonth) + "-" + (currentDay < 10 ? "0" + currentDay : currentDay));
            d(TAG, "当前时间为：" + currentMonthMills);

            List<ConditionAverage> listCondition = new ArrayList<>();//明天涨停的所有类型
            for (int i = 0; i < codes.size(); i++) {
                //这是某一只股票了，然后分析这只股票明天会涨停类型，返回一个Condition2
                List<ConditionAverage> currentCodeConditions = findConditionForTomorrowUp(codes.get(i), currentMonthMills);
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

            if (currentTimeConditionAverage.size() == 0) {
                d(TAG, "sorry, there is no current day record");
                callback.failedAnalysis(new Throwable("sorry, there is no possible occured."));
                return;
            }

            d(TAG, "今天的记录条数为：" + currentTimeConditionAverage.size());
            for (int i = 0; i < currentTimeConditionAverage.size(); i++) {
                boolean isMatched = isMatched(1, currentTimeConditionAverage.get(i), listCondition);
                if (isMatched) {
                    callback.finishOneStock(currentTimeConditionAverage.get(i).getTodayStock().getCode(), 1);
                }
            }

            callback.finishAllStock();
        } catch (Throwable e) {
            d("Error", "##" + e.getMessage());
            callback.failedAnalysis(e);
        }
    }

    /**
     * 分析5日会涨
     *
     * @param callback
     */
    private void doAnalysis5DayUpInner(AnalysisCallback callback) {

        try {
            //遍历股票，一只股票一只股票来
            List<String> codes = getRepository().getDatabase().stockDao().getDistinctCode();
            if (codes == null || codes.size() == 0) {
                d(TAG, "sorry, the database has no stock's code...Please add some data.");
                callback.failedAnalysis(new Throwable("sorry, the database has no stock's code...Please add some data."));
                return;
            }

            d(TAG, "数据库股票代码长度为：" + codes.size());
            if (currentTimeCondition5.size() != 0) {
                currentTimeCondition5.clear();
            }

            //保存最终结果
            if (DataSource.getInstance().getAnalysis5DayUp() == null) {
                DataSource.getInstance().setAnalysis5DayUp(new ArrayList<>());
            }
            DataSource.getInstance().getAnalysis5DayUp().clear();

            long maxCurrentTime = getRepository().getDatabase().stockDao().getMaxCurrentTime();
            d(TAG, "当前最大时间为：" + maxCurrentTime);

            List<Condition5> listCondition = new ArrayList<>();//明天买，后天卖的所有可能性
            for (int i = 0; i < codes.size(); i++) {
                //这是某一只股票了，然后分析这只股票5日的所有类型，返回一个Condition5
                List<Condition5> currentCodeConditions = findConditionFor5DayUp(codes.get(i), maxCurrentTime);
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

            if (currentTimeCondition5.size() == 0) {
                d(TAG, "sorry, there is no current day record");
                callback.failedAnalysis(new Throwable("sorry, there is no possible occured."));
                return;
            }

            d(TAG, "今天的记录条数为：" + currentTimeCondition5.size());
            for (int i = 0; i < currentTimeCondition5.size(); i++) {
                boolean isMatched = isMatched5Day(0, currentTimeCondition5.get(i), listCondition);
                if (isMatched) {
                    callback.finishOneStock(currentTimeCondition5.get(i).getCurrentDay().getCode(), 1);
                }
            }

            callback.finishAllStock();
        } catch (Throwable e) {
            d("Error", "##" + e.getMessage());
            callback.failedAnalysis(e);
        }
    }

    /**
     * 分析5日会涨
     *
     * @param callback
     */
    private void doAnalysis5DayDownInner(AnalysisCallback callback) {

        try {
            //遍历股票，一只股票一只股票来
            List<String> codes = getRepository().getDatabase().stockDao().getDistinctCode();
            if (codes == null || codes.size() == 0) {
                d(TAG, "sorry, the database has no stock's code...Please add some data.");
                callback.failedAnalysis(new Throwable("sorry, the database has no stock's code...Please add some data."));
                return;
            }

            d(TAG, "数据库股票代码长度为：" + codes.size());
            if (currentTimeCondition5.size() != 0) {
                currentTimeCondition5.clear();
            }

            //保存最终结果
            if (DataSource.getInstance().getAnalysis5DayDown() == null) {
                DataSource.getInstance().setAnalysis5DayDown(new ArrayList<>());
            }
            DataSource.getInstance().getAnalysis5DayDown().clear();

            long maxCurrentTime = getRepository().getDatabase().stockDao().getMaxCurrentTime();
            d(TAG, "当前最大时间为：" + maxCurrentTime);

            List<Condition5> listCondition = new ArrayList<>();//明天买，后天卖的所有可能性
            for (int i = 0; i < codes.size(); i++) {
                //这是某一只股票了，然后分析这只股票明天买后天卖能赚钱的所有类型，返回一个Condition2
                List<Condition5> currentCodeConditions = findConditionFor5DayDown(codes.get(i), maxCurrentTime);
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

            if (currentTimeCondition5.size() == 0) {
                d(TAG, "sorry, there is no current day record");
                callback.failedAnalysis(new Throwable("sorry, there is no possible occured."));
                return;
            }

            d(TAG, "今天的记录条数为：" + currentTimeCondition5.size());
            for (int i = 0; i < currentTimeCondition5.size(); i++) {
                boolean isMatched = isMatched5Day(1, currentTimeCondition5.get(i), listCondition);
                if (isMatched) {
                    callback.finishOneStock(currentTimeCondition5.get(i).getCurrentDay().getCode(), 1);
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
     * 分析这一天的股票是否符合条件
     *
     * @param conditionAverages
     * @return
     */
    private boolean isMatched(int type, ConditionAverage currentAverage, List<ConditionAverage> conditionAverages) {


        //首先得查找到这只股票最近数据
        if (currentAverage == null || conditionAverages == null || conditionAverages.size() == 0)
            return false;


        for (int i = 0; i < conditionAverages.size(); i++) {


            ConditionAverage conditionAverage = conditionAverages.get(i);

            float proximity1 = conditionAverage.getProximity() / 100.0f;//0.8
            float proximity2 = 2 - proximity1;//1.2

            float max = conditionAverage.getFiveAverage1() * proximity2;
            float min = conditionAverage.getFiveAverage1() * proximity1;
            //LogUtils.d(TAG, " max=" + max + " min=" + min + " fiveAverage=" + currentAverage.getTenAverage1());
            if (currentAverage.getFiveAverage1() > max || currentAverage.getFiveAverage1() < min) {

                continue;
            }

            max = conditionAverage.getFiveAverage2() * proximity2;
            min = conditionAverage.getFiveAverage2() * proximity1;
            //LogUtils.d(TAG, " max=" + max + " min=" + min + " fiveAverage=" + currentAverage.getFiveAverage2());
            if (currentAverage.getFiveAverage2() > max || currentAverage.getFiveAverage2() < min) {

                continue;
            }

            max = conditionAverage.getFiveAverage3() * proximity2;
            min = conditionAverage.getFiveAverage3() * proximity1;
            //LogUtils.d(TAG, " max=" + max + " min=" + min + " fiveAverage=" + currentAverage.getFiveAverage3());
            if (currentAverage.getFiveAverage3() > max || currentAverage.getFiveAverage3() < min) {

                continue;
            }

            max = conditionAverage.getTenAverage1() * proximity2;
            min = conditionAverage.getTenAverage1() * proximity1;
            if (currentAverage.getTenAverage1() > max || currentAverage.getTenAverage1() < min) {

                continue;
            }

            max = conditionAverage.getTenAverage2() * proximity2;
            min = conditionAverage.getTenAverage2() * proximity1;
            if (currentAverage.getTenAverage2() > max || currentAverage.getTenAverage2() < min) {

                continue;
            }

            max = conditionAverage.getTenAverage3() * proximity2;
            min = conditionAverage.getTenAverage3() * proximity1;
            if (currentAverage.getTenAverage3() > max || currentAverage.getTenAverage3() < min) {

                continue;
            }

            max = conditionAverage.getTwentyAverage1() * proximity2;
            min = conditionAverage.getTwentyAverage1() * proximity1;
            if (currentAverage.getTwentyAverage1() > max || currentAverage.getTwentyAverage1() < min) {

                continue;
            }

            max = conditionAverage.getTwentyAverage2() * proximity2;
            min = conditionAverage.getTwentyAverage2() * proximity1;
            if (currentAverage.getTwentyAverage2() > max || currentAverage.getTwentyAverage2() < min) {

                continue;
            }

            max = conditionAverage.getTwentyAverage3() * proximity2;
            min = conditionAverage.getTwentyAverage3() * proximity1;
            if (currentAverage.getTwentyAverage3() > max || currentAverage.getTwentyAverage3() < min) {

                continue;
            }

            d(TAG, " 经过匹对，此股符合条件...加入结果集合");
            AnalysisStock stock = new AnalysisStock();
            stock.setCode(currentAverage.getTodayStock().getCode());
            stock.setDate(currentAverage.getTodayStock().getDate());
            stock.setVolume(currentAverage.getTodayStock().getVolume());
            stock.setOpenPrice(currentAverage.getTodayStock().getOpenPrice());
            stock.setClosePrice(currentAverage.getTodayStock().getClosePrice());
            stock.setMinPrice(currentAverage.getTodayStock().getMinPrice());
            stock.setMaxPrice(currentAverage.getTodayStock().getMaxPrice());
            stock.setNextStock(conditionAverages.get(i).getTodayStock());
            stock.setName(currentAverage.getTodayStock().getName());

            if (DataSource.getInstance().getAnalysisTomorrowUp() == null) {
                DataSource.getInstance().setAnalysisTomorrowUp(new ArrayList<>());
            }
            DataSource.getInstance().getAnalysisTomorrowUp().add(stock);


            return true;
        }

        return false;
    }

    /**
     * 分析这一天的股票是否符合条件
     *
     * @param condition5s
     * @return
     */
    private boolean isMatched5Day(int type, Condition5 targetDay, List<Condition5> condition5s) {


        //首先得查找到这只股票最近数据
        if (targetDay == null || condition5s == null || condition5s.size() == 0) return false;

        float proximity1, proximity2, ratioRange1, ratioRange2, max, min;

        proximity1 = PROXIMITY / 100;//0.9
        proximity2 = 2 - proximity1;//1.1

        for (int i = 0; i < condition5s.size(); i++) {

            Condition5 condition5 = condition5s.get(i);
            ratioRange1 = condition5.getRatio_0_5() * proximity1;
            ratioRange2 = condition5.getRatio_0_5() * proximity2;
            max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
            min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;

            if (targetDay.getRatio_0_5() < min || targetDay.getRatio_0_5() > max) {
                continue;
            }

            ratioRange1 = condition5.getRatio_5_10() * proximity1;
            ratioRange2 = condition5.getRatio_5_10() * proximity2;
            max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
            min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;

            if (targetDay.getRatio_5_10() < min || targetDay.getRatio_5_10() > max) {
                continue;
            }

            ratioRange1 = condition5.getRatio_10_15() * proximity1;
            ratioRange2 = condition5.getRatio_10_15() * proximity2;
            max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
            min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;

            if (targetDay.getRatio_10_15() < min || targetDay.getRatio_10_15() > max) {
                continue;
            }

            ratioRange1 = condition5.getRatio_15_20() * proximity1;
            ratioRange2 = condition5.getRatio_15_20() * proximity2;
            max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
            min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;

            if (targetDay.getRatio_15_20() < min || targetDay.getRatio_15_20() > max) {
                continue;
            }

            ratioRange1 = condition5.getRatio_20_25() * proximity1;
            ratioRange2 = condition5.getRatio_20_25() * proximity2;
            max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
            min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;

            if (targetDay.getRatio_20_25() < min || targetDay.getRatio_20_25() > max) {
                continue;
            }

            ratioRange1 = condition5.getRatio_25_30() * proximity1;
            ratioRange2 = condition5.getRatio_25_30() * proximity2;
            max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
            min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;

            if (targetDay.getRatio_25_30() < min || targetDay.getRatio_25_30() > max) {
                continue;
            }

            AnalysisStock stock = new AnalysisStock();
            stock.setCode(targetDay.getCurrentDay().getCode());
            stock.setDate(targetDay.getCurrentDay().getDate());
            stock.setVolume(targetDay.getCurrentDay().getVolume());
            stock.setOpenPrice(targetDay.getCurrentDay().getOpenPrice());
            stock.setClosePrice(targetDay.getCurrentDay().getClosePrice());
            stock.setMinPrice(targetDay.getCurrentDay().getMinPrice());
            stock.setMaxPrice(targetDay.getCurrentDay().getMaxPrice());
            stock.setNextStock(targetDay.getTommorrowStock());
            stock.setName(targetDay.getCurrentDay().getName());

            if (type == 0) {
                if (DataSource.getInstance().getAnalysis5DayUp() == null) {
                    DataSource.getInstance().setAnalysis5DayUp(new ArrayList<>());
                }
                DataSource.getInstance().getAnalysis5DayUp().add(stock);
            } else if (type == 1) {
                if (DataSource.getInstance().getAnalysis5DayDown() == null) {
                    DataSource.getInstance().setAnalysis5DayDown(new ArrayList<>());
                }
                DataSource.getInstance().getAnalysis5DayDown().add(stock);
            }

            d(TAG, "符合条件哦" + stock.toString());

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
    private List<ConditionAverage> findConditionForTomorrowUp(String code, long currentTime) {
        List<ConditionAverage> condition2s = new ArrayList<>();
        List<Stock> theCodeStocks = getRepository().getDatabase().stockDao().getStocksByCode(code);

        if (theCodeStocks == null || theCodeStocks.size() <= 3) {
            d(TAG, "这个股票没有历史记录哦，继续分析下一只吧...");
            return condition2s;
        }

        //可以遍历分析这只股票了
        int size = theCodeStocks.size();
        for (int i = 0; i < size; i++) {
            if (i == size - 1 && i >= 3) {
                d(TAG, "当前时间为：" + theCodeStocks.get(i).getCurrentTime() + " " + currentTime);
                if (theCodeStocks.get(i).getCurrentTime() == currentTime) {
                    ConditionAverage currentConditon = new ConditionAverage();
                    getFiveAverage(currentConditon, theCodeStocks, i, 0);
                    getFiveAverage(currentConditon, theCodeStocks, i - 1, 1);
                    getFiveAverage(currentConditon, theCodeStocks, i - 2, 2);
                    getTenAverage(currentConditon, theCodeStocks, i, 0);
                    getTenAverage(currentConditon, theCodeStocks, i - 1, 1);
                    getTenAverage(currentConditon, theCodeStocks, i - 2, 2);
                    getTwentyAverage(currentConditon, theCodeStocks, i, 0);
                    getTwentyAverage(currentConditon, theCodeStocks, i - 1, 1);
                    getTwentyAverage(currentConditon, theCodeStocks, i - 2, 2);
                    currentConditon.setProximity(PROXIMITY);
                    currentConditon.setTodayStock(theCodeStocks.get(i));

                    currentTimeConditionAverage.add(currentConditon);
                }
            }
            if (i >= 3 && i < size - 2) {

                Stock todayStock = theCodeStocks.get(i);
                Stock tomorrowStock = theCodeStocks.get(i + 1);
                boolean isTarget = isTomorrowUpType(todayStock, tomorrowStock);

                if (isTarget) {
                    ConditionAverage currentConditon = new ConditionAverage();
                    getFiveAverage(currentConditon, theCodeStocks, i, 0);
                    getFiveAverage(currentConditon, theCodeStocks, i - 1, 1);
                    getFiveAverage(currentConditon, theCodeStocks, i - 2, 2);
                    getTenAverage(currentConditon, theCodeStocks, i, 0);
                    getTenAverage(currentConditon, theCodeStocks, i - 1, 1);
                    getTenAverage(currentConditon, theCodeStocks, i - 2, 2);
                    getTwentyAverage(currentConditon, theCodeStocks, i, 0);
                    getTwentyAverage(currentConditon, theCodeStocks, i - 1, 1);
                    getTwentyAverage(currentConditon, theCodeStocks, i - 2, 2);
                    currentConditon.setProximity(PROXIMITY);
                    currentConditon.setTodayStock(theCodeStocks.get(i));
                    currentConditon.setTodayStock(theCodeStocks.get(i));

                    condition2s.add(currentConditon);
                    //d(TAG," is null ? "+todayStock.toString());
                }
            }
        }

        return condition2s;
    }

    /**
     * 检测5日上涨的条件
     *
     * @return
     */
    private List<Condition5> findConditionFor5DayUp(String code, long currentTime) {
        List<Condition5> condition5s = new ArrayList<>();
        List<Stock> theCodeStocks = getRepository().getDatabase().stockDao().getStocksByCode(code);

        if (theCodeStocks == null || theCodeStocks.size() <= 3) {
            d(TAG, "这个股票没有历史记录哦，继续分析下一只吧...");
            return condition5s;
        }

//        //每天附近最大成交量
//        List<Float> listVolumeMaxs = new ArrayList<>();
//        for (int i = 0; i < theCodeStocks.size(); i++) {
//            float maxVolume = findRecentlyMax(theCodeStocks, i);
//
//            //d(TAG, "maxVolume = " + maxVolume);
//            listVolumeMaxs.add(maxVolume);
//        }

        //可以遍历分析这只股票了
        int size = theCodeStocks.size();
        for (int i = 0; i < size; i++) {
            if (i == size - 1 && i >= 30) {
                if (theCodeStocks.get(i).getCurrentTime() == currentTime) {
                    Condition5 condition5 = getCondition5ByPosition(i, theCodeStocks);
                    if (condition5 != null) {
                        currentTimeCondition5.add(condition5);
                        d(TAG, "condition5=" + condition5.getRatio_0_5());
                    }
                }
            }
            if (i >= 30 && i < size - 5) {
                Condition5 condition5 = isRecent5DayUpType(i, theCodeStocks);
                if (condition5 != null) {
                    condition5s.add(condition5);
                }
            }
        }

        return condition5s;
    }

    /**
     * 检测5日下跌的条件
     *
     * @return
     */
    private List<Condition5> findConditionFor5DayDown(String code, long currentTime) {
        List<Condition5> condition5s = new ArrayList<>();
        List<Stock> theCodeStocks = getRepository().getDatabase().stockDao().getStocksByCode(code);

        if (theCodeStocks == null || theCodeStocks.size() <= 3) {
            d(TAG, "这个股票没有历史记录哦，继续分析下一只吧...");
            return condition5s;
        }

        //每天附近最大成交量
//        List<Float> listVolumeMaxs = new ArrayList<>();
//        for (int i = 0; i < theCodeStocks.size(); i++) {
//            float maxVolume = findRecentlyMax(theCodeStocks, i);
//
//            //d(TAG, "maxVolume = " + maxVolume);
//            listVolumeMaxs.add(maxVolume);
//        }

        //可以遍历分析这只股票了
        int size = theCodeStocks.size();
        for (int i = 0; i < size; i++) {
            if (i == size - 1 && i >= 30) {
                if (theCodeStocks.get(i).getCurrentTime() == currentTime) {
                    Condition5 condition5 = getCondition5ByPosition(i, theCodeStocks);
                    if (condition5 != null) {
                        currentTimeCondition5.add(condition5);
                    }
                }
            }
            if (i >= 30 && i < size - 5) {//8
                Condition5 condition5 = isRecent5DayDownType(i, theCodeStocks);
                if (condition5 != null) {
                    condition5s.add(condition5);
                }
            }
        }

        return condition5s;
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
                if (isTarget && !todayStock.getDate().equals("2020-01-23")) {
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

    /**
     * 最近5日下跌类型
     *
     * @param position
     * @param stockList
     * @return
     */
    private Condition5 isRecent5DayDownType(int position, List<Stock> stockList) {

        Condition5 condition5 = null;

        if (stockList == null || stockList.size() <= 30) return null;

        float currentPrice = stockList.get(position).getClosePrice();

        float day5 = 0, day4 = 0, day3 = 0, day2 = 0, day1 = 0;
        int offset = 0;
        for (int i = 0; i < stockList.size(); i++) {
            if (i == position && position >= 30) {

                do {
                    day1 = stockList.get(i - (offset++)).getClosePrice();//0 5 10 15 20 25
                    day2 = stockList.get(i - (offset++)).getClosePrice();//1 6 11 16 21 26
                    day3 = stockList.get(i - (offset++)).getClosePrice();//2 7 12 17 22 27
                    day4 = stockList.get(i - (offset++)).getClosePrice();//3 8 13 18 23 28
                    day5 = stockList.get(i - (offset++)).getClosePrice();//4 9 14 19 24 29

                    if (condition5 == null) {
                        condition5 = new Condition5();
                    }

                    float ratio = (day1 + day2 + day3 + day4 + day5) / 5 / currentPrice;
                    d(TAG, "offset=" + offset);
                    switch (offset) {
                        case 5:
                            condition5.setRatio_0_5(ratio);
                            break;

                        case 10:
                            condition5.setRatio_5_10(ratio);
                            break;

                        case 15:
                            condition5.setRatio_10_15(ratio);
                            break;

                        case 20:
                            condition5.setRatio_15_20(ratio);
                            break;

                        case 25:
                            condition5.setRatio_20_25(ratio);
                            break;

                        case 30:
                            condition5.setRatio_25_30(ratio);
                            break;
                    }

                } while (offset < 30);

                //分析之后的
                day1 = stockList.get(i + 1).getClosePrice();//0 5 10 15 20 25
                day2 = stockList.get(i + 2).getClosePrice();//1 6 11 16 21 26
                day3 = stockList.get(i + 3).getClosePrice();//2 7 12 17 22 27
                day4 = stockList.get(i + 4).getClosePrice();//3 8 13 18 23 28
                day5 = stockList.get(i + 5).getClosePrice();//4 9 14 19 24 29
                float ratio = (day1 + day2 + day3 + day4 + day5) / 5 / currentPrice;

                if (ratio * FIVE_DAY_UP_OFFSET / 100 < condition5.getRatio_0_5()) {
                    condition5.setCurrentDay(stockList.get(position));
                    condition5.setProximity(PROXIMITY);
                    if (stockList.size() > position - 1) {
                        condition5.setTommorrowStock(stockList.get(position + 1));
                    }
                    return condition5;

                } else {
                    return null;
                }
            }
        }

        return null;
    }

    /**
     * 最近5日上涨类型
     *
     * @param position
     * @param stockList
     * @return
     */
    private Condition5 isRecent5DayUpType(int position, List<Stock> stockList) {

        Condition5 condition5 = null;

        if (stockList == null || stockList.size() <= 30) return null;

        float currentPrice = stockList.get(position).getClosePrice();

        float day5 = 0, day4 = 0, day3 = 0, day2 = 0, day1 = 0;
        int offset = 0;
        for (int i = 0; i < stockList.size(); i++) {
            if (i == position && position >= 30) {

                do {
                    day1 = stockList.get(i - (offset++)).getClosePrice();//0 5 10 15 20 25
                    day2 = stockList.get(i - (offset++)).getClosePrice();//1 6 11 16 21 26
                    day3 = stockList.get(i - (offset++)).getClosePrice();//2 7 12 17 22 27
                    day4 = stockList.get(i - (offset++)).getClosePrice();//3 8 13 18 23 28
                    day5 = stockList.get(i - (offset++)).getClosePrice();//4 9 14 19 24 29

                    if (condition5 == null) {
                        condition5 = new Condition5();
                    }

                    float ratio = (day1 + day2 + day3 + day4 + day5) / 5 / currentPrice;
                    //LogUtils.d(TAG, "offset=" + offset);

                    switch (offset) {
                        case 5:
                            condition5.setRatio_0_5(ratio);
                            break;

                        case 10:
                            condition5.setRatio_5_10(ratio);
                            break;

                        case 15:
                            condition5.setRatio_10_15(ratio);
                            break;

                        case 20:
                            condition5.setRatio_15_20(ratio);
                            break;

                        case 25:
                            condition5.setRatio_20_25(ratio);
                            break;

                        case 30:
                            condition5.setRatio_25_30(ratio);
                            break;
                    }

                } while (offset < 30);

                //分析之后的
                day1 = stockList.get(i + 1).getClosePrice();//0 5 10 15 20 25
                day2 = stockList.get(i + 2).getClosePrice();//1 6 11 16 21 26
                day3 = stockList.get(i + 3).getClosePrice();//2 7 12 17 22 27
                day4 = stockList.get(i + 4).getClosePrice();//3 8 13 18 23 28
                day5 = stockList.get(i + 5).getClosePrice();//4 9 14 19 24 29
                float ratio = (day1 + day2 + day3 + day4 + day5) / 5 / currentPrice;//0.9

                //LogUtils.d(TAG, "ratio="+ratio+" "+condition5.getRatio_0_5() * FIVE_DAY_UP_OFFSET / 100);
                if (ratio > condition5.getRatio_0_5() * FIVE_DAY_UP_OFFSET / 100) {
                    condition5.setCurrentDay(stockList.get(position));
                    condition5.setProximity(PROXIMITY);
                    if (stockList.size() > position - 1) {
                        condition5.setTommorrowStock(stockList.get(position + 1));
                    }
                    return condition5;

                } else {
                    return null;
                }

            }
        }

        return null;
    }

    /**
     * 通过位置得到5日均线
     *
     * @param position
     * @param stockList
     * @return
     */
    private Condition5 getCondition5ByPosition(int position, List<Stock> stockList) {
        Condition5 condition5 = null;

        if (stockList == null || stockList.size() <= 30) return null;

        float currentPrice = stockList.get(position).getClosePrice();

        float day5 = 0, day4 = 0, day3 = 0, day2 = 0, day1 = 0;
        int offset = 0;
        for (int i = 0; i < stockList.size(); i++) {
            if (i == position && position >= 30) {

                do {
                    day1 = stockList.get(i - (offset++)).getClosePrice();//0 5 10 15 20 25
                    day2 = stockList.get(i - (offset++)).getClosePrice();//1 6 11 16 21 26
                    day3 = stockList.get(i - (offset++)).getClosePrice();//2 7 12 17 22 27
                    day4 = stockList.get(i - (offset++)).getClosePrice();//3 8 13 18 23 28
                    day5 = stockList.get(i - (offset++)).getClosePrice();//4 9 14 19 24 29

                    if (condition5 == null) {
                        condition5 = new Condition5();
                    }

                    float ratio = (day1 + day2 + day3 + day4 + day5) / 5 / currentPrice;
                    switch (offset) {
                        case 5:
                            condition5.setRatio_0_5(ratio);
                            break;

                        case 10:
                            condition5.setRatio_5_10(ratio);
                            break;

                        case 15:
                            condition5.setRatio_10_15(ratio);
                            break;

                        case 20:
                            condition5.setRatio_15_20(ratio);
                            break;

                        case 25:
                            condition5.setRatio_20_25(ratio);
                            break;

                        case 30:
                            condition5.setRatio_25_30(ratio);
                            break;
                    }

                } while (offset < 30);

            }
        }

        if (condition5 != null) {
            condition5.setCurrentDay(stockList.get(position));
            condition5.setProximity(PROXIMITY);
            if (stockList.size() - 1 > position) {
                condition5.setTommorrowStock(stockList.get(position + 1));
            }
        }

        return condition5;
    }

    private void doAnalysisInnerForTommorrowUp(ConditionAverage condition, AnalysisCallback callback) {


        if (condition.getFiveAverage1() == 0 || condition.getTenAverage1() == 0 || condition.getTwentyAverage1() == 0) {
            d(TAG, "sorry, can't find target stock's info...");
            callback.failedAnalysis(new Throwable("sorry, can't find target stock's info..."));
            return;
        }

        if (condition.getFiveAverage2() == 0 || condition.getTenAverage2() == 0 || condition.getTwentyAverage2() == 0) {
            d(TAG, "sorry, can't find target stock's info...");
            callback.failedAnalysis(new Throwable("sorry, can't find target stock's info..."));
            return;
        }

        if (condition.getFiveAverage3() == 0 || condition.getTenAverage3() == 0 || condition.getTwentyAverage3() == 0) {
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

    private void doAnalysisInner(String code, String date, String pro, AnalysisCallback callback) {

        //获取查询参数
        ConditionAverage condition = new ConditionAverage();
        condition.setProximity(Float.parseFloat(pro));
        List<Stock> theCodeStocks = getRepository().getDatabase().stockDao().getStocksByCode(code);
        d(TAG, "code=" + code + " date=" + date + " pro=" + pro);
        for (int i = theCodeStocks.size() - 1; i > 0; i--) {
            if (i >= 2 && theCodeStocks.get(i).getDate().equals(date)) {
                getFiveAverage(condition, theCodeStocks, i, 0);
                getFiveAverage(condition, theCodeStocks, i - 1, 1);
                getFiveAverage(condition, theCodeStocks, i - 2, 2);
                getTenAverage(condition, theCodeStocks, i, 0);
                getTenAverage(condition, theCodeStocks, i - 1, 1);
                getTenAverage(condition, theCodeStocks, i - 2, 2);
                getTwentyAverage(condition, theCodeStocks, i, 0);
                getTwentyAverage(condition, theCodeStocks, i - 1, 1);
                getTwentyAverage(condition, theCodeStocks, i - 2, 2);
            }
        }

        if (condition.getFiveAverage1() == 0 || condition.getTenAverage1() == 0 || condition.getTwentyAverage1() == 0) {
            d(TAG, "sorry, can't find target stock's info...");
            callback.failedAnalysis(new Throwable("sorry, can't find target stock's info..."));
            return;
        }

        if (condition.getFiveAverage2() == 0 || condition.getTenAverage2() == 0 || condition.getTwentyAverage2() == 0) {
            d(TAG, "sorry, can't find target stock's info...");
            callback.failedAnalysis(new Throwable("sorry, can't find target stock's info..."));
            return;
        }

        if (condition.getFiveAverage3() == 0 || condition.getTenAverage3() == 0 || condition.getTwentyAverage3() == 0) {
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

    private ConditionAverage getCurrentConditionAverage(List<Stock> stockList, int currentPosition) {
        if (stockList == null || stockList.size() <= currentPosition || currentPosition < 19)
            return null;

        ConditionAverage condition = new ConditionAverage();

        getFiveAverage(condition, stockList, currentPosition, 0);
        getFiveAverage(condition, stockList, currentPosition - 1, 1);
        getFiveAverage(condition, stockList, currentPosition - 2, 2);
        getTenAverage(condition, stockList, currentPosition, 0);
        getTenAverage(condition, stockList, currentPosition - 1, 1);
        getTenAverage(condition, stockList, currentPosition - 2, 2);
        getTwentyAverage(condition, stockList, currentPosition, 0);
        getTwentyAverage(condition, stockList, currentPosition - 1, 1);
        getTwentyAverage(condition, stockList, currentPosition - 2, 2);

        return condition;
    }

    /**
     * 五日均线
     */
    private void getFiveAverage(ConditionAverage condition, List<Stock> stockList, int currentPosition, int type) {
        if (stockList == null || stockList.size() <= currentPosition || currentPosition < 4) return;

        float sum = 0;
        for (int i = 0; i < 5; i++) {
            sum += stockList.get(currentPosition - i).getClosePrice();
        }

        float currentPrice = stockList.get(currentPosition).getClosePrice();
        float fiveAverage = sum / 5 / currentPrice;
        switch (type) {
            case 0:
                condition.setFiveAverage1(fiveAverage);
                break;
            case 1:
                condition.setFiveAverage2(fiveAverage);
                break;
            case 2:
                condition.setFiveAverage3(fiveAverage);
                break;
        }

        //LogUtils.d(TAG, "五日均线为：" + fiveAverage);
    }

    /**
     * 十日均线
     */
    private void getTenAverage(ConditionAverage condition, List<Stock> stockList, int currentPosition, int type) {
        if (stockList == null || stockList.size() <= currentPosition || currentPosition < 9) return;

        float sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += stockList.get(currentPosition - i).getClosePrice();
        }
        float currentPrice = stockList.get(currentPosition).getClosePrice();
        float tenAverage = sum / 10 / currentPrice;
        switch (type) {
            case 0:
                condition.setTenAverage1(tenAverage);
                break;
            case 1:
                condition.setTenAverage2(tenAverage);
                break;
            case 2:
                condition.setTenAverage3(tenAverage);
                break;
        }
        //LogUtils.d(TAG, "十日均线为：" + tenAverage);
    }

    /**
     * 二十日均线
     */
    private void getTwentyAverage(ConditionAverage condition, List<Stock> stockList, int currentPosition, int type) {
        if (stockList == null || stockList.size() <= currentPosition || currentPosition < 19)
            return;

        float sum = 0;
        for (int i = 0; i < 20; i++) {
            sum += stockList.get(currentPosition - i).getClosePrice();
        }
        float currentPrice = stockList.get(currentPosition).getClosePrice();
        float twentyAverage = sum / 20 / currentPrice;
        switch (type) {
            case 0:
                condition.setTwentyAverage1(twentyAverage);
                break;
            case 1:
                condition.setTwentyAverage2(twentyAverage);
                break;
            case 2:
                condition.setTwentyAverage3(twentyAverage);
                break;
        }
        //LogUtils.d(TAG, "二十日均线为：" + twentyAverage);
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
     * @param conditionAverage 均线分析
     */
    private synchronized void getTheCodeStockAndAnalysis(int type, String code, ConditionAverage conditionAverage, AnalysisCallback callback) {

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

            //遍历分析这个股票
            int resultSize = 0;

            boolean isTarget = false;
            for (int i = 19; i < theCodeStocks.size(); i++) {

                Stock nextStock = (i == theCodeStocks.size() - 1) ? null : theCodeStocks.get(i + 1);
                ConditionAverage condition = getCurrentConditionAverage(theCodeStocks, i);
                isTarget = doAnalaysisTheCodeTheStock(condition, theCodeStocks.get(i), nextStock, conditionAverage);
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
     * 获取数据库中的股票信息然后分析
     *
     * @param callback
     */
    private synchronized void getTheCodeStockAndAnalysisIsJumpOrUp(int type, String code, AnalysisCallback callback) {

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
            int size = theCodeStocks.size();
            Stock today = theCodeStocks.get(size - 1);
            if (size <= 1) {
                callback.finishOneStock(code, resultSize);
                return;
            }

            Stock yesStock = theCodeStocks.get(size - 2);

            if (type == 0) {

                if (yesStock.getClosePrice() < today.getOpenPrice() || yesStock.getClosePrice() < today.getClosePrice()) {
                    return;
                }

                if (yesStock.getOpenPrice() < today.getOpenPrice() || yesStock.getOpenPrice() < today.getClosePrice()) {
                    return;
                }

                float down = (yesStock.getClosePrice() - today.getClosePrice()) / yesStock.getClosePrice();//0.03

                if (down * 100 > JUMP_WATER_RATIO) {
                    if (DataSource.getInstance().getAnalysisJumpWater() == null) {
                        DataSource.getInstance().setAnalysisJumpWater(new ArrayList<>());
                    }
                    DataSource.getInstance().getAnalysisJumpWater().add(DataUtils.convertByStock(today, null));
                    callback.finishOneStock(code, 1);
                }

            } else {

                if (yesStock.getClosePrice() > today.getOpenPrice() || yesStock.getClosePrice() > today.getClosePrice()) {
                    return;
                }

                if (yesStock.getOpenPrice() > today.getOpenPrice() || yesStock.getOpenPrice() > today.getClosePrice()) {
                    return;
                }

                float up = (today.getClosePrice() - yesStock.getClosePrice()) / yesStock.getClosePrice();//0.03

                if (up * 100 > JUMP_WATER_RATIO) {
                    if (DataSource.getInstance().getAnalysisGoUp() == null) {
                        DataSource.getInstance().setAnalysisGoUp(new ArrayList<>());
                    }
                    DataSource.getInstance().getAnalysisGoUp().add(DataUtils.convertByStock(today, null));
                    callback.finishOneStock(code, 1);
                }

            }


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

        float ratioTarget, ratioCondition, ratioRange1, ratioRange2, max, min;

        //分析今天涨幅是否符合 -0.8 -0.64
//        float ratioTarget = (todayStock.getClosePrice() - yesStock.getClosePrice()) / yesStock.getClosePrice();
//        float ratioCondition = (condition2.getTodayStock().getClosePrice() - condition2.getYesStock().getClosePrice()) / condition2.getYesStock().getClosePrice();
//        float ratioRange1 = ratioCondition * proximity1;//0.64 -0.64
//        float ratioRange2 = ratioCondition * proximity2;//0.96 -0.96
//        float max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
//        float min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;
//        if (ratioCondition > 0) {
//            //上涨
//            if (ratioCondition >= 0.08) {
//                max = 0.12f;//最大可以
//            }
//
//        } else {
//            //下跌
//            if (ratioCondition <= -0.08) {
//                min = -0.12f;
//            }
//        }
//
//        if (ratioTarget > max || ratioTarget < min) {
//            if (isSelf) {
//                d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
//                d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
//            }
//            return false;
//        }
////        if (isSelf) {
////            LogUtils.d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
////            LogUtils.d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
////        }
//
//        //分析昨天涨幅
//        ratioTarget = (yesStock.getClosePrice() - beforYesStock.getClosePrice()) / beforYesStock.getClosePrice();
//        ratioCondition = (condition2.getYesStock().getClosePrice() - condition2.getBeforeYesStock().getClosePrice()) / condition2.getBeforeYesStock().getClosePrice();
//        ratioRange1 = ratioCondition * proximity1;
//        ratioRange2 = ratioCondition * proximity2;
//        max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
//        min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;
//        if (ratioCondition > 0) {
//            //上涨
//            if (ratioCondition >= 0.08) {
//                max = 0.12f;//最大可以
//            }
//
//        } else {
//            //下跌
//            if (ratioCondition <= -0.08) {
//                min = -0.12f;
//            }
//        }
//
//        if (ratioTarget > max || ratioTarget < min) {
//            if (isSelf) {
//                d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
//                d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
//            }
//            return false;
//        }
////        if (isSelf) {
////            LogUtils.d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
////            LogUtils.d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
////        }
//
//        //分析前天涨幅
//        ratioTarget = (beforYesStock.getClosePrice() - before2YesStock.getClosePrice()) / before2YesStock.getClosePrice();
//        ratioCondition = (condition2.getBeforeYesStock().getClosePrice() - condition2.getBefore2YesStock().getClosePrice()) / condition2.getBefore2YesStock().getClosePrice();
//        ratioRange1 = ratioCondition * proximity1;
//        ratioRange2 = ratioCondition * proximity2;
//        max = ratioRange1 > ratioRange2 ? ratioRange1 : ratioRange2;
//        min = ratioRange1 < ratioRange2 ? ratioRange1 : ratioRange2;
//        if (ratioCondition > 0) {
//            //上涨
//            if (ratioCondition >= 0.08) {
//                max = 0.12f;//最大可以
//            }
//
//        } else {
//            //下跌
//            if (ratioCondition <= -0.08) {
//                min = -0.12f;
//            }
//        }
//
//        if (ratioTarget > max || ratioTarget < min) {
//            if (isSelf) {
//                d("ratioTarget = " + ratioTarget + " ratioCondition=" + ratioCondition + " ratioRange1=" + ratioRange1);
//                d("ratioRange2 = " + ratioTarget + " max=" + max + " min=" + min);
//            }
//            return false;
//        }
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
     * @return
     */
    private boolean doAnalaysisTheCodeTheStock(ConditionAverage currentAverage, Stock todayStock, Stock nextStock, ConditionAverage conditionAverage) {

        if (currentAverage == null) return false;
        float proximity1 = conditionAverage.getProximity() / 100.0f;//0.8
        float proximity2 = 2 - proximity1;//1.2

        float max = conditionAverage.getFiveAverage1() * proximity2;
        float min = conditionAverage.getFiveAverage1() * proximity1;
        //LogUtils.d(TAG, " max=" + max + " min=" + min + " fiveAverage=" + currentAverage.getTenAverage1());
        if (currentAverage.getFiveAverage1() > max || currentAverage.getFiveAverage1() < min) {

            return false;
        }

        max = conditionAverage.getFiveAverage2() * proximity2;
        min = conditionAverage.getFiveAverage2() * proximity1;
        //LogUtils.d(TAG, " max=" + max + " min=" + min + " fiveAverage=" + currentAverage.getFiveAverage2());
        if (currentAverage.getFiveAverage2() > max || currentAverage.getFiveAverage2() < min) {

            return false;
        }

        max = conditionAverage.getFiveAverage3() * proximity2;
        min = conditionAverage.getFiveAverage3() * proximity1;
        //LogUtils.d(TAG, " max=" + max + " min=" + min + " fiveAverage=" + currentAverage.getFiveAverage3());
        if (currentAverage.getFiveAverage3() > max || currentAverage.getFiveAverage3() < min) {

            return false;
        }

        max = conditionAverage.getTenAverage1() * proximity2;
        min = conditionAverage.getTenAverage1() * proximity1;
        if (currentAverage.getTenAverage1() > max || currentAverage.getTenAverage1() < min) {

            return false;
        }

        max = conditionAverage.getTenAverage2() * proximity2;
        min = conditionAverage.getTenAverage2() * proximity1;
        if (currentAverage.getTenAverage2() > max || currentAverage.getTenAverage2() < min) {

            return false;
        }

        max = conditionAverage.getTenAverage3() * proximity2;
        min = conditionAverage.getTenAverage3() * proximity1;
        if (currentAverage.getTenAverage3() > max || currentAverage.getTenAverage3() < min) {

            return false;
        }

        max = conditionAverage.getTwentyAverage1() * proximity2;
        min = conditionAverage.getTwentyAverage1() * proximity1;
        if (currentAverage.getTwentyAverage1() > max || currentAverage.getTwentyAverage1() < min) {

            return false;
        }

        max = conditionAverage.getTwentyAverage2() * proximity2;
        min = conditionAverage.getTwentyAverage2() * proximity1;
        if (currentAverage.getTwentyAverage2() > max || currentAverage.getTwentyAverage2() < min) {

            return false;
        }

        max = conditionAverage.getTwentyAverage3() * proximity2;
        min = conditionAverage.getTwentyAverage3() * proximity1;
        if (currentAverage.getTwentyAverage3() > max || currentAverage.getTwentyAverage3() < min) {

            return false;
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
