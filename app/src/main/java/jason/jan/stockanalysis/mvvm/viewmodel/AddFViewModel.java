package jason.jan.stockanalysis.mvvm.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import jason.jan.stockanalysis.base.BaseViewModel;
import jason.jan.stockanalysis.data.http.RepositoryImpl;
import jason.jan.stockanalysis.entity.BannerBean;
import jason.jan.stockanalysis.entity.Resource;
import jason.jan.stockanalysis.entity.Stock;

/**
 * desc:
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2020/4/3 13:16
 **/
public class AddFViewModel extends BaseViewModel<RepositoryImpl> {

    public AddFViewModel(@NonNull Application application) {
        super(application);
    }

    public void addStock(Stock stock){
        getRepository().getDatabase().stockDao().insert(stock);
    }

    public LiveData<String> queryStockName(String code){
        return getRepository().getDatabase().stockNameDao().queryStockName(code);
    }
}
