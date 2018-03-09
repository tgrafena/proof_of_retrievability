package at.aau.syssec.por.log;

import at.aau.syssec.por.Util;
import at.aau.syssec.por.msg.Message;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * LogInputStream
 *
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class LogInputStream {

    private String id;
    private ObjectInputStream ois;
    private int bytesTransfered;

    public LogInputStream(String id, ObjectInputStream ois) throws IOException {
        this.id = id;
        this.ois = ois;
    }

    public Object readObject() throws ClassNotFoundException, IOException {
        Object obj = ois.readObject();

        if (obj instanceof Message) {
            Message msg = (Message) obj;
            String name = msg.getClass().getSimpleName();
            int size = Util.size(obj);
            bytesTransfered += size;

            System.out.println(id + "-in (" + msg.seqId + "): " + name + "(" + size + ")");
        }

        return obj;
    }

    public int getBytesTransfered() {
        return bytesTransfered;
    }

    public void close() throws IOException {
        ois.close();
    }
}
