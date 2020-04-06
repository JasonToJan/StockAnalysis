package jason.jan.stockanalysis.mvvm.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.Observer;
import jason.jan.stockanalysis.R;
import jason.jan.stockanalysis.base.BaseFragment;
import jason.jan.stockanalysis.databinding.FragmentTestBinding;
import jason.jan.stockanalysis.entity.HttpStockResponse;
import jason.jan.stockanalysis.entity.Resource;
import jason.jan.stockanalysis.entity.Stock;
import jason.jan.stockanalysis.mvvm.viewmodel.TestFViewModel;
import jason.jan.stockanalysis.utils.CommonUtils;
import jason.jan.stockanalysis.utils.DataUtils;
import jason.jan.stockanalysis.utils.DialogUtils;
import jason.jan.stockanalysis.utils.LogUtils;
import jason.jan.stockanalysis.utils.ToastUtils;

/**
 * Description: 增加股票记录碎片
 * *
 * Creator: Wang
 * Date: 2020/4/2 20:19
 */
public class TestFragment extends BaseFragment<TestFViewModel, FragmentTestBinding> {

    private static final String TAG = "TestFragment";
    private static StringBuilder sb = new StringBuilder();

    public static TestFragment newInstance() {

        Bundle args = new Bundle();

        TestFragment fragment = new TestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_test;
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    @Override
    protected void setListener() {
        binding.ftTestBtn.setOnClickListener(this);
        binding.ftDateBeginTv.setOnClickListener(this);
        binding.ftDateEndTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ft_test_btn:
                testApi();
                break;

            case R.id.ft_date_begin_tv:
                DialogUtils.chooseDate(_mActivity, date -> binding.ftDateBeginTv.setText(date));
                break;

            case R.id.ft_date_end_tv:
                DialogUtils.chooseDate(_mActivity, date -> binding.ftDateEndTv.setText(date));
                break;
        }
    }

    private void testApi() {
        if (!CommonUtils.doFirstClick200()) return;

        String code = binding.ftCodeEt.getText().toString().trim();
        String begin = binding.ftDateBeginTv.getText().toString().trim();
        String end = binding.ftDateEndTv.getText().toString().trim();

        if (!valided(code, begin, end)) {
            ToastUtils.showToast("请输入有效查询信息^_^");
            return;
        }

        LogUtils.d(TAG, "开始请求了：" + code + " begin=" + begin + " end=" + end);
        mViewModel.requestApi(code, begin, end).observe(this, httpStockResponseResource -> {

            HttpStockResponse.ShowapiResBodyBean response = httpStockResponseResource.data;
            if (response == null) return;

            List<HttpStockResponse.ShowapiResBodyBean.ListBean> list = response.getList();
            if (list == null || list.size() == 0) return;

            sb.setLength(0);
            for (int i = 0; i < list.size(); i++) {
                sb.append("\n\n" + list.get(i).toString());
            }

            LogUtils.d(TAG, "成功拿到API数据了");

            binding.ftTxt.setText(sb.toString());

            mViewModel.queryStockName(list.get(0).getCode()).observe(this, new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    LogUtils.d(TAG, "查询到名称为：" + s);
                    //转换成Stock类，然后插入数据库
                    ArrayList<Stock> myStocks = DataUtils.convertToArrayStock(list, s);
                    mViewModel.insertManyAsync(myStocks);
                }
            });

        });
    }

    private boolean valided(String code, String begin, String end) {
        if (TextUtils.isEmpty(code)) {
            return false;
        }

        if (TextUtils.isEmpty(begin)) {
            return false;
        }

        if (TextUtils.isEmpty(end)) {
            return false;
        }

        return true;
    }
}
