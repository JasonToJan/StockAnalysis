package jason.jan.stockanalysis.mvvm.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import jason.jan.stockanalysis.R;
import jason.jan.stockanalysis.base.BaseFragment;
import jason.jan.stockanalysis.data.DataSource;
import jason.jan.stockanalysis.databinding.FragmentAnalysis2Binding;
import jason.jan.stockanalysis.entity.AnalysisStock;
import jason.jan.stockanalysis.mvvm.ui.adapter.AnalysisResultAdatper;
import jason.jan.stockanalysis.mvvm.viewmodel.AnalysisF2ViewModel;
import jason.jan.stockanalysis.utils.CommonUtils;
import jason.jan.stockanalysis.utils.DialogUtils;
import jason.jan.stockanalysis.utils.ToastUtils;
import jason.jan.stockanalysis.view.CustomProgress;

/**
 * Description: 增加股票记录碎片
 * *
 * Creator: Wang
 * Date: 2020/4/2 20:19
 */
public class Analysis2Fragment extends BaseFragment<AnalysisF2ViewModel, FragmentAnalysis2Binding> implements AnalysisResultAdatper.GetProbilityCallback {

    private static final String TAG = "QueryFragment";

    public static Analysis2Fragment newInstance() {

        Bundle args = new Bundle();

        Analysis2Fragment fragment = new Analysis2Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    //属性集合------------begin-----------------//

    private int pagePosition;//第几页
    public static final int PAGE_MAX = 50;//一页大概有多少个
    private AnalysisResultAdatper analysisAdapter;
    private ArrayList<AnalysisStock> resultList = new ArrayList<>();//adapter数据集合
    private CustomProgress customProgress;

    //属性集合------------end-------------------//

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_analysis2;
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

        binding.fa2RecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        binding.fa2RecyclerView.setAdapter(analysisAdapter);
        binding.fa2SmartRefreshLayout.setEnableRefresh(true);//是否启用下拉刷新功能
        binding.fa2SmartRefreshLayout.setEnableLoadMore(true);//是否启用上拉加载功能
        binding.fa2SmartRefreshLayout.setEnableAutoLoadMore(true);//是否启用列表惯性滑动到底部时自动加载更多
        binding.fa2SmartRefreshLayout.setEnableNestedScroll(true);//是否启用嵌套滚动
        binding.fa2SmartRefreshLayout.setEnableOverScrollBounce(true);//是否启用越界回弹
        binding.fa2SmartRefreshLayout.setDragRate(0.9f);//显示下拉高度/手指真实下拉高度=阻尼效果
        binding.fa2SmartRefreshLayout.setReboundDuration(300);//回弹动画时长（毫秒）

        binding.fa2SmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pagePosition = 0;
                List<AnalysisStock> listResult = DataSource.getInstance().getAnalysisList2();
                if (listResult == null || listResult.size() == 0) return;

                resultList.clear();
                for (int i = 0; i < listResult.size(); i++) {
                    if (i > PAGE_MAX) break;
                    resultList.add(listResult.get(i));
                }

                if (analysisAdapter != null) {
                    analysisAdapter.replaceMyData(resultList,Analysis2Fragment.this);
                }

                binding.fa2SmartRefreshLayout.finishRefresh(1500);
            }
        });

        binding.fa2SmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            pagePosition++;

            List<AnalysisStock> listResult = DataSource.getInstance().getAnalysisList();
            if (listResult == null || listResult.size() == 0) return;

            int currentSize = analysisAdapter.getData().size();
            for (int i = currentSize; i < listResult.size(); i++) {
                if (i > PAGE_MAX * pagePosition) break;
                resultList.add(listResult.get(i));
            }

            if (analysisAdapter != null) {
                analysisAdapter.replaceMyData(resultList,Analysis2Fragment.this);
            }

            binding.fa2SmartRefreshLayout.finishLoadMore(1500);
        });

    }

    @Override
    protected void setListener() {
        analysisAdapter = new AnalysisResultAdatper(null);

        binding.fa2AnalysisBtn.setOnClickListener(this);
        binding.fa2DateTv.setOnClickListener(this);
    }

    @Override
    public void getUpPro(float up0_2, float up2_4, float up4_6, float up6_8, float up8_10) {

        binding.fa2Up2ProTv.setText(up0_2+"\nup0->2" );
        binding.fa2Up4ProTv.setText(up2_4+"\nup2->4");
        binding.fa2Up6ProTv.setText(up4_6+"\nup4->6");
        binding.fa2Up8ProTv.setText(up6_8+"\nup6->8");
        binding.fa2Up10ProTv.setText(up8_10+"\nup8->10");
        binding.fa2Up010ProTv.setText((up0_2 + up2_4 + up4_6 + up6_8 + up8_10)+"\nup10\n");
    }

    @Override
    public void getDownPro(float down0_2, float down2_4, float down4_6, float down6_8, float down8_10) {

        binding.fa2Down2ProTv.setText("down0->2\n" + down0_2);
        binding.fa2Down4ProTv.setText("down2->4\n" + down2_4);
        binding.fa2Down6ProTv.setText("down4->6\n" + down4_6);
        binding.fa2Down8ProTv.setText("down6->8\n" + down6_8);
        binding.fa2Down10ProTv.setText("down8->10\n" + down8_10);
        binding.fa2Down010ProTv.setText("down10\n" + (down0_2 + down2_4 + down4_6 + down6_8 + down8_10));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fa2_analysis_btn:
                doAnalysis();
                break;

            case R.id.fa2_date_tv:
                DialogUtils.chooseDate(_mActivity, date -> binding.fa2DateTv.setText(date));
                break;
        }
    }

    private void doAnalysis() {
        if (!CommonUtils.doFirstClick200()) return;

        if (!isVerified()) {
            ToastUtils.showToast("请输入查询条件^_^");
            return;
        }

        String code = binding.fa2CodeEt.getText().toString();
        String date = binding.fa2DateTv.getText().toString();
        String pro = binding.fa2ProTv.getText().toString();

        customProgress = CustomProgress.show(_mActivity, "正在分析股票数据...", true, null);

        mViewModel.doAnalysis(code, date, pro, new AnalysisF2ViewModel.AnalysisCallback() {
            int sizeAll = 0;

            @Override
            public void finishOneStock(String code, int size) {
                _mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sizeAll += size;
                        binding.fa2AllSizeTv.setText(String.format("已找到%d条数据...", sizeAll));
                    }
                });
            }

            @Override
            public void finishAllStock() {
                _mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.fa2AllSizeTv.setText(String.format("总共找到%d条数据！", sizeAll));

                        if (customProgress != null) {
                            customProgress.dismiss();
                            customProgress = null;
                        }

                        //adapter
                        binding.fa2SmartRefreshLayout.autoRefresh();

                    }
                });
            }

            @Override
            public void failedAnalysis(Throwable e) {
                _mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.fa2AllSizeTv.setText("查找失败：\n" + e.getMessage());

                        if (customProgress != null) {
                            customProgress.dismiss();
                            customProgress = null;
                        }
                    }
                });
            }
        });
    }

    private boolean isVerified() {

        if (binding.fa2CodeEt.getText().toString().isEmpty()) return false;

        if (binding.fa2DateTv.getText().toString().isEmpty()) return false;

        if (binding.fa2ProTv.getText().toString().isEmpty()) return false;

        return true;
    }


}
