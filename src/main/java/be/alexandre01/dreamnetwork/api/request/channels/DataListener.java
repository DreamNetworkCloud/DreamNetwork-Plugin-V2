package be.alexandre01.dreamnetwork.api.request.channels;

import lombok.Getter;
import lombok.Setter;


    public abstract class DataListener<T>{
        @Getter
        @Setter
        private DNChannel dnChannel;
        @Setter private String key;
        @Setter @Getter private Class aClass;


        public void set(T data){
            dnChannel.setData(key,data);
        }
        public abstract void onUpdateData(T data);
    }

