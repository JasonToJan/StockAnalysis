package jason.jan.stockanalysis.entity;

/**
 * desc:
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2020/4/7 13:21
 **/
public class Condition2 {

    float proximity;//相似度

    float maxVolume;//最近最大成交量

    Stock todayStock;//今天

    Stock yesStock;//昨天

    Stock beforeYesStock;//前天

    Stock before2YesStock;//大前天

    public Condition2() {
    }

    public Condition2(float proximity, Stock todayStock, Stock yesStock, Stock beforeYesStock) {
        this.proximity = proximity;
        this.todayStock = todayStock;
        this.yesStock = yesStock;
        this.beforeYesStock = beforeYesStock;
    }

    public Stock getBefore2YesStock() {
        return before2YesStock;
    }

    public void setBefore2YesStock(Stock before2YesStock) {
        this.before2YesStock = before2YesStock;
    }

    public float getMaxVolume() {
        return maxVolume;
    }

    public void setMaxVolume(float maxVolume) {
        this.maxVolume = maxVolume;
    }

    public float getProximity() {
        return proximity;
    }

    public void setProximity(float proximity) {
        this.proximity = proximity;
    }

    public Stock getTodayStock() {
        return todayStock;
    }

    public void setTodayStock(Stock todayStock) {
        this.todayStock = todayStock;
    }

    public Stock getYesStock() {
        return yesStock;
    }

    public void setYesStock(Stock yesStock) {
        this.yesStock = yesStock;
    }

    public Stock getBeforeYesStock() {
        return beforeYesStock;
    }

    public void setBeforeYesStock(Stock beforeYesStock) {
        this.beforeYesStock = beforeYesStock;
    }
}
