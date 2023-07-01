package be.alexandre01.dnplugin.api.request.exception;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.utils.colors.Colors;

public class IDNotFoundException extends Exception{
    public IDNotFoundException(String name){
        super("The id isn't foundable from name " + name);
        NetworkBaseAPI.getInstance().getLogger().severe("ERROR CAUSE>> "+getMessage()+" || "+ getClass().getSimpleName());
        for(StackTraceElement s : getStackTrace()){
            System.out.println("----->");
            NetworkBaseAPI.getInstance().getLogger().severe("ERROR ON>> "+ s.getClassName()+":"+s.getMethodName()+":"+s.getLineNumber());
        }

    }
}
