package at.aau.syssec.por.verifier.handler;

import java.io.IOException;
import java.util.Arrays;

import at.aau.syssec.por.Config;
import at.aau.syssec.por.Util;
import at.aau.syssec.por.msg.SuccessMsg;
import at.aau.syssec.por.verifier.VerifierFileInfo;

/**
 * For handling success messages after a create.
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class EncodeHandler extends AbstractHandler<SuccessMsg> {
	
	private byte[] hash;
    private VerifierFileInfo fileInfo;

	public EncodeHandler(byte[] hash, VerifierFileInfo fileInfo) {
		this.hash = hash;
        this.fileInfo = fileInfo;
	}

	@Override
	public RETURN_TYPE handleMessage(SuccessMsg res) {
		if(Arrays.equals(hash, res.getHash())) {
            try {
                fileInfo.persist();
                Config.appendArrayValue("files", fileInfo.getHash());
                setMsg("hash: "+Util.shorten(hash)+"\n"+
                       "block-count: "+fileInfo.getBlockSize().blockCount+"\n"+
                        "file successfully uploaded!");
                return RETURN_TYPE.FINISHED;
            } catch(IOException e) {
                setMsg("couldn't write verifier file info");
                return RETURN_TYPE.ERROR;
            }
		} else {
			setMsg("file hash is wrong!");
			return RETURN_TYPE.ERROR;
		}
	}
}
