package be.alexandre01.dreamnetwork.plugins.spigot.communication.generated;

import be.alexandre01.dreamnetwork.api.request.RequestBuilder;
import be.alexandre01.dreamnetwork.api.request.RequestType;

public class SpigotGeneratedRequest extends RequestBuilder {
    public SpigotGeneratedRequest(){
        requestData.put(RequestType.CORE_ASK_DATA,(message, args) ->{
            message.set("MODE",args[0]);
            if(args.length > 1){
                message.set("TIME", args[1]);
            }

            return message;
        });
    }
}
