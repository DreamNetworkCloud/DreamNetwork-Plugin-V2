package be.alexandre01.dnplugin.api.utils.files.tablist;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;

@Getter
@AllArgsConstructor
public class LineState {
    private final HashMap<Integer, String> lines;
    private final int repeat;
}
