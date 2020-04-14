package jason.jan.stockanalysis.data;

import java.util.List;

import jason.jan.stockanalysis.entity.AnalysisStock;
import jason.jan.stockanalysis.entity.BulkStock;
import jason.jan.stockanalysis.entity.Condition;
import jason.jan.stockanalysis.entity.Stock;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2020/4/4 12:13
 */
public class DataSource {

    private DataSource() {
    }

    public static DataSource getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final DataSource sInstance = new DataSource();
    }

    /**
     * 批量操作记录集合
     */
    private List<BulkStock> bulkList;
    /**
     * 更新操作记录
     */
    private Stock updateStock;
    /**
     * 记录此时在哪个Type页，保证返回的时候恢复
     */
    private int currentPosition = -1;
    /**
     * 分析列表
     */
    private List<AnalysisStock> analysisList;
    /**
     * 分析列表2
     */
    private List<AnalysisStock> analysisList2;
    /**
     * 明天买
     */
    private List<AnalysisStock> analysisTomorrowBuy;
    /**
     * 明天涨停
     */
    private List<AnalysisStock> analysisTomorrowUp;
    /**
     * 明天跌停
     */
    private List<AnalysisStock> analysisTomorrowDown;
    /**
     * 分析参数
     */
    private List<Condition> analysisParams;
    /**
     * 搜索列表
     */
    private List<Stock> searchList;
    /**
     * 近5日上涨
     */
    private List<AnalysisStock> analysis5DayUp;
    /**
     * 近5日下跌
     */
    private List<AnalysisStock> analysis5DayDown;

    public List<AnalysisStock> getAnalysis5DayUp() {
        return analysis5DayUp;
    }

    public void setAnalysis5DayUp(List<AnalysisStock> analysis5DayUp) {
        this.analysis5DayUp = analysis5DayUp;
    }

    public List<AnalysisStock> getAnalysis5DayDown() {
        return analysis5DayDown;
    }

    public void setAnalysis5DayDown(List<AnalysisStock> analysis5DayDown) {
        this.analysis5DayDown = analysis5DayDown;
    }

    public List<AnalysisStock> getAnalysisTomorrowBuy() {
        return analysisTomorrowBuy;
    }

    public void setAnalysisTomorrowBuy(List<AnalysisStock> analysisTomorrowBuy) {
        this.analysisTomorrowBuy = analysisTomorrowBuy;
    }

    public List<AnalysisStock> getAnalysisTomorrowUp() {
        return analysisTomorrowUp;
    }

    public void setAnalysisTomorrowUp(List<AnalysisStock> analysisTomorrowUp) {
        this.analysisTomorrowUp = analysisTomorrowUp;
    }

    public List<AnalysisStock> getAnalysisTomorrowDown() {
        return analysisTomorrowDown;
    }

    public void setAnalysisTomorrowDown(List<AnalysisStock> analysisTomorrowDown) {
        this.analysisTomorrowDown = analysisTomorrowDown;
    }

    public List<AnalysisStock> getAnalysisList2() {
        return analysisList2;
    }

    public void setAnalysisList2(List<AnalysisStock> analysisList2) {
        this.analysisList2 = analysisList2;
    }

    public List<Stock> getSearchList() {
        return searchList;
    }

    public void setSearchList(List<Stock> searchList) {
        this.searchList = searchList;
    }

    public List<Condition> getAnalysisParams() {
        return analysisParams;
    }

    public void setAnalysisParams(List<Condition> analysisParams) {
        this.analysisParams = analysisParams;
    }

    public List<AnalysisStock> getAnalysisList() {
        return analysisList;
    }

    public void setAnalysisList(List<AnalysisStock> analysisList) {
        this.analysisList = analysisList;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public Stock getUpdateStock() {
        return updateStock;
    }

    public void setUpdateStock(Stock updateStock) {
        this.updateStock = updateStock;
    }

    public List<BulkStock> getBulkList() {
        return bulkList;
    }

    public void setBulkList(List<BulkStock> bulkList) {
        this.bulkList = bulkList;
    }


}
