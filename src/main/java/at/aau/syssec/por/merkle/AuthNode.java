package at.aau.syssec.por.merkle;

import java.io.Serializable;

/**
 * A node along an authentication-path (Authpath)
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class AuthNode implements Serializable{
	
	/**
	 * serialization ID
	 */
	private static final long serialVersionUID = -8235545017657731292L;
	protected enum POS {
		left,
		right,
		root
	}
	
	/**
	 * position of the node in the authentication-path
	 */
	private POS pos;
	
	/**
	 * hash-value of the node
	 */
	private byte[] hash;
	
	public AuthNode(POS pos, byte[] hash){
		this.pos = pos;
		this.hash = hash;
	}
	
	public POS getPos(){
		return pos;
	}
	
	public byte[] getHash(){
		return hash;
	}

}
