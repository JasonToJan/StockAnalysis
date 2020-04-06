package jason.jan.stockanalysis.mvvm.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

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
public class QueryFViewModel extends BaseViewModel<RepositoryImpl> {

    private static final String TAG = "QueryFViewModel";

    public QueryFViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 搜索查询数据库
     *
     * @param parms
     * @return
     */
    public LiveData<List<Stock>> getSearchList(SearchParms parms) {

        StockDao dao = getRepository().getDatabase().stockDao();
        if (parms == null) {
            return dao.getSearchListNoParams();
        }

        boolean hasCode = !TextUtils.isEmpty(parms.getCode());
        boolean hasDate = !TextUtils.isEmpty(parms.getDate());
        boolean hasForecast = parms.getIsForecast() != 2;//2表示都可以，1表示预测，0表示不预测

        int searchType = 0;
        if (hasCode && !hasDate && !hasForecast) {
            searchType = 1;//只有code
        } else if (!hasCode && hasDate && !hasForecast) {
            searchType = 2;//只有Date
        } else if (!hasCode && !hasDate && hasForecast) {
            searchType = 3;//只有forecast
        } else if (hasCode && hasDate && !hasForecast) {
            searchType = 4;//没有Forecast
        } else if (hasCode && !hasDate && hasForecast) {
            searchType = 5;//没有Date
        } else if (!hasCode && hasDate && hasForecast) {
            searchType = 6;//没有Code
        } else if (hasCode && hasDate && hasForecast) {
            searchType = 7;
        } else if (!hasCode && !hasDate && !hasForecast) {
            searchType = 8;
        }

        LogUtils.d(TAG,"searchType=" + searchType);

        switch (searchType) {
            case 1:
                return dao.getSearchListOnlyCode(parms.getCode());

            case 2:
                return dao.getSearchListOnlyDate(parms.getDate());

            case 3:
                return dao.getSearchListOnlyForecast(parms.getIsForecast());

            case 4:
                return dao.getSearchListNoForecast(parms.getCode(), parms.getDate());

            case 5:
                return dao.getSearchListNoDate(parms.getCode(), parms.getIsForecast());

            case 6:
                return dao.getSearchListNoCode(parms.getDate(), parms.getIsForecast());

            case 7:
                return dao.getSearchList(parms.getCode(), parms.getDate(), parms.getIsForecast());

            case 8:
                return dao.getSearchListNoParams();
        }

        return dao.getSearchListNoParams();
    }

    /**
     * 查询记录总是
     * @return
     */
    public LiveData<Integer> getAllCount(){
        StockDao dao = getRepository().getDatabase().stockDao();

        return dao.getCount();
    }

}
