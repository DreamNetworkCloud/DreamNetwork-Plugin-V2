package be.alexandre01.dreamnetwork.utils.messages;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Message extends LinkedHashMap<String, Object> {
    private static final Type HASH_MAP_TYPE = new TypeToken<Map<String, Object>>() {
    }.getType();
    public Message(Map<String, Object> map) {
        super(map);
    }
    public Message() {
        super(new LinkedHashMap<>());
    }
    public Message set(String id,Object value){
        put("DN-"+id,value);
        return this;
    }
    public boolean contains(String key){
        return containsKey("DN-"+key);
    }

    public HashMap<String, Object> getObjectData(){
        return this;
    }

    public <T> T getObject(String key,Class<T> tClass){
        return (T) get("DN-"+key);
    }

    public Message setChannel(String channel){
        return set("channel",channel);
    }

    public Message setHeader(String header){
        return set("header",header);
    }

    public String getHeader(){
        return getString("header");
    }

    public JsonObject toJsonObject() {
        return new Gson().toJsonTree(this).getAsJsonObject();
    }

    public String getString(String key){
        return String.valueOf(get("DN-"+key));
    }

    public int getInt(String key){
        return (int) Math.round((double) get("DN-"+key));
    }

    public float getFloat(String key){
        return (float) get("DN-"+key);
    }

    public boolean getBoolean(String key){
        return (boolean) get("DN-"+key);
    }

    public boolean hasChannel(){
        return contains("channel");
    }

    public static Message createFromJsonString(String json) {
        Message builder = new Message(new Gson().fromJson(json, HASH_MAP_TYPE));
        return builder;
    }

    public static boolean isJSONValid(String json) {
        try {
            new Gson().fromJson(json, Object.class);
            return true;
        } catch(com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }


    public String toString() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        // gsonBuilder.setLongSerializationPolicy(LongSerializationPolicy.STRING);
        gsonBuilder.registerTypeAdapter(Double.class,  new JsonSerializer<Double>() {
            @Override
            public JsonElement serialize(final Double src, final Type typeOfSrc, final JsonSerializationContext context) {
                BigDecimal value = BigDecimal.valueOf(src);

                return new JsonPrimitive(value);
            }
        });

        String json = gsonBuilder.create().toJson(this);
        return json;
    }

}
