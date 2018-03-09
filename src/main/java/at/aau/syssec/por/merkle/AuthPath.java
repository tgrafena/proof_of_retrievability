package at.aau.syssec.por.merkle;

import at.aau.syssec.por.Util;
import at.aau.syssec.por.hash.ChameleonHash;
import at.aau.syssec.por.hash.PublicKeySet;
import at.aau.syssec.por.log.Debug;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 * The authentication-path contains the hashes of all sibling nodes that are
 * necessary to calculate the root-hash, starting at a leaf-node
 * 
 * example: root-hash / \ hash auth-node <--sibling / \ sibling --> auth-node
 * hash / \ leaf-node auth-node <--sibling
 * 
 * 
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class AuthPath implements Serializable {

	private static final long serialVersionUID = 1631520998109590190L;
	private ArrayList<AuthNode> path;

	public AuthPath(int size) {
		this.path = new ArrayList<AuthNode>(size);
	}

	/**
	 * sets an auth-node into the auth-path
	 * 
	 * @param level
	 *            - the level in the hash-tree (or the index in the array)
	 * @param node
	 */
	public void setNode(int level, AuthNode node) {
		this.path.add(level, node);
	}

	public AuthNode getNode(int level) {
		return this.path.get(level);
	}

	public ArrayList<AuthNode> getPath() {
		return this.path;
	}

	public void setPath(ArrayList<AuthNode> path) {
		this.path = path;
	}

	/**
	 * calculates the root hash for this AuthPath with a given leaf-node and
	 * challenge
	 * 
	 * @param data - data of the leaf-node
	 * @param challenge - challenge for this leaf-node
	 * @param rho \
	 * @param delta  > for the chameleon-hash-function
	 * @param keys  /
	 * @return byte[]
	 */
	public byte[] calcRootHash(byte[] data, byte[] challenge, BigInteger rho,
			BigInteger delta, PublicKeySet keys) {
		byte[] chunk = Util.byteConcat(challenge, data);
		byte[] nodeHash = ChameleonHash.hash(chunk, rho, delta, keys);

		for (int level = path.size() - 1; level >= 0; level--) {
			// if sibling-node is left append sibling's hash first
			if (path.get(level).getPos() == AuthNode.POS.left) {
				nodeHash = Util.calcHash(Util.byteConcat(path.get(level)
						.getHash(), nodeHash));
			}
			// if sibling-node is right append node's hash first
			if (path.get(level).getPos() == AuthNode.POS.right) {
				nodeHash = Util.calcHash(Util.byteConcat(nodeHash,
						path.get(level).getHash()));
			}
		}

		/* Debug.info("challenge: ", challenge, "\n data: ", data, "\n hash: ",
				nodeHash, "\n rho: ", rho, "\n delta: ", delta, "\n keys: ",
				keys); */

		return nodeHash;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AuthPath: [");

		for (int i = 0; i < path.size(); i++) {
			sb.append(i + "=" + Util.shorten(path.get(i).getHash()) + ", ");
		}

		sb.append("]");

		return sb.toString();
	}
}
