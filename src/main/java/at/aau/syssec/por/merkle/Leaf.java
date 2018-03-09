package at.aau.syssec.por.merkle;

import at.aau.syssec.por.Block;

/**
 * Leaf
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class Leaf implements Node {

	private static final long serialVersionUID = 3680077544873880564L;
	private Block value;
	
	public void setValue(Block value) {
		this.value = value;
	}
	
	public byte[] getHash() {
		if(value != null)
			return value.getHash();
		else
			return new byte[0];
	}

	public Block getValue() {
		return value;
	}

}
