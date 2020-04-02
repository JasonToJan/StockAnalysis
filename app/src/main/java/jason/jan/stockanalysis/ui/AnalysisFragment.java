package jason.jan.stockanalysis.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import jason.jan.stockanalysis.R;
import jason.jan.stockanalysis.databinding.FragmentAnalysisBinding;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2020/4/2 20:27
 */
public class AnalysisFragment extends SupportFragment {

    FragmentAnalysisBinding mBinding;

    public static AnalysisFragment newInstance() {

        Bundle args = new Bundle();

        AnalysisFragment fragment = new AnalysisFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_analysis, container, false);

        return mBinding.getRoot();
    }
}
