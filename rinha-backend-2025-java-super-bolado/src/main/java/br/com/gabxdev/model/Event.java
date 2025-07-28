package br.com.gabxdev.model;

import br.com.gabxdev.model.enums.EventType;

public class Event {
    private final EventType type;

    private String payload;

    public Event(EventType type, String payload) {
        this.type = type;
        this.payload = payload;
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

    public static String buildEventDTO(int type, String payload) {
        return new StringBuilder(50)
                .append(type)
                .append("&")
                .append(payload)
                .toString();
    }

    public static Event parseEvent(String eventDTO) {
        var eventString = eventDTO.split("&");

        return new Event(EventType.values()[Integer.parseInt(eventString[0])],
                eventString[1]);
    }

    @Override
    public String toString() {
        return "Event{" +
               ", type=" + type +
               ", payload='" + payload + '\'' +
               '}';
    }
}
