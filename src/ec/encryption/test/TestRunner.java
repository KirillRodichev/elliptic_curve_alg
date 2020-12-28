package ec.encryption.test;

import static ec.encryption.utils.ConvertHelper.toBigInteger;

public class TestRunner {
    public static void main(String[] args) {
        PointTester pointTesterAddition = new PointTester(toBigInteger(3));
        PointTester pointTesterMultiplication = new PointTester(toBigInteger(14));

        try {
            pointTesterAddition.testAddition();
            pointTesterAddition.testGetNegative();

            pointTesterMultiplication.testMulByPointDoubling();
            pointTesterMultiplication.testTernaryMulByPointDoubling();
            pointTesterMultiplication.testPointGeneration();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Test completed successfully");
    }
}
