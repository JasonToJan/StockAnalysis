package jason.jan.stockanalysis.mvvm.ui.activity;

import android.view.View;

import jason.jan.stockanalysis.R;
import jason.jan.stockanalysis.base.BaseActivity;
import jason.jan.stockanalysis.databinding.ActivityMainBinding;
import jason.jan.stockanalysis.mvvm.ui.fragment.RootFragment;
import jason.jan.stockanalysis.mvvm.viewmodel.HomeViewModel;

public class MainActivity extends BaseActivity<HomeViewModel, ActivityMainBinding> {

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void processLogic() {
        loadRootFragment(R.id.am_frameLayout, RootFragment.newInstance());
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onClick(View view) {

    }
}
