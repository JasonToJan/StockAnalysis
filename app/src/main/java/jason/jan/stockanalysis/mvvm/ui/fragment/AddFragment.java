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
import jason.jan.stockanalysis.databinding.FragmentAddBinding;
import jason.jan.stockanalysis.mvvm.viewmodel.AddFViewModel;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Description: 增加股票记录碎片
 * *
 * Creator: Wang
 * Date: 2020/4/2 20:19
 */
public class AddFragment extends BaseFragment<AddFViewModel,FragmentAddBinding> {

    public static AddFragment newInstance() {

        Bundle args = new Bundle();

        AddFragment fragment = new AddFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getContentViewId() {
        return  R.layout.fragment_add;
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
