package be.alexandre01.dreamnetwork.plugins.sponge.api;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.request.RequestManager;
import be.alexandre01.dreamnetwork.connection.client.handler.BasicClientHandler;
import be.alexandre01.dreamnetwork.plugins.spigot.DNSpigot;
import be.alexandre01.dreamnetwork.plugins.spigot.communication.SpigotRequestReponse;
import be.alexandre01.dreamnetwork.plugins.sponge.DNSponge;
import be.alexandre01.dreamnetwork.plugins.sponge.communication.SpongeRequestReponse;
import org.spongepowered.api.Sponge;

public class DNSpongeAPI extends NetworkBaseAPI {
    BasicClientHandler basicClientHandler;
    private final DNSponge dnSponge;
    String processName = null;

    public DNSpongeAPI(DNSponge dnSponge){
        this.dnSponge = dnSponge;
    }
    
    @Override
    public String getInfo() {
        return "SPONGE-"+dnSponge.getVersion();
    }

    @Override
    public String getProcessName() {
        return processName;
    }

    @Override
    public void setProcessName(String processName) {
        this.processName = processName;
    }

    @Override
    public int getPort() {
        return dnSponge.getPort();
    }

    @Override
    public java.util.logging.Logger getLogger() {
        return java.util.logging.Logger.getGlobal();
    }

    @Override
    public RequestManager getRequestManager() {
        return dnSponge.getRequestManager();
    }

    @Override
    public void setRequestManager(RequestManager requestManager) {

    }

    @Override
    public BasicClientHandler getBasicClientHandler() {
        return null;
    }

    @Override
    public void setBasicClientHandler(BasicClientHandler basicClientHandler) {
        this.basicClientHandler = basicClientHandler;
        getRequestManager().setBasicClientHandler(basicClientHandler);
        basicClientHandler.getResponses().add(new SpongeRequestReponse());
    }

    @Override
    public void shutdownProcess() {
        Sponge.getServer().shutdown();
    }
}
