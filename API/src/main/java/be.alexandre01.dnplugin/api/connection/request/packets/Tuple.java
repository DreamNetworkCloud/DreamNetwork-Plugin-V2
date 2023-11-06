package be.alexandre01.dnplugin.api.connection.request.packets;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 04/11/2023 at 22:32
*/

import lombok.Data;

@Data
public class Tuple<A,B> {
    private A firstValue;
    private B secondValue;

    public Tuple(A a, B b){
        this.firstValue = a;
        this.secondValue = b;
    }
}
