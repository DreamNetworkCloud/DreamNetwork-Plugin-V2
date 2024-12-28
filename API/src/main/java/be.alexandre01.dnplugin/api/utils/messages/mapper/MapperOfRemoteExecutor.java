package be.alexandre01.dnplugin.api.utils.messages.mapper;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.request.RequestType;
import be.alexandre01.dnplugin.api.connection.request.packets.PacketGlobal;
import be.alexandre01.dnplugin.api.objects.RemoteExecutor;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.utils.messages.ObjectConverterMapper;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 04/11/2023 at 23:37
*/

public class MapperOfRemoteExecutor extends ObjectConverterMapper<RemoteExecutor,String> {
    @Override
    public String convert(RemoteExecutor object) {
        return object.getName();
    }

    @Override
    public RemoteExecutor read(String object) {
        return NetworkBaseAPI.getInstance().getByName(object).orElse(null);
    }
}

