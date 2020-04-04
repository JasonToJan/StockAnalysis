package jason.jan.stockanalysis.mvvm.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.Nullable;
import jason.jan.stockanalysis.R;
import jason.jan.stockanalysis.entity.BulkStock;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2020/4/4 12:15
 */
public class BulkAdatper extends BaseQuickAdapter<BulkStock, BaseViewHolder> {

    public BulkAdatper(@Nullable List<BulkStock> data) {
        super(R.layout.item_bulk, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BulkStock item) {
        helper.setText(R.id.ib_date_tv, item.getDate())
                .setText(R.id.ib_name_tv, item.getCode() + "  " + item.getName())
                .setText(R.id.ib_current_pri_tv, "当前价格：" + item.getClosePrice() + "")
                .setText(R.id.ib_open_pri_tv, "开盘价：" + item.getOpenPrice() + "")
                .setText(R.id.ib_close_pri_tv, "收盘价：" + item.getClosePrice() + "")
                .setText(R.id.ib_min_pri_tv, "最低价：" + item.getMinPrice() + "")
                .setText(R.id.ib_max_pri_tv, "最高价：" + item.getMaxPrice() + "")
                .setText(R.id.ib_volume_tv, "成交量：" + item.getVolume() + "");

        helper.setChecked(R.id.ib_choose_cb, item.isChoose());
    }
}
