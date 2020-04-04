package jason.jan.stockanalysis.mvvm.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import jason.jan.stockanalysis.R;
import jason.jan.stockanalysis.base.BaseFragment;
import jason.jan.stockanalysis.data.DataSource;
import jason.jan.stockanalysis.databinding.FragmentBulkBinding;
import jason.jan.stockanalysis.databinding.FragmentQueryBinding;
import jason.jan.stockanalysis.entity.BulkStock;
import jason.jan.stockanalysis.entity.SearchParms;
import jason.jan.stockanalysis.mvvm.ui.adapter.BulkAdatper;
import jason.jan.stockanalysis.mvvm.ui.adapter.SearchAdapter;
import jason.jan.stockanalysis.mvvm.viewmodel.BulkFViewModel;
import jason.jan.stockanalysis.mvvm.viewmodel.QueryFViewModel;
import jason.jan.stockanalysis.utils.CommonUtils;
import jason.jan.stockanalysis.utils.LogUtils;
import jason.jan.stockanalysis.utils.ToastUtils;

import static jason.jan.stockanalysis.utils.LogUtils.d;

/**
 * Description: 批量操作页面
 * *
 * Creator: Wang
 * Date: 2020/4/2 20:19
 */
public class BulkFragment extends BaseFragment<BulkFViewModel, FragmentBulkBinding> {

    private static final String TAG = "QueryFragment";

    public static BulkFragment newInstance() {

        Bundle args = new Bundle();

        BulkFragment fragment = new BulkFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private BulkAdatper bulkAdatper;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_bulk;
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

        binding.fbRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        binding.fbRecyclerView.setAdapter(bulkAdatper);

    }

    @Override
    protected void setListener() {
        binding.fbAllBtn.setOnClickListener(this);
        binding.fbDeleteBtn.setOnClickListener(this);

        bulkAdatper = new BulkAdatper(DataSource.getInstance().getBulkList());

        bulkAdatper.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                try {
                    List<BulkStock> list = adapter.getData();
                    boolean isChoose = list.get(position).isChoose();
                    list.get(position).setChoose(!isChoose);
                    adapter.notifyItemChanged(position);
                } catch (Throwable e) {
                    d("Error", "##" + e.getMessage());
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fb_all_btn:
                doSelectAll();
                break;

            case R.id.fb_delete_btn:
                doDeleteAll();
                break;
        }
    }

    private void doSelectAll() {
        if (!CommonUtils.doFirstClick200() || bulkAdatper == null) return;

        List<BulkStock> list = bulkAdatper.getData();
        if (list.size() == 0) return;

        boolean currentState = list.get(0).isChoose();
        for (BulkStock stock : list) {
            stock.setChoose(!currentState);
        }

        bulkAdatper.notifyItemRangeChanged(0, list.size());
    }

    private void doDeleteAll() {
        if (!CommonUtils.doFirstClick200() || bulkAdatper == null) return;

        List<BulkStock> list = bulkAdatper.getData();
        List<BulkStock> newList = new ArrayList<>();
        List<BulkStock> needRemove = new ArrayList<>();
        newList.addAll(list);

        if (list.size() == 0) return;

        for (BulkStock stock : list) {
            if (stock.isChoose()) {
                needRemove.add(stock);
                newList.remove(stock);
            }
        }
        if (needRemove.size() == 0) {
            ToastUtils.showToast("请选择要删除的条目^_^");
            return;
        }

        long[] ids = new long[needRemove.size()];
        for (int i = 0; i < needRemove.size(); i++) {
            ids[i] = needRemove.get(i).getId();
        }

        mViewModel.deleteSome(ids);

        //这里应该也有刷新一下adapter
        bulkAdatper.replaceData(newList);
    }
}
