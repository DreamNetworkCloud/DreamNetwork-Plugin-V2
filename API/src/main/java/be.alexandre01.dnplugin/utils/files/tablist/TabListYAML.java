package be.alexandre01.dnplugin.utils.files.tablist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TabListYAML {
    private boolean activate;
    private int delay;
    private TabList header;
    private TabList footer;
}
