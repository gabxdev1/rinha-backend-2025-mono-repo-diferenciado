package br.com.gabxdev.mapper;

import br.com.gabxdev.lb.Event;
import br.com.gabxdev.lb.EventType;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class EventBinarySerializer {
    public static byte[] serialize(Event event) {
        var idBytes = event.getId().getBytes(StandardCharsets.UTF_8);
        var payloadBytes = event.getPayload().getBytes(StandardCharsets.UTF_8);

        var totalSize = 2 + idBytes.length + 1 + 4 + payloadBytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(totalSize);

        buffer.putShort((short) idBytes.length);
        buffer.put(idBytes);
        buffer.put((byte) event.getType().ordinal());
        buffer.putInt(payloadBytes.length);
        buffer.put(payloadBytes);

        return buffer.array();
    }

    public static Event deserialize(byte[] bytes) {
        var buffer = ByteBuffer.wrap(bytes);

        var idLen = buffer.getShort();
        var idBytes = new byte[idLen];
        buffer.get(idBytes);
        String id = new String(idBytes, StandardCharsets.UTF_8);

        var typeOrdinal = buffer.get();
        EventType type = EventType.values()[typeOrdinal];

        var payloadLen = buffer.getInt();
        var payloadBytes = new byte[payloadLen];
        buffer.get(payloadBytes);
        String payload = new String(payloadBytes, StandardCharsets.UTF_8);

        return new Event(id, type, payload);
    }

}
