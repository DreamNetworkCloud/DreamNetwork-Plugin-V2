package be.alexandre01.dreamnetwork.api.objects;

import lombok.Data;

@Data
public abstract class RemoteService {
    public abstract void start();
    //public abstract void start();

    public abstract void stop();


}
