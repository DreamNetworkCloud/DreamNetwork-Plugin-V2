package be.alexandre01.dnplugin.api.utils.messages;


import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.request.DNCallbackReceiver;
import be.alexandre01.dnplugin.api.connection.request.Packet;
import be.alexandre01.dnplugin.api.objects.RemoteService;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.objects.server.NetEntity;
import be.alexandre01.dnplugin.api.connection.request.RequestInfo;
import be.alexandre01.dnplugin.api.connection.request.RequestType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Getter
public class Message extends LinkedHashMap<String, Object> {
    ObjectMapper mapper;

    final char prefix = '$';

    public Message(Map<String, Object> map) {
        this(map, createMapper());
    }

    public Message(Map<String, Object> map, ObjectMapper mapper) {
        super(map);
        this.mapper = mapper;
    }

    public Message() {
        this(new LinkedHashMap<>(), createMapper());
    }

    public static Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        //   gsonBuilder.setLenient();
        gsonBuilder.registerTypeAdapter(Integer.class, new JsonSerializer<Integer>() {
            @Override
            public JsonElement serialize(Integer src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src);
            }
        });

        gsonBuilder.registerTypeAdapter(Integer.class, new JsonDeserializer<Integer>() {
            @Override
            public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                try {
                    return json.getAsInt();
                } catch (NumberFormatException e) {
                    throw new JsonParseException(e);
                }
            }
        });

        gsonBuilder.setLongSerializationPolicy(LongSerializationPolicy.STRING);


        return gsonBuilder.create();
    }

    public static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        return mapper;
    }

    public static Message createFromJsonString(String json) {
        try {
            ObjectMapper mapper = createMapper();
            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            JavaType type = mapper.getTypeFactory().
                    constructMapType(Map.class, String.class, Object.class);

            return new Message(mapper.readValue(json, type), mapper);
            //return new Message(createGson().fromJson(json, HASH_MAP_TYPE));
        } catch (Exception e) {
            return null;
        }
    }


    public Message set(String id, Object value, Class<?>... overrideChild) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        if (overrideChild.length != 0) {
            List<Class<?>> classes = Arrays.asList(overrideChild);
            gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
                public boolean shouldSkipField(FieldAttributes field) {
                    return classes.stream().noneMatch(field.getDeclaringClass()::equals);
                }

                @Override
                public boolean shouldSkipClass(Class<?> aClass) {
                    return false;
                }
            });
            value = gsonBuilder.create().toJson(value);
        }
        super.put(prefix + id, value);
        System.out.println("Value to be set " + value);
        return this;
    }

    public Message setCustomObject(String id, Object value, Class<?> tClass) {
        System.out.println("Test2");


        ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        try {
            System.out.println(value);
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writer().forType(tClass).writeValueAsString(value);
            System.out.println(json);
            super.put(prefix + id, json);
        } catch (JsonProcessingException e) {
            System.out.println("Error with custom object");
            e.printStackTrace();
        }
        //super.put(prefix+id, value);
        System.out.println("Value to be set " + value);
        return this;
    }



    public Message setList(String id, List<?> value, Class<?>... overrideChild) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        if (overrideChild.length != 0) {

            List<Class<?>> classes = Arrays.asList(overrideChild);
            gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
                public boolean shouldSkipField(FieldAttributes field) {
                    return classes.stream().noneMatch(field.getDeclaringClass()::equals);
                }

                @Override
                public boolean shouldSkipClass(Class<?> aClass) {
                    return false;
                }
            });
        }
        String json = gsonBuilder.create().toJson(value);
        super.put(prefix + id, json);
        return this;
    }

    /*public Message setMap(String id, Map<?, ?> value) {
        return setMap(id, value, null);
    }

    public Message setMap(String id, Map<?, ?> value, Class<?> toOverride) {

        return null;
    }*/

    public Message setInRoot(String id, Object value) {
        super.put(id, value);
        return this;
    }

    public boolean contains(String key) {
        return super.containsKey(prefix + key);
    }
    public boolean containsKey(String key) {
        return super.containsKey(prefix + key);
    }
    public boolean containsValue(String key) {
        return super.containsValue(prefix + key);
    }

    public boolean containsKeyInRoot(String key) {
        return super.containsKey(key);
    }

    public boolean containsValueInRoot(String key) {
        return super.containsValue(key);
    }

    public Message remove(String key) {
        super.remove(prefix + key);
        return this;
    }

    public Message removeInRoot(String key) {
        super.remove(key);
        return this;
    }
    public HashMap<String, Object> getObjectData() {
        return this;
    }

    public int getMessageID() {
        return (int) super.get("MID");
    }

    @Override
    public Object get(Object key) {
        return super.get(Character.toString(prefix) + key);
    }

    public Object getInRoot(Object key) {
        return super.get(key);
    }

    public Object get(String key, JavaType type) {
        Object o = super.get(prefix + key);
        if (o instanceof String) {
            try {
                return mapper.readValue((String) o, type);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            //return gson.fromJson((String) o,tClass);
        }
        if (o == null) {
            return null;
        }
        return super.get(prefix + key);
    }
    public <T> T get(String key, Class<T> tClass){
        return (T) get(key, mapper.getTypeFactory().constructType(tClass));
    }


    public String getChannel() {
        return super.containsKey("channel") ? (String) super.get("channel") : "be.alexandre01.dnplugin/api/objects/core";
    }

    public Message setChannel(String channel) {
        super.put("channel", channel);
        return this;
    }

    public Optional<NetEntity> getProvider() {
        String provider = (String) super.get("from");
        if (provider == null) {
            return Optional.empty();
        }
        if(provider.contains("core")){
            return Optional.of(NetworkBaseAPI.getInstance());
        }
        String[] split = provider.split("-");
        RemoteService remoteService = NetworkBaseAPI.getInstance().getByName(split[0]);
        if(remoteService == null)
            return Optional.empty();

        return Optional.ofNullable(remoteService.getServers().get(Integer.parseInt(split[1])));
    }

    public void setProvider(String provider) {
        super.put("from", provider);
    }

    public String getReceiver() {
        return (String) super.get("to");
    }

    public void setReceiver(String receiver) {
        super.put("to", receiver);
    }

    public boolean hasReceiver() {
        return super.containsKey("to");
    }

    public boolean hasProvider() {
        return super.containsKey("from");
    }

    public String getHeader() {
        return (String) super.get("header");
    }

    public Message setHeader(String header) {
        super.put("header", header);
        return this;
    }

    public Message setRequestInfo(RequestInfo requestType) {
        super.put("RI", requestType.id);
        return this;
    }

    public RequestInfo getRequest() {
        return (RequestInfo) RequestType.getByID((Integer) super.get("RI"));
    }

    public int getRequestID() {
        return (int) super.get("RI");
    }

    public boolean hasRequest() {
        return super.containsKey("RI");
    }

    public String getString(String key) {
        return String.valueOf(super.get(prefix + key));
    }

    public int getInt(String key) {
        return (int) get(key);
        //  return (int) Integer.parseInt(getString(key));
    }

    public float getFloat(String key) {
        return (float) get(key);
        //return (float) Float.parseFloat(getString(key));
    }

    public long getLong(String key) {
        return (long) get(key);
        //return (long) Long.parseLong(getString(key));
    }

    public List<?> getList(String key) {
        return (List<?>) get(key);
        //return new ArrayList<>(Arrays.asList(getString(key).split(",")));
    }

    public <K, V> LinkedTreeMap<K,V> getMap(String key, Class<K> keyMap, Class<V> valueMap) {
        return (LinkedTreeMap<K, V>) get(key, mapper.getTypeFactory().constructMapType(LinkedTreeMap.class, keyMap, valueMap));
        //return new ArrayList<>(Arrays.asList(getString(key).split(",")));
    }

    public <T> ArrayList<T> getList(String key, Class<T> tClass) {
        Object o = get(key);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        Type token = TypeToken.getParameterized(List.class, tClass).getType();
        System.out.println(token.getTypeName());
        JavaType type = mapper.getTypeFactory().
                constructCollectionType(List.class, tClass);
        ArrayList<T> list = null;


        try {
            if (o instanceof String) {
                // T[] array = new Gson().fromJson((String) o, (Class<T[]>) tClass);
                list = mapper.readValue((String) o, type);
                // list = mapper.readValue((String) o, type);
            } else {
                list = mapper.readValue(mapper.writeValueAsString(o), type);

            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return list;
/*

        System.out.println(o.getClass());

        if(o instanceof String){
            return new Gson().fromJson((String) o,type);
        }
        if(o instanceof LinkedTreeMap){
            LinkedTreeMap<?,?> l = (LinkedTreeMap<?,?>) o;
            List<T> list = (List<T>) new ArrayList<>(l.values());
            return list;
        }

        if(o instanceof List){

        }



       // LinkedTreeMap
        //return new Gson().fromJson(new Gson().toJson(get(key)),type);
        //return (List<T>) get(key,type);
       // return new ArrayList<T>((Collection<? extends T>) Arrays.asList(getString(key).split(",")));*/

    }

    public Packet toPacket(NetEntity theReceiver){
        return new Packet() {
            @Override
            public Message getMessage() {
                return Message.this;
            }

            @Override
            public String getProvider() {
                return "be.alexandre01.dnplugin/api/objects/core"; // for later do core-id
            }

            @Override
            public NetEntity getReceiver() {
                return theReceiver;
            }
        };
    }

    public boolean getBoolean(String key) {
        return (boolean) get(key);
        //return (boolean) Boolean.parseBoolean(getString(key));
    }

    public boolean hasChannel() {
        return super.containsKey("channel");
    }



    public Optional<DNCallbackReceiver> getCallback(){
        AtomicReference<DNCallbackReceiver> callbackReceiver = new AtomicReference<>();
        getProvider().ifPresent(new Consumer<NetEntity>() {
            @Override
            public void accept(NetEntity netEntity) {
                callbackReceiver.set(new DNCallbackReceiver(getMessageID(),Message.this));
            }
        });
        return Optional.ofNullable(callbackReceiver.get());
    }

    public String toString() {
        try {
            String json = mapper.writeValueAsString(this);
            System.out.println(json);
            return json;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}