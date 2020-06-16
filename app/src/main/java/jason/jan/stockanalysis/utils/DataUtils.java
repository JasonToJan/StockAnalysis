package jason.jan.stockanalysis.utils;

import android.util.ArrayMap;

import com.blankj.utilcode.util.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import jason.jan.stockanalysis.data.http.Api;
import jason.jan.stockanalysis.entity.AnalysisStock;
import jason.jan.stockanalysis.entity.BulkStock;
import jason.jan.stockanalysis.entity.HttpStockResponse;
import jason.jan.stockanalysis.entity.Stock;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2020/4/4 12:48
 */
public class DataUtils {

    private static final String TAG = "DataUtils";

    private static SimpleDateFormat formatToTarget = new SimpleDateFormat("yyyy-MM-dd");

    private static SimpleDateFormat requestFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    public static final String[] upOrDown = new String[]{"高","低","..."};

    public static final String[] volumesUpOrDown = new String[]{"以上","以下","..."};

    public static final String[] stockDay = new String[]{"今天","昨天","前天","某一天"};

    public static final String[] stockField = new String[]{"开盘价","收盘价","最高价","最低价","成交量","属性值"};

    /**
     * 目标股票转换为分析结果实体
     * @param currentStock
     * @param nextStock
     * @return
     */
    public static AnalysisStock convertByStock(Stock currentStock,Stock nextStock){

        AnalysisStock analysisStock = new AnalysisStock();
        analysisStock.setCode(currentStock.getCode());
        analysisStock.setName(currentStock.getName());
        analysisStock.setClosePrice(currentStock.getClosePrice());
        analysisStock.setCurrentTime(currentStock.getCurrentTime());
        analysisStock.setIsForecast(currentStock.getIsForecast());
        analysisStock.setDate(currentStock.getDate());
        analysisStock.setMaxPrice(currentStock.getMaxPrice());
        analysisStock.setMinPrice(currentStock.getMinPrice());
        analysisStock.setOpenPrice(currentStock.getOpenPrice());
        analysisStock.setVolume(currentStock.getVolume());
        analysisStock.setNextStock(nextStock);

        return analysisStock;
    }

    public static List<BulkStock> convertStockList(List<Stock> list) {

        ArrayList<BulkStock> listTarget = new ArrayList();
        if (list == null) return listTarget;

        for (Stock stock : list) {
            BulkStock bulkStock = new BulkStock();

            bulkStock.setCode(stock.getCode());
            bulkStock.setClosePrice(stock.getClosePrice());
            bulkStock.setDate(stock.getDate());
            bulkStock.setName(stock.getName());
            bulkStock.setOpenPrice(stock.getOpenPrice());
            bulkStock.setMinPrice(stock.getMinPrice());
            bulkStock.setMaxPrice(stock.getMaxPrice());
            bulkStock.setVolume(stock.getVolume());

            listTarget.add(bulkStock);
        }

        return listTarget;
    }

    public static Stock[] convertbyHttpStock(List<HttpStockResponse.ShowapiResBodyBean.ListBean> httpList) {
        if (httpList == null || httpList.size() == 0) return null;

        int size = httpList.size();
        Stock[] stocks = new Stock[size];
        int i = 0;
        for (HttpStockResponse.ShowapiResBodyBean.ListBean bean : httpList) {

            Stock stock = new Stock();
            stock.setCode(bean.getCode());
            stock.setDate(bean.getDate());
            stock.setName("");
            stock.setClosePrice(bean.getClose_price());
            stock.setOpenPrice(bean.getOpen_price());
            stock.setMinPrice(bean.getMin_price());
            stock.setMaxPrice(bean.getMax_price());
            stock.setIsForecast(0);
            stock.setCurrentTime(TimeUtils.string2Millis(bean.getDate(), formatToTarget));
            stock.setVolume(bean.getTrade_num());

            stocks[i] = stock;
            i++;
        }

        return stocks;
    }

    public static ArrayList<Stock> convertToArrayStock(List<HttpStockResponse.ShowapiResBodyBean.ListBean> httpList, String name) {
        if (httpList == null || httpList.size() == 0) return null;

        int size = httpList.size();
        ArrayList<Stock> result = new ArrayList<>(size);
        formatToTarget.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        for (HttpStockResponse.ShowapiResBodyBean.ListBean bean : httpList) {

            Stock stock = new Stock();
            stock.setCode(bean.getCode());
            stock.setDate(bean.getDate());
            stock.setName(name);
            stock.setClosePrice(bean.getClose_price());
            stock.setOpenPrice(bean.getOpen_price());
            stock.setMinPrice(bean.getMin_price());
            stock.setMaxPrice(bean.getMax_price());
            stock.setIsForecast(0);

            long currentTime = MyTimeUtils.dateToStamp(bean.getDate(),formatToTarget);
            stock.setCurrentTime(currentTime);
            stock.setVolume(bean.getTrade_num());

            result.add(stock);
        }

        return result;
    }

    /**
     * 易源接口
     * 封装的接口参数
     *
     * @param code
     * @param begin
     * @param end
     * @return
     */
    public static Map<String, String> getRequestMapParams(String code, String begin, String end) {

        Map<String, String> requestMap = new ArrayMap<>(6);

        requestMap.clear();
        requestMap.put(Api.YI_APPID_STR, Api.YI_APPID);
        requestMap.put(Api.YI_SIGN_STR, Api.YI_SIGN);
        requestMap.put(Api.YI_TIMESTAMP_STR, requestFormat.format(new Date()));
        requestMap.put(Api.YI_CODE, code);
        requestMap.put(Api.YI_BEGAIN, begin);
        requestMap.put(Api.YI_END, end);

        return requestMap;
    }
}
