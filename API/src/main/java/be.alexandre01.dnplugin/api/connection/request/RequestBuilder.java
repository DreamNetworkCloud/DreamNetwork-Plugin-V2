package be.alexandre01.dnplugin.api.connection.request;

import be.alexandre01.dnplugin.api.utils.messages.Message;
import lombok.Data;

import java.util.HashMap;

@Data
public class RequestBuilder {
    protected HashMap<RequestInfo,RequestData> requestData;

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
