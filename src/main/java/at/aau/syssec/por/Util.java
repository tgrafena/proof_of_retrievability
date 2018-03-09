package at.aau.syssec.por;

import at.aau.syssec.por.hash.ChameleonHash;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.jcajce.provider.digest.SHA3.Digest256;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Util class
 *
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class Util {

    /**
     * calculate a Keccak (SHA-3) 256-bit hash for given data
     *
     * @param data data
     * @return hash
     */
    public static byte[] calcHash(byte[] data) {
        Digest256 md = new SHA3.Digest256();
        return md.digest(data);
    }

    /**
     * generate a random challenge of the bit-size t
     *
     * @param t length
     * @return challenge
     */
    public static byte[] generateChallenge(int t) {
        byte[] challenge = new byte[t];
        new SecureRandom().nextBytes(challenge);
        return challenge;
    }

    /**
     * concatenate two byte arrays
     *
     * @param a byte array a
     * @param b byte array b
     * @return concatenation
     */
    public static byte[] byteConcat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    /**
     * convert a byte array to it's hex-string representation
     *
     * @param bytes data
     * @return hex string
     */
    public static String bytesToHex(byte[] bytes) {
        return new String(Hex.encode(bytes));
    }

    /**
     * convert a hex-string into a byte array
     *
     * @param s hex string
     * @return byte array
     */
    public static byte[] hexToBytes(String s) {
        return Hex.decode(s);
    }

    /**
     * calculate the logarithmus dualis of a given value
     *
     * @param val value
     * @return ld(value)
     */
    public static int ld(long val) {
        return (int) Math.ceil(Math.log(val) / Math.log(2));
    }

    /**
     * calculate the block parameters for a given file-size
     *
     * @param length file-size
     * @return block parameters
     */
    public static BlockSize calcBlockSize(int length) {
        // block-size >= 128kb (2^17) <= 4096kb (2^22)

        BlockSize bs = new BlockSize(length, 0, 2 << 16, 0);

        // increase blockSize until we have less then MAX_BLOCKS
        for (bs.blockSize = 131072; length / bs.blockSize > BlockSize.MAX_BLOCKS; bs.blockSize *= 2) ;

        bs.blockCount = (int) Math.ceil(new Double(length) / bs.blockSize);
        bs.depth = Math.max(ld(bs.blockCount), 1);

        return bs;
    }

    /**
     * generate a secure random value of size t
     *
     * @param t size
     * @return random value
     */
    public static BigInteger secRand(int t) {
        return new BigInteger(t, ChameleonHash.certainty, ChameleonHash.rnd).abs();
    }

    /**
     * return the byte size of an object
     *
     * @param obj
     * @return size
     */
    public static int size(Object obj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream arr = new ObjectOutputStream(bos);
            arr.writeObject(obj);
            bos.close();
            return bos.size();
        } catch (IOException e) {
            return 0;
        }
    }

    public static String shorten(byte[] data) {
        return shorten(Util.bytesToHex(data));
    }

    public static String shorten(BigInteger data) {
        return shorten(data.toString(16));
    }

    public static String shorten(String s) {
        return s.substring(0, Math.min(8, s.length()));
    }
}
