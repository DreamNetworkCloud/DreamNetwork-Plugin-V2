package be.alexandre01.dnplugin.api.connection.request.packets;

import java.lang.annotation.Annotation;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 05/11/2023 at 11:53
*/
@SuppressWarnings("all")
public enum TestRequest implements RequestExample<MyRequestHandler> {
    MaRequeteCustom,
    SendPlayer;

    TestRequest() {
    }

    @Override
    public String getProjectName() {
        return "Edalia";
    }

    @Override
    public int getOrdinal() {
        return ordinal();
    }
}
