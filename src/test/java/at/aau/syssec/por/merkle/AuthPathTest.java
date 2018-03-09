package at.aau.syssec.por.merkle;

import at.aau.syssec.por.Assert;
import at.aau.syssec.por.Util;
import at.aau.syssec.por.hash.ChameleonHash;
import at.aau.syssec.por.hash.PrivateKeySet;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by unki2aut on 7/2/14.
 */
public class AuthPathTest {

    @Test
    public void testCalcRootHash() {
        int depth = 3;
        AuthPath path = new AuthPath(3);
        Random rand = new Random();

        for (int i = 0; i < depth; i++) {
            AuthNode.POS pos = rand.nextBoolean() ? AuthNode.POS.left : AuthNode.POS.right;
            path.setNode(i, new AuthNode(pos, Util.generateChallenge(128)));
        }

        BigInteger rho = Util.secRand(128);
        BigInteger delta = Util.secRand(128);
        PrivateKeySet keys = ChameleonHash.keyGen(128);
        byte[] data = Util.generateChallenge(4096);
        byte[] challenge = Util.generateChallenge(128);

        byte[] hash1 = path.calcRootHash(data, challenge, rho, delta, keys);

        byte[] hash2 = path.calcRootHash(Util.byteConcat(new byte[]{10, 20}, data), challenge, rho, delta, keys);
        byte[] hash3 = path.calcRootHash(data, Util.byteConcat(new byte[]{10, 20}, challenge), rho, delta, keys);

        Assert.assertTrue("challenge does not matter", !Arrays.equals(hash1, hash2));
        Assert.assertTrue("data does not matter", !Arrays.equals(hash1, hash3));
    }
}
