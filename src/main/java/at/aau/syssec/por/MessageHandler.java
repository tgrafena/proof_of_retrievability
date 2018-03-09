package at.aau.syssec.por;

import java.io.IOException;

import at.aau.syssec.por.msg.Message;
import at.aau.syssec.por.verifier.handler.AbstractHandler;

/**
 * Interface for handling different PoR messages
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public interface MessageHandler {

	@SuppressWarnings({ "rawtypes" })
	public void handleMsg(AbstractHandler handler) throws IOException;
	
	public void sendMsg(Message msg) throws IOException;
}