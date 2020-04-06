package jason.jan.stockanalysis.data.http;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import jason.jan.stockanalysis.base.BaseModel;
import jason.jan.stockanalysis.entity.HttpStockResponse;
import jason.jan.stockanalysis.entity.Resource;
import jason.jan.stockanalysis.utils.DataUtils;

/**
 * desc:
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2020/4/3 12:55
 **/
public class RepositoryImpl extends BaseModel {

    /**
     * 请求API
     * @param code
     * @param begin
     * @param end
     * @return
     */
    public MutableLiveData<Resource<HttpStockResponse.ShowapiResBodyBean>> getHttpStockList(String code, String begin, String end) {

        MutableLiveData<Resource<HttpStockResponse.ShowapiResBodyBean>> liveData = new MutableLiveData<>();
        return observeGo(getApiService().getStockAPI(DataUtils.getRequestMapParams(code, begin, end)), liveData);
    }

    /**
     * 请求API方式2
     * @param code
     * @param begin
     * @param end
     * @return
     */
    public void getHttpStockList2(String code, String begin, String end,DataCallback callback){

        observeGoAsync(getApiService().getStockAPI2(DataUtils.getRequestMapParams(code, begin, end)),callback);
    }
}
