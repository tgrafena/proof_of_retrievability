package at.aau.syssec.por.verifier.handler;

import at.aau.syssec.por.*;
import at.aau.syssec.por.hash.PrivateKeySet;
import at.aau.syssec.por.msg.Message;
import at.aau.syssec.por.msg.ResponseMsg;
import at.aau.syssec.por.msg.SuccessMsg;
import at.aau.syssec.por.msg.UpdateMsg;
import at.aau.syssec.por.verifier.Challenges;
import at.aau.syssec.por.verifier.PoRStruct;
import at.aau.syssec.por.verifier.VerifierChallenge;

import java.io.IOException;
import java.util.Arrays;

/**
 * For handling response and success-messages after an update.
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class UpdateHandler extends AbstractHandler<Message> {

    private PoRStruct por;
    private MessageHandler handler;
    private byte[] rootHash;
    private int pos;
    private byte[] data;
    private byte[] file;

    public UpdateHandler(PoRStruct por, MessageHandler handler,
                         byte[] hash, int pos, byte[] file) {
        this.por = por;
        this.handler = handler;
        this.rootHash = hash;
        this.pos = pos;
        this.file = file;
    }

    @Override
    public RETURN_TYPE handleMessage(Message res) {
        if (res instanceof ResponseMsg) {
            return handleResponseMessage((ResponseMsg) res);
        } else if (res instanceof SuccessMsg) {
            return handleSuccessMessage((SuccessMsg) res);
        } else {
            setMsg("wrong message type!");
            return RETURN_TYPE.ERROR;
        }
    }

    private RETURN_TYPE handleSuccessMessage(SuccessMsg res) {
        if (Arrays.equals(rootHash, res.getHash())) {
            setMsg("rootHash: " + Util.shorten(rootHash) + "\n" +
                    "block position: " + this.pos + "\n" +
                    "file successfully updated!");
            return RETURN_TYPE.FINISHED;
        } else {
            setMsg("got wrong rootHash code!");
            return RETURN_TYPE.ERROR;
        }
    }

    public RETURN_TYPE handleResponseMessage(ResponseMsg res) {
        BlockSize bs = por.getFileInfo().getBlockSize();

        if (this.pos >= bs.blockCount) {
            setMsg("block " + this.pos + " is out of file!");
            return RETURN_TYPE.ERROR;
        }

        int length = Math.min(bs.blockSize, this.file.length - pos * bs.blockSize);

        this.data = new byte[bs.blockSize];
        System.arraycopy(this.file, pos * bs.blockSize, this.data, 0, length);

        if (por.verify(res) && res.getPos() == pos) {
            try {
                UpdateMsg msg = por.update(rootHash, pos, data, res.getBlock().getHash(), res.getPath());
                handler.sendMsg(msg);
            } catch (IOException e) {
                setMsg("couldn't send update msg! (" + e.getMessage() + ")");
                return RETURN_TYPE.ERROR;
            }
        } else {
            setMsg("challenge " + res.getPos() + " not verified!");
            return RETURN_TYPE.ERROR;
        }

        return RETURN_TYPE.CONTINUE;
    }
}
