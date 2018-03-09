package at.aau.syssec.por.merkle;

import java.io.Serializable;

/**
 * The interface for all nodes (leaf and auth-node)
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public interface Node extends Serializable {
	
	public byte[] getHash();

}
