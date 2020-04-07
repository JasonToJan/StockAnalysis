package jason.jan.stockanalysis.mvvm.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;
import jason.jan.stockanalysis.R;
import jason.jan.stockanalysis.base.BaseFragment;
import jason.jan.stockanalysis.data.DataSource;
import jason.jan.stockanalysis.databinding.FragmentRootBinding;
import jason.jan.stockanalysis.mvvm.ui.adapter.MyFragment1Adapter;
import jason.jan.stockanalysis.mvvm.ui.adapter.MyFragment2Adapter;
import jason.jan.stockanalysis.mvvm.viewmodel.RootFViewModel;
import jason.jan.stockanalysis.utils.LogUtils;
import jason.jan.stockanalysis.utils.TabLayoutMediator;

/**
 * Description: 增加股票记录碎片
 * *
 * Creator: Wang
 * Date: 2020/4/2 20:19
 */
public class RootFragment extends BaseFragment<RootFViewModel, FragmentRootBinding> {

    private static final String TAG = "RootFragment";

    public static RootFragment newInstance() {

        Bundle args = new Bundle();

        RootFragment fragment = new RootFragment();
        fragment.setArguments(args);
        return fragment;
    }

    TabLayoutMediator tabLayoutMediator;

    public static final boolean IS_USE_VIEWPAGER2 = false;

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        LogUtils.d(TAG, "可见了哦");
    }

    @Override
    public void onResume() {
        super.onResume();

        if (DataSource.getInstance().getCurrentPosition() >= 0) {
            binding.frViewpager2.setCurrentItem(DataSource.getInstance().getCurrentPosition());
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_root;
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        LogUtils.d("go to this...");
        initView();
        initData();
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tabLayoutMediator != null) {
            tabLayoutMediator.detach();
        }
    }

    private void initView() {
        if (IS_USE_VIEWPAGER2) {
            binding.frViewpager2.setVisibility(View.VISIBLE);
            binding.frViewpager.setVisibility(View.GONE);
            binding.frViewpager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
            binding.frViewpager2.setAdapter(new MyFragment2Adapter(_mActivity));
            binding.frViewpager2.setOffscreenPageLimit(5);

            //关联TabLayout
            tabLayoutMediator = new TabLayoutMediator(binding.frTablayout, binding.frViewpager2,
                    true, this::doInConfigureTab);
            tabLayoutMediator.attach();

        } else {
            binding.frViewpager.setVisibility(View.VISIBLE);
            binding.frViewpager2.setVisibility(View.GONE);
            binding.frViewpager.setOffscreenPageLimit(6);
            binding.frViewpager.setAdapter(new MyFragment1Adapter(_mActivity));
            binding.frTablayout.setupWithViewPager(binding.frViewpager);
        }

    }

    private void initData() {

    }

    /**
     * Tab切换时处理逻辑
     *
     * @param tab
     * @param position
     */
    private void doInConfigureTab(@NonNull TabLayout.Tab tab, int position) {
        switch (position) {
            case 0:
                tab.setText("新增");
                break;

            case 1:
                tab.setText("分析");
                break;

            case 2:
                tab.setText("分析2");
                break;

            case 3:
                tab.setText("查询");
                break;

            case 4:
                tab.setText("测试");
                break;

            case 5:
                tab.setText("数据");
                break;
        }
    }

    @Override
    public void onClick(View v) {

    }
}
