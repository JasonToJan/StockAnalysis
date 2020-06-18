package jason.jan.stockanalysis.mvvm.ui.adapter;

import android.graphics.Color;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.Collection;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import jason.jan.stockanalysis.MyApplication;
import jason.jan.stockanalysis.R;
import jason.jan.stockanalysis.entity.AnalysisStock;
import jason.jan.stockanalysis.entity.Stock;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2020/4/6 10:29
 */
public class AnalysisResultAdatper extends BaseQuickAdapter<AnalysisStock, BaseViewHolder> {

    int type;

    /**
     * 获取概率回调
     */
    public interface GetProbilityCallback {

        void getUpPro(float up0_2, float up2_4, float up4_6, float up6_8, float up8_10);

        void getDownPro(float down0_2, float down2_4, float down4_6, float down6_8, float down8_10);
    }


    public AnalysisResultAdatper(@Nullable List<AnalysisStock> data) {
        super(R.layout.item_analysis_result, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AnalysisStock item) {
        helper.setText(R.id.iar_date_tv, item.getDate())
                .setText(R.id.iar_name_tv, item.getCode() + "  " + item.getName())
                .setText(R.id.iar_current_pri_tv, "当前价格：" + item.getClosePrice() + "")
                .setText(R.id.iar_open_pri_tv, "开盘价：" + item.getOpenPrice() + "")
                .setText(R.id.iar_close_pri_tv, "收盘价：" + item.getClosePrice() + "")
                .setText(R.id.iar_min_pri_tv, "最低价：" + item.getMinPrice() + "")
                .setText(R.id.iar_max_pri_tv, "最高价：" + item.getMaxPrice() + "")
                .setText(R.id.iar_volume_tv, "成交量：" + item.getVolume() + "");

        helper.setVisible(R.id.iar_forecast_tv,false);
        if (type == 1) {
            helper.setVisible(R.id.iar_forecast_tv, item.getIsForecast() == 1);
        } else if (type >= 2 && item.getNextStock() != null) {
            helper.setVisible(R.id.iar_forecast_tv,true);
            helper.setText(R.id.iar_forecast_tv,"参考："+item.getNextStock().getCode()+" "+item.getNextStock().getDate());
        }

        if (item.isHasPro()) {
            helper.setVisible(R.id.iar_upPro_tv, true);
            helper.setText(R.id.iar_upPro_tv, "上涨概率为："+item.getUpPro());
        }

        //判断后期开盘
        Stock nextStock = item.getNextStock();
        if (nextStock == null || type >= 2) return;

        CardView cardView = helper.getView(R.id.iar_root_cv);
        cardView.setCardBackgroundColor(Color.WHITE);
        float offset = nextStock.getClosePrice() - item.getClosePrice();
        if (offset > 0) {
            //上升
            float upRatio = 100 * offset / item.getClosePrice();
            if (upRatio < 2) {
                cardView.setCardBackgroundColor(MyApplication.getInstance().getResources().getColor(R.color.red5));
            } else if (upRatio < 4) {
                cardView.setCardBackgroundColor(MyApplication.getInstance().getResources().getColor(R.color.red4));
            } else if (upRatio < 6) {
                cardView.setCardBackgroundColor(MyApplication.getInstance().getResources().getColor(R.color.red3));
            } else if (upRatio < 8) {
                cardView.setCardBackgroundColor(MyApplication.getInstance().getResources().getColor(R.color.red2));
            } else {
                cardView.setCardBackgroundColor(MyApplication.getInstance().getResources().getColor(R.color.red1));
            }

        } else {
            //下降
            float downRatio = -100 * offset / item.getClosePrice();
            if (downRatio < 2) {
                cardView.setCardBackgroundColor(MyApplication.getInstance().getResources().getColor(R.color.green5));
            } else if (downRatio < 4) {
                cardView.setCardBackgroundColor(MyApplication.getInstance().getResources().getColor(R.color.green4));
            } else if (downRatio < 6) {
                cardView.setCardBackgroundColor(MyApplication.getInstance().getResources().getColor(R.color.green3));
            } else if (downRatio < 8) {
                cardView.setCardBackgroundColor(MyApplication.getInstance().getResources().getColor(R.color.green2));
            } else {
                cardView.setCardBackgroundColor(MyApplication.getInstance().getResources().getColor(R.color.green1));
            }
        }

    }

    public void replaceMyData(@NonNull Collection<? extends AnalysisStock> data, GetProbilityCallback callback) {
        replaceData(data);

        float count = 0;
        List<AnalysisStock> resultList = ((List<AnalysisStock>) data);
        Stock nextStock = null;
        AnalysisStock currentStock = null;
        float upNum0_2 = 0, upNum2_4 = 0, upNum4_6 = 0, upNum6_8 = 0, upNum8_10 = 0;
        float downNum0_2 = 0, downNum2_4 = 0, downNum4_6 = 0, downNum6_8 = 0, downNum8_10 = 0;
        for (int i = 0; i < data.size(); i++) {
            nextStock = resultList.get(i).getNextStock();
            currentStock = resultList.get(i);
            if (nextStock != null) {
                count++;

                float offset = nextStock.getClosePrice() - currentStock.getClosePrice();

                if (offset > 0) {
                    //上升
                    float upRatio = 100 * offset / currentStock.getClosePrice();
                    if (upRatio < 2) {
                        upNum0_2++;
                    } else if (upRatio < 4) {
                        upNum2_4++;
                    } else if (upRatio < 6) {
                        upNum4_6++;
                    } else if (upRatio < 8) {
                        upNum6_8++;
                    } else {
                        upNum8_10++;
                    }

                } else {
                    //下降
                    float downRatio = -100 * offset / currentStock.getClosePrice();
                    if (downRatio < 2) {
                        downNum0_2++;
                    } else if (downRatio < 4) {
                        downNum2_4++;
                    } else if (downRatio < 6) {
                        downNum4_6++;
                    } else if (downRatio < 8) {
                        downNum6_8++;
                    } else {
                        downNum8_10++;
                    }
                }
            }
        }

        if (callback != null) {

            callback.getUpPro(100 * upNum0_2 / count, 100 * upNum2_4 / count, 100 * upNum4_6 / count, 100 * upNum6_8 / count, 100 * upNum8_10 / count);
            callback.getDownPro(100 * downNum0_2 / count, 100 * downNum2_4 / count, 100 * downNum4_6 / count, 100 * downNum6_8 / count, 100 * downNum8_10 / count);

        }

    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
