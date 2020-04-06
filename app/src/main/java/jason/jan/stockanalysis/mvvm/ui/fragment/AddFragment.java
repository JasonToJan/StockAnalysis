package jason.jan.stockanalysis.mvvm.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.blankj.utilcode.util.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.lifecycle.Observer;
import jason.jan.stockanalysis.R;
import jason.jan.stockanalysis.base.BaseFragment;
import jason.jan.stockanalysis.databinding.FragmentAddBinding;
import jason.jan.stockanalysis.entity.Stock;
import jason.jan.stockanalysis.mvvm.viewmodel.AddFViewModel;
import jason.jan.stockanalysis.utils.CommonUtils;
import jason.jan.stockanalysis.utils.DialogUtils;
import jason.jan.stockanalysis.utils.LogUtils;
import jason.jan.stockanalysis.utils.ToastUtils;

/**
 * Description: 增加股票记录碎片
 * *
 * Creator: Wang
 * Date: 2020/4/2 20:19
 */
public class AddFragment extends BaseFragment<AddFViewModel, FragmentAddBinding> implements TextWatcher {

    private static final String TAG = "AddFragment";

    public static AddFragment newInstance() {

        Bundle args = new Bundle();

        AddFragment fragment = new AddFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private SimpleDateFormat formatToTarget = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_add;
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        initDefaultData();
    }

    @Override
    protected void setListener() {
        binding.faOkBtn.setOnClickListener(this);
        binding.faDate.setOnClickListener(this);
        binding.faOpenPri.setOnClickListener(this);
        binding.faCode.addTextChangedListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fa_ok_btn:
                addToDatabase();
                break;

            case R.id.fa_date:
                DialogUtils.chooseDate(_mActivity, date -> binding.faDate.setText(date));
                break;

            case R.id.fa_open_pri:

                break;
        }
    }

    /**
     * 设置默认数据，以免每次都手动输入
     */
    private void initDefaultData() {
        binding.faCode.setText("100100");
        binding.faName.setText("测试股票");
        binding.faDate.setText("2020-04-03");
        binding.faClosePri.setText("8.88");
        binding.faOpenPri.setText("7.79");
        binding.faMaxPri.setText("8.99");
        binding.faMinPri.setText("7.72");
        binding.faVolumeNum.setText("10203");
    }

    /**
     * 设置默认数据，以免每次都手动输入
     */
    private void clearData() {
        binding.faCode.setText("");
        binding.faName.setText("");
        binding.faDate.setText("");
        binding.faClosePri.setText("");
        binding.faOpenPri.setText("");
        binding.faMaxPri.setText("");
        binding.faMinPri.setText("");
        binding.faVolumeNum.setText("");
    }

    private void addToDatabase() {
        if (!CommonUtils.doFirstClick200()) return;

        String code = binding.faCode.getText().toString().trim();
        String date = binding.faDate.getText().toString().trim();
        String name = binding.faName.getText().toString().trim();
        String closePrice = binding.faClosePri.getText().toString().trim();
        String openPrice = binding.faOpenPri.getText().toString().trim();
        String minPrice = binding.faMinPri.getText().toString().trim();
        String maxPrice = binding.faMaxPri.getText().toString().trim();
        String volume = binding.faVolumeNum.getText().toString().trim();
        boolean isForecast = binding.faForecastCb.isChecked();

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

            Stock stock = new Stock();
            stock.setCode(code);
            stock.setDate(date);
            stock.setName(name);
            stock.setClosePrice(Float.parseFloat(closePrice));
            stock.setOpenPrice(Float.parseFloat(openPrice));
            stock.setMinPrice(Float.parseFloat(minPrice));
            stock.setMaxPrice(Float.parseFloat(maxPrice));
            stock.setIsForecast(isForecast ? 1 : 0);
            stock.setCurrentTime(TimeUtils.string2Millis(date, formatToTarget));
            stock.setVolume(Float.parseFloat(volume));

            mViewModel.addStock(stock);

            clearData();

        } catch (Throwable e) {
            LogUtils.d("Error", "##" + e.getMessage());
            ToastUtils.showToast("请输入正确的股票信息~");
        }

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!TextUtils.isEmpty(charSequence) && charSequence.length() == 6) {
            mViewModel.queryStockName(charSequence.toString()).observe(this, new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    binding.faName.setText(s);
                }
            });
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
