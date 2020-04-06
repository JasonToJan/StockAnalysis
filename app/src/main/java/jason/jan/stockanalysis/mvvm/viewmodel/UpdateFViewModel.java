package jason.jan.stockanalysis.mvvm.viewmodel;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
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
public class UpdateFViewModel extends BaseViewModel<RepositoryImpl> {

    public UpdateFViewModel(@NonNull Application application) {
        super(application);
    }


    public void updateStock(Stock stock){
        getRepository().getDatabase().stockDao().updateStockInfo(
                stock.getCode(),
                stock.getName(),
                stock.getDate(),
                stock.getCurrentTime(),
                stock.getMaxPrice(),
                stock.getMinPrice(),
                stock.getOpenPrice(),
                stock.getClosePrice(),
                stock.getVolume(),
                stock.getIsForecast()
                );
    }
}
