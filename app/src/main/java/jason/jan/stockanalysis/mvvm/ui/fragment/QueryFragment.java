package jason.jan.stockanalysis.mvvm.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
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
import jason.jan.stockanalysis.utils.LogUtils;
import jason.jan.stockanalysis.utils.ToastUtils;

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

    private SearchAdapter searchAdapter;
    private boolean isJumpToBulk;
    private boolean isToUpdate;

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

    }

    @Override
    protected void setListener() {
        searchAdapter = new SearchAdapter(null);

        binding.fqSearchBtn.setOnClickListener(this);

        searchAdapter.setOnItemClickListener((adapter, view, position) -> {

            try {
                List<Stock> stocks = adapter.getData();
                DataSource.getInstance().setUpdateStock(stocks.get(position));
                DataSource.getInstance().setCurrentPosition(2);

                isToUpdate = true;
                OperateActivity.jumpToOperateActivity(_mActivity, 1);

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
            OperateActivity.jumpToOperateActivity(_mActivity, 0);

            return true;
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fq_search_btn:
                doSearch();
                break;
        }
    }

    private void doSearch() {
        if (!CommonUtils.doFirstClick200()) return;

        SearchParms parms = new SearchParms();
        try {
            String code = binding.fqCodeEt.getText().toString().trim();
            String date = binding.fqDateEt.getText().toString().trim();
            boolean isSelectedForecast = binding.fqForecastOn.isChecked();
            boolean isSelectedUnForecast = binding.fqForecastOff.isChecked();

            if (!TextUtils.isEmpty(code)) {
                parms.setCode(Integer.parseInt(code));
            }
            if (!TextUtils.isEmpty(date)) {
                parms.setDate(date);
            }
            parms.setIsForecast((isSelectedForecast && isSelectedUnForecast || !isSelectedForecast && !isSelectedUnForecast)
                    ? 2 : isSelectedForecast && !isSelectedUnForecast ? 1 : 0);

        } catch (Throwable e) {
            d("Error", "##" + e.getMessage());
        }

        mViewModel.getSearchList(parms).observe(this, stocks -> {
            d(TAG, "stocks长度：" + stocks.size());

            if (searchAdapter != null) {
                searchAdapter.replaceData(stocks);
            }
        });

        mViewModel.getAllCount().observe(this, integer -> {
            d(TAG, "allCount=" + integer);
        });
    }

    /**
     * 如果更新了，可能需要得更新下之前的搜索结果
     */
    private void updateTargetPosition(){
        Stock stock = DataSource.getInstance().getUpdateStock();
        if (stock == null || searchAdapter == null) return;

        List<Stock> stocks = searchAdapter.getData();
        for (int i = 0; i < stocks.size(); i++) {
            if (stocks.get(i).getId() == stock.getId()) {
                stocks.set(i,stock);
                searchAdapter.notifyItemChanged(i);
                return;
            }
        }
    }
}
