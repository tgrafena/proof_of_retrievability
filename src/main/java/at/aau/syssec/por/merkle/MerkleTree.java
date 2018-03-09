package at.aau.syssec.por.merkle;

import at.aau.syssec.por.Block;
import at.aau.syssec.por.Util;

import java.io.Serializable;

/**
 * The merkle-hash-tree is a binary tree that contains the data-blocks
 * of a file in its leafs and calculates their hashes.
 * The intermediate nodes and the root of the tree are calculated by 
 * concatenating and hashing the hash-values of their children.
 * 
 * example: 				root-hash
 * 						   /		 \
 * 						hash		  hash		<--intermediate hashes
 * 					   /	\		 /	  \
 * 					 hash	hash	hash  hash	<--leaf hashes
 *					  |		  |		  |		|
 *					data	data	data  data	<--leaf data
 *
 *
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class MerkleTree implements Serializable, Node {

    private static final long serialVersionUID = -5700691853102266064L;
    protected Node[] children = new Node[2];
    private int depth = 0;

    /**
     * The tree is build recursively
     * @param n depth of Merkle-Tree or 2^n width
     */
    public MerkleTree(int n) {

        depth = n;

        if (n == 1) {
            children[0] = new Leaf();
            children[1] = new Leaf();
        } else if (n > 1) {
            children[0] = new MerkleTree(n - 1);
            children[1] = new MerkleTree(n - 1);
        } else {
            System.err.println("MerkleTree-Error: parameter n is zero or negative!");
        }

    }

    /**
     * setting a leaf in the tree
     * @param pos
     * @param block
     */
    public boolean setBlock(int pos, Block block) {
        Leaf leaf = findLeaf(pos);

        if (leaf != null) {
            leaf.setValue(block);
            return true;
        } else {
            System.err.println("MerkleTree: leaf " + pos + " not found!");
            return false;
        }
    }

    /**
     * finding a leaf in the tree
     * @param pos
     * @return Leaf
     */
    public Leaf findLeaf(int pos) {

        // if we haven't reached the leaves yet:
        if (children[0] instanceof MerkleTree) {

            // set bit-mask
            int mask = 1 << (depth - 1);
            // go left
            if ((mask & pos) == 0) {
                return ((MerkleTree) children[0]).findLeaf(pos);
                // go right
            } else {
                return ((MerkleTree) children[1]).findLeaf(pos);
            }

            // if we have reached the leaves:
        } else if (children[0] instanceof Leaf) {

            // set bit-mask
            int mask = 1 << (depth - 1);
            // get left leaf-block
            if ((mask & pos) == 0) {
                return ((Leaf) children[0]);
                // get right leaf-block
            } else {
                return ((Leaf) children[1]);
            }

            // if an error occurred:
        } else {
            System.err.println("MerkleTree: node is neither a tree nor a leaf!");
        }

        return null;
    }

    /**
     * Returns the authentication-path for a certain leaf-position.
     * @param pos
     * @return AuthPath
     */
    public AuthPath getPath(int pos) {
        AuthPath path = new AuthPath(depth + 1);
        MerkleTree tree = this;

        // set root hash:
        path.setNode(0, new AuthNode(AuthNode.POS.root, tree.getHash()));

        // set node-path hashes:
        for (int i = 1; i <= depth; i++) {
            // set mask
            int mask = 1 << (depth - i + 1);

            // go left
            if ((mask & pos) == 0) {
                AuthNode authNode = new AuthNode(AuthNode.POS.right, tree.children[1].getHash()); // add right child's hash
                path.setNode(i, authNode);

                if (tree.children[0] instanceof MerkleTree) {
                    tree = (MerkleTree) tree.children[0];
                }
            }

            // go right
            if ((mask & pos) > 0) {
                AuthNode authNode = new AuthNode(AuthNode.POS.left, tree.children[0].getHash()); // add left child's hash
                path.setNode(i, authNode);

                if (tree.children[1] instanceof MerkleTree) {
                    tree = (MerkleTree) tree.children[1];
                }
            }
        }
        return path;
    }

    /**
     * returns the root-hash of the merkle-tree
     * @return byte[]
     */
    public byte[] getHash() {
        return Util.calcHash(Util.byteConcat(
                children[0].getHash(),
                children[1].getHash())
        );
    }

    /**
     * returns the depth of the merkle-tree
     */
    public int getDepth() {
        return depth;
    }
}
