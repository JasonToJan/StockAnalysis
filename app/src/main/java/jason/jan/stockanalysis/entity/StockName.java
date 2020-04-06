package jason.jan.stockanalysis.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2020/4/5 8:08
 */
@Entity(indices = {@Index(value = {"id"}, unique = true)})
public class StockName {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String code;

    public String name;

    public StockName() {
    }

    public StockName(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
