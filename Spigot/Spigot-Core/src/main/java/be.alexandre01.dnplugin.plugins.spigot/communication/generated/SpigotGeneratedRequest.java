package be.alexandre01.dnplugin.plugins.spigot.communication.generated;

import be.alexandre01.dnplugin.api.connection.request.RequestBuilder;
import be.alexandre01.dnplugin.api.connection.request.RequestType;

public class SpigotGeneratedRequest extends RequestBuilder {
    public SpigotGeneratedRequest(){
        requestData.put(RequestType.CORE_ASK_DATA,(message, args) ->{
            message.set("TYPE",args[0]);
            message.set("MODE",args[1]);
            if(args.length > 2){
                message.set("TIME", args[2]);
            }

            return message;
        });
    }
}
