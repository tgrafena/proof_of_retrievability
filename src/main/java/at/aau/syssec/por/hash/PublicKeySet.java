package at.aau.syssec.por.hash;

import at.aau.syssec.por.Util;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * PublicKeySet
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class PublicKeySet implements Serializable {

    private static final long serialVersionUID = -8142647151801595679L;
    private final BigInteger p;
    private final BigInteger q;
    private final BigInteger g;
    private final BigInteger pk;

    public PublicKeySet(BigInteger p, BigInteger q, BigInteger g, BigInteger pk) {
        this.p = p;
        this.q = q;
        this.g = g;
        this.pk = pk;
    }

    public PublicKeySet(PrivateKeySet keys) {
        this.p = keys.getP();
        this.q = keys.getQ();
        this.g = keys.getG();
        this.pk = keys.getPublicKey();
    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getQ() {
        return q;
    }

    public BigInteger getG() {
        return g;
    }

    public BigInteger getPublicKey() {
        return pk;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PublicKeySet [p: " + Util.shorten(p) + ", ");
        sb.append("q: " + Util.shorten(q) + ", ");
        sb.append("g: " + Util.shorten(g) + ", ");
        sb.append("pk: " + Util.shorten(pk) + "]");
        return sb.toString();
    }
}
