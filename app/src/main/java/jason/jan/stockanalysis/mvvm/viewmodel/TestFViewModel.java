package jason.jan.stockanalysis.mvvm.viewmodel;

import android.app.Application;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jason.jan.stockanalysis.base.BaseViewModel;
import jason.jan.stockanalysis.data.db.AppDatabase;
import jason.jan.stockanalysis.data.http.RepositoryImpl;
import jason.jan.stockanalysis.entity.HttpStockResponse;
import jason.jan.stockanalysis.entity.Resource;
import jason.jan.stockanalysis.entity.Stock;
import jason.jan.stockanalysis.utils.LogUtils;

/**
 * desc:
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2020/4/3 13:16
 **/
public class TestFViewModel extends BaseViewModel<RepositoryImpl> {

    private static final String TAG = "TestFViewModel";

    public TestFViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Resource<HttpStockResponse.ShowapiResBodyBean>> requestApi(String code, String begin, String end){
        return getRepository().getHttpStockList(code, begin, end);
    }

    public void addManyStock(Stock[] stocks){
        getRepository().getDatabase().stockDao().insertMany(stocks);
    }

    public LiveData<String> queryStockName(String code){
        return getRepository().getDatabase().stockNameDao().queryStockName(code);
    }

    /**
     * 异步插入
     */
    public void insertManyAsync(ArrayList<Stock> stockArrayList){
        Observable.just(stockArrayList)
                .observeOn(Schedulers.io())
                .subscribe(new Observer<ArrayList<Stock>>() {
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
                        LogUtils.d(TAG, "插入成功");
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
}
