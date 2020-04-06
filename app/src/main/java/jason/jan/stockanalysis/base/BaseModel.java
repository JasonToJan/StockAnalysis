package jason.jan.stockanalysis.base;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.trello.rxlifecycle2.LifecycleTransformer;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jason.jan.stockanalysis.MyApplication;
import jason.jan.stockanalysis.data.db.AppDatabase;
import jason.jan.stockanalysis.data.http.Interceptor.NetCacheInterceptor;
import jason.jan.stockanalysis.data.http.Interceptor.OfflineCacheInterceptor;
import jason.jan.stockanalysis.data.http.RetrofitManager;
import jason.jan.stockanalysis.data.http.StockApiService;
import jason.jan.stockanalysis.entity.ParamsBuilder;
import jason.jan.stockanalysis.entity.Resource;
import jason.jan.stockanalysis.entity.ResponModel;
import jason.jan.stockanalysis.utils.downloadutils.DownFileUtils;
import okhttp3.ResponseBody;

/**
 * desc:
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2020/4/3 12:48
 **/
public abstract class BaseModel {

    public LifecycleTransformer objectLifecycleTransformer;
    public ArrayList<String> onNetTags;

    /**
     * 第一次时间
     */
    private static long firstReqestTime;
    /**
     * 总时间
     */
    private static final int DEALY_TIME = 2000;

    public ArrayList<Observable> observables = new ArrayList<>();

    /**
     * 网络服务
     * @return
     */
    public StockApiService getApiService() {
        return RetrofitManager.getRetrofitManager().getApiService();
    }

    public interface DataCallback {

        void successGetData(Object object);

        void failedGetData(Throwable e);
    }

    /**
     * 数据库
     * @return
     */
    public AppDatabase getDatabase(){
        return AppDatabase.getInstance(MyApplication.getInstance());
    }

    public void setObjectLifecycleTransformer(LifecycleTransformer objectLifecycleTransformer) {
        this.objectLifecycleTransformer = objectLifecycleTransformer;
    }


    public void setOnNetTags(ArrayList<String> onNetTags) {
        this.onNetTags = onNetTags;
    }

    public <T> MutableLiveData<T> observeGo(Observable observable, final MutableLiveData<T> liveData) {
        return observe(observable, liveData, null);
    }

    public void observeGoAsync(Observable observable, DataCallback callback){

        observable
                .subscribeOn(Schedulers.io())
                .compose(objectLifecycleTransformer)
                .subscribe(o -> {
                    callback.successGetData(o);
                }, throwable -> {
                    callback.failedGetData((Throwable) throwable);
                });

    }

    /**
     * 两秒类加入的
     * @param observable
     * @param liveData
     * @param <T>
     * @return
     */
    public <T> MutableLiveData<T> observeGoToTwoSeconds(Observable observable, final MutableLiveData<T> liveData) {
        return observeTwoSecond(observable, liveData, null);
    }

    public <T> MutableLiveData<T> observeGo(Observable observable, final MutableLiveData<T> liveData, ParamsBuilder paramsBuilder) {
        if (paramsBuilder == null) {
            paramsBuilder = ParamsBuilder.build();
        }
        int retryCount = paramsBuilder.getRetryCount();
        if (retryCount > 0) {
            return observeWithRetry(observable, liveData, paramsBuilder);
        } else {
            return observe(observable, liveData, paramsBuilder);
        }
    }


    //把统一操作全部放在这，不会重连
    @SuppressLint("CheckResult")
    public <T> MutableLiveData<T> observe(Observable observable, final MutableLiveData<T> liveData, ParamsBuilder paramsBuilder) {
        if (paramsBuilder == null) {
            paramsBuilder = paramsBuilder.build();
        }
        boolean showDialog = paramsBuilder.isShowDialog();
        String loadingMessage = paramsBuilder.getLoadingMessage();
        int onlineCacheTime = paramsBuilder.getOnlineCacheTime();
        int offlineCacheTime = paramsBuilder.getOfflineCacheTime();

        if (onlineCacheTime > 0) {
            setOnlineCacheTime(onlineCacheTime);
        }
        if (offlineCacheTime > 0) {
            setOfflineCacheTime(offlineCacheTime);
        }
        String oneTag = paramsBuilder.getOneTag();
        if (!TextUtils.isEmpty(oneTag)) {
            if (onNetTags.contains(oneTag)) {
                return liveData;
            }
        }


        observable
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable1 -> {
                    if (!TextUtils.isEmpty(oneTag)) {
                        onNetTags.add(oneTag);
                    }
                    if (showDialog) {
                        liveData.postValue((T) Resource.loading(loadingMessage));
                    }
                })
                .doFinally(() -> {
                    if (!TextUtils.isEmpty(oneTag)) {
                        onNetTags.remove(oneTag);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .compose(objectLifecycleTransformer)
                .subscribe(o -> {
                    liveData.postValue((T) Resource.response((ResponModel<Object>) o));
                }, throwable -> {
                    liveData.postValue((T) Resource.error((Throwable) throwable));
                });

        return liveData;
    }

    //把统一操作全部放在这，不会重连
    @SuppressLint("CheckResult")
    public <T> MutableLiveData<T> observeTwoSecond(Observable observable, final MutableLiveData<T> liveData, ParamsBuilder paramsBuilder) {
        if (paramsBuilder == null) {
            paramsBuilder = paramsBuilder.build();
        }
        boolean showDialog = paramsBuilder.isShowDialog();
        String loadingMessage = paramsBuilder.getLoadingMessage();
        int onlineCacheTime = paramsBuilder.getOnlineCacheTime();
        int offlineCacheTime = paramsBuilder.getOfflineCacheTime();

        if (onlineCacheTime > 0) {
            setOnlineCacheTime(onlineCacheTime);
        }
        if (offlineCacheTime > 0) {
            setOfflineCacheTime(offlineCacheTime);
        }
        String oneTag = paramsBuilder.getOneTag();
        if (!TextUtils.isEmpty(oneTag)) {
            if (onNetTags.contains(oneTag)) {
                return liveData;
            }
        }

        observable
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable1 -> {
                    if (!TextUtils.isEmpty(oneTag)) {
                        onNetTags.add(oneTag);
                    }
                    if (showDialog) {
                        liveData.postValue((T) Resource.loading(loadingMessage));
                    }
                })
                .doFinally(() -> {
                    if (!TextUtils.isEmpty(oneTag)) {
                        onNetTags.remove(oneTag);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .compose(objectLifecycleTransformer)
                .subscribe(o -> {
                    liveData.postValue((T) Resource.response((ResponModel<Object>) o));
                }, throwable -> {
                    liveData.postValue((T) Resource.error((Throwable) throwable));
                });

        return liveData;
    }


    //把统一操作全部放在这，这是带重连的
    public <T> MutableLiveData<T> observeWithRetry(Observable observable, final MutableLiveData<T> liveData, ParamsBuilder paramsBuilder) {
        if (paramsBuilder == null) {
            paramsBuilder = paramsBuilder.build();
        }
        boolean showDialog = paramsBuilder.isShowDialog();
        String loadingMessage = paramsBuilder.getLoadingMessage();
        int onlineCacheTime = paramsBuilder.getOnlineCacheTime();
        int offlineCacheTime = paramsBuilder.getOfflineCacheTime();

        if (onlineCacheTime > 0) {
            setOnlineCacheTime(onlineCacheTime);
        }
        if (offlineCacheTime > 0) {
            setOfflineCacheTime(offlineCacheTime);
        }

        String oneTag = paramsBuilder.getOneTag();
        if (!TextUtils.isEmpty(oneTag)) {
            if (onNetTags.contains(oneTag)) {
                return liveData;
            }
        }

        final int maxCount = paramsBuilder.getRetryCount();
        final int[] currentCount = {0};

        Disposable disposable = observable.subscribeOn(Schedulers.io())
                .retryWhen(throwable -> {
                    //如果还没到次数，就延迟5秒发起重连
                    if (currentCount[0] <= maxCount) {
                        currentCount[0]++;
                        return Observable.just(1).delay(5000, TimeUnit.MILLISECONDS);
                    } else {
                        //到次数了跑出异常
                        return Observable.error(new Throwable("重连次数已达最高,请求超时"));
                    }
                })
                .doOnSubscribe(disposable1 -> {
                    if (!TextUtils.isEmpty(oneTag)) {
                        onNetTags.add(oneTag);
                    }
                    if (showDialog) {
                        liveData.postValue((T) Resource.loading(loadingMessage));
                    }
                })
                .doFinally(() -> {
                    if (!TextUtils.isEmpty(oneTag)) {
                        onNetTags.remove(oneTag);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .compose(objectLifecycleTransformer)
                .subscribe(o -> {
                    liveData.postValue((T) Resource.response((ResponModel<Object>) o));
                }, throwable -> {
                    liveData.postValue((T) Resource.error((Throwable) throwable));
                });

        return liveData;
    }


    //设置在线网络缓存
    public void setOnlineCacheTime(int time) {
        NetCacheInterceptor.getInstance().setOnlineTime(time);
    }

    //设置离线网络缓存
    public void setOfflineCacheTime(int time) {
        OfflineCacheInterceptor.getInstance().setOfflineCacheTime(time);
    }

    //正常下载(重新从0开始下载)
    public <T> MutableLiveData<T> downLoadFile(Observable observable, final MutableLiveData<T> liveData, final String destDir, final String fileName) {
        return downLoadFile(observable, liveData, destDir, fileName, 0);
    }


    //断点下载，如果下载到一半，可从一半开始下载
    public <T> MutableLiveData<T> downLoadFile(Observable observable, final MutableLiveData<T> liveData, final String destDir, final String fileName, long currentLength) {
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(requestBody -> {
                    if (currentLength == 0) {
                        return DownFileUtils.saveFile((ResponseBody) requestBody, destDir, fileName, liveData);
                    } else {
                        return DownFileUtils.saveFile((ResponseBody) requestBody, destDir, fileName, currentLength, liveData);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(file -> {
                    liveData.postValue((T) Resource.success(file));
                }, throwable -> {
                    liveData.postValue((T) Resource.error((Throwable) throwable));
                });
        return liveData;
    }


    //上传文件只有2个参数，showDialog：是否显示dialog；loadmessage：showDialog显示的文字
    public <T> MutableLiveData<T> upLoadFile(Observable observable, MutableLiveData<T> liveData) {
        return upLoadFile(observable, liveData, null);
    }

    //上传文件
    public <T> MutableLiveData<T> upLoadFile(Observable observable, MutableLiveData<T> liveData, ParamsBuilder paramsBuilder) {

        if (paramsBuilder == null) {
            paramsBuilder = paramsBuilder.build();
        }
        boolean showDialog = paramsBuilder.isShowDialog();
        String loadingMessage = paramsBuilder.getLoadingMessage();

        observable.subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    if (showDialog) {
                        liveData.postValue((T) Resource.loading(loadingMessage));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                //防止RxJava内存泄漏
                .subscribe(o -> {
                    liveData.postValue((T) Resource.success("成功了"));
                }, throwable -> {
                    liveData.postValue((T) Resource.error((Throwable) throwable));
                });

        return liveData;
    }

}
