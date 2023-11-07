package be.alexandre01.dnplugin.api.objects.core;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.request.Packet;
import be.alexandre01.dnplugin.api.connection.request.RequestInfo;
import be.alexandre01.dnplugin.api.objects.server.NetEntity;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 22/09/2023 at 17:41
*/
public abstract class NetCore extends NetEntity {
    public NetCore() {
        super("core");
    }

}
