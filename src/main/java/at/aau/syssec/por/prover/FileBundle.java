package at.aau.syssec.por.prover;

import java.io.Serializable;

import at.aau.syssec.por.hash.PublicKeySet;
import at.aau.syssec.por.merkle.MerkleTree;

/**
 * A file-bundle stores the merkle-tree of a file and its corresponding key-set.
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class FileBundle implements Serializable {

	private static final long serialVersionUID = -584612990593114507L;
	private final MerkleTree tree;
	private final PublicKeySet keys;
	
	public FileBundle(MerkleTree tree, PublicKeySet keys) {
		this.tree = tree;
		this.keys = keys;
	}
	
	public MerkleTree getTree() {
		return tree;
	}

	public PublicKeySet getKeys() {
		return keys;
	}
}
