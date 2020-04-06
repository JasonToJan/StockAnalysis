package jason.jan.stockanalysis.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jason.jan.stockanalysis.R;


/**
 * Description: 对话框构建者 简化三方库，使用本App的对话框
 * 参考https://github.com/orhanobut/dialogplus
 * *
 * Creator: Wang
 * Date: 2019/12/30 20:52
 */
public class DialogPlus {

    private static final String TAG = "DialogPlus";

    private static final int INVALID = -1;

    /**
     * DialogPlus base layout root view
     */
    private final ViewGroup rootView;

    /**
     * DialogPlus content container which is a different layout rather than base layout
     */
    private final ViewGroup contentContainer;

    /**
     * Determines whether dialog should be dismissed by back button or touch in the black overlay
     */
    private final boolean isCancelable;

    /**
     * Determines whether dialog is showing dismissing animation and avoid to repeat it
     */
    private boolean isDismissing;

    /**
     * Listener for the user to take action by clicking any item
     */
    private final OnItemClickListener onItemClickListener;

    /**
     * Listener for the user to take action by clicking views in header or footer
     */
//    private final OnClickListener onClickListener;

    /**
     * 确定按钮点击事件
     */
    private OnClickListener onPositiveClickListener;

    /**
     * 取消按钮点击事件
     */
    private OnClickListener onNegativeClickListener;

    /**
     * Listener to notify the user that dialog has been dismissed
     */
    private final OnDismissListener onDismissListener;

    /**
     * Listener to notify the user that dialog has been canceled
     */
    private final OnCancelListener onCancelListener;

    /**
     * Listener to notify back press
     */
    private final OnBackPressListener onBackPressListener;

    /**
     * Listener to notify back press
     */
    private final OnCheckChangedListener onCheckChangedListener;

    /**
     * Content
     */
    private final Holder holder;

    /**
     * basically activity root view
     */
    private final ViewGroup decorView;
    /**
     * 对话框 销毁动画
     */
    private final Animation outAnim;
    /**
     * 对话框 弹出动画
     */
    private final Animation inAnim;
    /**
     * recyclerView的布局管理器
     */
    private LinearLayoutManager recycleLinearManager;

    DialogPlus(DialogPlusBuilder builder) {
        LayoutInflater layoutInflater = LayoutInflater.from(builder.getContext());

        Activity activity = (Activity) builder.getContext();

        if (recycleLinearManager == null) {
            recycleLinearManager = new LinearLayoutManager(activity);
        }

        holder = builder.getHolder();

        //传入的监听器
        onItemClickListener = builder.getOnItemClickListener();
        onPositiveClickListener = builder.getOnPositiveListener();
        onNegativeClickListener = builder.getOnNegativeClickListener();
        onDismissListener = builder.getOnDismissListener();
        onCancelListener = builder.getOnCancelListener();
        onBackPressListener = builder.getOnBackPressListener();
        onCheckChangedListener = builder.getOnCheckChangedListener();

        isCancelable = builder.isCancelable();

        /*
         * Avoid getting directly from the decor view because by doing that we are overlapping the black soft key on
         * nexus device. I think it should be tested on different devices but in my opinion is the way to go.
         * @link http://stackoverflow.com/questions/4486034/get-root-view-from-current-activity
         */
        //通过activity的decorView来 动态加载一个布局
        decorView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        rootView = (ViewGroup) layoutInflater.inflate(R.layout.dialog_base_container, decorView, false);
        rootView.setLayoutParams(builder.getOutmostLayoutParams());

        //设置背景 给最顶层视图
        View outmostView = rootView.findViewById(R.id.dialogplus_outmost_container);
        outmostView.setBackgroundResource(builder.getOverlayBackgroundResource());

        //内容区域设置布局参数，可以指定大小
        contentContainer = rootView.findViewById(R.id.dialogplus_content_container);
        contentContainer.setLayoutParams(builder.getContentParams());

        //动画效果
        outAnim = builder.getOutAnimation();
        inAnim = builder.getInAnimation();

//        initContentView(
//                layoutInflater,
//                builder.getAdapter(),
//                recycleLinearManager,
//                builder.getContentPadding(),
//                builder.getContentMargin()
//        );

        initContentView(
                layoutInflater,
                builder,
                recycleLinearManager
        );

        initCancelable();
    }

    /**
     * It is called to set whether the dialog is cancellable by pressing back button or
     * touching the black overlay
     */
    private void initCancelable() {
        if (!isCancelable) {
            return;
        }
        View view = rootView.findViewById(R.id.dialogplus_outmost_container);
        view.setOnTouchListener(onCancelableTouchListener);
    }

    /**
     * It is called in order to create content
     */
    private void initContentView(LayoutInflater inflater, DialogPlusBuilder builder, LinearLayoutManager manager) {
        View contentView = createView(inflater, builder.getAdapter(), manager);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        int[] margin = builder.getContentMargin();
        int[] padding = builder.getContentPadding();
        params.setMargins(margin[0], margin[1], margin[2], margin[3]);
        contentView.setLayoutParams(params);
        getHolderView().setPadding(padding[0], padding[1], padding[2], padding[3]);
        contentContainer.addView(contentView);
    }

    /**
     * Returns holder view.
     */
    public View getHolderView() {
        return holder.getInflatedView();
    }

    /**
     * 创建一个内容区域
     * it is called when the content view is created
     *
     * @param inflater used to inflate the content of the dialog
     * @return any view which is passed
     */
    private View createView(LayoutInflater inflater, MyRecyclerViewAdapter adapter, LinearLayoutManager manager) {
        View view = holder.getView(inflater, rootView);//加载的父布局带recyclerView

        if (adapter != null && holder instanceof HolderAdapter) {
            HolderAdapter holderAdapter = (HolderAdapter) holder;//ListHolder
            holderAdapter.setAdapter(adapter, manager);
        }
        return view;
    }

    /**
     * Dismisses the displayed dialog.
     */
    public void dismiss() {
        if (isDismissing) {
            return;
        }

        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                decorView.post(new Runnable() {
                    @Override
                    public void run() {
                        decorView.removeView(rootView);
                        isDismissing = false;
                        if (onDismissListener != null) {
                            onDismissListener.onDismiss(DialogPlus.this);
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        contentContainer.startAnimation(outAnim);
        isDismissing = true;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void animateContent(final View view, int to, Animation.AnimationListener listener) {
        HeightAnimation animation = new HeightAnimation(view, view.getHeight(), to);
        animation.setAnimationListener(listener);
        animation.setDuration(200);
        view.startAnimation(animation);
    }

    public static boolean listIsAtTop(AbsListView listView) {
        return listView.getChildCount() == 0 || listView.getChildAt(0).getTop() == listView.getPaddingTop();
    }

    /**
     * This will be called in order to create view, if the given view is not null,
     * it will be used directly, otherwise it will check the resourceId
     *
     * @return null if both resourceId and view is not set
     */
    public static View getView(Context context, int resourceId, View view) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (view != null) {
            return view;
        }
        if (resourceId != INVALID) {
            view = inflater.inflate(resourceId, null);
        }
        return view;
    }

    /**
     * Get default animation resource when not defined by the user
     *
     * @param gravity       the gravity of the dialog
     * @param isInAnimation determine if is in or out animation. true when is is
     * @return the id of the animation resource
     */
    public static int getAnimationResource(int gravity, boolean isInAnimation) {
        if ((gravity & Gravity.TOP) == Gravity.TOP) {
            return isInAnimation ? R.anim.slide_in_top : R.anim.slide_out_top;
        }
        if ((gravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
            return isInAnimation ? R.anim.slide_in_bottom : R.anim.slide_out_bottom;
        }
        if ((gravity & Gravity.CENTER) == Gravity.CENTER) {
            return isInAnimation ? R.anim.fade_in_center : R.anim.fade_out_center;
        }
        return INVALID;
    }

    /**
     * Creates a new dialog builder
     */
    public static DialogPlusBuilder newDialog(@NonNull Context context) {
        return new DialogPlusBuilder(context);
    }

    /**
     * Displays the dialog if it is not shown already.
     */
    public void show() {
        if (isShowing()) {
            return;
        }
        onAttached(rootView);
    }

    /**
     * Checks if the dialog is shown
     */
    public boolean isShowing() {
        View view = decorView.findViewById(R.id.dialogplus_outmost_container);
        return view != null;
    }

    /**
     * 改变Loading文字
     */
    public void changeLoadingTxt(String str) {
        if (holder != null) {
            if (holder instanceof ViewHolder) {
                ViewHolder viewHolder = (ViewHolder) holder;
                if (viewHolder.getLoadingTextView() != null) {
                    viewHolder.getLoadingTextView().setText(str);
                }
            }
        }
    }

    /**
     * 改变Loading文字
     */
    public void changeLoadingTxtRes(int strRes) {
        if (holder != null) {
            if (holder instanceof ViewHolder) {
                ViewHolder viewHolder = (ViewHolder) holder;
                if (viewHolder.getLoadingTextView() != null) {
                    viewHolder.getLoadingTextView().setText(strRes);
                }
            }
        }
    }

    /**
     * It is called when the show() method is called
     *
     * @param view is the dialog plus view
     */
    private void onAttached(View view) {
        decorView.addView(view);
        contentContainer.startAnimation(inAnim);

        contentContainer.requestFocus();
        holder.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (event.getAction()) {
                    case KeyEvent.ACTION_UP:
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            if (onBackPressListener != null) {
                                onBackPressListener.onBackPressed(DialogPlus.this);
                            }
                            if (isCancelable) {
                                onBackPressed(DialogPlus.this);
                            }
                            return true;
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    /**
     * Invoked when back button is pressed. Automatically dismiss the dialog.
     */
    public void onBackPressed(@NonNull DialogPlus dialogPlus) {
        if (onCancelListener != null) {
            onCancelListener.onCancel(DialogPlus.this);
        }
        dismiss();
    }

    /**
     * 选择框监听
     */
    public interface OnCheckChangedListener {

        void onCheckedChanged(@NonNull DialogPlus dialogPlus, CompoundButton button, boolean isChecked);
    }

    /**
     * DialogPlus tries to listen back press actions.
     */
    public interface OnBackPressListener {

        /**
         * Invoked when DialogPlus receives any back press button event.
         */
        void onBackPressed(@NonNull DialogPlus dialogPlus);

    }

    /**
     * DialogPlus will use this listener to propagate cancel events when back button is pressed.
     */
    public interface OnCancelListener {

        void onCancel(@NonNull DialogPlus dialog);
    }

    /**
     * Used to propagate click events from all views within DialogPlus.
     * <p>
     * <p>DialogPlus recursively traverse all views and listens on click by default when
     * holder is ViewHolder. </p>
     */
    public interface OnClickListener {

        /**
         * Invoked when any view within ViewHolder is clicked.
         *
         * @param view is the clicked view
         */
        void onClick(@NonNull DialogPlus dialog, @NonNull View view);

    }

    /**
     * Invoked when dialog is completely dismissed. This listener takes the animation into account and waits for it.
     *
     * <p>It is invoked after animation is completed</p>
     */
    public interface OnDismissListener {
        void onDismiss(@NonNull DialogPlus dialog);
    }

    public interface OnHolderListener {

        void onItemClick(@NonNull Object item, @NonNull View view, int position);

    }

    /**
     * It is used to propagate click events for {@link ListHolder} and {@link }
     *
     * <p>For each item click, {@link #(DialogPlus, Object, View, int)} will be invoked</p>
     */
    public interface OnItemClickListener {

        void onItemClick(DialogPlus dialogPlus, @NonNull View view, int position);

    }

    public interface Holder {

        void setBuilder(DialogPlusBuilder builder);

        @NonNull
        View getView(@NonNull LayoutInflater inflater, ViewGroup parent);

        void setOnKeyListener(View.OnKeyListener keyListener);

        @NonNull
        View getInflatedView();

    }

    public interface HolderAdapter extends Holder {

        void setAdapter(@NonNull MyRecyclerViewAdapter adapter, LinearLayoutManager linearLayoutManager);
    }

    /**
     * 抽象的RecyclerViewAdapter 自己的Adapter 一定要继承这个 才能实现item监听
     */
    public static abstract class MyRecyclerViewAdapter<T extends MyViewHolder> extends RecyclerView.Adapter<T> {

        DialogPlusBuilder dialogPlusBuilder;

        public void setDialogPlusBuilder(DialogPlusBuilder builder) {
            this.dialogPlusBuilder = builder;
        }

        public DialogPlusBuilder getDialogPlusBuilder() {
            return dialogPlusBuilder;
        }
    }

    /**
     * 抽象的视图持有者 这样 点击事件可以在这里完成
     */
    public static abstract class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(DialogPlusBuilder dialogPlusBuilder, @NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            if (listener != null) {
                itemView.setOnClickListener(view -> {
                    int onClickItemPosition = getAdapterPosition();
                    listener.onItemClick(dialogPlusBuilder.getDialogPlus(), view, onClickItemPosition);
                    clickedThendoOtherThings(view, onClickItemPosition);
                });
            }
        }

        /**
         * 这里可以处理自己其他逻辑
         */
        abstract void clickedThendoOtherThings(View view, int position);

    }

    /**
     * Called when the user touch on black overlay in order to dismiss the dialog
     */
    private final View.OnTouchListener onCancelableTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (onCancelListener != null) {
                    onCancelListener.onCancel(DialogPlus.this);
                }
                dismiss();
            }
            return false;
        }
    };

    public static class HeightAnimation extends Animation {

        private final int originalHeight;
        private float perValue;
        private final View view;

        HeightAnimation(View view, int fromHeight, int toHeight) {
            this.view = view;
            this.originalHeight = fromHeight;
            this.perValue = (toHeight - fromHeight);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            view.getLayoutParams().height = (int) (originalHeight + perValue * interpolatedTime);
            view.requestLayout();
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    public class SimpleAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    /**
     * 这个ViewHoldere可以用来创造 更多可能性 比如中间一个EditText 这种类型轻而易举
     */
    public static class ViewHolder implements Holder {

        private static final int INVALID = -1;

        private int backgroundResource;

        private View.OnKeyListener keyListener;
        private View contentView;
        private int viewResourceId = INVALID;

        /**
         * loading图片
         */
        private ImageView loadingImageView;
        /**
         * 加载文本
         */
        private TextView loadingTextView;
        /**
         * 构造器
         */
        private DialogPlusBuilder builder;

        public ViewHolder(int viewResourceId) {
            this.viewResourceId = viewResourceId;
        }

        public ViewHolder(View contentView) {
            this.contentView = contentView;
        }


        @Override
        public void setBuilder(DialogPlusBuilder builder) {
            this.builder = builder;
        }

        @Override
        @NonNull
        public View getView(@NonNull LayoutInflater inflater, ViewGroup parent) {
            View view = inflater.inflate(R.layout.dialog_base_view, parent, false);
            View outMostView = view.findViewById(R.id.dialogplus_outmost_container);
            outMostView.setBackgroundResource(builder.contentBackgroundResource);
            ViewGroup contentContainer = view.findViewById(R.id.dialogplus_view_container);
            contentContainer.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyListener == null) {
                        throw new NullPointerException("keyListener should not be null");
                    }
                    return keyListener.onKey(v, keyCode, event);
                }
            });
            addContent(inflater, parent, contentContainer);
            return view;
        }

        private void addContent(LayoutInflater inflater, ViewGroup parent, ViewGroup container) {
            if (viewResourceId != INVALID) {
                contentView = inflater.inflate(viewResourceId, parent, false);
            } else {
                ViewGroup parentView = (ViewGroup) contentView.getParent();
                if (parentView != null) {
                    parentView.removeView(contentView);
                }
            }

            container.addView(contentView);

            //初始化一些自定义的东西
            initBuilder(builder, contentView);

        }

        @Override
        public void setOnKeyListener(View.OnKeyListener keyListener) {
            this.keyListener = keyListener;
        }

        @Override
        @NonNull
        public View getInflatedView() {
            return contentView;
        }

        public TextView getLoadingTextView() {
            return loadingTextView;
        }

        public void setLoadingTextView(TextView loadingTextView) {
            this.loadingTextView = loadingTextView;

        }
    }

    /**
     * 默认传一个这个ListHolder
     */
    public static class ListHolder implements HolderAdapter {

        private RecyclerView listView;
        private View.OnKeyListener keyListener;
        private DialogPlusBuilder builder;

        ListHolder(DialogPlusBuilder builder) {
            this.builder = builder;
        }

        @Override
        public void setAdapter(@NonNull MyRecyclerViewAdapter adapter, LinearLayoutManager manager) {

            adapter.setDialogPlusBuilder(this.builder);//一定要把builder给他，否则拿不到dialog了
            listView.setLayoutManager(manager);
            listView.setAdapter(adapter);

        }

        @Override
        public void setBuilder(DialogPlusBuilder builder) {
            this.builder = builder;
        }

        @Override
        @NonNull
        public View getView(@NonNull LayoutInflater inflater, ViewGroup parent) {
            if (builder == null) {
                throw new NullPointerException("the builder is null,please checked the builder.");
            }

            View view = inflater.inflate(R.layout.dialog_base_list, parent, false);
            View outMostView = view.findViewById(R.id.dialogplus_outmost_container);
            outMostView.setBackgroundResource(this.builder.contentBackgroundResource);
            listView = view.findViewById(R.id.dialogplus_list);
            listView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyListener == null) {
                        throw new NullPointerException("keyListener should not be null");
                    }
                    return keyListener.onKey(v, keyCode, event);
                }
            });

            //初始化一些自定义的东西
            initBuilder(builder, view);


            return view;
        }

        @Override
        public void setOnKeyListener(View.OnKeyListener keyListener) {
            this.keyListener = keyListener;
        }

        @Override
        @NonNull
        public View getInflatedView() {
            return listView;
        }


    }

    /**
     * 自定义的一些功能
     *
     * @param rootView
     */
    private static void initBuilder(DialogPlusBuilder builder, View rootView) {
        if (builder == null) {
            throw new NullPointerException("the builder is null,please checked the builder.");
        }

        try {

            //标题
            TextView titleTextView = rootView.findViewById(R.id.dialogplus_top_title_tv);
            if (titleTextView == null) return;

            if (builder.titleRes != 0) {
                titleTextView.setText(builder.titleRes);
            } else if (!TextUtils.isEmpty(builder.title)) {
                titleTextView.setText(builder.title);
            }

            if (builder.titleSize != 0) {
                titleTextView.setTextSize(builder.titleSize);
            }

            if (builder.titleColor != 0) {
                titleTextView.setTextColor(builder.titleColor);
            }

            if (builder.titleGravity != 0) {
                titleTextView.setGravity(builder.titleGravity);
            }


            if (!builder.hasTitle) {//没有标题
                rootView.findViewById(R.id.dialogplus_top_title_ll).setVisibility(View.GONE);
            }

            //按钮相关
            LinearLayout bottomLi = rootView.findViewById(R.id.dialogplus_bottom_ll);
            bottomLi.setVisibility(builder.hasButton ? View.VISIBLE : View.GONE);
            if (!builder.hasButton) {
                return;//没有按钮的话 可以直接return了
            }

            //确定按钮
            Button positiveButton = rootView.findViewById(R.id.dialogplus_positive_btn);
            if (builder.hasPositiveButton) {
                positiveButton.setVisibility(View.VISIBLE);
                if (builder.positiveTextRes != 0) {
                    positiveButton.setText(builder.positiveTextRes);
                } else if (!TextUtils.isEmpty(builder.positiveText)) {
                    positiveButton.setText(builder.positiveText);
                } else {
                    positiveButton.setText("好的");
                }

                if (builder.positiveButtonBgRes != 0) {
                    positiveButton.setBackgroundResource(builder.positiveButtonBgRes);
                }

                if (builder.positiveButtonTextColorRes != 0) {
                    positiveButton.setTextColor(builder.getContext().getResources().getColor(builder.positiveButtonTextColorRes));
                } else if (builder.positiveButtonTextColor != 0) {
                    positiveButton.setTextColor(builder.positiveButtonTextColor);
                }

                if (builder.positiveButtonTextSize != 0) {
                    positiveButton.setTextSize(builder.positiveButtonTextSize);
                }

                //监听处理下
                positiveButton.setOnClickListener(view ->
                        builder.onPositiveClickListener.onClick(builder.getDialogPlus(), view));

            } else {
                positiveButton.setVisibility(View.GONE);
            }

            //取消按钮
            Button negativeButton = rootView.findViewById(R.id.dialogplus_negative_btn);
            if (builder.hasNegativeButton) {
                negativeButton.setVisibility(View.VISIBLE);
                if (builder.negativeTextRes != 0) {
                    negativeButton.setText(builder.negativeTextRes);
                } else if (!TextUtils.isEmpty(builder.negativeText)) {
                    negativeButton.setText(builder.negativeText);
                } else {
                    negativeButton.setText("取消");
                }

                if (builder.negativeButtonBgRes != 0) {
                    negativeButton.setBackgroundResource(builder.negativeButtonBgRes);
                } else if (builder.negativeButtonStyleSameWithPositive) {
                    if (builder.positiveButtonBgRes != 0) {
                        negativeButton.setBackgroundResource(builder.positiveButtonBgRes);
                    }
                }

                if (builder.negativeButtonTextColorRes != 0) {
                    negativeButton.setTextColor(builder.getContext().getResources().getColor(builder.negativeButtonTextColorRes));
                } else if (builder.negativeButtonTextColor != 0) {
                    negativeButton.setTextColor(builder.negativeButtonTextColor);
                } else if (builder.negativeButtonStyleSameWithPositive) {
                    if (builder.positiveButtonTextColorRes != 0) {
                        negativeButton.setTextColor(builder.positiveButtonTextColorRes);
                    } else if (builder.positiveButtonTextColor != 0) {
                        negativeButton.setTextColor(builder.positiveButtonTextColor);
                    }
                }

                if (builder.negativeButtonTextSize != 0) {
                    negativeButton.setTextSize(builder.negativeButtonTextSize);
                } else if (builder.negativeButtonStyleSameWithPositive) {
                    if (builder.positiveButtonTextSize != 0) {
                        negativeButton.setTextSize(builder.negativeButtonTextSize);
                    }
                }

                negativeButton.setOnClickListener(view ->
                        builder.onNegativeClickListener.onClick(builder.getDialogPlus(), view));

            } else {
                negativeButton.setVisibility(View.GONE);
            }

            //内容区域 只有部分tip才有的
            TextView contentView = rootView.findViewById(R.id.dialogplus_content_tv);
            if (contentView != null) {

                if (builder.contentRes != 0) {
                    contentView.setText(builder.contentRes);
                } else if (!TextUtils.isEmpty(builder.content)) {
                    contentView.setText(builder.content);
                }

                if (builder.contentSize != 0) {
                    contentView.setTextSize(builder.contentSize);
                }

                if (builder.contentColor != 0) {
                    contentView.setTextColor(builder.contentColor);
                } else if (builder.contentColorRes != 0) {
                    contentView.setTextColor(builder.getContext().getResources().getColor(builder.contentColorRes));
                }

                if (builder.contentGravity != 0) {
                    contentView.setGravity(builder.contentGravity);
                }
            }


            initNumberPicker(builder,rootView);


        } catch (Throwable e) {
            LogUtils.e("TEST##Error", "##" + e.getMessage());
        }

    }

    private static void initNumberPicker(DialogPlusBuilder builder, View rootView) {

        NumberPicker firstPicker = rootView.findViewById(R.id.dialogplus_first_np);
        if (firstPicker == null) return;

        NumberPicker secondPicker = rootView.findViewById(R.id.dialogplus_second_np);
        NumberPicker endPicker = rootView.findViewById(R.id.dialogplus_end_np);

        if (secondPicker == null || endPicker == null) return;

        firstPicker.setMaxValue(99);
        firstPicker.setMinValue(0);
        secondPicker.setMaxValue(99);
        secondPicker.setMinValue(0);
        endPicker.setMaxValue(99);
        endPicker.setMinValue(0);


    }


    public static class DialogPlusBuilder {
        private static final int INVALID = -1;

        private final int[] margin = new int[4];
        private final int[] padding = new int[4];
        private final int[] outMostMargin = new int[4];
        private final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM
        );

        private MyRecyclerViewAdapter adapter;
        private Context context;
        private Holder holder;
        private int gravity = Gravity.BOTTOM;
        private OnItemClickListener onItemClickListener;
        private OnClickListener onPositiveClickListener;
        private OnClickListener onNegativeClickListener;
        private OnDismissListener onDismissListener;
        private OnCancelListener onCancelListener;
        private OnBackPressListener onBackPressListener;
        private OnCheckChangedListener onCheckChangedListener;

        private boolean isCancelable = true;
        private int contentBackgroundResource = android.R.color.white;
        private int inAnimation = INVALID;
        private int outAnimation = INVALID;
        private boolean expanded;
        private int defaultContentHeight;
        private int overlayBackgroundResource = R.color.dialogplus_black_overlay;

        private int arrayRes;//从资源文件中传过来的
        private String[] strings;//或者直接传字符串数组也可以
        private List<String> stringList;//或者传递List也行叭
        private int initSelectionPosition;//最初默认选择项
        private String title;
        private String loadingTxt;
        private int loadingTxtRes;
        private int titleRes;
        private int titleColor;//文字颜色
        private int titleColorRes;//文字颜色，传资源id
        private int titleSize;//文字大小
        private int titleGravity;//文字方向 左右或居中
        private String content;//内容区域
        private int contentRes;
        private int contentColor;//文字颜色
        private int contentColorRes;//文字颜色，传资源id
        private int contentSize;//文字大小
        private int contentGravity;//文字方向 左右或居中
        private boolean hasTitle = true;//是否有标题，否则隐藏标题的布局
        private boolean hasButton = true;//是否有button,否则隐藏button的布局
        private boolean hasPositiveButton = false;//是否有确定按钮，否则隐藏
        private boolean hasNegativeButton = true;//是否有取消按钮，否则隐藏
        private int positiveButtonTextColor;//确定按钮颜色
        private int positiveButtonTextColorRes;//确定按钮颜色资源id
        private int positiveButtonTextSize;//确定按钮文本大小
        private String positiveText;
        private int positiveTextRes;
        private int positiveButtonBgRes;//确定按钮背景，传drawableid
        private boolean negativeButtonStyleSameWithPositive = true;
        private int negativeButtonTextColor;//确定按钮颜色
        private int negativeButtonTextColorRes;//确定按钮颜色资源id
        private int negativeButtonTextSize;//确定按钮文本大小
        private int negativeButtonBgRes;//确定按钮背景，传drawableid
        private String negativeText;
        private int negativeTextRes;
        public DialogPlus targetDialogPlus;

        private DialogPlusBuilder() {
        }

        /**
         * Initialize the builder with a valid context in order to inflate the dialog
         */
        DialogPlusBuilder(@NonNull Context context) {
            this.context = context;
            Arrays.fill(margin, INVALID);
        }

        /**
         * Set the adapter that will be used when ListHolder or GridHolder are passed
         */
        public DialogPlusBuilder setAdapter(@NonNull MyRecyclerViewAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        /**
         * 传递资源id
         *
         * @param arraryRes
         * @return
         */
        public DialogPlusBuilder setArraryRes(int arraryRes) {
            this.arrayRes = arraryRes;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param selectionPosition
         * @return
         */
        public DialogPlusBuilder setInitSelectionPosition(int selectionPosition) {
            this.initSelectionPosition = selectionPosition;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param title
         * @return
         */
        public DialogPlusBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param titleRes
         * @return
         */
        public DialogPlusBuilder setTitleRes(int titleRes) {
            this.titleRes = titleRes;
            return this;
        }

        /**
         * 加载文本
         *
         * @param loadingTxt
         */
        public DialogPlusBuilder setLoadingTxt(String loadingTxt) {
            this.loadingTxt = loadingTxt;
            return this;
        }

        /**
         * 加载文本
         *
         * @param loadingTxtRes
         */
        public DialogPlusBuilder setLoadingTxtRes(int loadingTxtRes) {
            this.loadingTxtRes = loadingTxtRes;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param titleColor
         * @return
         */
        public DialogPlusBuilder setTitleColor(int titleColor) {
            this.titleColor = titleColor;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param titleColorRes
         * @return
         */
        public DialogPlusBuilder setTitleColorRes(int titleColorRes) {
            this.titleColorRes = titleColorRes;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param titleSize
         * @return
         */
        public DialogPlusBuilder setTitleSize(int titleSize) {
            this.titleSize = titleSize;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param titleGravity
         * @return
         */
        public DialogPlusBuilder setTitleGravity(int titleGravity) {
            this.titleGravity = titleGravity;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param content
         * @return
         */
        public DialogPlusBuilder setContent(String content) {
            this.content = content;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param contentRes
         * @return
         */
        public DialogPlusBuilder setContentRes(int contentRes) {
            this.contentRes = contentRes;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param contentColor
         * @return
         */
        public DialogPlusBuilder setContentColor(int contentColor) {
            this.contentColor = contentColor;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param contentColorRes
         * @return
         */
        public DialogPlusBuilder setContentColorRes(int contentColorRes) {
            this.contentColorRes = contentColorRes;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param contentSize
         * @return
         */
        public DialogPlusBuilder setContentSize(int contentSize) {
            this.contentSize = contentSize;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param contentGravity
         * @return
         */
        public DialogPlusBuilder setContentGravity(int contentGravity) {
            this.contentGravity = contentGravity;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param hasTitle
         * @return
         */
        public DialogPlusBuilder setHasTitle(boolean hasTitle) {
            this.hasTitle = hasTitle;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param hasButton
         * @return
         */
        public DialogPlusBuilder setHasButton(boolean hasButton) {
            this.hasButton = hasButton;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param positiveButtonText
         * @return
         */
        public DialogPlusBuilder setPositiveButtonText(String positiveButtonText) {
            this.positiveText = positiveButtonText;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param positiveButtonTextRes
         * @return
         */
        public DialogPlusBuilder setPositiveButtonTextRes(int positiveButtonTextRes) {
            this.positiveTextRes = positiveButtonTextRes;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param hasPositiveButton
         * @return
         */
        public DialogPlusBuilder setHasPositiveButton(boolean hasPositiveButton) {
            this.hasPositiveButton = hasPositiveButton;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param hasNegativeButton
         * @return
         */
        public DialogPlusBuilder setHasNegativeButton(boolean hasNegativeButton) {
            this.hasNegativeButton = hasNegativeButton;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param positiveButtonTextColor
         * @return
         */
        public DialogPlusBuilder setPositiveButtonTextColor(int positiveButtonTextColor) {
            this.positiveButtonTextColor = positiveButtonTextColor;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param positiveButtonTextColorRes
         * @return
         */
        public DialogPlusBuilder setPositiveButtonTextColorRes(int positiveButtonTextColorRes) {
            this.positiveButtonTextColorRes = positiveButtonTextColorRes;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param positiveButtonTextSize
         * @return
         */
        public DialogPlusBuilder setPositiveButtonTextSize(int positiveButtonTextSize) {
            this.positiveButtonTextSize = positiveButtonTextSize;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param positiveButtonBgRes
         * @return
         */
        public DialogPlusBuilder setPositiveButtonBgRes(int positiveButtonBgRes) {
            this.positiveButtonBgRes = positiveButtonBgRes;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param isSame
         * @return
         */
        public DialogPlusBuilder setNegativeButtonStyleSameWithPositive(boolean isSame) {
            this.negativeButtonStyleSameWithPositive = isSame;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param negativeButtonTextColor
         * @return
         */
        public DialogPlusBuilder setNegativeButtonTextColor(int negativeButtonTextColor) {
            this.negativeButtonTextColor = negativeButtonTextColor;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param negativeButtonTextColorRes
         * @return
         */
        public DialogPlusBuilder setNegativeButtonTextColorRes(int negativeButtonTextColorRes) {
            this.negativeButtonTextColorRes = negativeButtonTextColorRes;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param negativeButtonTextSize
         * @return
         */
        public DialogPlusBuilder setNegativeButtonTextSize(int negativeButtonTextSize) {
            this.negativeButtonTextSize = negativeButtonTextSize;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param negativeButtonBgRes
         * @return
         */
        public DialogPlusBuilder setNegativeButtonBgRes(int negativeButtonBgRes) {
            this.negativeButtonBgRes = negativeButtonBgRes;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param negativeButtonText
         * @return
         */
        public DialogPlusBuilder setNegativeButtonText(String negativeButtonText) {
            this.negativeText = negativeButtonText;
            return this;
        }

        /**
         * 设置 自定义属性相关
         *
         * @param negativeButtonTextRes
         * @return
         */
        public DialogPlusBuilder setNegativeButtonTextRes(int negativeButtonTextRes) {
            this.negativeTextRes = negativeButtonTextRes;
            return this;
        }


        /**
         * 传递字符串数组
         *
         * @param strings
         * @return
         */
        public DialogPlusBuilder setStrings(String[] strings) {
            this.strings = strings;
            return this;
        }

        /**
         * 传递字符串数组
         *
         * @param stringList
         * @return
         */
        public DialogPlusBuilder setStringList(List<String> stringList) {
            this.stringList = stringList;
            return this;
        }

        /**
         * Define if the dialog is cancelable and should be closed when back pressed or click outside is pressed
         */
        public DialogPlusBuilder setCancelable(boolean isCancelable) {
            this.isCancelable = isCancelable;
            return this;
        }

        /**
         * Set the content of the dialog by passing one of the provided Holders
         */
        public DialogPlusBuilder setContentHolder(@NonNull Holder holder) {
            this.holder = holder;
            return this;
        }

        /**
         * Use setBackgroundResource
         */
        @Deprecated
        public DialogPlusBuilder setBackgroundColorResId(int resourceId) {
            return setContentBackgroundResource(resourceId);
        }

        /**
         * Set background color for your dialog. If no resource is passed 'white' will be used
         */
        public DialogPlusBuilder setContentBackgroundResource(int resourceId) {
            this.contentBackgroundResource = resourceId;
            return this;
        }

        public DialogPlusBuilder setOverlayBackgroundResource(int resourceId) {
            this.overlayBackgroundResource = resourceId;
            return this;
        }

        /**
         * Set the gravity you want the dialog to have among the ones that are provided
         */
        public DialogPlusBuilder setGravity(int gravity) {
            this.gravity = gravity;
            params.gravity = gravity;
            return this;
        }

        /**
         * Customize the in animation by passing an animation resource
         */
        public DialogPlusBuilder setInAnimation(int inAnimResource) {
            this.inAnimation = inAnimResource;
            return this;
        }

        /**
         * Customize the out animation by passing an animation resource
         */
        public DialogPlusBuilder setOutAnimation(int outAnimResource) {
            this.outAnimation = outAnimResource;
            return this;
        }

        /**
         * Add margins to your outmost view which contains everything. As default they are 0
         * are applied
         */
        public DialogPlusBuilder setOutMostMargin(int left, int top, int right, int bottom) {
            this.outMostMargin[0] = left;
            this.outMostMargin[1] = top;
            this.outMostMargin[2] = right;
            this.outMostMargin[3] = bottom;
            return this;
        }

        /**
         * Add margins to your dialog. They are set to 0 except when gravity is center. In that case basic margins
         * are applied
         */
        public DialogPlusBuilder setMargin(int left, int top, int right, int bottom) {
            this.margin[0] = left;
            this.margin[1] = top;
            this.margin[2] = right;
            this.margin[3] = bottom;
            return this;
        }

        /**
         * Set paddings for the dialog content
         */
        public DialogPlusBuilder setPadding(int left, int top, int right, int bottom) {
            this.padding[0] = left;
            this.padding[1] = top;
            this.padding[2] = right;
            this.padding[3] = bottom;
            return this;
        }

        /**
         * Set an item click listener when list or grid holder is chosen. In that way you can have callbacks when one
         * of your items is clicked
         */
        public DialogPlusBuilder setOnItemClickListener(OnItemClickListener listener) {
            this.onItemClickListener = listener;
            return this;
        }

        /**
         * Set a global click listener to you dialog in order to handle all the possible click events. You can then
         * identify the view by using its id and handle the correct behaviour
         */
        public DialogPlusBuilder setOnPositiveClickListener(@Nullable OnClickListener onPositiveClickListener) {
            this.onPositiveClickListener = onPositiveClickListener;
            return this;
        }

        public DialogPlusBuilder setOnNegativeClickListener(@Nullable OnClickListener onNegativeClickListener) {
            this.onNegativeClickListener = onNegativeClickListener;
            return this;
        }

        public DialogPlusBuilder setOnCheckChangedListener(@Nullable OnCheckChangedListener onCheckChangedListener) {
            this.onCheckChangedListener = onCheckChangedListener;
            return this;
        }

        public DialogPlusBuilder setOnDismissListener(@Nullable OnDismissListener listener) {
            this.onDismissListener = listener;
            return this;
        }

        public DialogPlusBuilder setOnCancelListener(@Nullable OnCancelListener listener) {
            this.onCancelListener = listener;
            return this;
        }

        public DialogPlusBuilder setOnBackPressListener(@Nullable OnBackPressListener listener) {
            this.onBackPressListener = listener;
            return this;
        }

        public DialogPlusBuilder setExpanded(boolean expanded) {
            this.expanded = expanded;
            return this;
        }

        public DialogPlusBuilder setExpanded(boolean expanded, int defaultContentHeight) {
            this.expanded = expanded;
            this.defaultContentHeight = defaultContentHeight;
            return this;
        }

        public DialogPlusBuilder setContentHeight(int height) {
            params.height = height;
            return this;
        }

        public DialogPlusBuilder setContentWidth(int width) {
            params.width = width;
            return this;
        }

        /**
         * Create the dialog using this builder
         */
        @NonNull
        public DialogPlus create() {
            targetDialogPlus = new DialogPlus(this);
            return targetDialogPlus;
        }

        public DialogPlus getDialogPlus() {
            return targetDialogPlus;
        }

        @NonNull
        public Holder getHolder() {
            if (holder == null) {
                holder = new ListHolder(this);
            }
            holder.setBuilder(this);

            return holder;
        }

        @NonNull
        public Context getContext() {
            return context;
        }

        public MyRecyclerViewAdapter getAdapter() {
            try {
                if (adapter == null) {
                    if (arrayRes != 0) {
                        adapter = new DefaultRecyclerviewAdapter(context,
                                context.getResources().getStringArray(arrayRes),
                                getOnItemClickListener(),
                                initSelectionPosition);
                    } else if (strings != null) {
                        adapter = new DefaultRecyclerviewAdapter(context,
                                strings,
                                getOnItemClickListener(),
                                initSelectionPosition
                        );
                    } else if (stringList != null) {
                        adapter = new DefaultRecyclerviewAdapter(context,
                                (String[]) stringList.toArray(),
                                getOnItemClickListener(),
                                initSelectionPosition
                        );
                    }
                }

            } catch (Throwable e) {
                throw new NullPointerException("sorry, getAdapter error:" + e.getMessage());
            }

            return adapter;
        }

        public Animation getInAnimation() {
            int res = (inAnimation == INVALID) ? getAnimationResource(this.gravity, true) : inAnimation;
            return AnimationUtils.loadAnimation(context, res);
        }

        public Animation getOutAnimation() {
            int res = (outAnimation == INVALID) ? getAnimationResource(this.gravity, false) : outAnimation;
            return AnimationUtils.loadAnimation(context, res);
        }

        public FrameLayout.LayoutParams getContentParams() {
            if (expanded) {
                params.height = getDefaultContentHeight();
            }
            return params;
        }

        public boolean isExpanded() {
            return expanded;
        }

        public FrameLayout.LayoutParams getOutmostLayoutParams() {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            );
            params.setMargins(outMostMargin[0], outMostMargin[1], outMostMargin[2], outMostMargin[3]);
            return params;
        }

        public boolean isCancelable() {
            return isCancelable;
        }

        public OnItemClickListener getOnItemClickListener() {
            return onItemClickListener;
        }

        public OnClickListener getOnPositiveListener() {
            return onPositiveClickListener;
        }

        public OnClickListener getOnNegativeClickListener() {
            return onNegativeClickListener;
        }

        public OnDismissListener getOnDismissListener() {
            return onDismissListener;
        }

        public OnCancelListener getOnCancelListener() {
            return onCancelListener;
        }

        public OnBackPressListener getOnBackPressListener() {
            return onBackPressListener;
        }

        public OnCheckChangedListener getOnCheckChangedListener() {
            return onCheckChangedListener;
        }


        public int[] getContentMargin() {
            int minimumMargin = context.getResources().getDimensionPixelSize(R.dimen.dialogplus_default_center_margin);
            for (int i = 0; i < margin.length; i++) {
                margin[i] = getMargin(this.gravity, margin[i], minimumMargin);
            }
            return margin;
        }

        public int[] getContentPadding() {
            return padding;
        }

        public int getDefaultContentHeight() {
            Activity activity = (Activity) context;
            Display display = activity.getWindowManager().getDefaultDisplay();
            int displayHeight = display.getHeight() - getStatusBarHeight(activity);
            if (defaultContentHeight == 0) {
                defaultContentHeight = (displayHeight * 2) / 5;
            }
            return defaultContentHeight;
        }

        public int getOverlayBackgroundResource() {
            return overlayBackgroundResource;
        }

        public int getContentBackgroundResource() {
            return contentBackgroundResource;
        }

        /**
         * Get margins if provided or assign default values based on gravity
         *
         * @param gravity       the gravity of the dialog
         * @param margin        the value defined in the builder
         * @param minimumMargin the minimum margin when gravity center is selected
         * @return the value of the margin
         */
        private int getMargin(int gravity, int margin, int minimumMargin) {
            switch (gravity) {
                case Gravity.CENTER:
                    return (margin == INVALID) ? minimumMargin : margin;
                default:
                    return (margin == INVALID) ? 0 : margin;
            }
        }

    }

    /**
     * 默认的RecyclerViewAdapter
     */
    public static class DefaultRecyclerviewAdapter extends MyRecyclerViewAdapter<DefaultRecyclerviewAdapter.ViewHolder> {

        private Context context;
        private String[] strings;
        private OnItemClickListener onItemClickListener;
        private int shouldChecked;//应该选中的位置
        private int lastChecked = -1;//上次选中的位置

        public DefaultRecyclerviewAdapter(Context context, String[] strings, OnItemClickListener onItemClickListener, int initPosition) {
            this.context = context;
            this.strings = strings;
            this.onItemClickListener = onItemClickListener;
            this.shouldChecked = initPosition;
            lastChecked = initPosition;
        }

        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_dialog_single_choose, parent, false);
            return new ViewHolder(view, onItemClickListener);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

            if (strings == null || position >= strings.length) return;

            holder.textView.setText(strings[position]);
            holder.radioButton.setChecked(position == shouldChecked);

        }

        @Override
        public int getItemCount() {
            return strings == null ? 0 : strings.length;
        }

        @Override
        public DialogPlusBuilder getDialogPlusBuilder() {
            return super.getDialogPlusBuilder();
        }

        public class ViewHolder extends MyViewHolder {

            private RadioButton radioButton;
            private TextView textView;

            public ViewHolder(View itemView, OnItemClickListener onItemClickListener) {
                super(getDialogPlusBuilder(), itemView, onItemClickListener);
                radioButton = itemView.findViewById(R.id.adsc_radio_rb);
                radioButton.setClickable(false);
                textView = itemView.findViewById(R.id.adsc_desc_tv);
            }

            @Override
            void clickedThendoOtherThings(View view, int position) {
                if (position < 0) return;
                shouldChecked = position;
                if (lastChecked != -1) {
                    notifyItemChanged(lastChecked);
                }
                notifyItemChanged(position);
                lastChecked = position;
            }
        }
    }
}
