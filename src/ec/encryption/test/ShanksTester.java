package ec.encryption.test;

import ec.encryption.constants.ErrorMessages;
import ec.encryption.model.EllipticCurve;

import java.util.ArrayList;
import java.util.List;

import static ec.encryption.utils.ConvertHelper.toBigInteger;

public class ShanksTester {

    public static void testShanksGetOrder() {
        EllipticCurve e1;
        EllipticCurve e2;
        EllipticCurve e3;
        try {
            e1 = EllipticCurve.create(toBigInteger(55), toBigInteger(187), toBigInteger(293));
            e2 = EllipticCurve.create(toBigInteger(109), toBigInteger(24), toBigInteger(232));
            e3 = EllipticCurve.create(toBigInteger(85), toBigInteger(93), toBigInteger(251));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        List<EllipticCurve> ecs = new ArrayList<>();
        ecs.add(e1);
        ecs.add(e2);
        ecs.add(e3);

        for (EllipticCurve e : ecs) {
            System.out.println(e.getOrder() + ", " + e.getPointsList().size());
            if (!e.getOrder().equals(toBigInteger(e.getPointsList().size()))) {
                throw new RuntimeException(ErrorMessages.SHANKS_TEST_FAILURE);
            }
        }
    }
}
