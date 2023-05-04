package be.alexandre01.dnplugin.utils.messages;
import be.alexandre01.dnplugin.api.request.RequestInfo;
import be.alexandre01.dnplugin.api.request.RequestType;
import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;

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
        super.put("DN-"+id,value);
        return this;
    }

    @Override
    public Object put(String id, Object value){
        return super.put("DN-"+id,value);
    }

    public Object setInRoot(String id, Object value){
        return super.put(id,value);
    }
    public boolean contains(String key){
        return containsKey("DN-"+key);
    }

    public HashMap<String, Object> getObjectData(){
        return this;
    }
    public boolean hasRequestID(){
        return super.containsKey("MID");
    }
    public int getMessageID(){
        return (int) super.get("MID");
    }
    @Override
    public Object get(Object key) {
        return super.get("DN-"+key);
    }

    public Object getInRoot(Object key){
        return super.get(key);
    }

    public <T> T get(String key,Class<T> tClass){
        return (T) super.get("DN-"+key);
    }

    public Message setChannel(String channel){
        super.put("channel",channel);
        return this;
    }

    public String getChannel(){
        return containsKey("channel") ? (String) super.get("channel") : "core";
    }
    public Message setHeader(String header){
        super.put("header",header);
        return this;
    }
    public void setProvider(String provider){
        super.put("provider",provider);
    }
    public void setSender(String provider){
        super.put("sender",provider);
    }
    public String getProvider(){
        return (String) super.get("provider");
    }
    public String getSender(){
        return (String) super.get("sender");
    }
    public boolean hasProvider(){
        return containsKey("provider");
    }
    public String getHeader(){
        return (String) super.get("header");
    }

    public Message setRequestInfo(RequestInfo requestInfo){
        super.put("RI",requestInfo.id);
        return this;
    }

    public RequestInfo getRequest(){
        return (RequestInfo) RequestType.getByID((Integer) super.get("RI"));
    }
    public int getRequestID(){
        return (int) super.get("RI");
    }

    public boolean hasRequest(){
        return containsKey("RI");
    }

    public JsonObject toJsonObject() {
        return new Gson().toJsonTree(this).getAsJsonObject();
    }

    public String getString(String key){
        return String.valueOf(super.get("DN-"+key));
    }

    public int getInt(String key){
        return (int) get(key);
        //  return (int) Integer.parseInt(getString(key));
    }

    public float getFloat(String key){
        return ((Double) get(key)).floatValue();
        //return (float) Float.parseFloat(getString(key));
    }

    public long getLong(String key){
        return (long) get(key);
        //return (long) Long.parseLong(getString(key));
    }

    public List<?> getList(String key) {
        return (List<?>) get(key);
        //return new ArrayList<>(Arrays.asList(getString(key).split(",")));
    }
    public <T> List<T> getList(String key, Class<T> tClass) {
        return (List<T>) get(key);
        // return new ArrayList<T>((Collection<? extends T>) Arrays.asList(getString(key).split(",")));
    }
    public List<Integer> getIntegersList(String key){
        /*List<Double> doubles = (List<Inte>) get(key);
        List<Integer> i = new ArrayList<>();
        for(Double d : doubles){
            i.add(d.intValue());
        }*/

        return (List<Integer>) get(key);
      //  return i;
    }

    public List<Float> getFloatList(String key){
     /*   List<Double> doubles = (List<Double>) get(key);
        List<Float> i = new ArrayList<>();
        for(Double d : doubles){
            i.add(d.floatValue());
        }*/

        return (List<Float>) get(key);
        //return i;
    }


    public boolean getBoolean(String key){
        return (boolean) get(key);
        //return (boolean) Boolean.parseBoolean(getString(key));
    }

    public boolean hasChannel(){
        return containsKey("channel");
    }

    public static Message createFromJsonString(String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(new TypeToken<Map <String, Object>>(){}.getType(),  new MapDeserializerDoubleAsIntFix());
        Gson gson = gsonBuilder.create();
        Message builder = new Message(gson.fromJson(json, HASH_MAP_TYPE));
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

    @Override
    public String toString() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(new TypeToken<Map <String, Object>>(){}.getType(),  new MapDeserializerDoubleAsIntFix());
        //gsonBuilder.setLongSerializationPolicy(LongSerializationPolicy.DEFAULT);
        String json = gsonBuilder.create().toJson(this,Message.class);
        return json;
    }

    public static class MapDeserializerDoubleAsIntFix implements JsonDeserializer<Map<String, Object>> {

        @Override  @SuppressWarnings("unchecked")
        public Map<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return (Map<String, Object>) read(json);
        }

        public Object read(JsonElement in) {

            if(in.isJsonArray()){
                List<Object> list = new ArrayList<Object>();
                JsonArray arr = in.getAsJsonArray();
                for (JsonElement anArr : arr) {
                    list.add(read(anArr));
                }
                return list;
            }else if(in.isJsonObject()){
                Map<String, Object> map = new LinkedTreeMap<String, Object>();
                JsonObject obj = in.getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entitySet = obj.entrySet();
                for(Map.Entry<String, JsonElement> entry: entitySet){
                    map.put(entry.getKey(), read(entry.getValue()));
                }
                return map;
            }else if( in.isJsonPrimitive()){
                JsonPrimitive prim = in.getAsJsonPrimitive();
                if(prim.isBoolean()){
                    return prim.getAsBoolean();
                }else if(prim.isString()){
                    return prim.getAsString();
                }else if(prim.isNumber()){

                    Number num = prim.getAsNumber();
                    // here you can handle double int/long values
                    // and return any type you want
                    // this solution will transform 3.0 float to long values
                    if(Math.ceil(num.doubleValue())  == num.longValue()){
                        if(num.longValue() > Integer.MAX_VALUE){
                            return num.longValue();
                        }else {
                            return num.intValue();
                        }
                    } else{
                        return num.doubleValue();
                    }
                }
            }
            return null;
        }
    }

}
