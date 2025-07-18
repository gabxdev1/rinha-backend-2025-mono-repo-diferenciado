package br.com.gabxdev.ws;


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

    public static String buildEventDTO(String id, EventType type, String payload) {
        return new StringBuilder(id)
                .append("&")
                .append(type)
                .append("&")
                .append(payload)
                .toString();
    }

    public static String buildEventDTO(Event event) {
        return new StringBuilder(event.id)
                .append("&")
                .append(event.type)
                .append("&")
                .append(event.payload)
                .toString();
    }

    public static Event parseEvent(String eventDTO) {
        var eventString = eventDTO.split("&");

        return new Event(eventString[0],
                EventType.valueOf(eventString[1]),
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
