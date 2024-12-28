package be.alexandre01.dnplugin.api.utils.files.tablist;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;

@Getter
@AllArgsConstructor
public class TabList {
    private int overrideLines;
    private HashMap<Integer, String> defaultLines;
    private HashMap<Integer, LineState> states;
}
