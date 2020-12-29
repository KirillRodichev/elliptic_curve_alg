package ec.encryption.test;

import ec.encryption.constants.ErrorMessages;
import ec.encryption.model.EllipticCurve;
import ec.encryption.model.Point;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ec.encryption.utils.ConvertHelper.toBigInteger;

public class PointTester {

    private static Point point_null = new Point(toBigInteger(1), toBigInteger(5), toBigInteger(13)).getNull();
    private static Point point_1_5 = new Point(toBigInteger(1), toBigInteger(5), toBigInteger(13));
    private static Point point_1_8 = new Point(toBigInteger(1), toBigInteger(8), toBigInteger(13));
    private static Point point_2_3 = new Point(toBigInteger(2), toBigInteger(3), toBigInteger(13));
    private static Point point_2_10 = new Point(toBigInteger(2), toBigInteger(10), toBigInteger(13));
    private static Point point_9_6 = new Point(toBigInteger(9), toBigInteger(6), toBigInteger(13));
    private static Point point_9_7 = new Point(toBigInteger(9), toBigInteger(7), toBigInteger(13));
    private static Point point_12_2 = new Point(toBigInteger(12), toBigInteger(2), toBigInteger(13));
    private static Point point_12_11 = new Point(toBigInteger(12), toBigInteger(11), toBigInteger(13));

    /**
     * addition table for finite field 13
     * column and row headers:
     * (0), (1,5), (1,8), (2,3), (2,10), (9,6), (9,7), (12,2), (12,11)
     */
    private static Point[][] additionTable = new Point[][] {
            {point_null, point_1_5, point_1_8, point_2_3, point_2_10, point_9_6, point_9_7, point_12_2, point_12_11},
            {point_1_5, point_2_10, point_null, point_1_8, point_9_7, point_2_3, point_12_2, point_12_11, point_9_6},
            {point_1_8, point_null, point_2_3, point_9_6, point_1_5, point_12_11, point_2_10, point_9_7, point_12_2},
            {point_2_3, point_1_8, point_9_6, point_12_11, point_null, point_12_2, point_1_5, point_2_10, point_9_7},
            {point_2_10, point_9_7, point_1_5, point_null, point_12_2, point_1_8, point_12_11, point_9_6, point_2_3},
            {point_9_6, point_2_3, point_12_11, point_12_2, point_1_8, point_9_7, point_null, point_1_5, point_2_10},
            {point_9_7, point_12_2, point_2_10, point_1_5, point_12_11, point_null, point_9_6, point_2_3, point_1_8},
            {point_12_2, point_12_11, point_9_7, point_2_10, point_9_6, point_1_5, point_2_3, point_1_8, point_null},
            {point_12_11, point_9_6, point_12_2, point_9_7, point_2_3, point_2_10, point_1_8, point_null, point_1_5},
    };

    /**
     * multiplication table for finite field 3623
     * multiplier = 947
     * Q = 2^i * P, i = [0,10]
     * P = (6, 730)
     */
    private static Point[] multiplicationTable = new Point[] {
            new Point(toBigInteger(6), toBigInteger(730), toBigInteger(3623)),
            new Point(toBigInteger(2521), toBigInteger(3601), toBigInteger(3623)),
            new Point(toBigInteger(2277), toBigInteger(502), toBigInteger(3623)),
            new Point(toBigInteger(3375), toBigInteger(535), toBigInteger(3623)),
            new Point(toBigInteger(1610), toBigInteger(1851), toBigInteger(3623)),
            new Point(toBigInteger(1753), toBigInteger(2436), toBigInteger(3623)),
            new Point(toBigInteger(2005), toBigInteger(1764), toBigInteger(3623)),
            new Point(toBigInteger(2425), toBigInteger(1791), toBigInteger(3623)),
            new Point(toBigInteger(3529), toBigInteger(2158), toBigInteger(3623)),
            new Point(toBigInteger(2742), toBigInteger(3254), toBigInteger(3623)),
            new Point(toBigInteger(1814), toBigInteger(3480), toBigInteger(3623)),
    };

    public void testAddition() {
        Point.initCoefficientA(toBigInteger(3));
        Point[] pointSet = additionTable[0];
        for (int iRow = 0; iRow < pointSet.length; iRow++) {
            for (int iCol = 0; iCol < pointSet.length; iCol++) {
                Point res = pointSet[iRow].add(pointSet[iCol]);
                if (!additionTable[iRow][iCol].equals(res)) {
                    throw new RuntimeException(ErrorMessages.ADDITION_TEST_FAILURE);
                }
            }
        }
    }

    public void testGetNegative() {
        Point.initCoefficientA(toBigInteger(3));
        Point[] pointSet = additionTable[0];
        for (Point point : pointSet) {
            Point negative = point.negative();
            if (!point.equals(point.getNull())) {
                if (
                        !negative.getX().equals(point.getX()) ||
                        !negative.getY().negate().mod(point.getModulus()).equals(point.getY()) ||
                        negative.getZ() != point.getZ()
                ) {
                    throw new RuntimeException(ErrorMessages.GET_NEGATIVE_TEST_FAILURE);
                }
            } else {
                if (!negative.equals(negative.getNull())) {
                    throw new RuntimeException(ErrorMessages.GET_NEGATIVE_TEST_FAILURE);
                }
            }
        }
    }

    /**
     * multiplier = 947 = 2^0 + 2^1 + 2^4 + 2^5 + 2^7 + 2^8 + 2^9
     * multiplier = 947 = 1 + 2 + 16 + 32 + 128 + 256 + 512
     */
    public void testMulByPointDoubling() {
        Point.initCoefficientA(toBigInteger(14));
        Point p = new Point(toBigInteger(6), toBigInteger(730), toBigInteger(3623));
        for (int power = 0; power < multiplicationTable.length; power++) {
            int iPower = toBigInteger(2).pow(power).intValue();
            Point mulRes = p.mul(iPower);
            if (!mulRes.equals(multiplicationTable[power])) {
                throw new RuntimeException(ErrorMessages.MULTIPLICATION_TEST_FAILURE);
            }
        }
    }

    /**
     * multiplier = 947 = 2^0 + 2^1 + 2^4 + 2^5 + 2^7 + 2^8 + 2^9
     * multiplier = 947 = 1 + 2 + 16 + 32 + 128 + 256 + 512
     */
    public void testTernaryMulByPointDoubling() {
        Point.initCoefficientA(toBigInteger(14));
        Point p = new Point(toBigInteger(6), toBigInteger(730), toBigInteger(3623));
        for (int power = 0; power < multiplicationTable.length; power++) {
            int iPower = toBigInteger(2).pow(power).intValue();
            Point mulRes = p.ternaryMul(iPower);
            if (!mulRes.equals(multiplicationTable[power])) {
                throw new RuntimeException(ErrorMessages.TERNARY_MULTIPLICATION_TEST_FAILURE);
            }
        }
    }

    public void testPointGeneration() {
        EllipticCurve e = EllipticCurve.create(toBigInteger(3), toBigInteger(8), toBigInteger(13));
        List<Point> points = new ArrayList<>(Arrays.asList(additionTable[0]));
        boolean passed;
        for (int i = 0; i < 10; i++) {
            Point randomPoint = e.getRandomPoint();
            passed = false;
            for (Point p : points) {
                if (p.equals(randomPoint)) {
                    passed = true;
                }
            }
            if (!passed) {
                throw new RuntimeException(ErrorMessages.POINT_GENERATION_TEST_FAILURE);
            }
        }
    }
}
