package jason.jan.stockanalysis.entity;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * Description: 网络接口回调
 * *
 * Creator: Wang
 * Date: 2020/4/4 16:58
 */
public class HttpStockResponse {

    /**
     * showapi_res_error :
     * showapi_res_code : 0
     * showapi_res_id : 1852d9bd81d44e7693ba4456bc5a79e0
     * showapi_res_body : {"ret_code":0,"showapi_fee_code":0,"list":[{"min_price":"6.10","market":"sz","trade_num":"92297","turnover":"0.29","trade_money":"56760988","diff_money":"-0.05","close_price":"6.12","open_price":"6.18","code":"002010","swing":"1.78","max_price":"6.21","date":"2020-04-03","diff_rate":"-0.81"},{"min_price":"6.02","market":"sz","trade_num":"120714","turnover":"0.38","trade_money":"73866274","diff_money":"0.10","close_price":"6.17","open_price":"6.04","code":"002010","swing":"2.64","max_price":"6.18","date":"2020-04-02","diff_rate":"1.65"},{"min_price":"6.06","market":"sz","trade_num":"122990","turnover":"0.39","trade_money":"75447243","diff_money":"-0.09","close_price":"6.07","open_price":"6.12","code":"002010","swing":"2.27","max_price":"6.20","date":"2020-04-01","diff_rate":"-1.46"}]}
     */

    private String showapi_res_error;
    private int showapi_res_code;
    private String showapi_res_id;
    private ShowapiResBodyBean showapi_res_body;

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

    public ShowapiResBodyBean getShowapi_res_body() {
        return showapi_res_body;
    }

    public void setShowapi_res_body(ShowapiResBodyBean showapi_res_body) {
        this.showapi_res_body = showapi_res_body;
    }

    public static class ShowapiResBodyBean {
        /**
         * ret_code : 0
         * showapi_fee_code : 0
         * list : [{"min_price":"6.10","market":"sz","trade_num":"92297","turnover":"0.29","trade_money":"56760988","diff_money":"-0.05","close_price":"6.12","open_price":"6.18","code":"002010","swing":"1.78","max_price":"6.21","date":"2020-04-03","diff_rate":"-0.81"},{"min_price":"6.02","market":"sz","trade_num":"120714","turnover":"0.38","trade_money":"73866274","diff_money":"0.10","close_price":"6.17","open_price":"6.04","code":"002010","swing":"2.64","max_price":"6.18","date":"2020-04-02","diff_rate":"1.65"},{"min_price":"6.06","market":"sz","trade_num":"122990","turnover":"0.39","trade_money":"75447243","diff_money":"-0.09","close_price":"6.07","open_price":"6.12","code":"002010","swing":"2.27","max_price":"6.20","date":"2020-04-01","diff_rate":"-1.46"}]
         */

        private int ret_code;
        private int showapi_fee_code;
        private List<ListBean> list;

        public int getRet_code() {
            return ret_code;
        }

        public void setRet_code(int ret_code) {
            this.ret_code = ret_code;
        }

        public int getShowapi_fee_code() {
            return showapi_fee_code;
        }

        public void setShowapi_fee_code(int showapi_fee_code) {
            this.showapi_fee_code = showapi_fee_code;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * min_price : 6.10
             * market : sz
             * trade_num : 92297
             * turnover : 0.29
             * trade_money : 56760988
             * diff_money : -0.05
             * close_price : 6.12
             * open_price : 6.18
             * code : 002010
             * swing : 1.78
             * max_price : 6.21
             * date : 2020-04-03
             * diff_rate : -0.81
             */

            private float min_price;
            private String market;
            private float trade_num;
            private float turnover;
            private float trade_money;
            private float diff_money;
            private float close_price;
            private float open_price;
            private String code;
            private float swing;
            private float max_price;
            private String date;
            private float diff_rate;

            public float getMin_price() {
                return min_price;
            }

            public void setMin_price(float min_price) {
                this.min_price = min_price;
            }

            public String getMarket() {
                return market;
            }

            public void setMarket(String market) {
                this.market = market;
            }

            public float getTrade_num() {
                return trade_num;
            }

            public void setTrade_num(float trade_num) {
                this.trade_num = trade_num;
            }

            public float getTurnover() {
                return turnover;
            }

            public void setTurnover(float turnover) {
                this.turnover = turnover;
            }

            public float getTrade_money() {
                return trade_money;
            }

            public void setTrade_money(float trade_money) {
                this.trade_money = trade_money;
            }

            public float getDiff_money() {
                return diff_money;
            }

            public void setDiff_money(float diff_money) {
                this.diff_money = diff_money;
            }

            public float getClose_price() {
                return close_price;
            }

            public void setClose_price(float close_price) {
                this.close_price = close_price;
            }

            public float getOpen_price() {
                return open_price;
            }

            public void setOpen_price(float open_price) {
                this.open_price = open_price;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public float getSwing() {
                return swing;
            }

            public void setSwing(float swing) {
                this.swing = swing;
            }

            public float getMax_price() {
                return max_price;
            }

            public void setMax_price(float max_price) {
                this.max_price = max_price;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public float getDiff_rate() {
                return diff_rate;
            }

            public void setDiff_rate(float diff_rate) {
                this.diff_rate = diff_rate;
            }

            @NonNull
            @Override
            public String toString() {
                return "\ncode=" + code +
                        "\nmin_price=" + min_price +
                        "\ntrade_num=" + trade_num +
                        "\ndiff_money=" + diff_money +
                        "\nclose_price=" + close_price +
                        "\nopen_price=" + open_price +
                        "\nmax_price=" + max_price +
                        "\ndate=" + date +
                        "\ndiff_rate=" + diff_rate +
                        "\nswing=" + swing;
            }
        }
    }
}
