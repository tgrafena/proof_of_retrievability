package at.aau.syssec.por.hash;

import at.aau.syssec.por.Util;

import java.math.BigInteger;

/**
 * PrivateKeySet
 * information for the verifier only
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class PrivateKeySet extends PublicKeySet {

    private static final long serialVersionUID = 2860418678600021458L;
    private final BigInteger sk;

    public PrivateKeySet(BigInteger p, BigInteger q, BigInteger g, BigInteger sk, BigInteger pk) {
        super(p, q, g, pk);
        this.sk = sk;
    }

    public BigInteger getSecretKey() {
        return sk;
    }

    public String toString() {
        String sup = super.toString();
        return "PrivateKeySet [" + sup.substring(14, sup.length() - 1) + ", sk: " + Util.shorten(sk) + "]";
    }
}