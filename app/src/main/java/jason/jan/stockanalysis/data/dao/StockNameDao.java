package jason.jan.stockanalysis.data.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import jason.jan.stockanalysis.entity.StockName;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2020/4/5 8:07
 */
@Dao
public interface StockNameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(StockName stock);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMany(StockName... stock);

    @Delete
    void delete(StockName stock);

    @Query("DELETE FROM StockName")
    void deleteAll();

    @Query("SELECT name From StockName WHERE code = :code")
    LiveData<String> queryStockName(String code);

    @Query("SELECT * From StockName LIMIT :count OFFSET :offset")
    LiveData<List<StockName>> queryAllStockName(int count, int offset);

    @Query("SELECT * From StockName LIMIT :count")
    List<StockName> queryAllStockNameAsync(int count);

    /**
     * 不包括offset
     * @param count
     * @param offset
     * @return
     */
    @Query("SELECT * From StockName LIMIT :count OFFSET :offset")
    List<StockName> queryAllStockNameAsyncByOffset(int count, int offset);

    @Query("SELECT count(*) From StockName")
    LiveData<Integer> queryCount();
}
