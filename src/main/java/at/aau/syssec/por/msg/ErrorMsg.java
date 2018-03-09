package at.aau.syssec.por.msg;

/**
 * Used for response if an action at the prover didn't succeed.
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class ErrorMsg extends Message {

	private static final long serialVersionUID = -8534463754270823007L;
	private String error;

	public ErrorMsg(String error) {
		this.error = error;
	}

	public String getError() {
		return this.error;
	}
}
