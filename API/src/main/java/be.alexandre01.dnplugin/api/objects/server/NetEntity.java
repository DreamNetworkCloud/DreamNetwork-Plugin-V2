package be.alexandre01.dnplugin.api.objects.server;

import be.alexandre01.dnplugin.api.connection.request.Packet;
import be.alexandre01.dnplugin.api.connection.request.RequestInfo;
import be.alexandre01.dnplugin.api.connection.request.RequestManager;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 21/09/2023 at 18:48
*/
@Getter
public abstract class

NetEntity {
    protected final String name;

    protected NetEntity(String name) {
        this.name = name;
    }
    public abstract Packet writeAndFlush(Message message);
    public abstract Packet writeAndFlush(Message message, GenericFutureListener<? extends Future<? super Void>> listener);

    public abstract Packet dispatch(Packet packet);
    public abstract Packet dispatch(Packet packet, GenericFutureListener<? extends Future<? super Void>> future);

    public abstract RequestManager getRequestManager();
}
