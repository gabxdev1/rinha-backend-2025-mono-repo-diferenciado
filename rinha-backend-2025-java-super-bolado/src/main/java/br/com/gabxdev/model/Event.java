package br.com.gabxdev.model;

import br.com.gabxdev.model.enums.EventType;

public class Event {
    private final String id;

    private final EventType type;

    private String payload;

    public Event(String id, EventType type, String payload) {
        this.id = id;
        this.type = type;
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public EventType getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public static String buildEventDTO(String id, int type, String payload) {
        return new StringBuilder(id)
                .append("&")
                .append(type)
                .append("&")
                .append(payload)
                .toString();
    }

    public static Event parseEvent(String eventDTO) {
        var eventString = eventDTO.split("&");

        return new Event(eventString[0],
                EventType.values()[Integer.parseInt(eventString[1])],
                eventString[2]);
    }

    @Override
    public String toString() {
        return "Event{" +
               "id='" + id + '\'' +
               ", type=" + type +
               ", payload='" + payload + '\'' +
               '}';
    }
}
