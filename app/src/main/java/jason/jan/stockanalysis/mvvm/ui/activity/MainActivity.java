package jason.jan.stockanalysis.mvvm.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import jason.jan.stockanalysis.MyApplication;
import jason.jan.stockanalysis.R;
import jason.jan.stockanalysis.base.BaseActivity;
import jason.jan.stockanalysis.databinding.ActivityMainBinding;
import jason.jan.stockanalysis.mvvm.ui.fragment.RootFragment;
import jason.jan.stockanalysis.mvvm.viewmodel.HomeViewModel;
import jason.jan.stockanalysis.utils.LogUtils;
import jason.jan.stockanalysis.view.CustomProgress;

public class MainActivity extends BaseActivity<HomeViewModel, ActivityMainBinding> {

    private static final String TAG = "MainActivity";

    private CustomProgress customProgress;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        LogUtils.d(TAG, "go to this");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        LogUtils.d(TAG, "go to this");
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void processLogic() {
        loadRootFragment(R.id.am_frameLayout, RootFragment.newInstance());
        mViewModel.recordOtherName();

    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onClick(View view) {

    }
}
