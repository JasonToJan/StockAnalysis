package jason.jan.stockanalysis.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import jason.jan.stockanalysis.dao.StockDao;
import jason.jan.stockanalysis.entity.Stock;
import jason.jan.stockanalysis.utils.Constants;

@Database(entities = {Stock.class}, version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final Object mLock = new Object();

    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        synchronized (mLock){
            if(sInstance == null){
                sInstance = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class, Constants.DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build();
            }
            return sInstance;
        }
    }

    public abstract StockDao stockDao();

}