package be.alexandre01.dnplugin.api.connection.request.packets.exceptions;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 04/11/2023 at 22:35
*/
public class PacketParameterNullException extends Exception{
    public PacketParameterNullException(String message) {
        super(message);
    }

    public PacketParameterNullException(String message, Throwable cause) {
        super(message, cause);
    }
}
