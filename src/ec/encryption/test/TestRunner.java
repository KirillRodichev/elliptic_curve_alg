package ec.encryption.test;

import ec.encryption.constants.TestSuccessMessages;

import java.util.Arrays;

public class TestRunner {
    public static void main(String[] args) {
        PointTester pointTester = new PointTester();

        try {
            pointTester.testAddition();
            System.out.println(TestSuccessMessages.ADDITION_TEST_SUCCESS);
            pointTester.testGetNegative();
            System.out.println(TestSuccessMessages.GET_NEG_TEST_SUCCESS);

            pointTester.testMulByPointDoubling();
            System.out.println(TestSuccessMessages.MULTIPLICATION_TEST_SUCCESS);
            pointTester.testTernaryMulByPointDoubling();
            System.out.println(TestSuccessMessages.TERNARY_MUL_TEST_SUCCESS);
            pointTester.testPointGeneration();
            System.out.println(TestSuccessMessages.POINT_GENERATION_TEST_SUCCESS);

            ShanksTester.testShanksGetOrder();
            System.out.println(TestSuccessMessages.SHANKS_TEST_SUCCESS);

            System.out.println(TestSuccessMessages.WHOLE_TEST_SUCCESS);
        } catch (RuntimeException e) {
            System.out.println("Error msg: " + e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
