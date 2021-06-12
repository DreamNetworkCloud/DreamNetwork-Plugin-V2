package be.alexandre01.dreamnetwork.api.request.exception;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.utils.colors.Colors;
import org.fusesource.jansi.Ansi;

public class RequestNotFoundException extends Exception{
    public RequestNotFoundException(){
        super("The request isn't foundable.");
        if(Ansi.isEnabled()){
            NetworkBaseAPI.getInstance().getLogger().severe(Ansi.Color.RED+"ERROR CAUSE>> "+getMessage()+" || "+ getClass().getSimpleName());
            for(StackTraceElement s : getStackTrace()){
                System.out.println("----->");
                NetworkBaseAPI.getInstance().getLogger().severe("ERROR ON>> "+ Colors.WHITE_BACKGROUND+Colors.ANSI_BLACK()+s.getClassName()+":"+s.getMethodName()+":"+s.getLineNumber()+Colors.ANSI_RESET());
            }
            return;
        }

        NetworkBaseAPI.getInstance().getLogger().severe("ERROR CAUSE>> "+getMessage()+" || "+ getClass().getSimpleName());
        for(StackTraceElement s : getStackTrace()){
            System.out.println("----->");
            NetworkBaseAPI.getInstance().getLogger().severe("ERROR ON>> "+ s.getClassName()+":"+s.getMethodName()+":"+s.getLineNumber());
        }

    }


}
