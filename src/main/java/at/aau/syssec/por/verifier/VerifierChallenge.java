package at.aau.syssec.por.verifier;

import at.aau.syssec.por.Util;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Contains all information of a challenge.
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class VerifierChallenge implements Serializable,
        Comparable<VerifierChallenge> {

	/**
	 * serialization ID
	 */
    private static final long serialVersionUID = 2737856064235934719L;
    
    /**
     * position of the corresponding block
     */
    private int pos;
    
    /**
     * challenge
     */
    private byte[] challenge;
    
    /**
     * expected response
     */
    private byte[] hash;
    
    /**
     * proof if challenge actually has been sent
     */
    private boolean sent = false;

    public VerifierChallenge(int pos, byte[] challenge, byte[] hash) {
        this.pos = pos;
        this.challenge = challenge;
        this.hash = hash;
    }

    public int getPos() {
        return pos;
    }

    public byte[] getChallenge() {
        return this.challenge;
    }

    public byte[] getHash() {
        return this.hash;
    }

    public void send() {
        sent = true;
    }

    public boolean isSent() {
        return sent;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VerifierChallenge[pos: " + pos);
        sb.append(", challenge: " + Util.shorten(challenge));
        sb.append(", hash: " + Util.shorten(hash));
        sb.append(", isSent: " + sent);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int compareTo(VerifierChallenge that) {
        return this.pos - that.pos +
                this.hash.length - that.hash.length +
                this.challenge.length - that.challenge.length +
                (Arrays.equals(this.hash, that.hash) ? 0 : this.hash.length) +
                (Arrays.equals(this.challenge, that.challenge) ? 0 : this.challenge.length);
    }
}
