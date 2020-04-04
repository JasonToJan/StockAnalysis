package jason.jan.stockanalysis.data;

import java.util.List;

import jason.jan.stockanalysis.entity.BulkStock;
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
