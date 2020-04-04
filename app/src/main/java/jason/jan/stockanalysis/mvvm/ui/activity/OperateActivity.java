package jason.jan.stockanalysis.mvvm.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import jason.jan.stockanalysis.R;
import jason.jan.stockanalysis.base.BaseActivity;
import jason.jan.stockanalysis.databinding.ActivityBulkBinding;
import jason.jan.stockanalysis.mvvm.ui.fragment.BulkFragment;
import jason.jan.stockanalysis.mvvm.ui.fragment.UpdateFragment;
import jason.jan.stockanalysis.mvvm.viewmodel.HomeViewModel;

/**
 * Description: 操作页面，可以删除，可以更新
 * *
 * Creator: Wang
 * Date: 2020/4/4 12:15
 */
public class OperateActivity extends BaseActivity<HomeViewModel, ActivityBulkBinding> {

    public static final String OPERATE_CODE = "operate_code";//操作代码
    public static final int OPERATE_BULK = 0;
    public static final int OPERATE_UPDATE = 1;


    /**
     * 跳转到操作页面
     * @param activity
     */
    public static void jumpToOperateActivity(Activity activity,int code){

        Intent intent = new Intent(activity, OperateActivity.class);
        intent.putExtra(OPERATE_CODE,code);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.h_fragment_enter,R.anim.h_fragment_exit);

    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_bulk;
    }

    @Override
    protected void processLogic() {

        loadTargetFragment();

    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
        overridePendingTransition(R.anim.h_fragment_enter,R.anim.h_fragment_exit);
    }

    private void loadTargetFragment(){
        Intent intent = getIntent();
        if (intent == null) return;

        int targetCode = intent.getIntExtra(OPERATE_CODE, OPERATE_BULK);

        switch (targetCode) {

            case OPERATE_BULK :
                loadRootFragment(R.id.ab_frameLayout, BulkFragment.newInstance());
                break;

            case OPERATE_UPDATE:
                loadRootFragment(R.id.ab_frameLayout, UpdateFragment.newInstance());
                break;
        }
    }
}
