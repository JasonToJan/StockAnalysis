package jason.jan.stockanalysis.mvvm.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jason.jan.stockanalysis.base.BaseModel;
import jason.jan.stockanalysis.base.BaseViewModel;
import jason.jan.stockanalysis.data.http.RepositoryImpl;
import jason.jan.stockanalysis.entity.HttpStockResponse;
import jason.jan.stockanalysis.entity.Resource;
import jason.jan.stockanalysis.entity.Stock;
import jason.jan.stockanalysis.entity.StockName;
import jason.jan.stockanalysis.mvvm.ui.fragment.DataFragment;
import jason.jan.stockanalysis.utils.DataUtils;
import jason.jan.stockanalysis.utils.LogUtils;
import jason.jan.stockanalysis.utils.MyTimeUtils;

import static jason.jan.stockanalysis.utils.LogUtils.d;

/**
 * desc:
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2020/4/3 13:16
 **/
public class DataFViewModel extends BaseViewModel<RepositoryImpl> {

    private static final String TAG = "DataFViewModel";

    /**
     * 当前最大数量
     */
    public static final int MAX_CURRENT = 3000;
    /**
     * 所有股票限制大小
     */
    public static final int MAX_SIZE = 250;

    /**
     * 从那个位置开始
     */
    public int offsetNum = 0;

    /**
     * 插入历史记录的月份大小
     */
    public static final int HISTORY_MONTH_SIZE = 6;

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public interface AddStockCallback {

        void successAdd(int size);

        void endAdd();

        void failAdd(Throwable e);

    }

    public DataFViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Resource<HttpStockResponse.ShowapiResBodyBean>> requestApi(String code, String begin, String end) {

        return getRepository().getHttpStockList(code, begin, end);
    }

    /**
     * 异步中请求网络
     */
    public void requestApi2(String code, String begin, String end, BaseModel.DataCallback callback) {
        getRepository().getHttpStockList2(code, begin, end, callback);
    }

    public LiveData<List<StockName>> queryAllCode() {

        return getRepository().getDatabase().stockNameDao().queryAllStockName(MAX_SIZE, offsetNum);
    }

    public LiveData<List<StockName>> queryAllCodeByOffset(int offsetNum) {

        return getRepository().getDatabase().stockNameDao().queryAllStockName(MAX_SIZE, offsetNum);
    }

    public LiveData<String> queryStockName(String code) {
        return getRepository().getDatabase().stockNameDao().queryStockName(code);
    }

    public void deleteAllStock() {
        Observable.just(1)
                .observeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        getRepository().getDatabase().stockDao().deleteAll();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 是否需要重新添加这个月的股票信息
     *
     * @param isNeedAddAgain
     */
    public void deleteCurrentMonthStock(boolean isNeedAddAgain, int offsetNum, AddStockCallback callback) {
        Observable.just(1)
                .observeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int currentMonth = calendar.get(Calendar.MONTH) + 1;
                        String realMonth = currentMonth < 10 ? "0" + currentMonth : "" + currentMonth;
                        String realDay = "01";
                        String dateBegin = year + "-" + realMonth + "-" + realDay;
                        long currentMonthMills = MyTimeUtils.dateToStamp(dateBegin);
                        if (!isNeedAddAgain) {
                            getRepository().getDatabase().stockDao().deleteCurrentMonth(currentMonthMills);
                        }

                        if (isNeedAddAgain) {
                            new MyAddDataAsync(true, offsetNum, callback).execute();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 是否需要重新添加这个月的股票信息
     *
     * @param isNeedAddAgain
     */
    public void deleteThreeMonthStock(boolean isNeedAddAgain, int offsetNum, AddStockCallback callback) {
        Observable.just(1)
                .observeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int currentMonth = calendar.get(Calendar.MONTH) + 1 - 2;
                        String realMonth = currentMonth < 10 ? "0" + currentMonth : "" + currentMonth;
                        String realDay = "01";
                        String dateBegin = year + "-" + realMonth + "-" + realDay;
                        long currentMonthMills = MyTimeUtils.dateToStamp(dateBegin);
                        if (!isNeedAddAgain) {
                            getRepository().getDatabase().stockDao().deleteCurrentMonth(currentMonthMills);
                        }

                        if (isNeedAddAgain) {
                            new MyAddDataAsync(false, offsetNum, 3, callback).execute();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 采用AsyncTask搜索数据
     *
     * @param callback
     */
    public void requestAllStockThenInsertAsyncTask(int offsetNum, AddStockCallback callback) {
        new MyAddDataAsync(false, offsetNum, callback).execute();
    }

    /**
     * 查询所有股票，然后插入
     */
    public void requestAllStockThenInsert(LifecycleOwner owner, AddStockCallback callback) {

        try {
            queryAllCode().observe(owner, new Observer<List<StockName>>() {
                @Override
                public void onChanged(List<StockName> stockNames) {
                    if (stockNames == null || stockNames.size() == 0) return;

                    Calendar calendar = Calendar.getInstance();
                    int currentMonth = calendar.get(Calendar.MONTH) + 1;
                    String currentDay = format.format(new Date());

                    String lastMonthDay = "";
                    int size = stockNames.size();
                    for (int n = 0; n < size; n++) {

                        String stockName = stockNames.get(n).getName();
                        String stockCode = stockNames.get(n).getCode();

                        d(TAG, " Name = " + stockName + " code=" + stockCode);

                        calendar.setTime(new Date());
                        final int currentN = n;
                        for (int i = 0; i < HISTORY_MONTH_SIZE; i++) {

                            final int currentI = i;
                            if (i == 0) {
                                //当前月份
                                int year = calendar.get(Calendar.YEAR);
                                String realMonth = currentMonth < 10 ? "0" + currentMonth : "" + currentMonth;
                                String realDay = "01";
                                String dateBegin = year + "-" + realMonth + "-" + realDay;

                                lastMonthDay = dateBegin;//记录这个月一号
                                d(TAG, " dateBegin=" + dateBegin + " dateEnd=" + currentDay + " code=" + stockCode);

                                requestApi(stockCode, dateBegin, currentDay).observe(owner, showapiResBodyBeanResource -> {

                                    HttpStockResponse.ShowapiResBodyBean response = showapiResBodyBeanResource.data;
                                    if (response == null) return;

                                    List<HttpStockResponse.ShowapiResBodyBean.ListBean> list = response.getList();
                                    if (list == null || list.size() == 0) return;

                                    d(TAG, "成功拿到API数据了");

                                    //转换成Stock类，然后插入数据库
                                    ArrayList<Stock> myStocks = DataUtils.convertToArrayStock(list, stockName);
                                    insertManyAsync(myStocks);

                                    callback.successAdd(myStocks.size());

                                    if (currentN == size - 1 && currentI == HISTORY_MONTH_SIZE - 1) {
                                        callback.endAdd();
                                    }

                                });

                            } else {

                                String dateEnd = lastMonthDay;

                                //月份减一
                                calendar.add(Calendar.MONTH, -1);
                                int year = calendar.get(Calendar.YEAR);
                                int month = calendar.get(Calendar.MONTH) + 1;
                                String realMonth = currentMonth < 10 ? "0" + month : "" + month;
                                String realDay = "01";
                                String dateBegin = year + "-" + realMonth + "-" + realDay;

                                //为下次做准备
                                lastMonthDay = dateBegin;

                                d(TAG, " dateBegin=" + dateBegin + " dateEnd=" + dateEnd + " code=" + stockCode);

                                requestApi(stockCode, dateBegin, dateEnd).observe(owner, showapiResBodyBeanResource -> {

                                    HttpStockResponse.ShowapiResBodyBean response = showapiResBodyBeanResource.data;
                                    if (response == null) return;

                                    List<HttpStockResponse.ShowapiResBodyBean.ListBean> list = response.getList();
                                    if (list == null || list.size() == 0) return;

                                    d(TAG, "成功拿到API数据了");

                                    //转换成Stock类，然后插入数据库
                                    ArrayList<Stock> myStocks = DataUtils.convertToArrayStock(list, stockName);
                                    insertManyAsync(myStocks);

                                    callback.successAdd(myStocks.size());

                                    if (currentN == size - 1 && currentI == HISTORY_MONTH_SIZE - 1) {
                                        callback.endAdd();
                                    }

                                });
                            }
                        }
                    }

                }
            });
        } catch (Throwable e) {
            d("Error", "##" + e.getMessage());
            callback.failAdd(e);
        }
    }

    /**
     * 异步插入
     */
    public void insertManyAsync(ArrayList<Stock> stockArrayList) {
        Observable.just(stockArrayList)
                .observeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<ArrayList<Stock>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<Stock> list) {
                        if (list == null || list.size() == 0) return;
                        Stock[] stocks = new Stock[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            stocks[i] = list.get(i);
                        }
                        addManyStock(stocks);
                        d(TAG, "插入成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("Error##" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 同步插入
     *
     * @param list
     */
    public void insertManySync(ArrayList<Stock> list) {
        if (list == null || list.size() == 0) return;
        Stock[] stocks = new Stock[list.size()];
        for (int i = 0; i < list.size(); i++) {
            stocks[i] = list.get(i);
        }
        addManyStock(stocks);
        d(TAG, "插入成功");
    }

    public void addManyStock(Stock[] stocks) {
        getRepository().getDatabase().stockDao().insertMany(stocks);
    }

    /**
     * 加载数据异步任务类
     */
    class MyAddDataAsync extends AsyncTask<Void, Integer, Void> {

        AddStockCallback callback;

        /**
         * 是否仅仅更新当前月份的
         */
        boolean isOnlyCurrentMonth;

        /**
         * 间隔多少个月
         */
        int offsetMonth = 0;

        int offsetNum = 0;

        MyAddDataAsync(AddStockCallback callback) {
            this.callback = callback;
        }

        MyAddDataAsync(boolean isOnlyCurrentMonth, AddStockCallback callback) {
            this.callback = callback;
            this.isOnlyCurrentMonth = isOnlyCurrentMonth;
        }

        MyAddDataAsync(boolean isOnlyCurrentMonth, int offsetNum, AddStockCallback callback) {
            this.callback = callback;
            this.isOnlyCurrentMonth = isOnlyCurrentMonth;
            this.offsetNum = offsetNum;
        }

        MyAddDataAsync(boolean isOnlyCurrentMonth, int offsetNum, int offsetMonth, AddStockCallback callback) {
            this.callback = callback;
            this.isOnlyCurrentMonth = isOnlyCurrentMonth;
            this.offsetNum = offsetNum;
            this.offsetMonth = offsetMonth;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            LogUtils.d(TAG, "from=" + MAX_SIZE + " offset=" + offsetNum);
            int realCount = MAX_SIZE;
            if (offsetNum == DataFragment.BEGIN_NUM) {
                realCount = MAX_SIZE - DataFragment.BEGIN_NUM % MAX_SIZE;
            }
            List<StockName> stockNames = getRepository().getDatabase().stockNameDao().queryAllStockNameAsyncByOffset(realCount, offsetNum);
            if (stockNames == null || stockNames.size() == 0) return null;

            Calendar calendar = Calendar.getInstance();
            int currentMonth = calendar.get(Calendar.MONTH) + 1;
            String currentDay = format.format(new Date());

            String lastMonthDay = "";
            int size = stockNames.size();
            for (int n = 0; n < size; n++) {

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    LogUtils.d(TAG, "Error##" + e.getMessage());
                }

                String stockName = stockNames.get(n).getName();
                String stockCode = stockNames.get(n).getCode();

                d(TAG, " Name = " + stockName + " code=" + stockCode);

                calendar.setTime(new Date());
                final int currentN = n;

                //是否仅仅更新当前月份
                int MONTH_SIZE = isOnlyCurrentMonth ? 1 : offsetMonth == 0 ? HISTORY_MONTH_SIZE : offsetMonth;

                for (int i = 0; i < MONTH_SIZE; i++) {

                    final int currentI = i;
                    if (i == 0) {

                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            LogUtils.d(TAG, "Error##" + e.getMessage());
                        }

                        //当前月份
                        int year = calendar.get(Calendar.YEAR);
                        String realMonth = currentMonth < 10 ? "0" + currentMonth : "" + currentMonth;
                        String realDay = "01";
                        String dateBegin = year + "-" + realMonth + "-" + realDay;

                        lastMonthDay = dateBegin;//记录这个月一号
                        d(TAG, " dateBegin=" + dateBegin + " dateEnd=" + currentDay + " stockName=" + stockName);

                        requestApi2(stockCode, dateBegin, currentDay, new BaseModel.DataCallback() {
                            @Override
                            public void successGetData(Object object) {
                                HttpStockResponse responseHttp = (HttpStockResponse) object;
                                if (responseHttp == null) return;
                                HttpStockResponse.ShowapiResBodyBean response = responseHttp.getShowapi_res_body();
                                if (response == null) return;

                                List<HttpStockResponse.ShowapiResBodyBean.ListBean> list = response.getList();
                                if (list == null || list.size() == 0) return;

                                d(TAG, "成功拿到API数据了");

                                //转换成Stock类，然后插入数据库
                                ArrayList<Stock> myStocks = DataUtils.convertToArrayStock(list, stockName);
                                insertManySync(myStocks);

                                publishProgress(myStocks.size());

                                if (currentN == size - 1 && currentI == MONTH_SIZE - 1) {
                                    callback.endAdd();
                                }

                            }

                            @Override
                            public void failedGetData(Throwable e) {
                                LogUtils.d(TAG, "Error##" + e.getMessage());
                            }
                        });

                    } else {


                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            LogUtils.d(TAG, "Error##" + e.getMessage());
                        }

                        String dateEnd = lastMonthDay;

                        //月份减一
                        calendar.add(Calendar.MONTH, -1);
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        String realMonth = currentMonth < 10 ? "0" + month : "" + month;
                        String realDay = "01";
                        String dateBegin = year + "-" + realMonth + "-" + realDay;

                        //为下次做准备
                        lastMonthDay = dateBegin;

                        d(TAG, " dateBegin=" + dateBegin + " dateEnd=" + dateEnd + " stockName=" + stockName);

                        requestApi2(stockCode, dateBegin, dateEnd, new BaseModel.DataCallback() {
                            @Override
                            public void successGetData(Object object) {
                                HttpStockResponse responseHttp = (HttpStockResponse) object;
                                if (responseHttp == null) return;
                                HttpStockResponse.ShowapiResBodyBean response = responseHttp.getShowapi_res_body();
                                if (response == null) return;

                                List<HttpStockResponse.ShowapiResBodyBean.ListBean> list = response.getList();
                                if (list == null || list.size() == 0) return;

                                d(TAG, "成功拿到API数据了");

                                //转换成Stock类，然后插入数据库
                                ArrayList<Stock> myStocks = DataUtils.convertToArrayStock(list, stockName);
                                insertManySync(myStocks);

                                publishProgress(myStocks.size());

                                if (currentN == size - 1 && currentI == MONTH_SIZE - 1) {
                                    callback.endAdd();
                                }
                            }

                            @Override
                            public void failedGetData(Throwable e) {
                                LogUtils.d(TAG, "Error##" + e.getMessage());
                            }
                        });

                    }
                }
            }

            return null;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (callback != null) {
                callback.successAdd(values[0]);
            }
        }

    }


}
