package jason.jan.stockanalysis.mvvm.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import jason.jan.stockanalysis.R;
import jason.jan.stockanalysis.base.BaseFragment;
import jason.jan.stockanalysis.data.DataSource;
import jason.jan.stockanalysis.databinding.FragmentAnalysisResultBinding;
import jason.jan.stockanalysis.entity.AnalysisStock;
import jason.jan.stockanalysis.entity.Stock;
import jason.jan.stockanalysis.mvvm.ui.adapter.AnalysisResultAdatper;
import jason.jan.stockanalysis.mvvm.viewmodel.AnalysisFViewModel;
import jason.jan.stockanalysis.utils.LogUtils;
import jason.jan.stockanalysis.utils.ToastUtils;
import jason.jan.stockanalysis.view.CustomProgress;

/**
 * Description: 分析结果页
 * *
 * Creator: Wang
 * Date: 2020/4/6 20:19
 */
public class AnalysisResultFragment extends BaseFragment<AnalysisFViewModel, FragmentAnalysisResultBinding> implements AnalysisResultAdatper.GetProbilityCallback {

    private static final String TAG = "QueryFragment";

    public static AnalysisResultFragment newInstance() {

        Bundle args = new Bundle();

        AnalysisResultFragment fragment = new AnalysisResultFragment();
        fragment.setArguments(args);
        return fragment;
    }


    //属性集合------------begin-----------------//

    private int pagePosition;//第几页

    public static final int PAGE_MAX = 200;//一页大概有多少个

    private ArrayList<AnalysisStock> resultList = new ArrayList<>();//adapter数据集合

    private AnalysisResultAdatper resultAdatper;

    private CustomProgress customProgress;

    //属性集合------------end-------------------//


    @Override
    protected int getContentViewId() {
        return R.layout.fragment_analysis_result;
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

        binding.farRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        binding.farRecyclerView.setAdapter(resultAdatper);

        binding.farSmartRefreshLayout.setEnableRefresh(true);//是否启用下拉刷新功能
        binding.farSmartRefreshLayout.setEnableLoadMore(true);//是否启用上拉加载功能
        binding.farSmartRefreshLayout.setEnableAutoLoadMore(true);//是否启用列表惯性滑动到底部时自动加载更多
        binding.farSmartRefreshLayout.setEnableNestedScroll(true);//是否启用嵌套滚动
        binding.farSmartRefreshLayout.setEnableOverScrollBounce(true);//是否启用越界回弹
        binding.farSmartRefreshLayout.setDragRate(0.5f);//显示下拉高度/手指真实下拉高度=阻尼效果
        binding.farSmartRefreshLayout.setReboundDuration(300);//回弹动画时长（毫秒）

        doAnalysis();
    }

    @Override
    protected void setListener() {

        resultAdatper = new AnalysisResultAdatper(DataSource.getInstance().getAnalysisList());

        resultAdatper.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });

        binding.farSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pagePosition = 0;
                List<AnalysisStock> listResult = DataSource.getInstance().getAnalysisList();
                if (listResult == null || listResult.size() == 0) return;

                resultList.clear();
                for (int i = 0; i < listResult.size(); i++) {
                    if (i > PAGE_MAX) break;
                    resultList.add(listResult.get(i));
                }

                if (resultAdatper != null) {
                    resultAdatper.replaceMyData(resultList, AnalysisResultFragment.this);
                }

                binding.farSmartRefreshLayout.finishRefresh();
            }
        });

        binding.farSmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            pagePosition++;

            List<AnalysisStock> listResult = DataSource.getInstance().getAnalysisList();
            if (listResult == null || listResult.size() == 0) return;

            int currentSize = resultAdatper.getData().size();
            for (int i = currentSize; i < listResult.size(); i++) {
                if (i > PAGE_MAX * pagePosition) break;
                resultList.add(listResult.get(i));
            }

            if (resultAdatper != null) {
                resultAdatper.replaceMyData(resultList, AnalysisResultFragment.this);
            }

            binding.farSmartRefreshLayout.finishLoadMore();
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

        }
    }

    @Override
    public void getUpPro(float up0_2, float up2_4, float up4_6, float up6_8, float up8_10) {

        binding.farUp2ProTv.setText(up0_2 + "% 上涨0-2%!");
        binding.farUp4ProTv.setText(up2_4 + "% 上涨2-4%!");
        binding.farUp6ProTv.setText(up4_6 + "% 上涨4-6%!");
        binding.farUp8ProTv.setText(up6_8 + "% 上涨6-8%!");
        binding.farUp10ProTv.setText(up8_10 + "% 上涨8-10%!");

        binding.farUp010ProTv.setText((up0_2 + up2_4 + up4_6 + up6_8 + up8_10) + "% 上涨0-10%!");
    }

    @Override
    public void getDownPro(float down0_2, float down2_4, float down4_6, float down6_8, float down8_10) {
        binding.farDown2ProTv.setText(down0_2 + "% 下跌0-2%!");
        binding.farDown4ProTv.setText(down2_4 + "% 下跌2-4%!");
        binding.farDown6ProTv.setText(down4_6 + "% 下跌4-6%!");
        binding.farDown8ProTv.setText(down6_8 + "% 下跌6-8%!");
        binding.farDown10ProTv.setText(down8_10 + "% 下跌8-10%!");

        binding.farDown010ProTv.setText((down0_2 + down2_4 + down4_6 + down6_8 + down8_10) + "% 下跌0-10%!");
    }

    private void doAnalysis() {

        if (customProgress != null) {
            customProgress.dismiss();
            customProgress = null;
        }

        customProgress = CustomProgress.show(_mActivity, "正在分析股票数据...", true, null);

        mViewModel.doAnalysis(new AnalysisFViewModel.AnalysisCallback() {

            int sizeAll = 0;

            @Override
            public void finishOneStock(String code, int size) {
                _mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sizeAll += size;
                        binding.farAllSizeTv.setText(String.format("已找到%d条数据...", sizeAll));
                    }
                });
            }

            @Override
            public void finishAllStock() {
                _mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.farAllSizeTv.setText(String.format("总共找到%d条数据！", sizeAll));

                        if (customProgress != null) {
                            customProgress.dismiss();
                            customProgress = null;
                        }

                        //adapter
                        binding.farSmartRefreshLayout.autoRefresh();

                    }
                });
            }

            @Override
            public void failedAnalysis(Throwable e) {
                _mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.farAllSizeTv.setText("查找失败：\n" + e.getMessage());

                        if (customProgress != null) {
                            customProgress.dismiss();
                            customProgress = null;
                        }
                    }
                });
            }
        });
    }

}
