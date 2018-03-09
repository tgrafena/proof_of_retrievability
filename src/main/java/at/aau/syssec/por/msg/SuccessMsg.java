package at.aau.syssec.por.msg;

/**
 * This message is meant to be sent by the prover as feedback of success
 * if no response message is demanded e.g. after a create.
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class SuccessMsg extends Message {

    private static final long serialVersionUID = -3563442950576043741L;
    private byte[] hash;

    public SuccessMsg(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getHash() {
        return hash;
    }
}
