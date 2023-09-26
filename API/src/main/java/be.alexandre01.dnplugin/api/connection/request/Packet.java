package be.alexandre01.dnplugin.api.connection.request;



import be.alexandre01.dnplugin.api.objects.server.NetEntity;
import be.alexandre01.dnplugin.api.utils.messages.Message;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/09/2023 at 10:16
*/
public interface Packet {
    public Message getMessage();
    public String getProvider();
    public NetEntity getReceiver();
}
