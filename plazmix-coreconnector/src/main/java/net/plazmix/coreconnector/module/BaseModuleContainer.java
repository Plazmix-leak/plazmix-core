package net.plazmix.coreconnector.module;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import net.plazmix.coreconnector.protocol.client.CModuleDataUpdatePacket;
import net.plazmix.coreconnector.utility.JsonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public final class BaseModuleContainer {

    private final BaseServerModule module;

    private final Map<String, Class<?>> dataTypeClassMap = new HashMap<>();
    private final Map<String, Object> moduleContainerMap = new HashMap<>();
    // FIXME: Потом нужно переписать так, чтобы вместо объекта мапа возвращался String (то есть исходный json)


    public boolean contains(String key) {
        return moduleContainerMap.containsKey(key.toLowerCase());
    }

    public Object read(String key) {
        Preconditions.checkArgument(key != null, "key");

        return moduleContainerMap.get(key.toLowerCase());
    }

    public Object read(String key, Object def) {
        Preconditions.checkArgument(key != null, "key");

        return moduleContainerMap.getOrDefault(key.toLowerCase(), def);
    }

    public <T> T read(Class<T> aClass, String key) {
        Preconditions.checkArgument(aClass != null, "class type");

        return (T) read(key);
    }

    public <T> T read(Class<T> aClass, String key, Object def) {
        Preconditions.checkArgument(aClass != null, "class type");

        return (T) read(key, def);
    }

    public String readString(String key) {
        return read(key).toString();
    }

    public List<String> readStringList(String key) {
        return (List<String>) read(key);
    }

    public boolean readBoolean(String key) {
        return Boolean.parseBoolean(key);
    }

    public int readInt(String key) {
        return Integer.parseInt(readString(key));
    }

    public double readDouble(String key) {
        return Double.parseDouble(readString(key));
    }

    public long readLong(String key) {
        return Long.parseLong(readString(key));
    }

    public float readFloat(String key) {
        return Float.parseFloat(readString(key));
    }

    private void onPacketHandle(ModuleExecuteType executeType, String key, Object value) {
        Preconditions.checkArgument(executeType != null, "executeType");
        Preconditions.checkArgument(key != null, "key");

        switch (executeType) {

            case DELETE: {
                moduleContainerMap.remove(key.toLowerCase());
                break;
            }

            case INSERT: {
                moduleContainerMap.put(key.toLowerCase(), value);
                break;
            }
        }

        module.onValueRead(executeType, key, value);
    }

    public void setOnReadPacketKey(String key, Class<?> typeClass) {
        Preconditions.checkArgument(key != null, "key");
        Preconditions.checkArgument(typeClass != null, "typeClass");

        dataTypeClassMap.put(key.toLowerCase(), typeClass);
    }

    public void handlePacket(CModuleDataUpdatePacket packet) {
        String dataKey = dataTypeClassMap.keySet()
                .stream()
                .filter(key -> packet.getKey().toLowerCase().startsWith(key.toLowerCase()))
                .findFirst()
                .orElse(null);

        if (dataKey == null) {
            return;
        }

        Class<?> dataClass = dataTypeClassMap.get(dataKey);

        if (dataClass != null) {
            onPacketHandle(packet.getExecuteType(), packet.getKey(), JsonUtil.fromJson(packet.getJson(), dataClass));
        }
    }

}
