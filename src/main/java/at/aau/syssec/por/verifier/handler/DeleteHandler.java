package at.aau.syssec.por.verifier.handler;

import java.io.File;
import java.util.Arrays;

import at.aau.syssec.por.Config;
import at.aau.syssec.por.Util;
import at.aau.syssec.por.msg.SuccessMsg;

/**
 * For handling success messages after a delete.
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class DeleteHandler extends AbstractHandler<SuccessMsg> {

	private byte[] hash;

	public DeleteHandler(byte[] hash) {
		this.hash = hash;
	}

	@Override
	public RETURN_TYPE handleMessage(SuccessMsg res) {
		String path = Util.bytesToHex(res.getHash());
		
		if (Arrays.equals(hash, res.getHash())) {
			if(Config.removeArrayValue("files", path)) {
				File file = new File(Config.FILES_PATH + path);
				
				if(file.delete()) {
					setMsg("file deleted: "+path);
					return RETURN_TYPE.FINISHED;
				} else {
					setMsg("couldn't delete file: "+path);
					return RETURN_TYPE.ERROR;
				}
			} else {
				setMsg("file not found: "+path);
				return RETURN_TYPE.ERROR;
			}
		} else {
			setMsg("hashes don't match");
			return RETURN_TYPE.ERROR;
		}
	}
}
