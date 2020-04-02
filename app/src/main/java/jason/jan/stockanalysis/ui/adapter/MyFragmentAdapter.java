package jason.jan.stockanalysis.ui.adapter;

import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import jason.jan.stockanalysis.ui.AddFragment;
import jason.jan.stockanalysis.ui.AnalysisFragment;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2020/4/2 20:21
 */
public class MyFragmentAdapter extends FragmentStateAdapter {

    /**
     * 记录当前缓存Fragment
     */
    ArrayMap<String,Fragment> arrayMap = new ArrayMap<>();

    public MyFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        arrayMap.clear();
        arrayMap.put(AddFragment.class.getSimpleName(),AddFragment.newInstance());
        arrayMap.put(AnalysisFragment.class.getSimpleName(),AnalysisFragment.newInstance());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return chooseFragment(position);
    }

    @Override
    public int getItemCount() {
        return arrayMap.size();
    }


    public Fragment chooseFragment(int position){

        String key = "";
        Fragment fragment = null;

        switch (position) {
            case 0 :
                key = AddFragment.class.getSimpleName();
                fragment = arrayMap.get(key);
                if (fragment == null) {
                    fragment = AddFragment.newInstance();
                    arrayMap.put(key,fragment);
                }

                break;

            case 1 :
                key = AnalysisFragment.class.getSimpleName();
                fragment = arrayMap.get(key);
                if (fragment == null) {
                    fragment = AnalysisFragment.newInstance();
                    arrayMap.put(key,fragment);
                }
                break;

        }

        return fragment;
    }
}
