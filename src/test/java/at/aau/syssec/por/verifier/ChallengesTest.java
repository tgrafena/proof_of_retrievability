package at.aau.syssec.por.verifier;

import at.aau.syssec.por.Assert;
import at.aau.syssec.por.Challenge;
import at.aau.syssec.por.TestUtil;
import at.aau.syssec.por.Util;
import at.aau.syssec.por.prover.ProverChallenge;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

/**
 * Created by unki2aut on 15.03.14.
 */
public class ChallengesTest {

    private Challenges challenges;

    @Before
    public void init() {
        challenges = new Challenges();
    }

    @Test
    public void testAddChallenge() {
        challenges.addChallenge(0, new VerifierChallenge(0, Util.generateChallenge(32), Util.generateChallenge(32)));
        challenges.addChallenge(1, new VerifierChallenge(1, Util.generateChallenge(32), Util.generateChallenge(32)));

        Assert.assertEquals("challanges not added", 2, challenges.getSize());
    }

    @Test
    public void testGetChallenge() {
        VerifierChallenge challenge = new VerifierChallenge(0, Util.generateChallenge(32), Util.generateChallenge(32));
        challenges.addChallenge(0, challenge);
        challenges.addChallenge(1, new VerifierChallenge(1, Util.generateChallenge(32), Util.generateChallenge(32)));

        Challenge actual = challenges.getChallenge(challenge.getPos());
        Assert.assertEquals("challanges are not equal", challenge.getPos(), actual.getPos());
        Assert.assertEquals("challanges are not equal", challenge.getChallenge(), actual.getChallenge());
    }

    @Test
    public void testVerifyChallenge() {
        VerifierChallenge challenge = new VerifierChallenge(0, Util.generateChallenge(32), Util.generateChallenge(32));
        challenges.addChallenge(0, challenge);

        Challenge c = challenges.getChallenge(0);
        ProverChallenge actual = new ProverChallenge(c.getPos(), challenge.getHash());

        Assert.assertTrue("couldn't verify challenge", challenges.verifyChallenge(actual.getPos(), c.getChallenge(), actual.getHash()));
    }

    @Test
    public void testGetSize() {
        challenges.addChallenge(0, new VerifierChallenge(0, Util.generateChallenge(32), Util.generateChallenge(32)));
        challenges.addChallenge(1, new VerifierChallenge(1, Util.generateChallenge(32), Util.generateChallenge(32)));

        Assert.assertEquals("wrong size", 2, challenges.getSize());
    }

    @Test
    public void testToString() {

    }

    @Test
    public void testIterator() {
        VerifierChallenge c1 = new VerifierChallenge(0, Util.generateChallenge(32), Util.generateChallenge(32));
        VerifierChallenge c2 = new VerifierChallenge(1, Util.generateChallenge(32), Util.generateChallenge(32));
        VerifierChallenge c3 = new VerifierChallenge(2, Util.generateChallenge(32), Util.generateChallenge(32));
        challenges.addChallenge(0, c1);
        challenges.addChallenge(1, c2);
        challenges.addChallenge(2, c3);

        Iterator<VerifierChallenge> iter = challenges.iterator();
        Assert.assertTrue("iterator has no elements left", iter.hasNext());
        Assert.assertEquals("iterator gives wrong element", c1, iter.next());

        Assert.assertTrue("iterator has no elements left", iter.hasNext());
        Assert.assertEquals("iterator gives wrong element", c2, iter.next());

        Assert.assertTrue("iterator has no elements left", iter.hasNext());
        Assert.assertEquals("iterator gives wrong element", c3, iter.next());

        Assert.assertFalse("iterator shouldn't have elements left", iter.hasNext());
        Assert.assertEquals("iterator shouldn't have elements left", null, iter.next());
    }

    @Test
    public void testComparator() {
        Challenges c1 = new Challenges();
        Challenges c2 = new Challenges();

        VerifierChallenge vc1 = new VerifierChallenge(0, Util.generateChallenge(32), Util.generateChallenge(32));
        VerifierChallenge vc2 = new VerifierChallenge(1, Util.generateChallenge(32), Util.generateChallenge(32));

        c1.addChallenge(0, vc1);
        c1.addChallenge(1, vc2);
        c2.addChallenge(0, vc1);
        c2.addChallenge(1, vc2);

        Assert.assertEquals("challange storages are not equal", 0, c1.compareTo(c2));
    }
}
