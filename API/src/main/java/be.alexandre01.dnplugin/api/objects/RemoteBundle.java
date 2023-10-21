package be.alexandre01.dnplugin.api.objects;

import lombok.Getter;

import java.util.HashMap;


@Getter
public class RemoteBundle {

    @Getter private final HashMap<String, RemoteExecutor> remoteServices = new HashMap<>();
    protected boolean isProxy;
    protected String name;
    public RemoteBundle(String name, boolean isProxy) {
        this.name = name;
        this.isProxy = isProxy;
    }

    public <T> T getRemoteService(String name, Class<T> clazz) {
        return (T) remoteServices.get(name);
    }

    public RemoteExecutor getRemoteService(String name) {
        return remoteServices.get(name);
    }

}
