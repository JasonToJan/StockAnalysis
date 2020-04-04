package jason.jan.stockanalysis.entity;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2020/4/3 20:48
 */
public class SearchParms {

    private String date;//日期

    private int code;//股票代号

    private int isForecast;//是否预测

    public SearchParms() {
    }

    public SearchParms(String date, int code, int isForecast) {
        this.date = date;
        this.code = code;
        this.isForecast = isForecast;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getIsForecast() {
        return isForecast;
    }

    public void setIsForecast(int isForecast) {
        this.isForecast = isForecast;
    }
}
