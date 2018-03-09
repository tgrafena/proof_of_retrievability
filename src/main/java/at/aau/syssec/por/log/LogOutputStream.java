package at.aau.syssec.por.log;

import at.aau.syssec.por.Util;
import at.aau.syssec.por.msg.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * LogOutputStream
 *
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class LogOutputStream {
    private String id;
    private ObjectOutputStream oos;
    private int bytesTransfered;

    public LogOutputStream(String id, ObjectOutputStream oos) {
        this.id = id;
        this.oos = oos;
    }

    public void writeObject(Object obj) throws IOException {
        if (obj instanceof Message) {
            Message msg = (Message) obj;
            msg.seqId = LogSequence.increaseSeqId(id);
            String name = msg.getClass().getSimpleName();
            int size = Util.size(obj);
            bytesTransfered += size;

            System.out.println(id + "-out (" + msg.seqId + "): " + name + "(" + size + ")");
        }

        oos.writeObject(obj);
    }

    public int getBytesTransfered() {
        return bytesTransfered;
    }

    public void close() throws IOException {
        oos.close();
    }
}
