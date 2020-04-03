package jason.jan.stockanalysis.data.http;

import java.util.List;

import io.reactivex.Observable;
import jason.jan.stockanalysis.entity.BannerBean;
import jason.jan.stockanalysis.entity.ResponModel;
import retrofit2.http.GET;

/**
 * desc: 网络请求接口
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2020/4/3 8:54
 **/
public interface StockApiService {

    @GET("banner/json")
    Observable<ResponModel<List<BannerBean>>> getBanner();

}
