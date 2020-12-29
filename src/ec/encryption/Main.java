package ec.encryption;

import static ec.encryption.utils.ConvertHelper.toBigInteger;
import static ec.encryption.utils.ShanksHelper.getLegendreSymbol;

public class Main {

    public static void main(String[] args) {

        for (int i = 1; i < 31; i++) {
            System.out.print(getLegendreSymbol(toBigInteger(i), toBigInteger(3)) + ", ");
        }
    }
}
