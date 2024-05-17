package org.example.atm;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumerates the denominations of currency available in the ATM.
 * <p>
 * The Denomination enum represents the different denominations of currency supported by the ATM.
 * Each denomination is associated with a specific value, which represents the monetary worth of that denomination.
 */
public enum Denomination {
    HUNDRED(100),
    TWO_HUNDRED(200),
    FIVE_HUNDRED(500);

    private int value;

    Denomination(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    // TODO: Consider using a Map for dynamic denominations
    public static Map<Integer, Denomination> createDenominationMap() {
        Map<Integer, Denomination> denominationMap = new HashMap<>();
        for (Denomination denomination : values()) {
            denominationMap.put(denomination.getValue(), denomination);
        }
        return denominationMap;
    }
}
