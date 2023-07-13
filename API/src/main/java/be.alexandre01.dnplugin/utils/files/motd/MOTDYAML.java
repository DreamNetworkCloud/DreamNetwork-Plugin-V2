package be.alexandre01.dnplugin.utils.files.motd;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MOTDYAML {
    private boolean activated;
    private boolean customVersionProtocol;
    private String customVersionMessage;
    private boolean autoSlotIncrement;
    private List<String> content;
    private List<String> versionHover;
}
