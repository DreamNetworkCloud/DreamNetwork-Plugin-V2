package be.alexandre01.dnplugin.api.request;

import lombok.Getter;
import lombok.Setter;

public class RequestInfo {
    @Setter @Getter
    public int id;
    public String name;

    public RequestInfo(int id,String name){
        this.id = id;
        this.name = name;
    }
    public RequestInfo(){
    }
}
