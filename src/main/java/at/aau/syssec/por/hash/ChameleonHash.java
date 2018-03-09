package at.aau.syssec.por.hash;

import at.aau.syssec.por.Util;
import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * hash-function to find hash-collisions
 *
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class ChameleonHash {

    public static final int certainty = 80;
    public static final BigInteger two = new BigInteger("2");
    public static final SecureRandom rnd = new SecureRandom();

    /**
     * Generate a random prime number of the form 2 * q + 1
     *
     * @param t security parameter, number of bits of the prime number
     * @return save prime
     */
    private static BigInteger getSafePrime(int t) {
        BigInteger p;
        do {
            BigInteger q = BigInteger.probablePrime(t, rnd);
            p = two.multiply(q).add(BigInteger.ONE);
        } while (!p.isProbablePrime(certainty));

        return p;
    }

    /**
     * Get random BigInteger in range 1 ... n-1
     *
     * @param n prime number for range
     * @return random value in range
     */
    private static BigInteger getRandomInRange(BigInteger n) {
        BigInteger v;
        do {
            v = new BigInteger(n.bitLength(), rnd);
        } while (v.compareTo(n) >= 0 || v.compareTo(BigInteger.ZERO) <= 0);
        return v;
    }

    /**
     * find generator out of the subgroup of squares of order q
     * for p, q holds q|(p-1), p of the form 2*q+1
     * 1. choose h in [1 ... p-1]
     * 2. g = h ^ ((p-1)/q) mod p
     * 3. if g = 1, goto 1.
     *
     * @param p prime number
     * @return random generator of p
     */
    private static BigInteger findGenerator(BigInteger p) {
        BigInteger g;
        do {
            BigInteger h = getRandomInRange(p);
            g = h.modPow(two, p);
        } while (g.compareTo(BigInteger.ONE) == 0);

        return g.modPow(two, p);
    }

    /**
     * hash function with variable bit length
     * H(x) || H(x+1) || H(x+3) || H(x+6) ...
     *
     * @param a
     * @param b
     * @param bitLength bit length of hash
     * @return hash
     */
    private static BigInteger cryptoHash(BigInteger a, BigInteger b, int bitLength) {
        BigInteger x = new BigInteger(Util.byteConcat(a.toByteArray(), b.toByteArray()));
        SHA3.Digest256 md = new SHA3.Digest256();
        BigInteger hash = new BigInteger(md.digest(x.toByteArray())).abs();

        while (hash.bitLength() < bitLength) {
            BigInteger block = new BigInteger(md.digest(x.toByteArray())).abs();
            hash = hash.shiftLeft(block.bitLength()).add(block);
            x = x.add(BigInteger.ONE);
        }

        int shiftBack = hash.bitLength() - bitLength;
        return hash.shiftRight(shiftBack);
    }

    /**
     * generate a random pk/sk-KeyPair with all the parameters
     *
     * @param t security parameter, number of bits of the prime number
     * @return key pair
     */
    public static PrivateKeySet keyGen(int t) {
        // u = 2, t = 512 (1024)

        BigInteger p = getSafePrime(t); // p of the form 2*q+1
        BigInteger q = p.subtract(BigInteger.ONE).divide(two);

        BigInteger g = findGenerator(p); // p of the form 2*q+1
        BigInteger sk = getRandomInRange(q);
        BigInteger pk = g.modPow(sk, p);

        return new PrivateKeySet(p, q, g, sk, pk);
    }

    /**
     * generate chameleon hash of the message data
     *
     * @param data  bytes of the message to hash
     * @param rho   randomizer value
     * @param delta randomizer value
     * @param keys  pk/sk key pair and parameters
     * @return hash value
     */
    public static byte[] hash(byte[] data, BigInteger rho, BigInteger delta, PublicKeySet keys) {
        BigInteger e = cryptoHash(new BigInteger(data), rho, keys.getP().bitLength());

        // Debug.info("e =", e, "length", keys.getP().bitLength());

        BigInteger t1 = keys.getPublicKey().modPow(e, keys.getP());
        BigInteger t2 = keys.getG().modPow(delta, keys.getP());
        BigInteger ch = rho.subtract(t1.multiply(t2).mod(keys.getP()).mod(keys.getQ()));
        return ch.toByteArray();
    }

    /**
     * forge a pre-image for the new message
     *
     * @param hash old chameleon hash
     * @param data new message
     * @param keys pk/sk key pair and parameters
     * @return new pre-image (randomizers) for the message
     */
    public static PreImage forge(byte[] hash, byte[] data, PrivateKeySet keys) {
        BigInteger c = new BigInteger(hash);
        BigInteger mPrime = new BigInteger(data);
        BigInteger k = getRandomInRange(keys.getQ());
        BigInteger rhoPrime = c.add(keys.getG().modPow(k, keys.getP()).mod(keys.getQ()));
        BigInteger ePrime = cryptoHash(mPrime, rhoPrime, keys.getP().bitLength());
        BigInteger deltaPrime = k.subtract(ePrime.multiply(keys.getSecretKey())).mod(keys.getQ());
        return new PreImage(data, rhoPrime, deltaPrime);
    }
} 
