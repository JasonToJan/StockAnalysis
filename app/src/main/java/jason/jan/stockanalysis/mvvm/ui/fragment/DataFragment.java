package jason.jan.stockanalysis.mvvm.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import java.util.Calendar;

import jason.jan.stockanalysis.R;
import jason.jan.stockanalysis.base.BaseFragment;
import jason.jan.stockanalysis.data.DataSource;
import jason.jan.stockanalysis.databinding.FragmentDataBinding;
import jason.jan.stockanalysis.mvvm.ui.adapter.MyGridViewAdapter;
import jason.jan.stockanalysis.mvvm.viewmodel.DataFViewModel;
import jason.jan.stockanalysis.utils.CommonUtils;
import jason.jan.stockanalysis.utils.DialogPlus;
import jason.jan.stockanalysis.utils.DialogUtils;
import jason.jan.stockanalysis.utils.LogUtils;
import jason.jan.stockanalysis.utils.ToastUtils;
import jason.jan.stockanalysis.view.CustomProgress;

/**
 * Description: 批量操作页面
 * *
 * Creator: Wang
 * Date: 2020/4/2 20:19
 */
public class DataFragment extends BaseFragment<DataFViewModel, FragmentDataBinding> {

    private static final String TAG = "DataFragment";

    public static DataFragment newInstance() {

        Bundle args = new Bundle();

        DataFragment fragment = new DataFragment();
        fragment.setArguments(args);
        return fragment;
    }


    //属性集合------------begin-----------------//

    private Calendar calendar;

    private CustomProgress customProgress;

    private int offsetNum = 0;

    private static final int OFFSET_NUM = 250;//间隔大小

    public static final int BEGIN_NUM = 0;//开始大小


    //属性集合------------end-------------------//


    @Override
    protected int getContentViewId() {
        return R.layout.fragment_data;
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

        if (calendar == null) {
            calendar = Calendar.getInstance();
        }

        int month = calendar.get(Calendar.MONTH) + 1;
        binding.fdUpdateMonthBtn.setText("更新" + month + "月记录");
        binding.fdDeleteMonthBtn.setText("清空" + month + "月记录");

        initRadio();


    }

    @Override
    protected void setListener() {
        binding.fdAddAllBtn.setOnClickListener(this);
        binding.fdDeleteAllBtn.setOnClickListener(this);
        binding.fdUpdateMonthBtn.setOnClickListener(this);
        binding.fdDeleteMonthBtn.setOnClickListener(this);
        binding.fdUpdateThreeMonthBtn.setOnClickListener(this);
        binding.fdDeleteThreeMonthBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fd_add_all_btn:
                doAddAll();
                break;

            case R.id.fd_delete_all_btn:
                doDeleteAll();
                break;

            case R.id.fd_update_month_btn:
                doUpdateMonth();
                break;

            case R.id.fd_delete_month_btn:
                doDeleteMonth();
                break;

            case R.id.fd_update_three_month_btn:
                doUpdateThreeMonth();
                break;

            case R.id.fd_delete_three_month_btn:
                doDeleteThreeMonth();
                break;
        }
    }

    private void initRadio() {
        //设置单选按钮组
        int size = DataFViewModel.MAX_CURRENT / OFFSET_NUM + 1;
        String[] strings = new String[size];
        int[] beginNum = new int[size];
        int firstNum = 0;
        offsetNum = BEGIN_NUM;
        for (int i = 0; i < size; i++) {

            int firstEndNum = (firstNum + OFFSET_NUM - 1);//0--249
            strings[i] = firstNum + "->" + firstEndNum;
            beginNum[i] = firstNum;
            firstNum = firstNum + OFFSET_NUM;

        }

        MyGridViewAdapter adapter = new MyGridViewAdapter(_mActivity);
        adapter.setData(strings, 0);
        binding.fdRadioGv.setAdapter(adapter);
        binding.fdRadioGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                adapter.setSeclection(position);
                adapter.notifyDataSetChanged();
                if (BEGIN_NUM > beginNum[position] && BEGIN_NUM < beginNum[position + 1]) {
                    offsetNum = BEGIN_NUM;
                } else {
                    offsetNum = beginNum[position];
                }

                LogUtils.d(TAG, "当前起始数据应该从：" + offsetNum + "开始了！");
            }
        });
    }

    private void doAddAll() {
        if (!CommonUtils.doFirstClick200()) return;

        DataSource.getInstance().setCurrentPosition(4);
        DialogUtils.showTipsThenCallback(_mActivity, "您确定要插入所有股票历史记录吗？", new DialogUtils.IDialogTwoButtonCallback() {
            @Override
            public void onPositiveCallback(DialogPlus dialog) {

                if (customProgress != null) {
                    customProgress.dismiss();
                    customProgress = null;
                }

                customProgress = CustomProgress.show(_mActivity, "正在查询并插入历史股票数据...", true, null);

                binding.fdLogTv.setText("");
                mViewModel.requestAllStockThenInsertAsyncTask(offsetNum, new DataFViewModel.AddStockCallback() {

                    int currentSize = 0;
                    int scrollHeight = 1000;

                    @Override
                    public void successAdd(int size) {
                        currentSize += size;
                        binding.fdLogTv.append("当前成功插入了：" + size + "条数据!\n");
                        binding.fdRootScroll.scrollTo(0, scrollHeight += 100);

                    }

                    @Override
                    public void endAdd() {
                        _mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.fdLogTv.append("\n总共成功插入了：" + currentSize + "条数据!\n(可能有重复的)");
                                if (customProgress != null) {
                                    customProgress.dismiss();
                                    customProgress = null;
                                }
                            }
                        });
                    }

                    @Override
                    public void failAdd(Throwable e) {
                        ToastUtils.showToast("插入失败：" + e.getMessage());
                        binding.fdLogTv.setText("插入失败！\n原因：" + e.getMessage());
                        if (customProgress != null) {
                            customProgress.dismiss();
                            customProgress = null;
                        }
                    }
                });

            }

            @Override
            public void onNegativeCallback(DialogPlus dialog) {

            }
        });
    }

    private void doDeleteAll() {
        if (!CommonUtils.doFirstClick200()) return;

        DataSource.getInstance().setCurrentPosition(4);
        DialogUtils.showTipsThenCallback(_mActivity, "您确定要清空所有股票历史记录吗？", new DialogUtils.IDialogTwoButtonCallback() {
            @Override
            public void onPositiveCallback(DialogPlus dialog) {
                mViewModel.deleteAllStock();
            }

            @Override
            public void onNegativeCallback(DialogPlus dialog) {

            }
        });
    }

    private void doUpdateMonth() {
        if (!CommonUtils.doFirstClick200()) return;

        DataSource.getInstance().setCurrentPosition(4);
        DialogUtils.showTipsThenCallback(_mActivity, "您确定要更新所有股票这个月的历史记录吗？", new DialogUtils.IDialogTwoButtonCallback() {
            @Override
            public void onPositiveCallback(DialogPlus dialog) {

                customProgress = CustomProgress.show(_mActivity, "正在查询并插入历史股票数据...", true, null);
                binding.fdLogTv.setText("");
                mViewModel.deleteCurrentMonthStock(true, offsetNum, new DataFViewModel.AddStockCallback() {
                    int currentSize = 0;

                    @Override
                    public void successAdd(int size) {
                        currentSize += size;
                        binding.fdLogTv.append("当前成功插入了：" + size + "条数据!\n");
                    }

                    @Override
                    public void endAdd() {
                        _mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.fdLogTv.append("\n总共成功插入了：" + currentSize + "条数据!\n(可能有重复的)");
                                if (customProgress != null) {
                                    customProgress.dismiss();
                                    customProgress = null;
                                }
                            }
                        });
                    }

                    @Override
                    public void failAdd(Throwable e) {
                        ToastUtils.showToast("插入失败：" + e.getMessage());
                        binding.fdLogTv.setText("插入失败！\n原因：" + e.getMessage());
                        if (customProgress != null) {
                            customProgress.dismiss();
                            customProgress = null;
                        }
                    }
                });
            }

            @Override
            public void onNegativeCallback(DialogPlus dialog) {

            }
        });

    }

    private void doDeleteMonth() {
        if (!CommonUtils.doFirstClick200()) return;

        DataSource.getInstance().setCurrentPosition(4);
        DialogUtils.showTipsThenCallback(_mActivity, "您确定要清空所有股票这个月的历史记录吗？", new DialogUtils.IDialogTwoButtonCallback() {
            @Override
            public void onPositiveCallback(DialogPlus dialog) {
                mViewModel.deleteCurrentMonthStock(false, offsetNum,null);
            }

            @Override
            public void onNegativeCallback(DialogPlus dialog) {

            }
        });
    }

    private void doUpdateThreeMonth() {
        if (!CommonUtils.doFirstClick200()) return;

        DataSource.getInstance().setCurrentPosition(4);
        DialogUtils.showTipsThenCallback(_mActivity, "您确定要更新所有股票最近三个月历史记录吗？", new DialogUtils.IDialogTwoButtonCallback() {
            @Override
            public void onPositiveCallback(DialogPlus dialog) {

                customProgress = CustomProgress.show(_mActivity, "正在查询并插入历史股票数据...", true, null);
                binding.fdLogTv.setText("");
                mViewModel.deleteThreeMonthStock(true, offsetNum, new DataFViewModel.AddStockCallback() {
                    int currentSize = 0;

                    @Override
                    public void successAdd(int size) {
                        currentSize += size;
                        binding.fdLogTv.append("当前成功插入了：" + size + "条数据!\n");
                    }

                    @Override
                    public void endAdd() {
                        _mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.fdLogTv.append("\n总共成功插入了：" + currentSize + "条数据!\n(可能有重复的)");
                                if (customProgress != null) {
                                    customProgress.dismiss();
                                    customProgress = null;
                                }
                            }
                        });
                    }

                    @Override
                    public void failAdd(Throwable e) {
                        ToastUtils.showToast("插入失败：" + e.getMessage());
                        binding.fdLogTv.setText("插入失败！\n原因：" + e.getMessage());
                        if (customProgress != null) {
                            customProgress.dismiss();
                            customProgress = null;
                        }
                    }
                });
            }

            @Override
            public void onNegativeCallback(DialogPlus dialog) {

            }
        });

    }

    private void doDeleteThreeMonth() {
        if (!CommonUtils.doFirstClick200()) return;

        DataSource.getInstance().setCurrentPosition(4);
        DialogUtils.showTipsThenCallback(_mActivity, "您确定要清空所有股票三个月的历史记录吗？", new DialogUtils.IDialogTwoButtonCallback() {
            @Override
            public void onPositiveCallback(DialogPlus dialog) {
                mViewModel.deleteThreeMonthStock(false, offsetNum,null);
            }

            @Override
            public void onNegativeCallback(DialogPlus dialog) {

            }
        });
    }

}
