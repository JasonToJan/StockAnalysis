package jason.jan.stockanalysis.mvvm.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jason.jan.stockanalysis.MyApplication;
import jason.jan.stockanalysis.R;
import jason.jan.stockanalysis.base.BaseViewModel;
import jason.jan.stockanalysis.data.http.RepositoryImpl;
import jason.jan.stockanalysis.entity.BannerBean;
import jason.jan.stockanalysis.entity.Resource;
import jason.jan.stockanalysis.entity.Stock;
import jason.jan.stockanalysis.entity.StockName;
import jason.jan.stockanalysis.utils.AssetsUtils;
import jason.jan.stockanalysis.utils.LogUtils;

/**
 * desc:
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2020/4/3 13:16
 **/
public class HomeViewModel extends BaseViewModel<RepositoryImpl> {

    private static final String TAG = "HomeViewModel";

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 记录所有股票名称
     */
    public void recordAllName() {
        if (MyApplication.getInstance().getPreference().hasRecordAllName()) {
            LogUtils.d(TAG, "抱歉，已经记录过名称哦!");
            return;
        }

        Observable.just(1)
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer list) {

                        LogUtils.d(TAG, "开始记录名称了");

                        String str = AssetsUtils.readAssetsTxt(MyApplication.getInstance(), "all_stock_name");
                        String[] strings = str.split("\\)");
                        LogUtils.d(TAG, "长度：" + strings.length);

                        //以右括号进行分割
                        ArrayList<StockName> arrayList = new ArrayList<>(strings.length);
                        for (String str1 : strings) {
                            if (!TextUtils.isEmpty(str1)) {
                                String[] string2 = str1.split("\\(");
                                if (string2.length < 2) continue;

                                arrayList.add(new StockName(string2[1].trim(), string2[0].trim()));
                            }
                        }

                        int size = arrayList.size();
                        StockName[] stockNames = new StockName[size];
                        for (int i = 0; i < size; i++) {
                            stockNames[i] = arrayList.get(i);
                            //LogUtils.d(TAG, "code=" + arrayList.get(i).code + " name=" + arrayList.get(i).getName());
                        }
                        LogUtils.d(TAG, "插入长度为：" + size);
                        getRepository().getDatabase().stockNameDao().insertMany(stockNames);
                        MyApplication.getInstance().getPreference().setHasRecordName(true);
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
     * 移除所有股票名称
     */
    public void removeAllName() {
        Observable.just(1)
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer list) {
                        getRepository().getDatabase().stockNameDao().deleteAll();
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
