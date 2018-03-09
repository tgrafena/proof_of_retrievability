package at.aau.syssec.por.verifier.handler;

import at.aau.syssec.por.log.Debug;
import at.aau.syssec.por.msg.ResponseMsg;
import at.aau.syssec.por.verifier.PoRStruct;

import java.util.LinkedList;
import java.util.List;

/**
 * For handling response messages.
 *
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class ChallengeHandler extends AbstractHandler<ResponseMsg> {

    private PoRStruct por;
    private List<Integer> keys;
    private List<Integer> unverified = new LinkedList<Integer>();

    public ChallengeHandler(PoRStruct por, List<Integer> keys) {
        this.por = por;
        this.keys = keys;
    }

    @Override
    public RETURN_TYPE handleMessage(ResponseMsg res) {
        keys.remove(new Integer(res.getPos()));

        if (!por.verify(res)) {
            Debug.info(res);
            unverified.add(new Integer(res.getPos()));
        }

        if (keys.isEmpty()) {
            if (unverified.isEmpty()) {
                setMsg("all challenges verified");
                return RETURN_TYPE.FINISHED;
            } else {
                setMsg("challenges " + unverified + " not verified!");
                return RETURN_TYPE.ERROR;
            }
        } else {
            return RETURN_TYPE.CONTINUE;
        }
    }
}
