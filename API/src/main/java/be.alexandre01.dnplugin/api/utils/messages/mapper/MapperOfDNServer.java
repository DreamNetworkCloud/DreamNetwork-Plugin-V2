package be.alexandre01.dnplugin.api.utils.messages.mapper;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.request.RequestType;
import be.alexandre01.dnplugin.api.connection.request.packets.PacketGlobal;
import be.alexandre01.dnplugin.api.connection.request.packets.PacketHandler;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import be.alexandre01.dnplugin.api.utils.messages.ObjectConverterMapper;

import java.util.Optional;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 04/11/2023 at 23:37
*/

public class MapperOfDNServer extends ObjectConverterMapper<DNServer,String> {
    @Override
    public String convert(DNServer object) {
        return object.getName();
    }

    @Override
    public DNServer read(String object) {
        return NetworkBaseAPI.getInstance().getByFullName(object).orElse(null);
    }
}

