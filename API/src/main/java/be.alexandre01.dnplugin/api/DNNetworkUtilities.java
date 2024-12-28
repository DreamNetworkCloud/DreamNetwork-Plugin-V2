package be.alexandre01.dnplugin.api;

import be.alexandre01.dnplugin.api.connection.IBasicClient;
import lombok.Getter;

import java.util.Optional;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 21/10/2023 at 21:53
*/
public abstract class DNNetworkUtilities {
    @Getter private static DNNetworkUtilities instance;

    static {
        try {
            Class.forName("be.alexandre01.dnplugin.api.DreamNetworkingInitializer").newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public DNNetworkUtilities() {
        instance = this;
    }
    public abstract Optional<IBasicClient> initConnection();
}
