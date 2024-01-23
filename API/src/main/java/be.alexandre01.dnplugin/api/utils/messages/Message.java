package be.alexandre01.dnplugin.api.utils.messages;


import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.request.DNCallbackReceiver;
import be.alexandre01.dnplugin.api.connection.request.Packet;
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
    private final ObjectMapper jacksonMapper;

    final char prefix = '$';

    @Getter static final ObjectsMappingFactory defaultMapper = new ObjectsMappingFactory();

    private ObjectsMappingFactory currentMapper = defaultMapper;

    private static final Set<Class<?>> WRAPPER_TYPES = WrapperTypes.get();

    public static boolean isWrapperType(Class<?> clazz)
    {
        return WRAPPER_TYPES.contains(clazz);
    }


    public Message emptyMapper(){
        currentMapper = null;
        return this;
    }

    public Message newMapper(){
        currentMapper = new ObjectsMappingFactory();
        return this;
    }



    public Message(Map<String, Object> map) {
        this(map, createMapper());
    }


    public Message(Map<String, Object> map, ObjectMapper jacksonMapper) {
        super(map);
        this.jacksonMapper = jacksonMapper;
    }

    public Message(String header){
        this();
        setHeader(header);
    }

    public Message(String key, Object value, boolean objectFormat, Class<?>... overrideChild) {
        this();
        set(key, value, objectFormat, overrideChild);
    }

    public Message(String key, Object value, Class<?>... overrideChild) {
        this();
        set(key, value, overrideChild);
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


    public Message set(String id, Object value, boolean objectFormat, Class<?>... overrideChild) {
        Class<?> valueType = value.getClass();

        ObjectConverterMapper<Object,?> mapper = (ObjectConverterMapper<Object, ?>) currentMapper.getMapper(valueType);
        if(mapper != null){
            valueType = mapper.findConvertableTypeOf(valueType);
            value = mapper.convert(value);
        }
        if(objectFormat){
            GsonBuilder gsonBuilder = null;
            if (overrideChild.length != 0) {
                gsonBuilder = new GsonBuilder();
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
            if(!valueType.isPrimitive() && !isWrapperType(valueType) && valueType != String.class && !valueType.isEnum()){
                if(gsonBuilder == null)
                    gsonBuilder = new GsonBuilder();

                value = gsonBuilder.create().toJson(value,valueType);
            }
        }


        super.put(prefix + id, value);
        return this;
    }

    public Message set(String id, Object value, Class<?>... overrideChild) {
        return set(id, value, true, overrideChild);
    }

    public Message setCustomObject(String id, Object value, Class<?> tClass) {
        Class<?> valueType = tClass;

        ObjectConverterMapper<Object,?> mapper = (ObjectConverterMapper<Object, ?>) currentMapper.getMapper(valueType);
        if(mapper != null){
            valueType = mapper.findConvertableTypeOf(valueType);
            value = mapper.convert(value);
        }

        ObjectMapper jsonMapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        try {
            jsonMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = jsonMapper.writer().forType(valueType).writeValueAsString(value);
            super.put(prefix + id, json);
        } catch (JsonProcessingException e) {
            System.out.println("Error with custom object");
            e.printStackTrace();
        }
        //super.put(prefix+id, value);
        System.out.println("Value to be set " + value);
        return this;
    }



    public Message setList(String id, List<Object> value, Class<?>... overrideChild) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Class<?> valueType = value.getClass().getTypeParameters()[0].getClass();

        ObjectConverterMapper<Object,?> mapper = (ObjectConverterMapper<Object, ?>) currentMapper.getMapper(valueType);
        if(mapper != null){
            valueType = mapper.findConvertableTypeOf(valueType);
            value.replaceAll(mapper::convert);
        }
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
        Object value = super.get(prefix + key);
        Class<?> valueType = type.getRawClass();

        if(valueType == Object.class || valueType == String.class || isWrapperType(type.getRawClass())){
            return get(key);
        }

        ObjectConverterMapper<?,Object> mapper = (ObjectConverterMapper<?, Object>) currentMapper.getMapper(valueType);
        if(mapper != null && mapper.findConvertableTypeOf(valueType) == value.getClass()){
            return mapper.read(value);
        }

        if(type.getRawClass() == Object.class){
            return get(key);
        }
        if (value instanceof String) {
            try {

                return jacksonMapper.readValue((String) value, type);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            //return gson.fromJson((String) o,tClass);
        }
        if (value == null) {
            return null;
        }
        return super.get(prefix + key);
    }
    public <T> T get(String key, Class<T> tClass){
        return (T) get(key, jacksonMapper.getTypeFactory().constructType(tClass));
    }

    public <T> Optional<T> getOptional(String key, Class<T> tClass){
        if(!containsKey(key))
            return Optional.empty();
        return Optional.ofNullable((T) get(key, jacksonMapper.getTypeFactory().constructType(tClass)));
    }

    public <T> Optional<T> getOptional(String key, JavaType type){
        if(!containsKey(key))
            return Optional.empty();
        return Optional.ofNullable((T) get(key, type));
    }

    public <T> Optional<T> getOptional(String key){
        if(!containsKey(key))
            return Optional.empty();
        return Optional.ofNullable((T) get(key));
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
        return NetworkBaseAPI.getInstance().getByFullName(provider).map(dnServer -> dnServer);
        /*NetworkBaseAPI.getInstance().getByName(split[0]).map(dnServer -> )
        if(remoteExecutor == null)
            return Optional.empty();

        return Optional.ofNullable(remoteExecutor.getServers().get(Integer.parseInt(split[1])));*/
    }
    public <T extends NetEntity> Optional<T> getProvider(Class<T> tClass) {
        String provider = (String) super.get("from");
        if (provider == null) {
            return Optional.empty();
        }
        if(provider.contains("core")){
            if(tClass.isAssignableFrom(NetworkBaseAPI.class))
                return Optional.of(tClass.cast(NetworkBaseAPI.getInstance()));
        }
        if(tClass.isAssignableFrom(DNServer.class)){
            return NetworkBaseAPI.getInstance().getByFullName(provider).map(dnServer -> (T) dnServer);
        }
        return Optional.empty();
        /*NetworkBaseAPI.getInstance().getByName(split[0]).map(dnServer -> )
        if(remoteExecutor == null)
            return Optional.empty();

        return Optional.ofNullable(remoteExecutor.getServers().get(Integer.parseInt(split[1])));*/
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

    public boolean hasHeader() {
        return super.containsKey("header");
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
        return (LinkedTreeMap<K, V>) get(key, jacksonMapper.getTypeFactory().constructMapType(LinkedTreeMap.class, keyMap, valueMap));
        //return new ArrayList<>(Arrays.asList(getString(key).split(",")));
    }

    public <T> ArrayList<T> getList(String key, Class<T> tClass) {
        Object o = get(key);
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        Type token = TypeToken.getParameterized(List.class, tClass).getType();
        System.out.println(token.getTypeName());
        JavaType type = jsonMapper.getTypeFactory().
                constructCollectionType(List.class, tClass);
        ArrayList<T> list = null;


        try {
            if (o instanceof String) {
                // T[] array = new Gson().fromJson((String) o, (Class<T[]>) tClass);
                list = jsonMapper.readValue((String) o, type);
                // list = mapper.readValue((String) o, type);
            } else {
                list = jsonMapper.readValue(jsonMapper.writeValueAsString(o), type);
            }

            ObjectConverterMapper<T,Object> mapper = (ObjectConverterMapper<T, Object>) currentMapper.getMapper(type.getRawClass());
            if(mapper != null){
                list.replaceAll(mapper::read);
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
                return NetworkBaseAPI.getInstance().getProcessName();
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
            String json = jacksonMapper.writeValueAsString(this);
            System.out.println(json);
            return json;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}