package at.aau.syssec.por;

import at.aau.syssec.por.hash.ChameleonHash;
import at.aau.syssec.por.hash.PublicKeySet;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * A block contains a part of a file.
 *
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class Block implements Serializable {

    /**
     * serialization ID
     */
    private static final long serialVersionUID = -2013356824737475546L;

    /**
     * hash - holds the hashed data of the block
     */
    private byte[] hash;

    /**
     * rho - part of the hash-function
     */
    private BigInteger rho;

    /**
     * delta - part of the hash-function
     */
    private BigInteger delta;

    public Block(byte[] data, BigInteger rho, BigInteger delta, PublicKeySet keys) {
        this.rho = rho;
        this.delta = delta;
        this.hash = updateHash(data, keys);
    }

    public byte[] getHash() {
        return hash;
    }

    public String getHex() {
        return Util.bytesToHex(hash);
    }

    public BigInteger getRho() {
        return rho;
    }

    public BigInteger getDelta() {
        return delta;
    }

    public byte[] updateHash(byte[] data, PublicKeySet keys) {
        this.hash = ChameleonHash.hash(data, rho, delta, keys);
        return this.hash;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Block[hash: " + Util.shorten(hash));
        builder.append(", rho: " + Util.shorten(rho));
        builder.append(", delta: " + Util.shorten(delta) + "]");
        return builder.toString();
    }
}
