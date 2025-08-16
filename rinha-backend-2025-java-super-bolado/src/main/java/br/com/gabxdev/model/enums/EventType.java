package br.com.gabxdev.model.enums;

public enum EventType {
    PAYMENT_SUMMARY('a'),

    PAYMENT_SUMMARY_MERGE('b'),

    PAYMENT_POST('c'),

    PURGE('d');

    private final char value;

    EventType(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    public static EventType valueOf(char value) {
        if (value == 'a') {
            return EventType.PAYMENT_SUMMARY;
        }

        if (value == 'd') {
            return EventType.PURGE;
        }

        if (value == 'c') {
            return EventType.PAYMENT_POST;
        }

        if (value == 'b') {
            return EventType.PAYMENT_SUMMARY_MERGE;
        }

        return PAYMENT_POST;
    }

}
