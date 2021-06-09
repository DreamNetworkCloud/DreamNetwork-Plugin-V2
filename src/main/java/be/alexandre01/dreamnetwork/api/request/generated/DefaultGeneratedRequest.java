package be.alexandre01.dreamnetwork.api.request.generated;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.request.RequestBuilder;
import be.alexandre01.dreamnetwork.api.request.RequestType;
import be.alexandre01.dreamnetwork.utils.messages.Message;

public class DefaultGeneratedRequest extends RequestBuilder {
    public DefaultGeneratedRequest(){
        requestData.put(RequestType.CORE_HANDSHAKE,(message,args) ->{
            message.set("INFO", NetworkBaseAPI.getInstance().getInfo());
            message.set("PORT", NetworkBaseAPI.getInstance().getPort());
            message.set("PASSWORD", "NULL");
            return message;
        });

        requestData.put(RequestType.CORE_RETRANSMISSION,(message,args) ->{
            if(args.length != 0)
                message.set("RETRANSMISSION", args);
            return message;
        });

        requestData.put(RequestType.CORE_HANDSHAKE_SUCCESS,(message, args) -> {
            message.set("STATUS","SUCCESS");
           return message;
        });

        requestData.put(RequestType.CORE_LOG_MESSAGE,(message, args) -> {
            message.set("LOG",args);
            message.set("LOG-TYPE","INFO");
            return message;
        });
        requestData.put(RequestType.CORE_WARNING_MESSAGE,(message, args) -> {
            message.set("LOG",args);
            message.set("LOG-TYPE","WARNING");
            return message;
        });
        requestData.put(RequestType.CORE_ERROR_MESSAGE,(message, args) -> {
            message.set("LOG",args);
            message.set("LOG-TYPE","ERROR");
            return message;
        });
    }
}
