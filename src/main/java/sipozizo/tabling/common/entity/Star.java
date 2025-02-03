package sipozizo.tabling.common.entity;

// Star.java
public enum Star {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    private final int value;

    Star(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}