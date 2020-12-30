package ec.encryption.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Randomizer {
    public static int getNumber(int upperBound) {
        return ThreadLocalRandom.current().nextInt(0, upperBound + 1);
    }

    public static BigInteger getRandomXCoord(List<Point> points) {
        List<BigInteger> xCoords = new ArrayList<>();
        for (Point p : points) {
            xCoords.add(p.getX());
        }
        return xCoords.get(getNumber(xCoords.size() - 1));
    }
}
