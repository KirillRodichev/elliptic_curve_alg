package ec.encryption;

import ec.encryption.model.EllipticCurve;
import ec.encryption.model.Point;

import java.math.BigInteger;
import java.util.Arrays;

import static ec.encryption.utils.ConvertHelper.*;
import static ec.encryption.utils.TernaryHelper.getTernaryExpansion;
import static ec.encryption.utils.TernaryHelper.pushNull;

public class Main {

    public static void main(String[] args) {
        /*for (int i = 0; i < 100; i++) {
            System.out.println(getValue(i, 4, 10));
        }*/
        //EllipticCurve ellipticCurve = new EllipticCurve();

        /*System.out.println(getValue(5, -1, 13));
        System.out.println(getValue1(4, 4, 13));*/

        /*System.out.println(BigInteger.valueOf(5).multiply(BigInteger.valueOf(20)).mod(BigInteger.valueOf(13)));
        System.out.println(BigInteger.valueOf(5).multiply(BigInteger.valueOf(20).mod(BigInteger.valueOf(13))));

        System.out.println(
                String
                .format("%32s", Integer.toBinaryString(123123))
                .replaceAll(" ", "0")
        );*/

        /*String s = intToBinStr(228);
        String []s1 = s.split("");
        System.out.println(s);
        for (int i = 0; i < s.length(); i++) {
            System.out.println(Integer.parseInt(s1[i]));
        }*/

        /*System.out.println(intToBinStr(4));
        System.out.println("0" + intToBinStr(4));*/

        /*System.out.println(Arrays.toString(strToIntegersArray(intToBinStr(57188))));
        System.out.println(Arrays.toString(strToIntegersArray(pushNull(intToBinStr(57188)))));
        System.out.println(Arrays.toString(strToIntegersArray(pushNull(reverseString(intToBinStr(57188))))));
        System.out.println(Arrays.toString(getTernaryExpansion(strToIntegersArray(pushNull(reverseString(intToBinStr(57188)))))));*/

        //System.out.println(toBigInteger(0).mod(toBigInteger(10)));

        Point.initCoefficientA(toBigInteger(3));
        Point p = new Point(toBigInteger(12), toBigInteger(11), toBigInteger(13));
        System.out.println(p.getX() + ", " +  p.getY());
        System.out.println(toBigInteger(0).mod(toBigInteger(13)));
    }

    public static BigInteger getValue(int a, int b, int n) {
        return BigInteger.valueOf(a).modPow(BigInteger.valueOf(b), BigInteger.valueOf(n));
    }

    public static BigInteger getValue1(int a, int b, int n) {
        return BigInteger.valueOf(a).multiply(BigInteger.valueOf(b)).mod(BigInteger.valueOf(n));
    }
}
