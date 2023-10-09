package be.alexandre01.dnplugin.api.connection.request;



import be.alexandre01.dnplugin.api.objects.server.NetEntity;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;


/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/09/2023 at 10:16
*/
public interface Packet {
    public Message getMessage();
    public String getProvider();
    public NetEntity getReceiver();

    default Packet dispatch(){
        getReceiver().dispatch(this);
        return this;
    }

    default Packet dispatch(GenericFutureListener<? extends Future<? super Void>> future){
        getReceiver().dispatch(this,future);
        return this;
    }
}
