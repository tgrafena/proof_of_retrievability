package at.aau.syssec.por.msg;

import at.aau.syssec.por.Block;
import at.aau.syssec.por.Util;
import at.aau.syssec.por.merkle.AuthPath;

/**
 * Respones-messages return the response after a challenge including
 * position, response and the authentification-path of a leaf-node
 * and of course the block-information and the data itself.
 * With this information, new challenges can be created.
 *
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class ResponseMsg extends Message {

    private static final long serialVersionUID = -5140075065869256884L;
    private int pos;
    private byte[] challenge;
    private byte[] response;
    private byte[] data;
    private Block block;
    private AuthPath path;

    public ResponseMsg(int pos, byte[] challenge, byte[] response, byte[] data, Block block, AuthPath path) {
        this.pos = pos;
        this.challenge = challenge;
        this.response = response;
        this.data = data;
        this.block = block;
        this.path = path;
    }

    public int getPos() {
        return pos;
    }

    public byte[] getChallenge() {
        return this.challenge;
    }

    public byte[] getResponse() {
        return response;
    }

    public byte[] getData() {
        return data;
    }

    public Block getBlock() {
        return block;
    }

    public AuthPath getPath() {
        return path;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ResponseMsg[pos: " + pos);
        builder.append(", challenge: " + Util.shorten(challenge));
        builder.append(", response: " + Util.shorten(response));
        builder.append(", data: " + Util.shorten(data));
        builder.append(", block: " + block);
        builder.append(", path: " + path);
        return builder.toString();
    }
}
