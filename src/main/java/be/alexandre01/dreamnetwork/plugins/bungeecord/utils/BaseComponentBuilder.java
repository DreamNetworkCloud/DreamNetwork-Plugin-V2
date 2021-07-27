package be.alexandre01.dreamnetwork.plugins.bungeecord.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.HashMap;

public class BaseComponentBuilder {
    private ArrayList<Integer> index = new ArrayList<>();
    private HashMap<Integer, String> lines = new HashMap<>();

    public void setLine(Integer integer,String line){
        lines.put(integer,line);
        index.add(integer);
    }

    public TextComponent toTextComponent(){
        TextComponent textComponent = new TextComponent();
        int lastLine = 1;
        for (int i = 0; i < index.size(); i++) {
            int l = index.get(i);

            if(l-lastLine != 0){
                for (int j = 0; j < l-lastLine; j++) {
                    textComponent.addExtra("\n");
                }
            }
            textComponent.addExtra(lines.get(l));
        }
        return textComponent;
    }
}
