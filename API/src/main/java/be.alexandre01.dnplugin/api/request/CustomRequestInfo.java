package be.alexandre01.dnplugin.api.request;


import lombok.Getter;
import lombok.Setter;

public class CustomRequestInfo extends RequestInfo {
    @Getter @Setter
    private String addonName;
    public CustomRequestInfo(int id,String fullName){
        super(id,fullName.split("#")[0]);
        addonName = fullName.split("#")[1];
    }

    public CustomRequestInfo(String name){
        super();
        super.name = name;
    }
}
