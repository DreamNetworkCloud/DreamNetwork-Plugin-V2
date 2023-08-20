package be.alexandre01.dnplugin.api.connection;

public interface IBasicClient extends Runnable {
    @Override
    void run();

    void connect();

    boolean isExternal();
}
