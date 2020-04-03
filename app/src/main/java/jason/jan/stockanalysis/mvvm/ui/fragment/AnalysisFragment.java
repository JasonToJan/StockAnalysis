package jason.jan.stockanalysis.mvvm.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import jason.jan.stockanalysis.R;
import jason.jan.stockanalysis.base.BaseFragment;
import jason.jan.stockanalysis.databinding.FragmentAnalysisBinding;
import jason.jan.stockanalysis.mvvm.viewmodel.AnalysisFViewModel;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2020/4/2 20:27
 */
public class AnalysisFragment extends BaseFragment<AnalysisFViewModel,FragmentAnalysisBinding> {

    public static AnalysisFragment newInstance() {

        Bundle args = new Bundle();

        AnalysisFragment fragment = new AnalysisFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_analysis;
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onClick(View v) {

    }
}
