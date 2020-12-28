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
        for (int i = 0; i < stepPower; i++) {
            babyStepSet.add(p.mul(p.getModulus().intValue() + 1 + i).getX());
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
        for (int i = 0; i < stepPower + 1; i++) {
            giantStepSet.add(p.mul(i * stepPower).getX());
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

    public static BigInteger getLegendreSymbol(BigInteger val, BigInteger m) {
        BigInteger exponent = m.add(BigInteger.ONE.negate()).divide(BigInteger.TWO);
        return val.modPow(exponent, m);
    }

    public static BigInteger getNonResidue(BigInteger m) {
        for (BigInteger i = toBigInteger(0); i.compareTo(m) == -1; i = i.add(toBigInteger(1))) {
            if (getLegendreSymbol(i, m).equals(toBigInteger(-1))) {
                return i;
            }
        }
        return toBigInteger(-1);
    }

    public static BigInteger getIndex(BigInteger xCoord, List<BigInteger> set) {
        for (int i = 0; i < set.size(); i++) {
            if (set.get(i).equals(xCoord)) {
                return toBigInteger(i);
            }
        }
        throw new RuntimeException(ErrorMessages.INDEX_NOT_FOUND_EXCEPTION_MSG);
    }
}
