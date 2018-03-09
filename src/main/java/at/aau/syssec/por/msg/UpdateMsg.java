package at.aau.syssec.por.msg;

import at.aau.syssec.por.Block;
import at.aau.syssec.por.Util;

/**
 * To send the prover an update of a certain file on a certain position.
 *
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class UpdateMsg extends Message {

    private static final long serialVersionUID = -6361635095335141093L;
    private byte[] hash;
    private byte[] data;
    private int pos;
    private Block block;

    public UpdateMsg(byte[] hash, byte[] data, int pos, Block block) {
        this.hash = hash;
        this.data = data;
        this.pos = pos;
        this.block = block;
    }

    public byte[] getHash() {
        return hash;
    }

    public byte[] getData() {
        return data;
    }

    public int getPos() {
        return pos;
    }

    public Block getBlock() {
        return this.block;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UpdateMsg [pos: " + pos);
        builder.append(", hash: " + Util.shorten(hash));
        builder.append(", data: " + Util.shorten(data));
        builder.append(", block: " + block);
        builder.append("]");
        return builder.toString();
    }
}
