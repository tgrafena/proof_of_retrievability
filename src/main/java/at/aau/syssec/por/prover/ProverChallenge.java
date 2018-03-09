package at.aau.syssec.por.prover;

import at.aau.syssec.por.Util;

import java.io.Serializable;

/**
 * Meant for Prover to send challenges to the verifier
 * (not used yet)
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class ProverChallenge implements Serializable {

    private static final long serialVersionUID = 1797856064834934719L;
    private int pos;
    private byte[] hash;

    public ProverChallenge(int pos, byte[] hash) {
        this.pos = pos;
        this.hash = hash;
    }

    public int getPos() {
        return pos;
    }

    public byte[] getHash() {
        return this.hash;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[pos: " + pos);
        sb.append(", hash: " + Util.shorten(hash));
        sb.append("]");
        return sb.toString();
    }
}
