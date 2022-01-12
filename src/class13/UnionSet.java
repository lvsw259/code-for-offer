package class13;

import java.math.BigDecimal;

public class UnionSet {


    public static void main(String[] args) {
        BigDecimal qty = new BigDecimal("0.000110001");
        String a = "select da >= " + qty.toPlainString();
        System.out.println(a);
    }
}
