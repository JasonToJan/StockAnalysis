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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMany(Stock... stock);

    @Update
    void update(Stock stock);

    @Query("UPDATE Stock SET name = :name , date = :date , currentTime = :currentTime , maxPrice = :maxPrice , minPrice = :minPrice , openPrice = :openPrice , closePrice = :closePrice , volume = :volume , isForecast = :isForecast WHERE code = :code")
    void updateStockInfo(String code, String name, String date, long currentTime, float maxPrice, float minPrice, float openPrice, float closePrice, float volume, int isForecast);

    @Delete
    void delete(Stock stock);

    @Query("DELETE FROM Stock")
    void deleteAll();

    @Query("DELETE FROM Stock WHERE currentTime >= :currentMonthBegin")
    void deleteCurrentMonth(long currentMonthBegin);

    @Query("DELETE FROM Stock WHERE currentTime IN (:currentTime) AND code = :code")
    void deleteSome(long[] currentTime,String code);

    @Query("SELECT DISTINCT code FROM Stock")
    List<String> getDistinctCode();

    @Query("SELECT max(currentTime) code FROM Stock")
    long getMaxCurrentTime();

    @Query("SELECT * FROM Stock WHERE code = :code ORDER BY currentTime ASC")
    List<Stock> getStocksByCode(String code);

    @Query("SELECT * FROM Stock WHERE currentTime = :currentTime")
    List<Stock> getStocksByCurrentTime(long currentTime);

    @Query("SELECT max(volume) FROM Stock WHERE code = :code AND currentTime > currentTime - :range AND currentTime < currentTime + :range")
    float getMaxVolumeByCode(String code,long range);

    @Query("SELECT max(volume) FROM Stock WHERE code = :code LIMIT :limit Offset :offset")
    float getMaxVolumeByCode(String code, int limit,int offset);

    @Transaction
    @Query("SELECT * FROM Stock WHERE code = :code AND date = :date AND isForecast = :isForecast ORDER BY currentTime DESC")
    LiveData<List<Stock>> getSearchList(String code, String date, int isForecast);

    @Transaction
    @Query("SELECT * FROM Stock WHERE date = :date AND isForecast = :isForecast ORDER BY currentTime DESC")
    LiveData<List<Stock>> getSearchListNoCode(String date, int isForecast);

    @Transaction
    @Query("SELECT * FROM Stock WHERE code = :code AND isForecast = :isForecast ORDER BY currentTime DESC")
    LiveData<List<Stock>> getSearchListNoDate(String code, int isForecast);

    @Transaction
    @Query("SELECT * FROM Stock WHERE code = :code AND date = :date ORDER BY currentTime DESC")
    LiveData<List<Stock>> getSearchListNoForecast(String code, String date);

    @Transaction
    @Query("SELECT * FROM Stock WHERE code = :code ORDER BY currentTime DESC")
    LiveData<List<Stock>> getSearchListOnlyCode(String code);

    @Transaction
    @Query("SELECT * FROM Stock WHERE isForecast = :isForecast ORDER BY currentTime DESC")
    LiveData<List<Stock>> getSearchListOnlyForecast(int isForecast);

    @Transaction
    @Query("SELECT * FROM Stock WHERE date = :date ORDER BY currentTime DESC")
    LiveData<List<Stock>> getSearchListOnlyDate(String date);

    @Transaction
    @Query("SELECT * FROM Stock ORDER BY currentTime DESC")
    LiveData<List<Stock>> getSearchListNoParams();

    @Query("SELECT count(*) FROM Stock")
    LiveData<Integer> getCount();
}
