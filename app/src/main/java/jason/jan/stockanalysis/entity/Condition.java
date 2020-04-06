package jason.jan.stockanalysis.entity;

import androidx.annotation.NonNull;

/**
 * Description: 分析条件实体
 * *
 * Creator: Wang
 * Date: 2020/4/6 11:07
 */
public class Condition {

    private int day1 = 0; // 0表示今天，1表示昨天，2表示前天

    private int attr1 = 0; //0表示开盘，1表示收盘，2表示最高价，3表示最低价，4表示成交量

    private int day2 = 0; // 0表示，1表示昨天，2表示前天

    private int attr2 = 0; //0表示开盘，1表示收盘，2表示最高价，3表示最低价，4表示成交量

    private int feature = 0; //0表示低，1表示高

    private float featureValue = 0; //表示特征值，百分比表示

    private boolean onlyOneAttr = false; // 是否只有一个属性，只比较成交量，这样只用day1，attr1就行了

    private boolean hasTwoValue = false; // 是否有两个特征值

    private float featureValue2 = 0 ;//表示第二个特征值

    public Condition() {
    }

    public Condition(int day1, int attr1, int day2, int attr2, int feature, float featureValue) {
        this.day1 = day1;
        this.attr1 = attr1;
        this.day2 = day2;
        this.attr2 = attr2;
        this.feature = feature;
        this.featureValue = featureValue;
    }

    @NonNull
    @Override
    public String toString() {
        return "\nday1=" + day1
                + "\nattr1=" + attr1
                + "\nday2=" + day2
                + "\nattr2=" + attr2
                + "\nfeature=" + feature
                + "\nfeatureValue=" + featureValue;
    }

    public boolean isHasTwoValue() {
        return hasTwoValue;
    }

    public void setHasTwoValue(boolean hasTwoValue) {
        this.hasTwoValue = hasTwoValue;
    }

    public float getFeatureValue2() {
        return featureValue2;
    }

    public void setFeatureValue2(float featureValue2) {
        this.featureValue2 = featureValue2;
    }

    public boolean isOnlyOneAttr() {
        return onlyOneAttr;
    }

    public void setOnlyOneAttr(boolean onlyOneAttr) {
        this.onlyOneAttr = onlyOneAttr;
    }

    public int getDay1() {
        return day1;
    }

    public void setDay1(int day1) {
        this.day1 = day1;
    }

    public int getAttr1() {
        return attr1;
    }

    public void setAttr1(int attr1) {
        this.attr1 = attr1;
    }

    public int getDay2() {
        return day2;
    }

    public void setDay2(int day2) {
        this.day2 = day2;
    }

    public int getAttr2() {
        return attr2;
    }

    public void setAttr2(int attr2) {
        this.attr2 = attr2;
    }

    public int getFeature() {
        return feature;
    }

    public void setFeature(int feature) {
        this.feature = feature;
    }

    public float getFeatureValue() {
        return featureValue;
    }

    public void setFeatureValue(float featureValue) {
        this.featureValue = featureValue;
    }
}
