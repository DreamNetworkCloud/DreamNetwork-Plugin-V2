package be.alexandre01.dnplugin.api.connection.request.packets;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 05/11/2023 at 12:57
*/
@Target(value={ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MyRequestHandler {
    TestRequest id();
    int priority() default 0;
    String[] channels() default {};
    RequestHandler.PacketCastOption castOption() default RequestHandler.PacketCastOption.NOT_SET;
}
