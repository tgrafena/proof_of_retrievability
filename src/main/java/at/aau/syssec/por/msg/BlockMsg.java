package at.aau.syssec.por.msg;

import at.aau.syssec.por.Block;

/**
 * Blockmessages are used to deliver data-blocks during a create or update.
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class BlockMsg extends Message {

	private static final long serialVersionUID = 3944657955000271600L;
	private int pos;
	private byte[] data;
	private Block block;
	
	public BlockMsg(int pos, byte[] data, Block block) {
		this.pos = pos;
		this.data = data;
		this.block = block;
	}
	
	public int getPos() {
		return pos;
	}
	
	public byte[] getData() {
		return data;
	}

	public Block getBlock() {
		return block;
	}
}
