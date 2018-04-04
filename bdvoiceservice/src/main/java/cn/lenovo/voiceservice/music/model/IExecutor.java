package cn.lenovo.voiceservice.music.model;

/**
 * IExecutor
 * Created by chao on 2018/4/1.
 */
public interface IExecutor<T> {
    void execute();

    void onPrepare();

    void onExecuteSuccess(T t);

    void onExecuteFail(Exception e);
}
