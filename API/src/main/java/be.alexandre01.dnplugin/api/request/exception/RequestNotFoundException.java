package be.alexandre01.dnplugin.api.request.exception;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;

public class RequestNotFoundException extends Exception{
    public RequestNotFoundException(){
        super("The request isn't foundable.");
        NetworkBaseAPI.getInstance().getLogger().severe("ERROR CAUSE>> "+getMessage()+" || "+ getClass().getSimpleName());
        for(StackTraceElement s : getStackTrace()){
            System.out.println("----->");
            NetworkBaseAPI.getInstance().getLogger().severe("ERROR ON>> "+ s.getClassName()+":"+s.getMethodName()+":"+s.getLineNumber());
        }

    }


}
