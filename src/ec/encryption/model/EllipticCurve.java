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
        a = a.mod(fieldPower);
        b = b.mod(fieldPower);
        if (hasNoRepeatedRoots(a, b, fieldPower)) {
            List<Point> points = getEllipticCurvePoints(a, b, fieldPower);
            return new EllipticCurve(a, b, fieldPower, points);
        } else {
            throw new RuntimeException(ErrorMessages.COEFFICIENTS_CONDITION_EXCEPTION_MSG);
        }
    }

    public BigInteger getOrder() {
        if (fieldPower.compareTo(toBigInteger(230)) == -1) {
            return getSimpleOrder();
        }
        BigInteger nonResidue = getNonResidue(fieldPower);
        int stepPower = getStepPower(fieldPower.intValue());
        BigInteger distortionParamC = nonResidue
                .modPow(BigInteger.TWO, fieldPower)
                .multiply(a)
                .mod(fieldPower);
        BigInteger distortionParamD = nonResidue
                .modPow(toBigInteger(3), fieldPower)
                .multiply(b)
                .mod(fieldPower);
        return getMestreOrder(distortionParamC, distortionParamD, nonResidue, stepPower);
    }

    /**
     *
     * @param distortionParamC Mestre distortion param
     * @param distortionParamD Mestre distortion param
     * @param nonResidue non residue modulo finite field power
     * @param stepPower power for giant and baby steps
     * @return point order
     */
    private BigInteger getMestreOrder(
            BigInteger distortionParamC,
            BigInteger distortionParamD,
            BigInteger nonResidue,
            int stepPower
    ) {
        int legendre = 0;
        BigInteger order = BigInteger.ZERO;
        while (order.equals(BigInteger.ZERO)) {
            BigInteger xCoord = toBigInteger(Randomizer.getNumber(fieldPower.intValue()));
            BigInteger numerator = getLegendreNumerator(xCoord, fieldPower, a, b);
            legendre = getLegendreSymbol(numerator, fieldPower);
            if (legendre != 0) {
                if (legendre == -1) {
                    this.a = distortionParamC;
                    this.b = distortionParamD;
                    xCoord = xCoord.multiply(nonResidue).mod(fieldPower);
                }
                Point p = getPointByXcoord(xCoord);
                List<BigInteger> babyStepSet = getBabyStepSet(stepPower, p);
                List<BigInteger> giantStepSet = getGiantStepSet(stepPower, p);
                List<BigInteger> shanksRes = getShanksIntersection(babyStepSet, giantStepSet);
                if (shanksRes.size() == 1) {
                    BigInteger shanksOnlyVal = shanksRes.get(0);
                    BigInteger babyStepInd = getIndex(shanksOnlyVal, babyStepSet);
                    BigInteger giantStepInd = getIndex(shanksOnlyVal, giantStepSet);
                    BigInteger t = babyStepInd
                            .add(giantStepInd.multiply(toBigInteger(stepPower)).mod(fieldPower))
                            .mod(fieldPower);
                    Point expectedNullPoint = getExpectedNullPoint(p, fieldPower, t);
                    if (!expectedNullPoint.equals(p.getNull())) {
                        t = babyStepInd
                                .subtract(giantStepInd.multiply(toBigInteger(stepPower)).mod(fieldPower))
                                .mod(fieldPower);
                        expectedNullPoint = getExpectedNullPoint(p, fieldPower, t);
                        if (expectedNullPoint.equals(p.getNull())) {
                            order = t
                                    .multiply(toBigInteger(legendre)).mod(fieldPower)
                                    .add(fieldPower).mod(fieldPower)
                                    .add(BigInteger.ONE).mod(fieldPower);
                        }
                    }
                }
            }
        }
        return order;
    }

    public Point getRandomPoint() {
        return this.points.get(Randomizer.getNumber(this.points.size() - 1));
    }

    public List<Point> getPointsList() {
        return this.points;
    }

    private List<BigInteger> getShanksIntersection(List<BigInteger> babySteps, List<BigInteger> giantSteps) {
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
                possibleX = possibleX.add(BigInteger.ONE)
        ) {
            BigInteger quantityVal = possibleX
                    .modPow(toBigInteger(3), fieldPower)
                    .add(a.multiply(possibleX).mod(fieldPower))
                    .add(b).mod(fieldPower);
            for (
                    BigInteger possibleY = BigInteger.ZERO;
                    possibleY.compareTo(fieldPower) == -1;
                    possibleY = possibleY.add(BigInteger.ONE)
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
        int legendreSum = 0;
        for (BigInteger xCoord : xCoords) {
            BigInteger numerator = getLegendreNumerator(xCoord, fieldPower, a, b);
            legendreSum += getLegendreSymbol(numerator, fieldPower);
        }
        res = toBigInteger(legendreSum)
                .add(fieldPower).mod(fieldPower)
                .add(toBigInteger(1)).mod(fieldPower);
        return res;
    }
}
