package be.alexandre01.dreamnetwork.plugins.spigot.components;

import org.bukkit.entity.Player;

public class GUIAction {
    public enum Action{
        OPEN,
        CLOSE,
        NEXT,
        PREVIOUS,
        SELECT,
        EXIT,
        BACK,
        CUSTOM,
        NULL;
    }

    private Action action;
    private GUIItem item;
    private CustomAction customAction;
    private Object[] objects;
    public GUIAction(GUIItem item){
        this.action = Action.NULL;
        this.item = item;
    }

    public GUIAction(Action action){
        this.action = action;
    }

    public Action getAction(){
        return action;
    }

    public void setAction(Action action,Object... objects){
        this.objects = objects;
        this.action = action;
    }
    public void setCustomAction(CustomAction customAction,Object... objects){
        this.objects = objects;
        this.action = Action.CUSTOM;
        this.customAction = customAction;
    }

    public void exec(Player player){
        if(action == Action.NULL) return;
        if(action == Action.CUSTOM && customAction != null){
            customAction.onClick(player,objects);
            return;
        }

        if(action == Action.OPEN){
            if(objects[0] instanceof NetworkGUI){
                NetworkGUI gui = (NetworkGUI) objects[0];
              //  gui.(player);
            }
        }
    }
    public interface CustomAction{
        void onClick(Player player,Object... objects);
    }

}
