package be.alexandre01.dnplugin.api.request.generated;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.request.RequestBuilder;
import be.alexandre01.dnplugin.api.request.RequestType;

public class DefaultGeneratedRequest extends RequestBuilder {
    /**
     * DefaultGeneratedRequest is a class that initializes the requestData map with default values for various request types.
     * Each request type is associated with a lambda expression that defines the behavior when the request is executed.
     *
     * The requestData map is of type Map<RequestType, BiFunction<Message, Object[], Message>>.
     * The key is a RequestType enum that represents the type of the request.
     * The value is a BiFunction that takes in a Message object and an array of objects as arguments, and returns a Message object.
     * The Message object represents the request message that will be sent.
     *
     * The DefaultGeneratedRequest constructor initializes the requestData map by associating each request type with its corresponding lambda expression.
     * The lambda expressions define how the request message should be constructed based on the provided arguments.
     */
    public DefaultGeneratedRequest(){
        requestData.put(RequestType.CORE_HANDSHAKE,(message,args) ->{
            message.set("INFO", NetworkBaseAPI.getInstance().getInfo());
            message.set("PORT", NetworkBaseAPI.getInstance().getPort());
            if(NetworkBaseAPI.getInstance().isExternal())
                message.set("EXTERNAL",true);
            return message;
        });

        requestData.put(RequestType.CORE_RETRANSMISSION,(message,args) ->{
            if(args.length != 0)
                message.setInRoot("RETRANS", args[0]);
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

        requestData.put(RequestType.CORE_START_SERVER,(message, args) -> {
            message.set("SERVERNAME",args[0]);
            return message;
        });
        requestData.put(RequestType.CORE_STOP_SERVER,(message, args) -> {
            message.set("SERVERNAME",args[0]);
            return message;
        });
        requestData.put(RequestType.SERVER_EXECUTE_COMMAND,(message, args) -> {
            message.set("SERVERNAME",args[0]);
            message.set("CMD",args[1]);
            return message;
        });
        requestData.put(RequestType.CORE_REGISTER_CHANNEL,(message, args) -> {
            System.out.println("Registering channel");
            message.set("CHANNEL",args[0]);
            if((boolean) args[1]){
                message.set("RESEND",true);
            }

            return message;
        });
        requestData.put(RequestType.CORE_UNREGISTER_CHANNEL,(message, args) -> {
            message.set("CHANNEL",args[0]);
            return message;
        });
    }
}
