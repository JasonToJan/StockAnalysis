package jason.jan.stockanalysis.entity;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * desc: 股票实体
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2020/4/2 12:49
 **/
@Entity(primaryKeys = {"code", "currentTime"})
public class Stock {

    @NonNull
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

    public Stock() {
    }

    public Stock(String name, String date, float maxPrice, float minPrice, float openPrice, float closePrice, float volume, int isForecast) {
        this.name = name;
        this.date = date;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.volume = volume;
        this.isForecast = isForecast;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock that = (Stock) o;
        return (currentTime + code).equals(that.currentTime + that.code);
    }

    @Override
    public int hashCode() {

        return hashCode(currentTime + code);
    }

    @NonNull
    @Override
    public String toString() {
        return "\nname = " + name
                + "\ndate = " + date
                + "\nname = " + name
                + "\nmaxPrice = " + maxPrice
                + "\nminPrice = " + minPrice
                + "\nopenPrice = " + openPrice
                + "\nclosePrice = " + closePrice
                + "\ncurrentTime = " + currentTime
                + "\nvolume = " + volume;
    }

    public static int hashCode(Object... a) {
        if (a == null)
            return 0;

        int result = 1;

        for (Object element : a)
            result = 31 * result + (element == null ? 0 : element.hashCode());

        return result;
    }
}
