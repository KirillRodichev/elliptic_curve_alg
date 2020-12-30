package ec.encryption;

import java.util.Arrays;

import static ec.encryption.utils.ConvertHelper.*;
import static ec.encryption.utils.TernaryHelper.getTernaryExpansion;
import static ec.encryption.utils.TernaryHelper.pushNull;

public class Main {

    public static void main(String[] args) {

        String sBinMultiplier = pushNull(reverseString(intToBinStr(3959)));
        int[] iBinMultiplier = getTernaryExpansion(strToIntegersArray(sBinMultiplier));
        System.out.println(sBinMultiplier);
        System.out.println(Arrays.toString(iBinMultiplier));
    }
}
