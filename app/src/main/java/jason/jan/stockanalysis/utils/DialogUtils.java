package jason.jan.stockanalysis.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.NumberPicker;

import com.blankj.utilcode.util.AppUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import androidx.fragment.app.FragmentActivity;
import jason.jan.stockanalysis.R;
import me.yokeyword.fragmentation.SupportActivity;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2020/4/4 19:16
 */
public class DialogUtils {

    private static final String TAG = "DialogUtils";


    public interface ChooseDateCallback {

        void successChoose(String date);

    }

    public interface ChooseNumCallback {

        void successChoose(float num);
    }

    public interface IDialogTwoButtonCallback {

        void onPositiveCallback(DialogPlus dialog);

        void onNegativeCallback(DialogPlus dialog);
    }

    public interface SingleChooseCallback {

        void choosePosition(int position);
    }

    /**
     * 选择一个日期
     *
     * @param activity
     * @param callback
     */
    public static void chooseDate(FragmentActivity activity, ChooseDateCallback callback) {

        DatePickerDialog dpd = DatePickerDialog.newInstance((view, year, monthOfYear, dayOfMonth) -> {

            String month = (monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : "" + (monthOfYear + 1);
            String day = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;

            String date = year + "-" + month + "-" + day;

            callback.successChoose(date);

        });

        dpd.show(activity.getSupportFragmentManager(), TAG);
    }

    /**
     * 选择一个数字， 整数部分，小数部分
     *
     * @param mActivity
     * @param chooseNumCallback
     */
    public static void chooseNum(SupportActivity mActivity, ChooseNumCallback chooseNumCallback) {
        if (mActivity == null) return;

        final DialogPlus dialogPlus = DialogPlus.newDialog(mActivity)
                .setHasTitle(true)
                .setTitle("选择数字")
                .setTitleColor(Color.BLACK)
                .setContentHolder(new DialogPlus.ViewHolder(R.layout.dialog_num_tips))
                .setGravity(Gravity.CENTER)
                .setCancelable(false)
                .setInAnimation(R.anim.fade_in_center)
                .setOutAnimation(R.anim.fade_out_center)
                .setHasPositiveButton(true)
                .setHasNegativeButton(true)
                .setContentBackgroundResource(R.drawable.bg_corner_white)
                .setOnPositiveClickListener((dialog, view) -> {
                    dialog.dismiss();


                })
                .create();

        dialogPlus.show();
    }

    /**
     * 展示一个Tips 有两个按钮 都可以回到
     * eg: 打开定位服务 然后 两个按钮都可以回调
     */
    public static void showTipsThenCallback(Context context, String tips, IDialogTwoButtonCallback callback) {
        final DialogPlus dialogPlus = DialogPlus.newDialog(context)
                .setHasTitle(true)
                .setTitle("提示")
                .setContent(tips)
                .setContentHolder(new DialogPlus.ViewHolder(R.layout.dialog_tips))
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .setInAnimation(R.anim.fade_in_center)
                .setOutAnimation(R.anim.fade_out_center)
                .setHasPositiveButton(true)
                .setHasNegativeButton(true)
                .setContentBackgroundResource(R.drawable.bg_corner_white)
                .setOnPositiveClickListener((dialog, view) -> {
                    callback.onPositiveCallback(dialog);
                    dialog.dismiss();
                })
                .setOnNegativeClickListener(((dialog, view) -> {
                    callback.onNegativeCallback(dialog);
                    dialog.dismiss();
                }))
                .create();

        dialogPlus.show();

    }

    /**
     * 展示一个单选按钮组
     */
    public static void showSingleChooseDialog(Context context, int arrayRes, SingleChooseCallback callback) {
        final DialogPlus dialog = DialogPlus.newDialog(context)
                .setTitle("请选择：")
                .setTitleColorRes(Color.BLACK)
                .setArraryRes(arrayRes)
                .setInitSelectionPosition(0)
                .setOnItemClickListener((dialogPlus, view, position) -> {
                    callback.choosePosition(position);
                    dialogPlus.dismiss();
                })
                .setGravity(Gravity.BOTTOM)
                .setCancelable(true)
                .setContentBackgroundResource(R.drawable.bg_corner_top_gradient)
                .setHasNegativeButton(true)
                .setHasPositiveButton(false)
                .setOnNegativeClickListener((dialog1, view) -> {
                    dialog1.dismiss();
                })
                .setInAnimation(R.anim.slide_in_bottom)
                .setOutAnimation(R.anim.slide_out_bottom)
                .create();

        dialog.show();
    }

}
