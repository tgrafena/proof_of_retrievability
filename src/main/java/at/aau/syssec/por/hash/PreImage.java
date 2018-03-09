package at.aau.syssec.por.hash;

import at.aau.syssec.por.Block;
import at.aau.syssec.por.Util;

import java.math.BigInteger;

/**
 * PreImage
 *
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class PreImage {
    private final byte[] msg;
    private final BigInteger rho;
    private final BigInteger delta;

    public PreImage(byte[] msg, BigInteger rho, BigInteger delta) {
        this.msg = msg;
        this.rho = rho;
        this.delta = delta;
    }

    public byte[] getMsg() {
        return msg;
    }

    public Block getBlock(PublicKeySet keys) {
        return new Block(msg, rho, delta, keys);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PreImage [msg: " + Util.shorten(msg));
        builder.append(", rho: " + Util.shorten(rho));
        builder.append(", delta: " + Util.shorten(delta));
        builder.append("]");
        return builder.toString();
    }
}
