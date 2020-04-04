package jason.jan.stockanalysis.mvvm.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

import jason.jan.stockanalysis.R;
import jason.jan.stockanalysis.base.BaseFragment;
import jason.jan.stockanalysis.data.DataSource;
import jason.jan.stockanalysis.databinding.FragmentAddBinding;
import jason.jan.stockanalysis.databinding.FragmentUpdateBinding;
import jason.jan.stockanalysis.entity.Stock;
import jason.jan.stockanalysis.mvvm.viewmodel.AddFViewModel;
import jason.jan.stockanalysis.mvvm.viewmodel.UpdateFViewModel;
import jason.jan.stockanalysis.utils.CommonUtils;
import jason.jan.stockanalysis.utils.LogUtils;
import jason.jan.stockanalysis.utils.ToastUtils;

/**
 * Description: 更新股票记录碎片
 * *
 * Creator: Wang
 * Date: 2020/4/2 20:19
 */
public class UpdateFragment extends BaseFragment<UpdateFViewModel, FragmentUpdateBinding> {

    public static UpdateFragment newInstance() {

        Bundle args = new Bundle();

        UpdateFragment fragment = new UpdateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private SimpleDateFormat formatToTarget = new SimpleDateFormat("yyyy-mm-dd",Locale.getDefault());

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_update;
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        initDefuultData();
    }

    @Override
    protected void setListener() {
        binding.fuOkBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fu_ok_btn:
                updateToDatabase();
                break;
        }
    }

    /**
     * 设置默认数据，以免每次都手动输入
     *
     */
    private void initDefuultData(){
        Stock stock = DataSource.getInstance().getUpdateStock();
        if (stock == null) return;

        binding.fuCode.setText(stock.getCode()+"");
        binding.fuName.setText(stock.getName());
        binding.fuDate.setText(stock.getDate());
        binding.fuClosePri.setText(stock.getClosePrice()+"");
        binding.fuOpenPri.setText(stock.getOpenPrice()+"");
        binding.fuMaxPri.setText(stock.getMaxPrice()+"");
        binding.fuMinPri.setText(stock.getMinPrice()+"");
        binding.fuVolumeNum.setText(stock.getVolume()+"");
    }

    private void updateToDatabase() {
        if (!CommonUtils.doFirstClick200()) return;

        String code = binding.fuCode.getText().toString().trim();
        String date = binding.fuDate.getText().toString().trim();
        String name = binding.fuName.getText().toString().trim();
        String closePrice = binding.fuClosePri.getText().toString().trim();
        String openPrice = binding.fuOpenPri.getText().toString().trim();
        String minPrice = binding.fuMinPri.getText().toString().trim();
        String maxPrice = binding.fuMaxPri.getText().toString().trim();
        String volume = binding.fuVolumeNum.getText().toString().trim();
        boolean isForecast = binding.fuForecastCb.isChecked();

        if (TextUtils.isEmpty(code)
                || TextUtils.isEmpty(date)
                || TextUtils.isEmpty(openPrice)
                || TextUtils.isEmpty(minPrice)
                || TextUtils.isEmpty(closePrice)
                || TextUtils.isEmpty(maxPrice)
                || TextUtils.isEmpty(volume)
        ) {
            ToastUtils.showToast("请输入正确的股票信息~");
        }

        try {

            Stock stock = DataSource.getInstance().getUpdateStock();
            stock.setId(stock.getId());
            stock.setCode(Integer.parseInt(code));
            stock.setDate(date);
            stock.setName(name);
            stock.setClosePrice(Float.parseFloat(closePrice));
            stock.setOpenPrice(Float.parseFloat(openPrice));
            stock.setMinPrice(Float.parseFloat(minPrice));
            stock.setMaxPrice(Float.parseFloat(maxPrice));
            stock.setIsForecast(isForecast ? 1 : 0);
            stock.setCurrentTime(TimeUtils.string2Millis(date,formatToTarget));
            stock.setVolume(Float.parseFloat(volume));

            mViewModel.updateStock(stock);

            ToastUtils.showToast("更新成功^_^");

        } catch (Throwable e) {
            LogUtils.d("Error", "##" + e.getMessage());
            ToastUtils.showToast("请输入正确的股票信息~");
        }

    }
}
