package jason.jan.stockanalysis.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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

    @Delete
    void delete(Stock stock);

    @Query("DELETE FROM Stock")
    void deleteAll();
}
