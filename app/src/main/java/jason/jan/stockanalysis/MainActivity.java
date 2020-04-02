package jason.jan.stockanalysis;

import android.os.Bundle;
import android.view.WindowManager;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;
import jason.jan.stockanalysis.databinding.ActivityMainBinding;
import jason.jan.stockanalysis.ui.adapter.MyFragmentAdapter;
import jason.jan.stockanalysis.utils.TabLayoutMediator;
import me.yokeyword.fragmentation.SupportActivity;

public class MainActivity extends SupportActivity {

    ActivityMainBinding mBinding;
    TabLayoutMediator tabLayoutMediator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tabLayoutMediator != null) {
            tabLayoutMediator.detach();
        }
    }

    private void initView(){
        mBinding.amViewpager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        mBinding.amViewpager2.setAdapter(new MyFragmentAdapter(this));
        mBinding.amViewpager2.setOffscreenPageLimit(3);

        //关联TabLayout
        tabLayoutMediator = new TabLayoutMediator(mBinding.amTablayout, mBinding.amViewpager2,
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
        }
    }

}
