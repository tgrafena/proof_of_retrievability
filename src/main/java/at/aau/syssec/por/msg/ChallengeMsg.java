package at.aau.syssec.por.msg;

import at.aau.syssec.por.Challenge;
import at.aau.syssec.por.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Challengemessages are used to send challenges to the prover.
 * They are used for verifications, downloads and updates.
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class ChallengeMsg extends Message {

    private static final long serialVersionUID = 1797856064833434719L;
    private String hash;
    private List<Challenge> challenges;
    private List<Integer> keys;

    public ChallengeMsg(String hash) {
        this.hash = hash;
        challenges = new ArrayList<Challenge>();
        keys = new ArrayList<Integer>();
    }

    public String getHash() {
        return hash;
    }

    public void addChallenge(int pos, byte[] challenge) {
        keys.add(pos);
        challenges.add(new Challenge(pos, challenge));
    }

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public List<Integer> getKeys() {
        return keys;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ChallangeMsg[hash: " + Util.shorten(hash) + ", ");
        builder.append("challenges: [");

        for (Challenge c : challenges) {
            builder.append(c.getPos() + ":" + Util.shorten(c.getChallenge()) + ", ");
        }

        builder.append("], keys: " + keys + "]");

        return builder.toString();
    }
}
