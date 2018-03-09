package at.aau.syssec.por.verifier;

import at.aau.syssec.por.Block;
import at.aau.syssec.por.Challenge;
import at.aau.syssec.por.Config;
import at.aau.syssec.por.Util;
import at.aau.syssec.por.hash.PrivateKeySet;
import at.aau.syssec.por.log.Debug;
import at.aau.syssec.por.merkle.AuthPath;

import java.io.Serializable;
import java.util.*;

/**
 * Contains all challenges for a file.
 *
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class Challenges implements Serializable, Iterable<VerifierChallenge>,
        Iterator<VerifierChallenge>, Comparable<Challenges> {

    /**
     * serialization ID
     */
    private static final long serialVersionUID = 2737856064834934719L;

    /**
     * map from block-position on list of challenges for this data-block
     */
    private Map<Integer, List<VerifierChallenge>> challenges;

    /**
     * map from block-position on list of pseudo-challenges for this block
     */
    private Map<Integer, List<Challenge>> pseudoChallenges;

    /**
     * list iterator
     */
    private Iterator<List<VerifierChallenge>> listIter;

    /**
     * element iterator
     */
    private Iterator<VerifierChallenge> elemIter;

    public Challenges() {
        challenges = new TreeMap<Integer, List<VerifierChallenge>>();
        pseudoChallenges = new TreeMap<Integer, List<Challenge>>();
    }

    /**
     * Adds a challenge for a certain block-position
     *
     * @param pos
     * @param challenge
     */
    public void addChallenge(int pos, VerifierChallenge challenge) {
        List<VerifierChallenge> list = challenges.get(pos);

        if (list == null) {
            list = new LinkedList<VerifierChallenge>();
            challenges.put(pos, list);
        }

        list.add(challenge);
    }

    /**
     * Adds a pseudo-challenge for a certain block-position
     *
     * @param pos
     * @param challenge
     */
    public void addPseudoChallenge(int pos, Challenge challenge) {
        List<Challenge> list = pseudoChallenges.get(pos);

        if (list == null) {
            list = new LinkedList<Challenge>();
            pseudoChallenges.put(pos, list);
        }

        list.add(challenge);
    }

    /**
     * Returns a challenge for a certain block-position. If no challenge is
     * available for this position, a pseudo-challenge is returned.
     *
     * @param pos - block-position
     * @return Challenge
     */
    public Challenge getChallenge(int pos) {
        List<VerifierChallenge> list = challenges.get(pos);

        if (list != null && list.size() > 0) {
            for (VerifierChallenge challenge : list) {
                if (!challenge.isSent()) {
                    challenge.send();
                    return new Challenge(pos, challenge.getChallenge());
                }
            }
        }

        byte[] challenge = Util.generateChallenge(Config
                .getIntValue(Config.SEC_PARAM));
        Challenge pseudoChallenge = new Challenge(pos, challenge);
        addPseudoChallenge(pos, pseudoChallenge);
        return pseudoChallenge;
    }

    /**
     * Compares a given hash-value to the expected one.
     *
     * @param pos  - position of the challenge
     * @param hash - value to be compared
     * @return true if the given value matches with the expected one
     */
    public boolean verifyChallenge(int pos, byte[] challenge, byte[] hash) {
        List<VerifierChallenge> list = challenges.get(pos);
        List<Challenge> pseudoList = pseudoChallenges.get(pos);

        // comparing with real challenges:
        if (list != null) {
            for (Iterator<VerifierChallenge> iter = list.iterator(); iter
                    .hasNext(); ) {
                VerifierChallenge vc = iter.next();
                if (Arrays.equals(challenge, vc.getChallenge())) {
                    Debug.info("real(" + pos + "): expected: ", vc.getHash(),
                            " actual: ", hash);
                }
                if (Arrays.equals(challenge, vc.getChallenge())
                        && Arrays.equals(hash, vc.getHash()) && vc.isSent()) {
                    iter.remove();

                    return true;
                }
             }
        }

        // comparing with pseudo-challenges:
        if (pseudoList != null) {
            for (Iterator<Challenge> iter = pseudoList.iterator(); iter
                    .hasNext(); ) {
                Challenge c = iter.next();
                if (Arrays.equals(challenge, c.getChallenge())) {
                    iter.remove();
                    Debug.info("pseudo(" + pos + "): expected: none actual: ",
                            hash);
                    return true;
                }
            }
        }

        // else the challenge was in none of the lists:
        return false;
    }

    /**
     * Remove all challenges for this position
     * @param pos block position
     * @return amount of deleted challenges
     */
    public int removeChallenges(int pos) {
        if(challenges.containsKey(pos)) {
            return challenges.remove(pos).size();
        }

        return 0;
    }

    public VerifierChallenge createChallenge(int pos, byte[] data, AuthPath path, Block block, PrivateKeySet keys) {
        byte[] rand = Util.generateChallenge(Config.getIntValue(Config.SEC_PARAM));
        byte[] hash = path.calcRootHash(data, rand, block.getRho(), block.getDelta(), keys);

        VerifierChallenge vc = new VerifierChallenge(pos, rand, hash);
        addChallenge(pos, vc);

        Debug.info(vc);

        return vc;
    }

    /**
     * Returns the amount of challenges available.
     *
     * @return int
     */
    public int getSize() {
        int size = 0;
        for (Integer pos : challenges.keySet()) {
            List<VerifierChallenge> list = challenges.get(pos);
            if (list != null)
                size += list.size();
        }
        return size;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (VerifierChallenge vc : this) {
            sb.append(vc);
            sb.append(", ");
        }
        sb.append("\n##########\n");
        for (Integer key : pseudoChallenges.keySet()) {
            for (Challenge c : pseudoChallenges.get(key)) {
                sb.append(c);
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean hasNext() {
        if (elemIter.hasNext()) {
            return true;
        } else {
            while (listIter.hasNext() && !elemIter.hasNext())
                elemIter = listIter.next().iterator();
            return elemIter.hasNext();
        }
    }

    @Override
    public VerifierChallenge next() {
        if (hasNext())
            return elemIter.next();
        else
            return null;
    }

    @Override
    public void remove() {
        elemIter.remove();
    }

    @Override
    public Iterator<VerifierChallenge> iterator() {
        listIter = challenges.values().iterator();
        elemIter = listIter.next().iterator();
        return this;
    }

    @Override
    public int compareTo(Challenges that) {
        int res = this.getSize() - that.getSize();

        for (VerifierChallenge vc : this) {
            boolean found = false;
            for (VerifierChallenge tc : that.challenges.get(vc.getPos())) {
                found |= vc.compareTo(tc) == 0;
            }
            if (!found)
                res += vc.getPos();
        }

        return res;
    }

}
