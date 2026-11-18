package network;

import at.favre.lib.crypto.bcrypt.BCrypt;

public final class Passwords {
    private static final int COST = 10;

    private Passwords() {
    }

    public static String hash(String password) {
        return BCrypt.withDefaults().hashToString(COST, password.toCharArray());
    }

    public static boolean matches(String password, String hash) {
        return hash != null && !hash.isEmpty()
                && BCrypt.verifyer().verify(password.toCharArray(), hash).verified;
    }
}
