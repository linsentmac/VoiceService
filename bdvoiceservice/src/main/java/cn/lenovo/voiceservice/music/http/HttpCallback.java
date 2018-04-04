package cn.lenovo.voiceservice.music.http;

/**
 * HttpCallback
 * Created by chao on 2018/4/1.
 */
public abstract class HttpCallback<T> {
    public abstract void onSuccess(T t);

    public abstract void onFail(Exception e);

    public void onFinish() {
    }
}
