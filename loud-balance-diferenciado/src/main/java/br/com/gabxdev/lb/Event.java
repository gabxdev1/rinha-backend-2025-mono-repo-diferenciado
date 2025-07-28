package br.com.gabxdev.lb;


public class Event {
    private final EventType type;

    private final String payload;

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

    public static String buildEventDTO(int type, String payload) {
        return new StringBuilder(50)
                .append(type)
                .append("&")
                .append(payload)
                .toString();
    }
}
