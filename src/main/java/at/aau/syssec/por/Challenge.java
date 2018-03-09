package at.aau.syssec.por;

import java.io.Serializable;

/**
 * contains a challenge for a certain position (block)
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class Challenge implements Serializable {

    private static final long serialVersionUID = 2737856064834934719L;
    private int pos;
    private byte[] challenge;

    public Challenge(int pos, byte[] challenge) {
        this.pos = pos;
        this.challenge = challenge;
    }

    public int getPos() {
        return pos;
    }

    public byte[] getChallenge() {
        return this.challenge;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Challenge pos: " + pos);
        sb.append(", challenge: " + Util.shorten(challenge));
        sb.append("]");
        return sb.toString();
    }
}
