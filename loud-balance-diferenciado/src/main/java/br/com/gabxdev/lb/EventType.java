package br.com.gabxdev.lb;

public enum EventType {
    PAYMENT_SUMMARY('a'),

    PAYMENT_SUMMARY_MERGE('b'),

    PAYMENT_POST('c'),

    PURGE('d');

    private final char value;

    EventType(char value) {
        this.value = value;
    }

    public byte getValue() {
        return (byte) value;
    }
}
