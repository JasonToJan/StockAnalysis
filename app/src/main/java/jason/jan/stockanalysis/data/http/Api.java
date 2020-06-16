package jason.jan.stockanalysis.data.http;

/**
 * desc: 网络请求接口
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2020/4/3 8:54
 **/
public interface Api {


    //http://hq.sinajs.cn/list=sh601003,sh601001 最新行情


    /**
     * 易源API
     * 官网：https://www.showapi.com/
     */
    String YI_YUAN_API = "https://route.showapi.com/";

    String YI_APPID = "168359";//对应请求参数：showapi_appid

    String YI_SIGN = "85580f6faa8f4773a48a3f45a1623745";//对应请求参数：showapi_sign

    String YI_APPID_STR = "showapi_appid";

    String YI_SIGN_STR = "showapi_sign";

    String YI_TIMESTAMP_STR = "showapi_timestamp";

    String YI_BEGAIN = "begin";

    String YI_END = "end";

    String YI_CODE = "code";

    String YI_TYPE = "type";

}
