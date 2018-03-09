package at.aau.syssec.por.verifier.handler;

import at.aau.syssec.por.msg.Message;

/**
 * Abstract class for all PoR message-handler.
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public abstract class AbstractHandler<T extends Message> {
	public enum RETURN_TYPE {
		CONTINUE, FINISHED, ERROR
	}
	
	protected String msg;
	
	protected void setMsg(String msg) {
		this.msg = msg;
	}
	
	public String getMessage() {
		return msg;
	}
	
	public abstract RETURN_TYPE handleMessage(T res);
}
