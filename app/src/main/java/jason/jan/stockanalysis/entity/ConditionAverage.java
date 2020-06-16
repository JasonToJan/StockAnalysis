package jason.jan.stockanalysis.entity;

/**
 * desc: 均线条件，判断5日均线，10日均线，20日均线很相似的
 * 就判断最近3天吧，和历史所有股票中筛选一下
 * *
 * update record:
 * *
 * created: Jason Jan
 * time:    2020/6/16 19:04
 * contact: jason1211241203@gmail.com
 **/
public class ConditionAverage {

    /**
     * 相似度
     */
    private float proximity;

    /**
     * 五日均线/当前收盘价 的百分比值
     */
    private float fiveAverage1;

    /**
     * 十日均线/当前收盘价 的百分比值
     */
    private float tenAverage1;

    /**
     * 二十日均线/当前收盘价 的百分比值
     */
    private float twentyAverage1;

    private float fiveAverage2;

    private float tenAverage2;

    private float twentyAverage2;

    private float fiveAverage3;

    private float tenAverage3;

    private float twentyAverage3;

    /**
     * 明天的股票信息
     */
    private Stock tommorrowStock;

    public ConditionAverage() {
    }

    public float getProximity() {
        return proximity;
    }

    public void setProximity(float proximity) {
        this.proximity = proximity;
    }

    public float getFiveAverage1() {
        return fiveAverage1;
    }

    public void setFiveAverage1(float fiveAverage1) {
        this.fiveAverage1 = fiveAverage1;
    }

    public float getTenAverage1() {
        return tenAverage1;
    }

    public void setTenAverage1(float tenAverage1) {
        this.tenAverage1 = tenAverage1;
    }

    public float getTwentyAverage1() {
        return twentyAverage1;
    }

    public void setTwentyAverage1(float twentyAverage1) {
        this.twentyAverage1 = twentyAverage1;
    }

    public float getFiveAverage2() {
        return fiveAverage2;
    }

    public void setFiveAverage2(float fiveAverage2) {
        this.fiveAverage2 = fiveAverage2;
    }

    public float getTenAverage2() {
        return tenAverage2;
    }

    public void setTenAverage2(float tenAverage2) {
        this.tenAverage2 = tenAverage2;
    }

    public float getTwentyAverage2() {
        return twentyAverage2;
    }

    public void setTwentyAverage2(float twentyAverage2) {
        this.twentyAverage2 = twentyAverage2;
    }

    public float getFiveAverage3() {
        return fiveAverage3;
    }

    public void setFiveAverage3(float fiveAverage3) {
        this.fiveAverage3 = fiveAverage3;
    }

    public float getTenAverage3() {
        return tenAverage3;
    }

    public void setTenAverage3(float tenAverage3) {
        this.tenAverage3 = tenAverage3;
    }

    public float getTwentyAverage3() {
        return twentyAverage3;
    }

    public void setTwentyAverage3(float twentyAverage3) {
        this.twentyAverage3 = twentyAverage3;
    }

    public Stock getTommorrowStock() {
        return tommorrowStock;
    }

    public void setTommorrowStock(Stock tommorrowStock) {
        this.tommorrowStock = tommorrowStock;
    }
}
