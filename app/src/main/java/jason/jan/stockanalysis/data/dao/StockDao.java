package jason.jan.stockanalysis.data.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import androidx.room.Transaction;
import androidx.room.Update;
import jason.jan.stockanalysis.entity.Stock;

/**
 * desc: 股票数据Dao
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2020/4/2 13:48
 **/
@Dao
public interface StockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Stock stock);

    @Update
    void update(Stock stock);

    @Query("UPDATE Stock SET name = :name , date = :date , currentTime = :currentTime , maxPrice = :maxPrice , minPrice = :minPrice , openPrice = :openPrice , closePrice = :closePrice , volume = :volume , isForecast = :isForecast WHERE id = :stockId")
    void updateStockInfo(long stockId, String name, String date, long currentTime, float maxPrice, float minPrice, float openPrice, float closePrice, float volume, int isForecast);

    @Delete
    void delete(Stock stock);

    @Query("DELETE FROM Stock")
    void deleteAll();

    @Query("DELETE FROM Stock WHERE id IN (:ids)")
    void deleteSome(long[] ids);

    @Transaction
    @Query("SELECT * FROM Stock WHERE code = :code AND date = :date AND isForecast = :isForecast")
    LiveData<List<Stock>> getSearchList(int code, String date, int isForecast);

    @Transaction
    @Query("SELECT * FROM Stock WHERE date = :date AND isForecast = :isForecast")
    LiveData<List<Stock>> getSearchListNoCode(String date, int isForecast);

    @Transaction
    @Query("SELECT * FROM Stock WHERE code = :code AND isForecast = :isForecast")
    LiveData<List<Stock>> getSearchListNoDate(int code, int isForecast);

    @Transaction
    @Query("SELECT * FROM Stock WHERE code = :code AND date = :date")
    LiveData<List<Stock>> getSearchListNoForecast(int code, String date);

    @Transaction
    @Query("SELECT * FROM Stock WHERE code = :code")
    LiveData<List<Stock>> getSearchListOnlyCode(int code);

    @Transaction
    @Query("SELECT * FROM Stock WHERE isForecast = :isForecast")
    LiveData<List<Stock>> getSearchListOnlyForecast(int isForecast);

    @Transaction
    @Query("SELECT * FROM Stock WHERE date = :date")
    LiveData<List<Stock>> getSearchListOnlyDate(String date);

    @Transaction
    @Query("SELECT * FROM Stock")
    LiveData<List<Stock>> getSearchListNoParams();

    @Query("SELECT count(*) FROM Stock")
    LiveData<Integer> getCount();
}
