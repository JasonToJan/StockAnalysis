package jason.jan.stockanalysis.entity;

/**
 * desc: 测试WanAndroid接口
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2020/4/3 12:46
 **/
public class ResponModel<T> {

    /**
     * showapi_res_error :
     * showapi_res_code : 0
     * showapi_res_id : 1852d9bd81d44e7693ba4456bc5a79e0
     * showapi_res_body : {"ret_code":0,"showapi_fee_code":0,"list":[{"min_price":"6.10","market":"sz","trade_num":"92297","turnover":"0.29","trade_money":"56760988","diff_money":"-0.05","close_price":"6.12","open_price":"6.18","code":"002010","swing":"1.78","max_price":"6.21","date":"2020-04-03","diff_rate":"-0.81"},{"min_price":"6.02","market":"sz","trade_num":"120714","turnover":"0.38","trade_money":"73866274","diff_money":"0.10","close_price":"6.17","open_price":"6.04","code":"002010","swing":"2.64","max_price":"6.18","date":"2020-04-02","diff_rate":"1.65"},{"min_price":"6.06","market":"sz","trade_num":"122990","turnover":"0.39","trade_money":"75447243","diff_money":"-0.09","close_price":"6.07","open_price":"6.12","code":"002010","swing":"2.27","max_price":"6.20","date":"2020-04-01","diff_rate":"-1.46"}]}
     */

    private String showapi_res_error;
    private int showapi_res_code;
    private String showapi_res_id;
    private T showapi_res_body;

    public String getShowapi_res_error() {
        return showapi_res_error;
    }

    public void setShowapi_res_error(String showapi_res_error) {
        this.showapi_res_error = showapi_res_error;
    }

    public int getShowapi_res_code() {
        return showapi_res_code;
    }

    public void setShowapi_res_code(int showapi_res_code) {
        this.showapi_res_code = showapi_res_code;
    }

    public String getShowapi_res_id() {
        return showapi_res_id;
    }

    public void setShowapi_res_id(String showapi_res_id) {
        this.showapi_res_id = showapi_res_id;
    }

    public T getShowapi_res_body() {
        return showapi_res_body;
    }

    public void setShowapi_res_body(T showapi_res_body) {
        this.showapi_res_body = showapi_res_body;
    }

}