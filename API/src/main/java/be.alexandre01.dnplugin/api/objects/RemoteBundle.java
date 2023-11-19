package be.alexandre01.dnplugin.api.objects;

import lombok.Getter;

import java.util.HashMap;


@Getter
public class RemoteBundle {

    @Getter private final HashMap<String, RemoteExecutor> remoteExecutors = new HashMap<>();
    protected boolean isProxy;
    protected String name;
    public RemoteBundle(String name, boolean isProxy) {
        this.name = name;
        this.isProxy = isProxy;
    }

    public <T> T getRemoteExecutor(String name, Class<T> clazz) {
        return (T) remoteExecutors.get(name);
    }

    public RemoteExecutor getRemoteExecutor(String name) {
        return remoteExecutors.get(name);
    }

}
