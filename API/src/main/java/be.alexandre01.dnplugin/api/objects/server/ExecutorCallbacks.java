package be.alexandre01.dnplugin.api.objects.server;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ExecutorCallbacks {

    @Setter private boolean hasFailed = false;
    @Getter private List<ICallbackStart> startList;
    @Getter public List<ICallbackStop> stopList;
    @Getter private List<ICallbackConnect> connectList;
    @Getter private List<ICallbackFail> failList;

    @Setter DNServer jvmService = null;
    boolean hasStarted = false;


    public ExecutorCallbacks whenStart(ICallbackStart onStart){
        if(this.startList == null){
            this.startList = new ArrayList<>();
        }
        this.startList.add(onStart);
        if(jvmService != null && !hasStarted){
            onStart.whenStart(jvmService);
            hasStarted = !hasStarted;
        }
        return this;
    }

    public ExecutorCallbacks whenStop(ICallbackStop onStop){
        if(this.stopList == null){
            this.stopList = new ArrayList<>();
        }
        this.stopList.add(onStop);
        return this;
    }

    public ExecutorCallbacks whenConnect(ICallbackConnect onConnect){
        if(this.connectList == null){
            this.connectList = new ArrayList<>();
        }
        this.connectList.add(onConnect);
        return this;
    }

    public ExecutorCallbacks whenFail(ICallbackFail onFail){
        if(this.failList == null){
            this.failList = new ArrayList<>();
        }
        this.failList.add(onFail);
        if(hasFailed){
            onFail.whenFail();
        }
        return this;
    }



    public interface ICallbackStart{
       void whenStart(DNServer service);
    }

    public interface ICallbackStop{
        void whenStop(DNServer service);
    }

    public interface ICallbackConnect{
        void whenConnect(DNServer service);
    }

    public interface ICallbackFail{
        void whenFail();
    }

}
