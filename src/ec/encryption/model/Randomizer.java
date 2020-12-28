package ec.encryption.model;

import java.util.concurrent.ThreadLocalRandom;

public class Randomizer {
    public static int getNumber(int upperBound) {
        return ThreadLocalRandom.current().nextInt(0, upperBound + 1);
    }
}
