package jason.jan.stockanalysis.mvvm.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import jason.jan.stockanalysis.base.BaseViewModel;
import jason.jan.stockanalysis.data.http.RepositoryImpl;
import jason.jan.stockanalysis.entity.BannerBean;
import jason.jan.stockanalysis.entity.Resource;

/**
 * desc:
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2020/4/3 13:16
 **/
public class HomeViewModel extends BaseViewModel<RepositoryImpl> {

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    //获取banner轮播
    public LiveData<Resource<List<BannerBean>>> getBanner() {
        return getRepository().getBannerList();
    }
}
