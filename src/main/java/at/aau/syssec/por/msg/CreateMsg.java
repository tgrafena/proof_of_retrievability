package at.aau.syssec.por.msg;

import at.aau.syssec.por.BlockSize;
import at.aau.syssec.por.hash.PublicKeySet;

/**
 * Create messages are used to give the prover information about
 * a new incoming file. Used for "create".
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class CreateMsg extends Message {

	private static final long serialVersionUID = -8715979886080032117L;
	private PublicKeySet keys;
	private BlockSize bs;

	public CreateMsg(BlockSize blockSize, PublicKeySet keys) {
		this.bs = blockSize;
        this.keys = keys;
	}
	
	public BlockSize getBlockSize() {
		return bs;
	}
	
	public PublicKeySet getProverKeys() {
		return keys;
	}
}
