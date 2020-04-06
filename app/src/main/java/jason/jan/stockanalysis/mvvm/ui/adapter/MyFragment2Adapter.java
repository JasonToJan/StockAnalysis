package jason.jan.stockanalysis.mvvm.ui.adapter;

import android.util.ArrayMap;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import jason.jan.stockanalysis.mvvm.ui.fragment.AddFragment;
import jason.jan.stockanalysis.mvvm.ui.fragment.AnalysisFragment;
import jason.jan.stockanalysis.mvvm.ui.fragment.DataFragment;
import jason.jan.stockanalysis.mvvm.ui.fragment.QueryFragment;
import jason.jan.stockanalysis.mvvm.ui.fragment.TestFragment;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2020/4/2 20:21
 */
public class MyFragment2Adapter extends FragmentStateAdapter {

    /**
     * 记录当前缓存Fragment
     */
    ArrayMap<String,Fragment> arrayMap = new ArrayMap<>();

    public MyFragment2Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        arrayMap.clear();
        arrayMap.put(AddFragment.class.getSimpleName(),AddFragment.newInstance());
        arrayMap.put(AnalysisFragment.class.getSimpleName(),AnalysisFragment.newInstance());
        arrayMap.put(QueryFragment.class.getSimpleName(),QueryFragment.newInstance());
        arrayMap.put(TestFragment.class.getSimpleName(),TestFragment.newInstance());
        arrayMap.put(DataFragment.class.getSimpleName(),DataFragment.newInstance());
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

            case 2 :
                key = QueryFragment.class.getSimpleName();
                fragment = arrayMap.get(key);
                if (fragment == null) {
                    fragment = QueryFragment.newInstance();
                    arrayMap.put(key,fragment);
                }
                break;

            case 3 :
                key = TestFragment.class.getSimpleName();
                fragment = arrayMap.get(key);
                if (fragment == null) {
                    fragment = TestFragment.newInstance();
                    arrayMap.put(key,fragment);
                }
                break;

            case 4 :
                key = DataFragment.class.getSimpleName();
                fragment = arrayMap.get(key);
                if (fragment == null) {
                    fragment = DataFragment.newInstance();
                    arrayMap.put(key,fragment);
                }
                break;

        }

        return fragment;
    }
}
