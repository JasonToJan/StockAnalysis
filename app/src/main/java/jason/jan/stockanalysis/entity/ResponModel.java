package jason.jan.stockanalysis.entity;

import java.io.Serializable;

/**
 * desc: 测试WanAndroid接口
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2020/4/3 12:46
 **/
public class ResponModel<T> implements Serializable {
    public static final int RESULT_SUCCESS = 0;

    private T data;
    private int errorCode;
    private String errorMsg;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean isSuccess(){
        return RESULT_SUCCESS == errorCode;
    }
}