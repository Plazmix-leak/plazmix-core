package net.plazmix.core.protocol;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class PacketMapper {

    private final TObjectIntMap<Class<? extends Packet<?>>> idToPackets = new TObjectIntHashMap<>();
    private final TIntObjectMap<Supplier<? extends Packet<?>>> toIdPackets = new TIntObjectHashMap<>();

    public <T extends Packet<?>> void registerPacket(int id, Class<T> cls, Supplier<T> supplier) {
        if (id < 0) {
            return;
        }

        if (idToPackets.containsValue(id) || toIdPackets.containsKey(id)) {
            throw new ProtocolException(String.format("Packet %s is already registered", id));
        }

        idToPackets.put(cls, id);
        toIdPackets.put(id, supplier);
    }

    public int getPacketId(Packet<?> packet) {
        return idToPackets.get(packet.getClass());
    }

    public Packet<?> getPacket(int id) {
        return toIdPackets.get(id).get();
    }
}
