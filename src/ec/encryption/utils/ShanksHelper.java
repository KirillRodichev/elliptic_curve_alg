package ec.encryption.utils;

import ec.encryption.constants.ErrorMessages;
import ec.encryption.model.Point;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ec.encryption.utils.ConvertHelper.toBigInteger;

public class ShanksHelper {
    private static final double QUARTER = 0.25;

    /**
     *
     * @param fieldPower finite field power
     * @return W = step power (power of giant or baby step)
     */
    public static int getStepPower(int fieldPower) {
        return (int) Math.floor(Math.pow(fieldPower, QUARTER) * Math.sqrt(2));
    }

    /**
     * @param stepPower W = [p^(1/4) * sqrt(2)] calculated by getStepPower()
     * @param p         Point
     * @return array of 'x' coords of [p + 1 + β]Point
     */
    public static List<BigInteger> getBabyStepSet(int stepPower, Point p) {
        List<BigInteger> babyStepSet = new ArrayList<>();
        for (int babyStepIndex = 0; babyStepIndex < stepPower; babyStepIndex++) {
            BigInteger fieldPower = p.getModulus();
            BigInteger xCoord = p.mul(
                    fieldPower
                            .add(BigInteger.ONE).mod(fieldPower)
                            .add(toBigInteger(babyStepIndex)).mod(fieldPower).intValue()
            ).getX();
            babyStepSet.add(xCoord);
        }
        return babyStepSet;
    }

    /**
     * @param stepPower W = [p^(1/4) * sqrt(2)] calculated by getStepPower()
     * @param p         Point
     * @return array of 'x' coords of [γ * W]Point
     */
    public static List<BigInteger> getGiantStepSet(int stepPower, Point p) {
        List<BigInteger> giantStepSet = new ArrayList<>();
        for (int giantStepIndex = 0; giantStepIndex < stepPower + 1; giantStepIndex++) {
            BigInteger xCoord = p.mul(toBigInteger(giantStepIndex * stepPower).mod(p.getModulus()).intValue()).getX();
            giantStepSet.add(xCoord);
        }
        return giantStepSet;
    }

    public static List<BigInteger> getIntersection(List<BigInteger> set1, List<BigInteger> set2) {
        Collections.sort(set1);
        Collections.sort(set2);
        int i = 0, j = 0;
        List<BigInteger> intersection = new ArrayList<>();
        while (i < set1.size() && j < set2.size()) {
            if (set1.get(i).equals(set2.get(j)) || set1.get(i).compareTo(set2.get(j)) == -1) {
                if (set1.get(i).equals(set2.get(j))) {
                    intersection.add(set1.get(i));
                }
                i++;
                while (i < set1.size() - 1 && set1.get(i).equals(set1.get(i - 1))) {
                    i++;
                }
            } else {
                j++;
                while (j < set2.size() - 1 && set2.get(j).equals(set2.get(j - 1))) {
                    j++;
                }
            }
        }
        return intersection;
    }

    /**
     *
     * @param numerator - legendre numerator
     * @param denominator - legendre denominator
     * @return {0, 1, -1}
     * 0 if numerator === 0(mod m)
     * 1 if x from Z exists: x^2 === numerator(mod m)
     * -1 if x from Z doesn't exist: x^2 === numerator(mod m)
     */
    public static int getLegendreSymbol(BigInteger numerator, BigInteger denominator) {
        numerator = numerator.mod(denominator);
        int legendre = 1;
        while (numerator.compareTo(BigInteger.ZERO) == 1) {
            while (numerator.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
                numerator = numerator.divide(BigInteger.TWO);
                BigInteger r = denominator.mod(toBigInteger(8));
                if (r.equals(toBigInteger(3)) || r.equals(toBigInteger(5))) {
                    legendre = -legendre;
                }
            }
            BigInteger temp = denominator;
            denominator = numerator;
            numerator = temp;
            if (
                    numerator.mod(toBigInteger(4)).equals(toBigInteger(3)) &&
                    denominator.mod(toBigInteger(4)).equals(toBigInteger(3))
            ) {
                legendre = -legendre;
            }
            numerator = numerator.mod(denominator);
        }
        if (denominator.equals(BigInteger.ONE)) {
            return legendre;
        }
        return 0;
    }

    public static BigInteger getNonResidue(BigInteger m) {
        for (BigInteger i = BigInteger.ZERO; i.compareTo(m) == -1; i = i.add(BigInteger.ONE)) {
            if (getLegendreSymbol(i, m) == -1) {
                return i;
            }
        }
        throw new RuntimeException(ErrorMessages.NON_RESIDUE_NOT_FOUND_EXCEPTION_MSG);
    }

    public static BigInteger getIndex(BigInteger xCoord, List<BigInteger> set) {
        for (int i = 0; i < set.size(); i++) {
            if (set.get(i).equals(xCoord)) {
                return toBigInteger(i);
            }
        }
        throw new RuntimeException(ErrorMessages.INDEX_NOT_FOUND_EXCEPTION_MSG);
    }

    public static BigInteger getLegendreNumerator(BigInteger xCoord, BigInteger fieldPower, BigInteger a, BigInteger b) {
        return xCoord
                .modPow(toBigInteger(3), fieldPower)
                .add(xCoord.multiply(a).mod(fieldPower))
                .add(b)
                .mod(fieldPower);
    }

    /**
     *
     * @param p multiplied point
     * @param fieldPower finite field power
     * @param t = babyStepIndex +- giantStepIndex * stepPower
     * @return point that can be null
     */
    public static Point getExpectedNullPoint(Point p, BigInteger fieldPower, BigInteger t) {
        return p.mul(fieldPower.add(BigInteger.ONE).mod(fieldPower).add(t).mod(fieldPower).intValue());
    }
}
