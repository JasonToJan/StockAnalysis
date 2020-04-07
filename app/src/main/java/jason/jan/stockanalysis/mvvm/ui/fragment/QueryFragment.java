package jason.jan.stockanalysis.mvvm.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
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
import jason.jan.stockanalysis.databinding.FragmentQueryBinding;
import jason.jan.stockanalysis.entity.BulkStock;
import jason.jan.stockanalysis.entity.SearchParms;
import jason.jan.stockanalysis.entity.Stock;
import jason.jan.stockanalysis.mvvm.ui.activity.OperateActivity;
import jason.jan.stockanalysis.mvvm.ui.adapter.SearchAdapter;
import jason.jan.stockanalysis.mvvm.viewmodel.QueryFViewModel;
import jason.jan.stockanalysis.utils.CommonUtils;
import jason.jan.stockanalysis.utils.DataUtils;
import jason.jan.stockanalysis.utils.DialogUtils;

import static jason.jan.stockanalysis.utils.LogUtils.d;

/**
 * Description: 增加股票记录碎片
 * *
 * Creator: Wang
 * Date: 2020/4/2 20:19
 */
public class QueryFragment extends BaseFragment<QueryFViewModel, FragmentQueryBinding> {

    private static final String TAG = "QueryFragment";

    public static QueryFragment newInstance() {

        Bundle args = new Bundle();

        QueryFragment fragment = new QueryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //属性集合------------begin-----------------//

    private int pagePosition;//第几页
    public static final int PAGE_MAX = 50;//一页大概有多少个
    private SearchAdapter searchAdapter;
    private ArrayList<Stock> resultList = new ArrayList<>();//adapter数据集合
    private boolean isJumpToBulk;
    private boolean isToUpdate;

    //属性集合------------end-------------------//

    @Override
    public void onResume() {
        super.onResume();
        if (isJumpToBulk) {
            isJumpToBulk = false;
            doSearch();
        }
        if (isToUpdate) {
            isToUpdate = false;
            updateTargetPosition();
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_query;
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

        binding.fqRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        binding.fqRecyclerView.setAdapter(searchAdapter);
        binding.fqSmartRefreshLayout.setEnableRefresh(true);//是否启用下拉刷新功能
        binding.fqSmartRefreshLayout.setEnableLoadMore(true);//是否启用上拉加载功能
        binding.fqSmartRefreshLayout.setEnableAutoLoadMore(true);//是否启用列表惯性滑动到底部时自动加载更多
        binding.fqSmartRefreshLayout.setEnableNestedScroll(true);//是否启用嵌套滚动
        binding.fqSmartRefreshLayout.setEnableOverScrollBounce(true);//是否启用越界回弹
        binding.fqSmartRefreshLayout.setDragRate(0.9f);//显示下拉高度/手指真实下拉高度=阻尼效果
        binding.fqSmartRefreshLayout.setReboundDuration(300);//回弹动画时长（毫秒）

        binding.fqSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pagePosition = 0;
                List<Stock> listResult = DataSource.getInstance().getSearchList();
                if (listResult == null || listResult.size() == 0) return;

                resultList.clear();
                for (int i = 0; i < listResult.size(); i++) {
                    if (i > PAGE_MAX) break;
                    resultList.add(listResult.get(i));
                }

                if (searchAdapter != null) {
                    searchAdapter.replaceData(resultList);
                }

                binding.fqSmartRefreshLayout.finishRefresh(1500);
            }
        });

        binding.fqSmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            pagePosition++;

            List<Stock> listResult = DataSource.getInstance().getSearchList();
            if (listResult == null || listResult.size() == 0) return;

            int currentSize = searchAdapter.getData().size();
            for (int i = currentSize; i < listResult.size(); i++) {
                if (i > PAGE_MAX * pagePosition) break;
                resultList.add(listResult.get(i));
            }

            if (searchAdapter != null) {
                searchAdapter.replaceData(resultList);
            }

            binding.fqSmartRefreshLayout.finishLoadMore(1500);
        });

    }

    @Override
    protected void setListener() {
        searchAdapter = new SearchAdapter(null);

        binding.fqSearchBtn.setOnClickListener(this);
        binding.fqDateTv.setOnClickListener(this);

        searchAdapter.setOnItemClickListener((adapter, view, position) -> {

            try {
                List<Stock> stocks = adapter.getData();
                DataSource.getInstance().setUpdateStock(stocks.get(position));
                DataSource.getInstance().setCurrentPosition(2);

                isToUpdate = true;
                OperateActivity.jumpToOperateActivity(_mActivity, OperateActivity.OPERATE_UPDATE);

            } catch (Throwable e) {
                d("Error", "##" + e.getMessage());
            }

        });

        searchAdapter.setOnItemLongClickListener((adapter, view, position) -> {

            List<Stock> stocks = adapter.getData();
            List<BulkStock> bulkStocks = DataUtils.convertStockList(stocks);
            DataSource.getInstance().setBulkList(bulkStocks);
            DataSource.getInstance().setCurrentPosition(2);

            isJumpToBulk = true;
            OperateActivity.jumpToOperateActivity(_mActivity, OperateActivity.OPERATE_BULK);

            return true;
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fq_search_btn:
                doSearch();
                break;

            case R.id.fq_date_tv:
                DialogUtils.chooseDate(_mActivity, date -> binding.fqDateTv.setText(date));
                break;
        }
    }

    private void doSearch() {
        if (!CommonUtils.doFirstClick200()) return;

        SearchParms parms = new SearchParms();
        try {
            String code = binding.fqCodeEt.getText().toString().trim();
            String date = binding.fqDateTv.getText().toString().trim();
            boolean isSelectedForecast = binding.fqForecastOn.isChecked();
            boolean isSelectedUnForecast = binding.fqForecastOff.isChecked();

            if (!TextUtils.isEmpty(code)) {
                parms.setCode(code);
            }
            if (!TextUtils.isEmpty(date)) {
                parms.setDate(date);
            }
            parms.setIsForecast((isSelectedForecast && isSelectedUnForecast || !isSelectedForecast && !isSelectedUnForecast)
                    ? 2 : isSelectedForecast && !isSelectedUnForecast ? 1 : 0);

            d(TAG, "检测到搜索事件：" + code + " " + date);

        } catch (Throwable e) {
            d("Error", "##" + e.getMessage());
        }

        mViewModel.getSearchList(parms).observe(this, stocks -> {
            d(TAG, "stocks长度：" + stocks.size());

            binding.fqSearchNumTv.setText("(共搜到"+stocks.size()+"条记录)");
            binding.fqSearchNumTv.setVisibility(View.VISIBLE);

            DataSource.getInstance().setSearchList(stocks);

            binding.fqSmartRefreshLayout.autoRefresh();

        });

        mViewModel.getAllCount().observe(this, integer -> {
            d(TAG, "allCount=" + integer);
        });
    }

    /**
     * 如果更新了，可能需要得更新下之前的搜索结果
     */
    private void updateTargetPosition() {
        Stock stock = DataSource.getInstance().getUpdateStock();
        if (stock == null || searchAdapter == null) return;

        List<Stock> stocks = searchAdapter.getData();
        for (int i = 0; i < stocks.size(); i++) {
            if (stocks.get(i).getCode() == stock.getCode() && stocks.get(i).getCurrentTime() == stock.getCurrentTime()) {
                stocks.set(i, stock);
                searchAdapter.notifyItemChanged(i);
                return;
            }
        }
    }
}
