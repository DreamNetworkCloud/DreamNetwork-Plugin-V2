package be.alexandre01.dreamnetwork.api.objects.player.exceptions;

public class OnlineModeException extends Exception{
    public OnlineModeException(boolean isEnabled){
        super("Cannot get UUID if your server is crack only");
    }
}
