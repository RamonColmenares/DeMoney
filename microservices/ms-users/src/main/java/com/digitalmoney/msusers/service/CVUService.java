package com.digitalmoney.msusers.service;
import java.util.Random;

public class CVUService {
        private static final int COD_BCRA = 0;
        private static final int NRO_SUCURSAL = 1;
        private final String nroCuenta;
        private final Random random;

        public CVUService(long seed) {
            this.random = new Random(seed);
            this.nroCuenta = generateNroCuenta();
        }

        private String generateNroCuenta() {
            return "111" + String.format("%010d", random.nextInt(9999999));
        }

        private int calculateVerificador1() {
            String B = String.format("%03d", COD_BCRA);
            String S = String.format("%04d", NRO_SUCURSAL);

            return (10 - (B.charAt(0) * 7 +
                    B.charAt(1) +
                    B.charAt(2) * 3 +
                    S.charAt(0) * 9 +
                    S.charAt(1) * 7 +
                    S.charAt(2) +
                    S.charAt(3) * 3) % 10) % 10;
        }

        private int calculateVerificador2() {
            String C = String.format("%013d", Long.parseLong(nroCuenta));

            int sum = 0;
            for (int i = 0; i < C.length(); i++) {
                int digit = Integer.parseInt(String.valueOf(C.charAt(i)));
                sum += digit * ((i % 2 == 0) ? 3 : 7);
            }

            return (10 - sum % 10) % 10;
        }

        public String generateCVU() {
            String B = String.format("%03d", COD_BCRA);
            String S = String.format("%04d", NRO_SUCURSAL);
            int verificador1 = calculateVerificador1();
            int verificador2 = calculateVerificador2();

            return B + S + verificador1 + nroCuenta + verificador2;
        }
    }

