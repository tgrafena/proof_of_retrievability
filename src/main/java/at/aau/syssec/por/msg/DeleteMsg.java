package at.aau.syssec.por.msg;

/**
 * Deletemessages are used to delete a file at the prover.
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class DeleteMsg extends Message {

	private static final long serialVersionUID = -252494816195161806L;
	private String hash;

	public DeleteMsg(String hash) {
		this.hash = hash;
	}
	
	public String getHash() {
		return hash;
	}
}
