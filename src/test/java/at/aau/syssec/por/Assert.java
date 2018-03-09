package at.aau.syssec.por;

import at.aau.syssec.por.hash.PrivateKeySet;
import at.aau.syssec.por.hash.PublicKeySet;
import at.aau.syssec.por.merkle.MerkleTree;
import at.aau.syssec.por.prover.FileBundle;
import at.aau.syssec.por.prover.ProverChallenge;
import at.aau.syssec.por.verifier.Challenges;
import at.aau.syssec.por.verifier.VerifierFileInfo;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeMap;

public class Assert extends junit.framework.Assert {

	public static void assertEquals(String msg, byte[] expected, byte[] actual) {
		assertTrue(msg, Arrays.equals(expected, actual));
	}
	
	public static void assertEquals(byte[] expected, byte[] actual) {
		assertEquals("", expected, actual);
	}
	
	public static void assertNotEquals(String msg, byte[] expected, byte[] actual) {
		assertTrue(msg, !Arrays.equals(expected, actual));
	}
	
	public static void assertNotEquals(byte[] expected, byte[] actual) {
		assertNotEquals("", expected, actual);
	}
	
	public static void assertEquals(String msg, BigInteger expected, BigInteger actual) {
		assertTrue(msg, expected.compareTo(actual) == 0);
	}
	
	public static void assertEquals(BigInteger expected, BigInteger actual) {
		assertEquals("", expected, actual);
	}
	
	public static void assertNotEquals(String msg, BigInteger expected, BigInteger actual) {
		assertTrue(msg, expected.compareTo(actual) != 0);
	}
	
	public static void assertNotEquals(BigInteger expected, BigInteger actual) {
		assertNotEquals("", expected, actual);
	}

    public static void assertEquals(PublicKeySet expected, PublicKeySet actual) {
        assertEquals("", expected, actual);
    }

    public static void assertEquals(String msg, PublicKeySet expected, PublicKeySet actual) {
        assertEquals(msg, expected.getPublicKey(), actual.getPublicKey());
        assertEquals(msg, expected.getG(), actual.getG());
        assertEquals(msg, expected.getP(), actual.getP());
        assertEquals(msg, expected.getQ(), actual.getQ());
    }

    public static void assertEquals(String msg, PrivateKeySet expected, PrivateKeySet actual) {
        assertEquals(msg, expected.getSecretKey(), actual.getSecretKey());
        assertEquals(msg, expected.getPublicKey(), actual.getPublicKey());
        assertEquals(msg, expected.getG(), actual.getG());
        assertEquals(msg, expected.getP(), actual.getP());
        assertEquals(msg, expected.getQ(), actual.getQ());
    }

    public static void assertEquals(MerkleTree expected, MerkleTree actual) {
        assertEquals(expected, actual);
    }

    public static void assertEquals(String msg, MerkleTree expected, MerkleTree actual) {
        assertEquals(msg, expected.getDepth(), actual.getDepth());
        assertEquals(msg, expected.getHash(), actual.getHash());
    }

    public static void assertEquals(FileBundle expected, FileBundle actual) {
        assertEquals("", expected, actual);
    }

    public static void assertEquals(String msg, FileBundle expected, FileBundle actual) {
        assertEquals(msg, expected.getKeys(), actual.getKeys());
        assertEquals(msg, expected.getTree(), actual.getTree());
    }

    public static void assertEquals(String msg, Challenges expected, Challenges actual) {
        assertEquals(msg, expected.compareTo(actual), 0);
    }

    public static void assertEquals(String msg, BlockSize expected, BlockSize actual) {
        assertEquals(msg, expected.fileSize, actual.fileSize);
        assertEquals(msg, expected.depth, actual.depth);
        assertEquals(msg, expected.blockSize, actual.blockSize);
        assertEquals(msg, expected.blockCount, actual.blockCount);
    }

    public static void assertEquals(String msg, VerifierFileInfo expected, VerifierFileInfo actual) {
        assertEquals(msg, expected.getKeys(), actual.getKeys());
        assertEquals(msg, expected.getChallenges(), actual.getChallenges());
        assertEquals(msg, expected.getFileName(), actual.getFileName());
        assertEquals(msg, expected.getHash(), actual.getHash());
        assertEquals(msg, expected.getBlockSize(), actual.getBlockSize());
    }
}
