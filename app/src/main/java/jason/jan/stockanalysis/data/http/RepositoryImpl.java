package jason.jan.stockanalysis.data.http;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import jason.jan.stockanalysis.base.BaseModel;
import jason.jan.stockanalysis.entity.BannerBean;
import jason.jan.stockanalysis.entity.Resource;

/**
 * desc:
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2020/4/3 12:55
 **/
public class RepositoryImpl extends BaseModel {

    /**
     * 获取 banner列表
     * @return
     */
    public MutableLiveData<Resource<List<BannerBean>>> getBannerList() {

        MutableLiveData<Resource<List<BannerBean>>> liveData = new MutableLiveData<>();
        return observeGo(getApiService().getBanner(), liveData);
    }


}
