package ec.encryption.constants;

import java.math.BigInteger;

public class ErrorMessages {
    public static final String FIELD_POWER_EXCEPTION_MSG = "The field power must be greater than 3";
    public static final String COEFFICIENTS_CONDITION_EXCEPTION_MSG =
            "The coefficients A and B doesn't satisfy the condition 4 * A^3 + 27 * B^2 != 0";
    public static final String NON_RESIDUE_NOT_FOUND_EXCEPTION_MSG = "Non Residue wasn't found";
    public static final String INDEX_NOT_FOUND_EXCEPTION_MSG = "Index not found";
    public static String getPointNotFoundExceptionMsg(BigInteger xCoord) {
        return "Point with 'x' = " + xCoord + " not found";
    }

    public static final String ADDITION_TEST_FAILURE = "Addition test not passed";
    public static final String GET_NEGATIVE_TEST_FAILURE = "Get negative point test not passed";
    public static final String MULTIPLICATION_TEST_FAILURE = "Multiplication test not passed";
    public static final String TERNARY_MULTIPLICATION_TEST_FAILURE = "Ternary multiplication test not passed";
    public static final String SHANKS_TEST_FAILURE = "Shanks was not able to pass the test";
    public static final String POINT_GENERATION_TEST_FAILURE = "Point generation test not passed";
}
