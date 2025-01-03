package be.alexandre01.dnplugin.plugins.velocity.communication.generated;

import be.alexandre01.dnplugin.api.connection.request.RequestBuilder;
import be.alexandre01.dnplugin.api.connection.request.RequestType;

public class VelocityGeneratedRequest extends RequestBuilder {
    public VelocityGeneratedRequest(){
        requestData.put(RequestType.CORE_UPDATE_PLAYER,(message, args) ->{
            message.set("ID",args[0]);
            message.set("S", args[1]);
            if(args.length > 2){
                message.set("P", args[2]);
            }
            if(args.length > 3){
                message.set("U", args[3]);
            }
            return message;
        });
        requestData.put(RequestType.CORE_REMOVE_PLAYER,(message, args) ->{
            message.set("ID",args[0]);
            return message;
        });
    }
}
