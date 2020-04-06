package jason.jan.stockanalysis.data.http;

import java.util.Map;

import io.reactivex.Observable;
import jason.jan.stockanalysis.entity.HttpStockResponse;
import jason.jan.stockanalysis.entity.ResponModel;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * desc: 网络请求接口
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2020/4/3 8:54
 **/
public interface StockApiService {

    /**
     * 请求历史每日股票行情
     * 参考文档：https://www.showapi.com/apiGateway/view/?apiCode=131&pointCode=47
     * 需要传递的参数：
     * showapi_appid appId
     * showapi_sign 秘钥
     * begin 开始日期
     * end  结束日期
     * code 股票id
     * type 复权类型 bfq
     *
     * @param request
     * @return
     */
    @GET("131-47")
    Observable<ResponModel<HttpStockResponse.ShowapiResBodyBean>> getStockAPI(@QueryMap Map<String, String> request);

    /**
     * 请求历史每日股票行情2
     * 参考文档：https://www.showapi.com/apiGateway/view/?apiCode=131&pointCode=47
     * 需要传递的参数：
     * showapi_appid appId
     * showapi_sign 秘钥
     * begin 开始日期
     * end  结束日期
     * code 股票id
     * type 复权类型 bfq
     *
     * @param request
     * @return
     */
    @GET("131-47")
    Observable<HttpStockResponse> getStockAPI2(@QueryMap Map<String, String> request);
}
