package jason.jan.stockanalysis.mvvm.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import jason.jan.stockanalysis.R;
import jason.jan.stockanalysis.base.BaseFragment;
import jason.jan.stockanalysis.data.DataSource;
import jason.jan.stockanalysis.databinding.FragmentAnalysisBinding;
import jason.jan.stockanalysis.entity.Condition;
import jason.jan.stockanalysis.mvvm.ui.activity.OperateActivity;
import jason.jan.stockanalysis.mvvm.viewmodel.AnalysisFViewModel;
import jason.jan.stockanalysis.utils.CommonUtils;
import jason.jan.stockanalysis.utils.DataUtils;
import jason.jan.stockanalysis.utils.DialogUtils;
import jason.jan.stockanalysis.utils.LogUtils;
import jason.jan.stockanalysis.utils.ToastUtils;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2020/4/2 20:27
 */
public class AnalysisFragment extends BaseFragment<AnalysisFViewModel, FragmentAnalysisBinding> {

    private static final String TAG = "AnalysisFragment";

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

        binding.faOkBtn.setOnClickListener(this);

        binding.faCondition1Attribute1.setOnClickListener(this);
        binding.faCondition1Day1.setOnClickListener(this);
        binding.faCondition1Attribute2.setOnClickListener(this);
        binding.faCondition1Day2.setOnClickListener(this);
        binding.faCondition1UpTv.setOnClickListener(this);

        binding.faCondition2Attribute1.setOnClickListener(this);
        binding.faCondition2Day1.setOnClickListener(this);
        binding.faCondition2Attribute2.setOnClickListener(this);
        binding.faCondition2Day2.setOnClickListener(this);
        binding.faCondition2UpTv.setOnClickListener(this);

        binding.faCondition3Attribute1.setOnClickListener(this);
        binding.faCondition3Day1.setOnClickListener(this);
        binding.faCondition3Attribute2.setOnClickListener(this);
        binding.faCondition3Day2.setOnClickListener(this);
        binding.faCondition3UpTv.setOnClickListener(this);

        binding.faCondition4Attribute1.setOnClickListener(this);
        binding.faCondition4Day1.setOnClickListener(this);
        binding.faCondition4Attribute2.setOnClickListener(this);
        binding.faCondition4Day2.setOnClickListener(this);
        binding.faCondition4UpTv.setOnClickListener(this);

        binding.faCondition5Attribute1.setOnClickListener(this);
        binding.faCondition5Day1.setOnClickListener(this);
        binding.faCondition5Attribute2.setOnClickListener(this);
        binding.faCondition5Day2.setOnClickListener(this);
        binding.faCondition5UpTv.setOnClickListener(this);

        binding.faCondition6Attribute1.setOnClickListener(this);
        binding.faCondition6Day1.setOnClickListener(this);
        binding.faCondition6Attribute2.setOnClickListener(this);
        binding.faCondition6Day2.setOnClickListener(this);
        binding.faCondition6UpTv.setOnClickListener(this);

        binding.faCondition7Day1.setOnClickListener(this);
        binding.faCondition7UpTv.setOnClickListener(this);

        binding.faCondition8Day1.setOnClickListener(this);
        binding.faCondition8UpTv.setOnClickListener(this);

        binding.faCondition9Day1.setOnClickListener(this);
        binding.faCondition9UpTv.setOnClickListener(this);

        binding.faCondition10Attribute1.setOnClickListener(this);
        binding.faCondition10Day1.setOnClickListener(this);
        binding.faCondition10Attribute2.setOnClickListener(this);
        binding.faCondition10Day2.setOnClickListener(this);

        binding.faCondition11Attribute1.setOnClickListener(this);
        binding.faCondition11Day1.setOnClickListener(this);
        binding.faCondition11Attribute2.setOnClickListener(this);
        binding.faCondition11Day2.setOnClickListener(this);

        binding.faCondition12Attribute1.setOnClickListener(this);
        binding.faCondition12Day1.setOnClickListener(this);
        binding.faCondition12Attribute2.setOnClickListener(this);
        binding.faCondition12Day2.setOnClickListener(this);

        binding.faCondition13Attribute1.setOnClickListener(this);
        binding.faCondition13Day1.setOnClickListener(this);
        binding.faCondition13Attribute2.setOnClickListener(this);
        binding.faCondition13Day2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fa_ok_btn:
                doAnalysis();
                break;

            case R.id.fa_condition1_attribute1:
                doClickCondition1Attr1();
                break;

            case R.id.fa_condition1_day1:
                doClickCondition1Day1();
                break;

            case R.id.fa_condition1_attribute2:
                doClickCondition1Attr2();
                break;

            case R.id.fa_condition1_day2:
                doClickCondition1Day2();
                break;

            case R.id.fa_condition1_up_tv:
                doClickCondition1Up();
                break;

            case R.id.fa_condition2_attribute1:
                doClickConditio2Attr1();
                break;

            case R.id.fa_condition2_day1:
                doClickCondition2Day1();
                break;

            case R.id.fa_condition2_attribute2:
                doClickCondition2Attr2();
                break;

            case R.id.fa_condition2_day2:
                doClickCondition2Day2();
                break;

            case R.id.fa_condition2_up_tv:
                doClickCondition2Up();
                break;

            case R.id.fa_condition3_attribute1:
                doClickConditio3Attr1();
                break;

            case R.id.fa_condition3_day1:
                doClickCondition3Day1();
                break;

            case R.id.fa_condition3_attribute2:
                doClickCondition3Attr2();
                break;

            case R.id.fa_condition3_day2:
                doClickCondition3Day2();
                break;

            case R.id.fa_condition3_up_tv:
                doClickCondition3Up();
                break;

            case R.id.fa_condition4_attribute1:
                doClickConditio4Attr1();
                break;

            case R.id.fa_condition4_day1:
                doClickCondition4Day1();
                break;

            case R.id.fa_condition4_attribute2:
                doClickCondition4Attr2();
                break;

            case R.id.fa_condition4_day2:
                doClickCondition4Day2();
                break;

            case R.id.fa_condition4_up_tv:
                doClickCondition4Up();
                break;

            case R.id.fa_condition5_attribute1:
                doClickConditio5Attr1();
                break;

            case R.id.fa_condition5_day1:
                doClickCondition5Day1();
                break;

            case R.id.fa_condition5_attribute2:
                doClickCondition5Attr2();
                break;

            case R.id.fa_condition5_day2:
                doClickCondition5Day2();
                break;

            case R.id.fa_condition5_up_tv:
                doClickCondition5Up();
                break;

            case R.id.fa_condition6_attribute1:
                doClickConditio6Attr1();
                break;

            case R.id.fa_condition6_day1:
                doClickCondition6Day1();
                break;

            case R.id.fa_condition6_attribute2:
                doClickCondition6Attr2();
                break;

            case R.id.fa_condition6_day2:
                doClickCondition6Day2();
                break;

            case R.id.fa_condition6_up_tv:
                doClickCondition6Up();
                break;

            case R.id.fa_condition7_day1:
                doClickCondition7Day1();
                break;

            case R.id.fa_condition7_up_tv:
                doClickCondition7Up();
                break;

            case R.id.fa_condition8_day1:
                doClickCondition8Day1();
                break;

            case R.id.fa_condition8_up_tv:
                doClickCondition8Up();
                break;

            case R.id.fa_condition9_day1:
                doClickCondition9Day1();
                break;

            case R.id.fa_condition9_up_tv:
                doClickCondition9Up();
                break;

            case R.id.fa_condition10_attribute1:
                doClickConditio10Attr1();
                break;

            case R.id.fa_condition10_day1:
                doClickCondition10Day1();
                break;

            case R.id.fa_condition10_attribute2:
                doClickCondition10Attr2();
                break;

            case R.id.fa_condition10_day2:
                doClickCondition10Day2();
                break;

            case R.id.fa_condition11_attribute1:
                doClickConditio11Attr1();
                break;

            case R.id.fa_condition11_day1:
                doClickCondition11Day1();
                break;

            case R.id.fa_condition11_attribute2:
                doClickCondition11Attr2();
                break;

            case R.id.fa_condition11_day2:
                doClickCondition11Day2();
                break;

            case R.id.fa_condition12_attribute1:
                doClickConditio12Attr1();
                break;

            case R.id.fa_condition12_day1:
                doClickCondition12Day1();
                break;

            case R.id.fa_condition12_attribute2:
                doClickCondition12Attr2();
                break;

            case R.id.fa_condition12_day2:
                doClickCondition12Day2();
                break;

            case R.id.fa_condition13_attribute1:
                doClickConditio13Attr1();
                break;

            case R.id.fa_condition13_day1:
                doClickCondition13Day1();
                break;

            case R.id.fa_condition13_attribute2:
                doClickCondition13Attr2();
                break;

            case R.id.fa_condition13_day2:
                doClickCondition13Day2();
                break;
        }
    }

    /**
     * 开始分析，最重要的函数
     */
    private void doAnalysis() {
        if (!CommonUtils.doFirstClick200()) return;

        if (!isVerified()) {
            ToastUtils.showToast("请检查输入条件！");
            return;
        }

        OperateActivity.jumpToOperateActivity(_mActivity, OperateActivity.OPERATE_ANALYSIS);
    }

    /**
     * 验证是否成功
     *
     * @return
     */
    private boolean isVerified() {
        ArrayList<Condition> conditionList = new ArrayList<>(20);

        //Condition1 条件
        if (!binding.faCondition1Value.getText().toString().trim().isEmpty()) {

            Condition condition = new Condition();

            for (int i = 0; i < 4; i++) {
                if (binding.faCondition1Day1.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay1(i);
                }
                if (binding.faCondition1Day2.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay2(i);
                }
            }

            for (int i = 0; i < 6; i++) {
                if (binding.faCondition1Attribute1.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr1(i);
                }
                if (binding.faCondition1Attribute2.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr2(i);
                }
            }

            for (int i = 0; i < 3; i++) {
                if (binding.faCondition1UpTv.getText().equals(DataUtils.upOrDown[i])) {
                    if (i == 2) return false;

                    condition.setFeature(i);
                }
            }

            condition.setFeatureValue(Float.parseFloat(binding.faCondition1Value.getText().toString().trim()));
            conditionList.add(condition);
        }

        //Condition2 条件
        if (!binding.faCondition2Value.getText().toString().trim().isEmpty()) {

            Condition condition = new Condition();

            for (int i = 0; i < 4; i++) {
                if (binding.faCondition2Day1.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay1(i);
                }
                if (binding.faCondition2Day2.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay2(i);
                }
            }

            for (int i = 0; i < 6; i++) {
                if (binding.faCondition2Attribute1.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr1(i);
                }
                if (binding.faCondition2Attribute2.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr2(i);
                }
            }

            for (int i = 0; i < 3; i++) {
                if (binding.faCondition2UpTv.getText().equals(DataUtils.upOrDown[i])) {
                    if (i == 2) return false;

                    condition.setFeature(i);
                }
            }

            condition.setFeatureValue(Float.parseFloat(binding.faCondition2Value.getText().toString().trim()));
            conditionList.add(condition);
        }

        //Condition3 条件
        if (!binding.faCondition3Value.getText().toString().trim().isEmpty()) {

            Condition condition = new Condition();

            for (int i = 0; i < 4; i++) {
                if (binding.faCondition3Day1.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay1(i);
                }
                if (binding.faCondition3Day2.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay2(i);
                }
            }

            for (int i = 0; i < 6; i++) {
                if (binding.faCondition3Attribute1.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr1(i);
                }
                if (binding.faCondition3Attribute2.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr2(i);
                }
            }

            for (int i = 0; i < 3; i++) {
                if (binding.faCondition3UpTv.getText().equals(DataUtils.upOrDown[i])) {
                    if (i == 2) return false;

                    condition.setFeature(i);
                }
            }

            condition.setFeatureValue(Float.parseFloat(binding.faCondition3Value.getText().toString().trim()));
            conditionList.add(condition);
        }

        //Condition4 条件
        if (!binding.faCondition4Value.getText().toString().trim().isEmpty()) {

            Condition condition = new Condition();

            for (int i = 0; i < 4; i++) {
                if (binding.faCondition4Day1.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay1(i);
                }
                if (binding.faCondition4Day2.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay2(i);
                }
            }

            for (int i = 0; i < 6; i++) {
                if (binding.faCondition4Attribute1.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr1(i);
                }
                if (binding.faCondition4Attribute2.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr2(i);
                }
            }

            for (int i = 0; i < 3; i++) {
                if (binding.faCondition4UpTv.getText().equals(DataUtils.upOrDown[i])) {
                    if (i == 2) return false;

                    condition.setFeature(i);
                }
            }

            condition.setFeatureValue(Float.parseFloat(binding.faCondition4Value.getText().toString().trim()));
            conditionList.add(condition);
        }

        //Condition5 条件
        if (!binding.faCondition5Value.getText().toString().trim().isEmpty()) {

            Condition condition = new Condition();

            for (int i = 0; i < 4; i++) {
                if (binding.faCondition5Day1.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay1(i);
                }
                if (binding.faCondition5Day2.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay2(i);
                }
            }

            for (int i = 0; i < 6; i++) {
                if (binding.faCondition5Attribute1.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr1(i);
                }
                if (binding.faCondition5Attribute2.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr2(i);
                }
            }

            for (int i = 0; i < 3; i++) {
                if (binding.faCondition5UpTv.getText().equals(DataUtils.upOrDown[i])) {
                    if (i == 2) return false;

                    condition.setFeature(i);
                }
            }

            condition.setFeatureValue(Float.parseFloat(binding.faCondition5Value.getText().toString().trim()));
            conditionList.add(condition);
        }

        //Condition6 条件
        if (!binding.faCondition6Value.getText().toString().trim().isEmpty()) {

            Condition condition = new Condition();

            for (int i = 0; i < 4; i++) {
                if (binding.faCondition6Day1.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay1(i);
                }
                if (binding.faCondition6Day2.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay2(i);
                }
            }

            for (int i = 0; i < 6; i++) {
                if (binding.faCondition6Attribute1.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr1(i);
                }
                if (binding.faCondition6Attribute2.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr2(i);
                }
            }

            for (int i = 0; i < 3; i++) {
                if (binding.faCondition6UpTv.getText().equals(DataUtils.upOrDown[i])) {
                    if (i == 2) return false;

                    condition.setFeature(i);
                }
            }

            condition.setFeatureValue(Float.parseFloat(binding.faCondition6Value.getText().toString().trim()));
            conditionList.add(condition);
        }

        //Condition7 条件
        if (!binding.faCondition7Value.getText().toString().trim().isEmpty()) {

            Condition condition = new Condition();

            condition.setOnlyOneAttr(true);

            for (int i = 0; i < 4; i++) {
                if (binding.faCondition7Day1.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay1(i);
                }
            }

            for (int i = 0; i < 6; i++) {
                if (binding.faCondition7Attribute1.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr1(i);
                }
            }

            for (int i = 0; i < 3; i++) {
                if (binding.faCondition7UpTv.getText().equals(DataUtils.volumesUpOrDown[i])) {
                    if (i == 2) return false;

                    condition.setFeature(i);
                }
            }

            condition.setFeatureValue(Float.parseFloat(binding.faCondition7Value.getText().toString().trim()));
            conditionList.add(condition);
        }

        //Condition8 条件
        if (!binding.faCondition8Value.getText().toString().trim().isEmpty()) {

            Condition condition = new Condition();

            condition.setOnlyOneAttr(true);

            for (int i = 0; i < 4; i++) {
                if (binding.faCondition8Day1.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay1(i);
                }
            }

            for (int i = 0; i < 6; i++) {
                if (binding.faCondition8Attribute1.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr1(i);
                }
            }

            for (int i = 0; i < 3; i++) {
                if (binding.faCondition8UpTv.getText().equals(DataUtils.volumesUpOrDown[i])) {
                    if (i == 2) return false;

                    condition.setFeature(i);
                }
            }

            condition.setFeatureValue(Float.parseFloat(binding.faCondition8Value.getText().toString().trim()));
            conditionList.add(condition);
        }

        //Condition9 条件
        if (!binding.faCondition9Value.getText().toString().trim().isEmpty()) {

            Condition condition = new Condition();

            condition.setOnlyOneAttr(true);

            for (int i = 0; i < 4; i++) {
                if (binding.faCondition9Day1.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay1(i);
                }
            }

            for (int i = 0; i < 6; i++) {
                if (binding.faCondition9Attribute1.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr1(i);
                }
            }

            for (int i = 0; i < 3; i++) {
                if (binding.faCondition9UpTv.getText().equals(DataUtils.volumesUpOrDown[i])) {
                    if (i == 2) return false;

                    condition.setFeature(i);
                }
            }

            condition.setFeatureValue(Float.parseFloat(binding.faCondition9Value.getText().toString().trim()));
            conditionList.add(condition);
        }

        //Condition10 条件
        if (!binding.faCondition10Value1.getText().toString().trim().isEmpty()) {
            if (binding.faCondition10Value2.getText().toString().trim().isEmpty()) return false;

            Condition condition = new Condition();

            for (int i = 0; i < 4; i++) {
                if (binding.faCondition10Day1.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay1(i);
                }
                if (binding.faCondition10Day2.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay2(i);
                }
            }

            for (int i = 0; i < 6; i++) {
                if (binding.faCondition10Attribute1.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr1(i);
                }
                if (binding.faCondition10Attribute2.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr2(i);
                }
            }

            condition.setFeatureValue(Float.parseFloat(binding.faCondition10Value1.getText().toString().trim()));
            condition.setFeatureValue2(Float.parseFloat(binding.faCondition10Value2.getText().toString().trim()));
            conditionList.add(condition);
        }

        //Condition11 条件
        if (!binding.faCondition11Value1.getText().toString().trim().isEmpty()) {
            if (binding.faCondition11Value2.getText().toString().trim().isEmpty()) return false;

            Condition condition = new Condition();

            for (int i = 0; i < 4; i++) {
                if (binding.faCondition11Day1.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay1(i);
                }
                if (binding.faCondition11Day2.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay2(i);
                }
            }

            for (int i = 0; i < 6; i++) {
                if (binding.faCondition11Attribute1.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr1(i);
                }
                if (binding.faCondition11Attribute2.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr2(i);
                }
            }

            condition.setFeatureValue(Float.parseFloat(binding.faCondition11Value1.getText().toString().trim()));
            condition.setFeatureValue2(Float.parseFloat(binding.faCondition11Value2.getText().toString().trim()));
            conditionList.add(condition);
        }

        //Condition12 条件
        if (!binding.faCondition12Value1.getText().toString().trim().isEmpty()) {
            if (binding.faCondition12Value2.getText().toString().trim().isEmpty()) return false;

            Condition condition = new Condition();

            for (int i = 0; i < 4; i++) {
                if (binding.faCondition12Day1.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay1(i);
                }
                if (binding.faCondition12Day2.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay2(i);
                }
            }

            for (int i = 0; i < 6; i++) {
                if (binding.faCondition12Attribute1.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr1(i);
                }
                if (binding.faCondition12Attribute2.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr2(i);
                }
            }

            condition.setFeatureValue(Float.parseFloat(binding.faCondition12Value1.getText().toString().trim()));
            condition.setFeatureValue2(Float.parseFloat(binding.faCondition12Value2.getText().toString().trim()));
            conditionList.add(condition);
        }

        //Condition13 条件
        if (!binding.faCondition13Value1.getText().toString().trim().isEmpty()) {
            if (binding.faCondition13Value2.getText().toString().trim().isEmpty()) return false;

            Condition condition = new Condition();

            for (int i = 0; i < 4; i++) {
                if (binding.faCondition13Day1.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay1(i);
                }
                if (binding.faCondition13Day2.getText().equals(DataUtils.stockDay[i])) {
                    if (i == 3) return false;

                    condition.setDay2(i);
                }
            }

            for (int i = 0; i < 6; i++) {
                if (binding.faCondition13Attribute1.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr1(i);
                }
                if (binding.faCondition13Attribute2.getText().equals(DataUtils.stockField[i])) {
                    if (i == 5) return false;

                    condition.setAttr2(i);
                }
            }

            condition.setFeatureValue(Float.parseFloat(binding.faCondition13Value1.getText().toString().trim()));
            condition.setFeatureValue2(Float.parseFloat(binding.faCondition13Value2.getText().toString().trim()));
            conditionList.add(condition);
        }

        if (conditionList.size() == 0) {
            return false;
        }

        LogUtils.d(TAG, "条件长度为：" + conditionList.size());

        DataSource.getInstance().setAnalysisParams(conditionList);

        return true;
    }

    private void doClickCondition13Day2() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition13Day2.setText(DataUtils.stockDay[position]));
    }

    private void doClickCondition13Attr2() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_field,
                position -> binding.faCondition13Attribute2.setText(DataUtils.stockField[position]));
    }

    private void doClickCondition13Day1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition13Day1.setText(DataUtils.stockDay[position]));
    }

    private void doClickConditio13Attr1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_field,
                position -> binding.faCondition13Attribute1.setText(DataUtils.stockField[position]));
    }

    private void doClickCondition12Day2() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition12Day2.setText(DataUtils.stockDay[position]));
    }

    private void doClickCondition12Attr2() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_field,
                position -> binding.faCondition12Attribute2.setText(DataUtils.stockField[position]));
    }

    private void doClickCondition12Day1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition12Day1.setText(DataUtils.stockDay[position]));
    }

    private void doClickConditio12Attr1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_field,
                position -> binding.faCondition12Attribute1.setText(DataUtils.stockField[position]));
    }

    private void doClickCondition11Day2() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition11Day2.setText(DataUtils.stockDay[position]));
    }

    private void doClickCondition11Attr2() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_field,
                position -> binding.faCondition11Attribute2.setText(DataUtils.stockField[position]));
    }

    private void doClickCondition11Day1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition11Day1.setText(DataUtils.stockDay[position]));
    }

    private void doClickConditio11Attr1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_field,
                position -> binding.faCondition11Attribute1.setText(DataUtils.stockField[position]));
    }

    private void doClickCondition10Day2() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition10Day2.setText(DataUtils.stockDay[position]));
    }

    private void doClickCondition10Attr2() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_field,
                position -> binding.faCondition10Attribute2.setText(DataUtils.stockField[position]));
    }

    private void doClickCondition10Day1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition10Day1.setText(DataUtils.stockDay[position]));
    }

    private void doClickConditio10Attr1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_field,
                position -> binding.faCondition10Attribute1.setText(DataUtils.stockField[position]));
    }

    private void doClickCondition9Up() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_volums_up_down,
                position -> binding.faCondition9UpTv.setText(DataUtils.volumesUpOrDown[position]));
    }

    private void doClickCondition9Day1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition9Day1.setText(DataUtils.stockDay[position]));
    }

    private void doClickCondition8Up() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_volums_up_down,
                position -> binding.faCondition8UpTv.setText(DataUtils.volumesUpOrDown[position]));
    }

    private void doClickCondition8Day1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition8Day1.setText(DataUtils.stockDay[position]));
    }

    private void doClickCondition7Up() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_volums_up_down,
                position -> binding.faCondition7UpTv.setText(DataUtils.volumesUpOrDown[position]));
    }

    private void doClickCondition7Day1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition7Day1.setText(DataUtils.stockDay[position]));
    }

    private void doClickCondition6Up() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_up_down,
                position -> binding.faCondition6UpTv.setText(DataUtils.upOrDown[position]));
    }

    private void doClickCondition6Day2() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition6Day2.setText(DataUtils.stockDay[position]));
    }

    private void doClickCondition6Attr2() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_field,
                position -> binding.faCondition6Attribute2.setText(DataUtils.stockField[position]));
    }

    private void doClickCondition6Day1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition6Day1.setText(DataUtils.stockDay[position]));
    }

    private void doClickConditio6Attr1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_field,
                position -> binding.faCondition6Attribute1.setText(DataUtils.stockField[position]));
    }

    private void doClickCondition5Up() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_up_down,
                position -> binding.faCondition5UpTv.setText(DataUtils.upOrDown[position]));
    }

    private void doClickCondition5Day2() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition5Day2.setText(DataUtils.stockDay[position]));
    }

    private void doClickCondition5Attr2() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_field,
                position -> binding.faCondition5Attribute2.setText(DataUtils.stockField[position]));
    }

    private void doClickCondition5Day1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition5Day1.setText(DataUtils.stockDay[position]));
    }

    private void doClickConditio5Attr1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_field,
                position -> binding.faCondition5Attribute1.setText(DataUtils.stockField[position]));
    }

    private void doClickCondition4Up() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_up_down,
                position -> binding.faCondition4UpTv.setText(DataUtils.upOrDown[position]));
    }

    private void doClickCondition4Day2() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition4Day2.setText(DataUtils.stockDay[position]));
    }

    private void doClickCondition4Attr2() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_field,
                position -> binding.faCondition4Attribute2.setText(DataUtils.stockField[position]));
    }

    private void doClickCondition4Day1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition4Day1.setText(DataUtils.stockDay[position]));
    }

    private void doClickConditio4Attr1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_field,
                position -> binding.faCondition4Attribute1.setText(DataUtils.stockField[position]));
    }

    private void doClickCondition3Up() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_up_down,
                position -> binding.faCondition3UpTv.setText(DataUtils.upOrDown[position]));
    }

    private void doClickCondition3Day2() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition3Day2.setText(DataUtils.stockDay[position]));
    }

    private void doClickCondition3Attr2() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_field,
                position -> binding.faCondition3Attribute2.setText(DataUtils.stockField[position]));
    }

    private void doClickCondition3Day1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition3Day1.setText(DataUtils.stockDay[position]));
    }

    private void doClickConditio3Attr1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_field,
                position -> binding.faCondition3Attribute1.setText(DataUtils.stockField[position]));
    }

    private void doClickCondition2Up() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_up_down,
                position -> binding.faCondition2UpTv.setText(DataUtils.upOrDown[position]));
    }

    private void doClickCondition2Day2() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition2Day2.setText(DataUtils.stockDay[position]));
    }

    private void doClickCondition2Attr2() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_field,
                position -> binding.faCondition2Attribute2.setText(DataUtils.stockField[position]));
    }

    private void doClickCondition2Day1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition2Day1.setText(DataUtils.stockDay[position]));
    }

    private void doClickConditio2Attr1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_field,
                position -> binding.faCondition2Attribute1.setText(DataUtils.stockField[position]));
    }

    private void doClickCondition1Up() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_up_down,
                position -> binding.faCondition1UpTv.setText(DataUtils.upOrDown[position]));
    }

    private void doClickCondition1Day2() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition1Day2.setText(DataUtils.stockDay[position]));
    }

    private void doClickCondition1Attr2() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_field,
                position -> binding.faCondition1Attribute2.setText(DataUtils.stockField[position]));
    }

    private void doClickCondition1Day1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_day,
                position -> binding.faCondition1Day1.setText(DataUtils.stockDay[position]));
    }

    private void doClickCondition1Attr1() {
        DialogUtils.showSingleChooseDialog(_mActivity,
                R.array.stock_field,
                position -> binding.faCondition1Attribute1.setText(DataUtils.stockField[position]));
    }
}
