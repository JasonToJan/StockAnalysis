package jason.jan.stockanalysis.utils;

import java.util.ArrayList;
import java.util.List;

import jason.jan.stockanalysis.entity.BulkStock;
import jason.jan.stockanalysis.entity.Stock;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2020/4/4 12:48
 */
public class DataUtils {

    public static List<BulkStock> convertStockList(List<Stock> list){

        ArrayList<BulkStock> listTarget = new ArrayList();
        if (list == null) return listTarget;

        for (Stock stock : list) {
            BulkStock bulkStock = new BulkStock();

            bulkStock.setId(stock.getId());
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
}
