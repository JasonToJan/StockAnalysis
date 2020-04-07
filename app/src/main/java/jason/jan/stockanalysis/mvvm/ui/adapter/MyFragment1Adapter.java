package jason.jan.stockanalysis.mvvm.ui.adapter;

import android.util.ArrayMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import jason.jan.stockanalysis.mvvm.ui.fragment.AddFragment;
import jason.jan.stockanalysis.mvvm.ui.fragment.Analysis2Fragment;
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
public class MyFragment1Adapter extends FragmentPagerAdapter {

    /**
     * 记录当前缓存Fragment
     */
    ArrayMap<String,Fragment> arrayMap = new ArrayMap<>();

    public MyFragment1Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity.getSupportFragmentManager());
        arrayMap.clear();
        arrayMap.put(AddFragment.class.getSimpleName(),AddFragment.newInstance());
        arrayMap.put(AnalysisFragment.class.getSimpleName(),AnalysisFragment.newInstance());
        arrayMap.put(Analysis2Fragment.class.getSimpleName(),Analysis2Fragment.newInstance());
        arrayMap.put(QueryFragment.class.getSimpleName(),QueryFragment.newInstance());
        arrayMap.put(TestFragment.class.getSimpleName(),TestFragment.newInstance());
        arrayMap.put(DataFragment.class.getSimpleName(),DataFragment.newInstance());
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return chooseFragment(position);
    }

    @Override
    public int getCount() {
        return arrayMap.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return getMyPageTitle(position);
    }

    private String getMyPageTitle(int position){

        String title = "";
        switch (position) {

            case 0 :
                title = "新增";
                break;

            case 1 :
                title = "分析";
                break;

            case 2 :
                title = "GO";
                break;

            case 3 :
                title = "查询";
                break;

            case 4 :
                title = "测试";
                break;

            case 5 :
                title = "数据";
                break;
        }

        return title;
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
                key = Analysis2Fragment.class.getSimpleName();
                fragment = arrayMap.get(key);
                if (fragment == null) {
                    fragment = Analysis2Fragment.newInstance();
                    arrayMap.put(key,fragment);
                }
                break;

            case 3 :
                key = QueryFragment.class.getSimpleName();
                fragment = arrayMap.get(key);
                if (fragment == null) {
                    fragment = QueryFragment.newInstance();
                    arrayMap.put(key,fragment);
                }
                break;

            case 4 :
                key = TestFragment.class.getSimpleName();
                fragment = arrayMap.get(key);
                if (fragment == null) {
                    fragment = TestFragment.newInstance();
                    arrayMap.put(key,fragment);
                }
                break;

            case 5 :
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
