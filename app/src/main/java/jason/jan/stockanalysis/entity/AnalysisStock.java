package jason.jan.stockanalysis.entity;

/**
 * Description: 分析后的实体
 * *
 * Creator: Wang
 * Date: 2020/4/6 17:56
 */
public class AnalysisStock {

    private String code;//股票代码

    private long currentTime;//今日时间戳，0点的即可

    private String name;//股票名称

    private String date;//时间

    private float maxPrice;//今天最高价

    private float minPrice;//今天最低价

    private float openPrice;//今天开盘价

    private float closePrice;//今天收盘价

    private float volume;//今日成交量

    private int isForecast;//是否是预测 0表示不预测 1表示预测 2表示都行

    private Stock nextStock;

    public AnalysisStock() {
    }

    public AnalysisStock(String code,
                         long currentTime,
                         String name,
                         String date,
                         float maxPrice,
                         float minPrice,
                         float openPrice,
                         float closePrice,
                         float volume,
                         int isForecast,
                         Stock nextStock) {
        this.code = code;
        this.currentTime = currentTime;
        this.name = name;
        this.date = date;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.volume = volume;
        this.isForecast = isForecast;
        this.nextStock = nextStock;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(float maxPrice) {
        this.maxPrice = maxPrice;
    }

    public float getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(float minPrice) {
        this.minPrice = minPrice;
    }

    public float getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(float openPrice) {
        this.openPrice = openPrice;
    }

    public float getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(float closePrice) {
        this.closePrice = closePrice;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public int getIsForecast() {
        return isForecast;
    }

    public void setIsForecast(int isForecast) {
        this.isForecast = isForecast;
    }

    public Stock getNextStock() {
        return nextStock;
    }

    public void setNextStock(Stock nextStock) {
        this.nextStock = nextStock;
    }
}
