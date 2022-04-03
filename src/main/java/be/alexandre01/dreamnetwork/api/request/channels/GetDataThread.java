package be.alexandre01.dreamnetwork.api.request.channels;

public abstract class GetDataThread<T> {
    public abstract void onComplete(T t);
}
