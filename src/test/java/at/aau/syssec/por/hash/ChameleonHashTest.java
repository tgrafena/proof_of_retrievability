package at.aau.syssec.por.hash;

import at.aau.syssec.por.Assert;
import at.aau.syssec.por.Block;
import at.aau.syssec.por.Util;
import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;

public class ChameleonHashTest {

    /* @Test
    public void testGetRandomInRange() {
        for(int i = 0; i < 20; i++) {
            BigInteger a = new BigInteger(128, new Random());
            BigInteger b = new BigInteger(128, new Random());
            BigInteger hash = ChameleonHash.cryptoHash(a, b, 63);

            Assert.assertEquals("hash not the right size", 63, hash.bitLength());
        }
    }*/

    @Test
    public void testKeyGen() {
        int t = 128;
        // TODO: bedingungen für param-längen
        // p = 1024 => q = 1023,
        PrivateKeySet pair = ChameleonHash.keyGen(t);
        // System.out.println("public-key: "+pair.getPublicKey().bitLength());
        // System.out.println("g: "+pair.getG().bitLength());
        // System.out.println("q: "+pair.getQ().bitLength());
        // Assert.assertEquals("secret key is not size of t: "+t, t, pair.getSecretKey().bitLength());
    }

    @Test
    public void testHash() {
        int t = 3;
        PrivateKeySet keys = ChameleonHash.keyGen(t);
        SecureRandom sr = new SecureRandom();
        BigInteger rho = new BigInteger(t, sr);
        BigInteger delta = new BigInteger(t, sr);
        byte[] data = new byte[t * 4];
        sr.nextBytes(data);

        byte[] hash = ChameleonHash.hash(data, rho, delta, keys);
        // TODO: hash is to small, investigate
        Assert.assertNotNull("hash is null", hash);
    }

    /* private void testImagePair(int m1, int rho1, int delta1, int m2, int rho2, int delta2) {
        PrivateKeySet keys = ChameleonHash.keyGen(512);

        byte[] hash = ChameleonHash.hash(BigInteger.valueOf(m1).toByteArray(),
                BigInteger.valueOf(rho1),
                BigInteger.valueOf(delta1),
                keys);

        PreImage img = ChameleonHash.forge(hash, BigInteger.valueOf(m2).toByteArray(), keys);
        Block block = img.getBlock(keys);

        Assert.assertEquals("hashes not equal not equal", hash, block.getHash());
    }

    @Test
    public void testForgeMultiple() {
        testImagePair(158, 373, 422, 255, 94, 323);
        testImagePair(85, 125, 226, 500, -38, 129783);
        testImagePair(48, 103, 36, 76, 10, 129466);
        testImagePair(416, 389, 503, 399, 497, 129708);
        testImagePair(101, 114, 362, 137, 558, 421);
        testImagePair(118, 317, 303, 138, 575, 129642);
        testImagePair(256, 507, 315, 335, 557, 328);
        testImagePair(499, 312, 435, 58, 554, 129783);
        testImagePair(391, 299, 131, 220, 361, 129475);
        testImagePair(31, 118, 445, 215, -117, 129434);
    }*/

    @Test
    public void testForgeManual() {
        SecureRandom rnd = new SecureRandom();

        // q = 7, p = 2*7+1 = 15
        // g = 25 (g' = 1..q-1 = 5)
        // sk = 3, pk = g^sk mod p = 25^3 mod 15 = 10
        PrivateKeySet keys = new PrivateKeySet(
                new BigInteger("15"),
                new BigInteger("7"),
                new BigInteger("25"),
                new BigInteger("3"),
                new BigInteger("10"));

        BigInteger rho = new BigInteger(100, rnd);
        BigInteger delta = new BigInteger(100, rnd);

        byte[] data1 = Util.generateChallenge(200);
        byte[] data2 = Util.generateChallenge(180);

        byte[] hash = ChameleonHash.hash(data1, rho, delta, keys);
        PreImage img = ChameleonHash.forge(hash, data2, keys);

        Assert.assertNotEquals("rho equals delta", rho, delta);

        // Assert.assertNotEquals("rho is equal", rho, img.getBlock(keys).getRho());
        Assert.assertNotEquals("delta is equal", delta, img.getBlock(keys).getDelta());
        Assert.assertNotEquals("message is equal", data1, data2);
        Assert.assertEquals("hashes not equal", hash, img.getBlock(keys).getHash());
    }

    @Test
    public void testForgeRandom() {
        for (int i = 0; i < 20; i++) {
            PrivateKeySet keys = ChameleonHash.keyGen(128);
            byte[] data = Util.generateChallenge(200);
            byte[] update = Util.generateChallenge(200);
            BigInteger rho = Util.secRand(128);
            BigInteger delta = Util.secRand(128);

            byte[] expected = ChameleonHash.hash(data, rho, delta, keys);

            PreImage image = ChameleonHash.forge(expected, update, keys);
            Block block = image.getBlock(keys);

            byte[] actual = block.updateHash(update, keys);

            Assert.assertEquals("hashes not equal (" + i + "): " + Util.shorten(expected) + " != " + Util.shorten(actual), expected, actual);
        }
    }

    @Test
    public void testBigInteger() {
        byte[] data = Util.generateChallenge(200);
        BigInteger big = new BigInteger(data);

        Assert.assertEquals("big int bytes wrong", data, big.toByteArray());
    }
}
