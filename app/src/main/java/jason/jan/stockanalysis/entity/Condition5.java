package jason.jan.stockanalysis.entity;

/**
 * desc:
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2020/4/14 8:56
 **/
public class Condition5 {

    private float proximity;//相似度

    private float ratio_25_30;//25日到30日

    private float ratio_20_25;

    private float ratio_15_20;

    private float ratio_10_15;

    private float ratio_5_10;

    private float ratio_0_5;

    private Stock currentDay;

    private Stock tommorrowStock;

    public Condition5() {
    }

    public Condition5(float ratio_25_30, float ratio_20_25, float ratio_15_20, float ratio_10_15, float ratio_5_10, float ratio_0_5) {
        this.ratio_25_30 = ratio_25_30;
        this.ratio_20_25 = ratio_20_25;
        this.ratio_15_20 = ratio_15_20;
        this.ratio_10_15 = ratio_10_15;
        this.ratio_5_10 = ratio_5_10;
        this.ratio_0_5 = ratio_0_5;
    }

    public float getProximity() {
        return proximity;
    }

    public void setProximity(float proximity) {
        this.proximity = proximity;
    }

    public Stock getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(Stock currentDay) {
        this.currentDay = currentDay;
    }

    public Stock getTommorrowStock() {
        return tommorrowStock;
    }

    public void setTommorrowStock(Stock tommorrowStock) {
        this.tommorrowStock = tommorrowStock;
    }

    public float getRatio_25_30() {
        return ratio_25_30;
    }

    public void setRatio_25_30(float ratio_25_30) {
        this.ratio_25_30 = ratio_25_30;
    }

    public float getRatio_20_25() {
        return ratio_20_25;
    }

    public void setRatio_20_25(float ratio_20_25) {
        this.ratio_20_25 = ratio_20_25;
    }

    public float getRatio_15_20() {
        return ratio_15_20;
    }

    public void setRatio_15_20(float ratio_15_20) {
        this.ratio_15_20 = ratio_15_20;
    }

    public float getRatio_10_15() {
        return ratio_10_15;
    }

    public void setRatio_10_15(float ratio_10_15) {
        this.ratio_10_15 = ratio_10_15;
    }

    public float getRatio_5_10() {
        return ratio_5_10;
    }

    public void setRatio_5_10(float ratio_5_10) {
        this.ratio_5_10 = ratio_5_10;
    }

    public float getRatio_0_5() {
        return ratio_0_5;
    }

    public void setRatio_0_5(float ratio_0_5) {
        this.ratio_0_5 = ratio_0_5;
    }
}
