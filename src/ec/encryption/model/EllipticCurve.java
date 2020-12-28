package ec.encryption.model;

import ec.encryption.constants.ErrorMessages;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static ec.encryption.utils.ConvertHelper.toBigInteger;
import static ec.encryption.utils.ShanksHelper.*;

public class EllipticCurve {
    BigInteger a;
    BigInteger b;
    BigInteger fieldPower;
    List<Point> points;

    private EllipticCurve(BigInteger a, BigInteger b, BigInteger fieldPower, List<Point> points) {
        this.a = a;
        this.b = b;
        this.fieldPower = fieldPower;
        this.points = points;
    }

    public static EllipticCurve create(BigInteger a, BigInteger b, BigInteger fieldPower) {
        if (fieldPower.compareTo(toBigInteger(3)) == -1) {
            throw new RuntimeException(ErrorMessages.FIELD_POWER_EXCEPTION_MSG);
        }
        if (a.compareTo(fieldPower) == 1 || b.compareTo(fieldPower) == 1) {
            throw new RuntimeException(ErrorMessages.getCoefficientsExceptionMsg(fieldPower));
        }
        if (hasNoRepeatedRoots(a, b, fieldPower)) {
            List<Point> points = getEllipticCurvePoints(a, b, fieldPower);
            return new EllipticCurve(a, b, fieldPower, points);
        } else {
            throw new RuntimeException(ErrorMessages.COEFFICIENTS_CONDITION_EXCEPTION_MSG);
        }
    }

    public BigInteger getOrder() {
        BigInteger order = BigInteger.ZERO;
        if (fieldPower.compareTo(toBigInteger(230)) == -1) {
            return getSimpleOrder();
        }
        BigInteger nonResidue;
        try {
            nonResidue = getNonResidue(fieldPower);
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessages.NON_RESIDUE_NOT_FOUND_EXCEPTION_MSG);
        }
        BigInteger stepPower = toBigInteger(getStepPower(fieldPower.intValue()));
        BigInteger c = nonResidue
                .modPow(BigInteger.TWO, fieldPower)
                .multiply(a)
                .mod(fieldPower);
        BigInteger d = nonResidue
                .modPow(toBigInteger(3), fieldPower)
                .multiply(a)
                .mod(fieldPower);
        BigInteger legendre = toBigInteger(0);
        while (legendre.equals(toBigInteger(0))) {
            BigInteger xCoord = toBigInteger(Randomizer.getNumber(fieldPower.intValue()));
            legendre = getLegendreSymbol(xCoord, fieldPower);
            if (!legendre.equals(toBigInteger(0))) {
                if (legendre.equals(toBigInteger(-1))) {
                    this.a = c;
                    this.b = d;
                    xCoord = xCoord.multiply(nonResidue).mod(fieldPower);
                }
                Point p = getPointByXcoord(xCoord);
                List<BigInteger> shanksRes = shanks(p);
                if (shanksRes.size() == 1) {
                    BigInteger shanksOnlyVal = shanksRes.get(0);
                    BigInteger babyStepInd = getIndex(
                            shanksOnlyVal,
                            getBabyStepSet(getStepPower(fieldPower.intValue()), p)
                    );
                    BigInteger giantStepInd = getIndex(
                            shanksOnlyVal,
                            getGiantStepSet(getStepPower(fieldPower.intValue()), p)
                    );
                    BigInteger t = babyStepInd
                            .add(giantStepInd.multiply(stepPower).mod(fieldPower))
                            .mod(fieldPower);
                    Point nullPoint = p.mul(fieldPower.intValue() + 1 + t.intValue());
                    if (!nullPoint.equals(p.getNull())) {
                        t = babyStepInd
                                .add(giantStepInd.multiply(stepPower).mod(fieldPower).negate())
                                .mod(fieldPower);
                    }
                    t.multiply(legendre).mod(fieldPower)
                            .add(fieldPower).mod(fieldPower)
                            .add(BigInteger.ONE).mod(fieldPower);
                    order = t;
                }
            }
        }
        return order;
    }

    public Point getRandomPoint() {
        return this.points.get(Randomizer.getNumber(this.points.size()));
    }

    public List<Point> getPointsList() {
        return this.points;
    }

    private List<BigInteger> shanks(Point p) {
        int stepPower = getStepPower(fieldPower.intValue());
        List<BigInteger> babySteps = getBabyStepSet(stepPower, p);
        List<BigInteger> giantSteps = getGiantStepSet(stepPower, p);
        return getIntersection(babySteps, giantSteps);
    }

    private Point getPointByXcoord(BigInteger xCoord) {
        for (Point p : points) {
            if (p.getX().equals(xCoord)) {
                return p;
            }
        }
        throw new RuntimeException(ErrorMessages.getPointNotFoundExceptionMsg(xCoord));
    }

    /**
     * substitute all possible X values to the quantity X^3 + AX + B
     * and see if the quantity turns to a value that is a square of a possible Y value
     * if so add a Point with X and Y coords to points array
     * @param a constant A
     * @param b constant B
     * @param fieldPower finite field power
     * @return points - array of all points included into the elliptic curve
     */
    private static List<Point> getEllipticCurvePoints(BigInteger a, BigInteger b, BigInteger fieldPower) {
        List<Point> points = new ArrayList<>();
        initPoints(a);
        for (
                BigInteger possibleX = BigInteger.ZERO;
                possibleX.compareTo(fieldPower) == -1;
                possibleX.add(BigInteger.ONE)
        ) {
            BigInteger quantityVal = possibleX
                    .modPow(toBigInteger(3), fieldPower)
                    .add(a.multiply(possibleX).mod(fieldPower))
                    .add(b).mod(fieldPower);
            for (
                    BigInteger possibleY = BigInteger.ZERO;
                    possibleY.compareTo(fieldPower) == -1;
                    possibleY.add(BigInteger.ONE)
            ) {
                if (possibleY.modPow(BigInteger.TWO, fieldPower).equals(quantityVal)) {
                    points.add(new Point(possibleX, possibleY, fieldPower));
                }
            }
        }
        return points;
    }

    private static void initPoints(BigInteger a) {
        Point.initCoefficientA(a);
    }

    /**
     * if elliptic curve E has ec.encryption.constants A and B that satisfy the condition
     * 4 * A^3 + 27 * B^2 != 0
     * then the cubic polynomial X^3 + AX + B has no repeated roots
     * @param a constant A
     * @param b constant B
     * @return true if the cubic polynomial has no repeated roots
     */
    private static boolean hasNoRepeatedRoots(BigInteger a, BigInteger b, BigInteger fieldPower) {
        BigInteger res = toBigInteger(4)
                .multiply(a.modPow(toBigInteger(3), fieldPower))
                .mod(fieldPower)
                .add(toBigInteger(27).multiply(b.modPow(toBigInteger(2), fieldPower)))
                .mod(fieldPower);
        return !res.equals(BigInteger.ZERO);
    }

    private List<BigInteger> getXcoords() {
        List<BigInteger> xCoords = new ArrayList<>();
        for (Point point : points) {
            xCoords.add(point.getX());
        }
        return xCoords;
    }

    private BigInteger getSimpleOrder() {
        BigInteger res;
        List<BigInteger> xCoords = getXcoords();
        BigInteger legendreSum = toBigInteger(0);
        for (BigInteger xCoord : xCoords) {
            BigInteger numerator = xCoord
                    .modPow(toBigInteger(3), fieldPower)
                    .add(xCoord.multiply(a).mod(fieldPower))
                    .add(b)
                    .mod(fieldPower);
            legendreSum = legendreSum.add(getLegendreSymbol(numerator, fieldPower));
        }
        res = legendreSum.add(fieldPower).mod(fieldPower).add(toBigInteger(1)).mod(fieldPower);
        return res;
    }
}
