package jason.jan.stockanalysis.mvvm.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.Observer;

import java.util.List;

import jason.jan.stockanalysis.R;
import jason.jan.stockanalysis.base.BaseFragment;
import jason.jan.stockanalysis.databinding.FragmentQueryBinding;
import jason.jan.stockanalysis.databinding.FragmentTestBinding;
import jason.jan.stockanalysis.entity.BannerBean;
import jason.jan.stockanalysis.entity.Resource;
import jason.jan.stockanalysis.mvvm.viewmodel.QueryFViewModel;
import jason.jan.stockanalysis.utils.CommonUtils;

/**
 * Description: 增加股票记录碎片
 * *
 * Creator: Wang
 * Date: 2020/4/2 20:19
 */
public class TestFragment extends BaseFragment<QueryFViewModel, FragmentTestBinding> {

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ft_test_btn:
                testApi();
                break;
        }
    }

    private void testApi(){
        if (CommonUtils.doFirstClick200()) {
            mViewModel.getBanner().observe(this, listResource -> {

                if (listResource == null) return;
                List<BannerBean> list = listResource.data;
                if (list == null || list.size() == 0) return;

                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < list.size(); i++) {
                    stringBuilder.append("\n"+list.get(i).toString()+"\n");
                }

                binding.ftTxt.setText(stringBuilder.toString());

            });
        }
    }
}
