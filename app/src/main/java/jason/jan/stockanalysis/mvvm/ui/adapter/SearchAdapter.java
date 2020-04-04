package jason.jan.stockanalysis.mvvm.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.Nullable;
import jason.jan.stockanalysis.R;
import jason.jan.stockanalysis.entity.Stock;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2020/4/3 21:43
 */
public class SearchAdapter extends BaseQuickAdapter<Stock, BaseViewHolder> {

    public SearchAdapter(@Nullable List<Stock> data) {
        super(R.layout.item_search, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Stock item) {
        helper.setText(R.id.is_date_tv, item.getDate())
                .setText(R.id.is_name_tv, item.getCode() + "  " + item.getName())
                .setText(R.id.is_current_pri_tv, "当前价格：" + item.getClosePrice() + "")
                .setText(R.id.is_open_pri_tv, "开盘价：" + item.getOpenPrice() + "")
                .setText(R.id.is_close_pri_tv, "收盘价：" + item.getClosePrice() + "")
                .setText(R.id.is_min_pri_tv, "最低价：" + item.getMinPrice() + "")
                .setText(R.id.is_max_pri_tv, "最高价：" + item.getMaxPrice() + "")
                .setText(R.id.is_volume_tv, "成交量：" + item.getVolume() + "");
    }
}
