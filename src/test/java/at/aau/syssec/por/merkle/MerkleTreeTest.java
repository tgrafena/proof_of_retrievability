package at.aau.syssec.por.merkle;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Arrays;

import org.junit.Test;

import at.aau.syssec.por.Assert;
import at.aau.syssec.por.Block;
import at.aau.syssec.por.hash.ChameleonHash;
import at.aau.syssec.por.hash.PrivateKeySet;

public class MerkleTreeTest {

	@Test
	public void testMerkleTree() {
		int n = 1;
		MerkleTree tree = new MerkleTree(n);
		if (checkDepth(tree) != n) {
			fail("Failure_Test_MerkleTree_size_1: size does not match!\n");
		}
		if (checkLeafNodes(tree) != Math.pow(2, n)) {
			fail("Failure_Test_MerkleTree_size_1: nodes weren't build correctly!\n");
		}

		n = 10;
		tree = new MerkleTree(n);
		if (checkDepth(tree) != n) {
			fail("Failure_Test_MerkleTree_size_100: size does not match!\n");
		}
		if (checkLeafNodes(tree) != Math.pow(2, n)) {
			fail("Failure_Test_MerkleTree_size_100: nodes weren't build correctly!\n");
		}

	}

	public int checkDepth(MerkleTree tree) {
		if (tree.children[0] instanceof MerkleTree
				&& tree.children[1] instanceof MerkleTree) {
			return 1 + checkDepth((MerkleTree) tree.children[0]); // return 1
																	// for
																	// your
																	// successors
																	// +
																	// recursive(subtree)
		} else if (tree.children[0] instanceof Leaf
				&& tree.children[1] instanceof Leaf) {
			return 1; // return 1 for your successors
		} else {
			return 0;
		}

	}

	public int checkLeafNodes(MerkleTree tree) {

		if (tree.children[0] instanceof MerkleTree
				&& tree.children[1] instanceof MerkleTree) {
			return checkLeafNodes((MerkleTree) tree.children[0])
					+ checkLeafNodes((MerkleTree) tree.children[1]);
		} else if (tree.children[0] instanceof Leaf
				&& tree.children[1] instanceof Leaf) {
			return 2; // return 2 for your leaf nodes
		} else {
			return 0;
		}
	}

	@Test
	public void testSetBlock() {
		int n = 4;
		MerkleTree tree = new MerkleTree(n);
		byte[] data = Double.toString(Math.random()).getBytes();
		PrivateKeySet keys = ChameleonHash.keyGen(128);
		Block block = new Block(data, new BigInteger("1"), new BigInteger("1"),
				keys);
		byte[] hash = block.getHash();
		tree.setBlock(n / 2, block);
		byte[] testData = tree.findLeaf(n / 2).getHash();

		if (!Arrays.equals(hash, testData)) {
			fail("Failure_Test_setBlock: data isn't the same as it was set!\n");
		}
	}

	@Test
	public void testFindLeaf() {
		int n = 3; // tree-depth
		MerkleTree tree = new MerkleTree(n);
		PrivateKeySet keys = ChameleonHash.keyGen(128);
		String[] testData = new String[(int) Math.pow(2, n)];
		byte[] data;
		BigInteger rho = new BigInteger("1");
		BigInteger delta = new BigInteger("1");
		Block block;

		// fill tree with random data and store data additionally in string
		// array
		for (int i = 0; i < testData.length; i++) {
			testData[i] = Double.toString(Math.random());
			data = testData[i].getBytes();
			block = new Block(data, rho, delta, keys);
			tree.setBlock(i, block);
		}

		// check if data of string array is at right position in tree
		for (int i = 0; i < testData.length; i++) {
			Assert.assertEquals("Fail_Test_findLeaf: Wrong value at index " + i
					+ "! Expected:" + testData[i].getBytes() + " Actual:"
					+ tree.findLeaf(i).getValue().getHash() + "\n",
					ChameleonHash.hash(testData[i].getBytes(), rho, delta,
							keys), tree.findLeaf(i).getValue().getHash());
		}
	}

	@Test
	public void testGetPath() {
		int n = 3;
		MerkleTree tree = new MerkleTree(n);
		PrivateKeySet keys = ChameleonHash.keyGen(128);
		String[] testData = new String[(int) Math.pow(2, n)];
		byte[] data;
		Block block;

		// fill tree with random data and store data additionally in string
		// array
		for (int i = 0; i < testData.length; i++) {
			testData[i] = Double.toString(Math.random());
			data = testData[i].getBytes();
			block = new Block(data, new BigInteger("1"), new BigInteger("1"),
					keys);
			tree.setBlock(i, block);
		}
		// create AuthPath
		AuthPath authPath = tree
				.getPath((int) (Math.random() * Math.pow(2, n)));

		// checkAuthPath length
		if (authPath.getPath().size() != tree.getDepth() + 1) { // depth + root
			fail("Failure_Test_getPath: size of authPath doesn't match to tree depth!\n");
		}

	}

}
