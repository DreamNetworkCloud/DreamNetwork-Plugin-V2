package be.alexandre01.dnplugin.api.request.channels;

public abstract class GetDataThread<T> {
    public abstract void onComplete(T t);
}
