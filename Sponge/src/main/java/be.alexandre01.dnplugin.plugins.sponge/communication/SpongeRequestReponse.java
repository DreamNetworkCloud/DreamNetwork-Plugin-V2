package be.alexandre01.dnplugin.plugins.sponge.communication;

import be.alexandre01.dnplugin.api.request.RequestType;
import be.alexandre01.dnplugin.api.request.communication.ClientResponse;
import org.spongepowered.api.Sponge;

public class SpongeRequestReponse extends ClientResponse {

    public SpongeRequestReponse(){
        addRequestInterceptor(RequestType.SPIGOT_EXECUTE_COMMAND,(message, ctx) -> {
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), message.getString("CMD"));
        });
        addRequestInterceptor(RequestType.CORE_STOP_SERVER,(message, ctx) -> {
            Sponge.getServer().shutdown();
        });
    }
}
