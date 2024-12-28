package be.alexandre01.dnplugin.api.connection.request.packets;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 04/11/2023 at 21:37
*/

@AllArgsConstructor
@Builder
public class RequestHandler {
    String id;
    int priority;
    String[] channels;
    PacketCastOption castOption;
    String id(){
        return id;
    }
    int priority(){
        return priority;
    }

    String[] channels(){
        return channels;
    }

    RequestHandler.PacketCastOption castOption(){
        return castOption;
    }

    public enum PacketCastOption {
        NOT_SET,
        NOT_NULL,
        NULLABLE,
        IGNORE_ALL,
    }
}
