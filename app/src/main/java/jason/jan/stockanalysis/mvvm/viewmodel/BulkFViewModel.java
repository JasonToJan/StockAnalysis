package jason.jan.stockanalysis.mvvm.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import jason.jan.stockanalysis.base.BaseViewModel;
import jason.jan.stockanalysis.data.dao.StockDao;
import jason.jan.stockanalysis.data.http.RepositoryImpl;
import jason.jan.stockanalysis.entity.BannerBean;
import jason.jan.stockanalysis.entity.Resource;
import jason.jan.stockanalysis.entity.SearchParms;
import jason.jan.stockanalysis.entity.Stock;
import jason.jan.stockanalysis.utils.LogUtils;

/**
 * desc:
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2020/4/3 13:16
 **/
public class BulkFViewModel extends BaseViewModel<RepositoryImpl> {

    public BulkFViewModel(@NonNull Application application) {
        super(application);
    }

    //获取banner轮播
    public LiveData<Resource<List<BannerBean>>> getBanner() {
        return getRepository().getBannerList();
    }

    /**
     * 删除某些条目 id in (12,13,1414)
     * @param ids = 12,13,1414
     */
    public void deleteSome(long[] ids){
        if (ids == null || ids.length == 0) return;

        StockDao dao = getRepository().getDatabase().stockDao();

        dao.deleteSome(ids);
    }

}
