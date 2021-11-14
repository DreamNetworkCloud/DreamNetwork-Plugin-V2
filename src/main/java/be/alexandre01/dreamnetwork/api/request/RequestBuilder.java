package be.alexandre01.dreamnetwork.api.request;

import be.alexandre01.dreamnetwork.api.request.generated.DefaultGeneratedRequest;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import lombok.Data;

import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.HashMap;

@Data
public class RequestBuilder {
    protected HashMap<RequestType,RequestData> requestData;

    protected RequestBuilder(){
        requestData = new HashMap<>();
    }

    public void addRequestBuilder(RequestBuilder... requestBuilders){
        for(RequestBuilder requestBuilder : requestBuilders){
            requestData.putAll(requestBuilder.requestData);
        }
    }


    public interface RequestData{
        public Message write(Message message,Object... args);
    }

}
