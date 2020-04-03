package jason.jan.stockanalysis.mvvm.ui.activity;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import jason.jan.stockanalysis.R;
import jason.jan.stockanalysis.base.BaseActivity;
import jason.jan.stockanalysis.databinding.ActivityMainBinding;
import jason.jan.stockanalysis.mvvm.ui.adapter.MyFragmentAdapter;
import jason.jan.stockanalysis.mvvm.viewmodel.HomeViewModel;
import jason.jan.stockanalysis.utils.LogUtils;
import jason.jan.stockanalysis.utils.TabLayoutMediator;

public class MainActivity extends BaseActivity<HomeViewModel, ActivityMainBinding> {

    TabLayoutMediator tabLayoutMediator;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void processLogic() {
        LogUtils.d("go to this...");
        initView();
        initData();
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tabLayoutMediator != null) {
            tabLayoutMediator.detach();
        }
    }

    private void initView(){
        binding.amViewpager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.amViewpager2.setAdapter(new MyFragmentAdapter(this));
        binding.amViewpager2.setOffscreenPageLimit(3);

        //关联TabLayout
        tabLayoutMediator = new TabLayoutMediator(binding.amTablayout, binding.amViewpager2,
                true, this::doInConfigureTab);
        tabLayoutMediator.attach();
    }

    private void initData(){

    }

    /**
     * Tab切换时处理逻辑
     * @param tab
     * @param position
     */
    private void doInConfigureTab(@NonNull TabLayout.Tab tab, int position) {
        switch (position) {
            case 0 :
                tab.setText("新增");
                break;

            case 1 :
                tab.setText("分析");
                break;

            case 2 :
                tab.setText("查询");
                break;

            case 3:
                tab.setText("测试");
                break;
        }
    }

    @Override
    public void onClick(View v) {

    }
}
