package at.aau.syssec.por.log;

import at.aau.syssec.por.Config;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * LogSequence
 * <p/>
 * Functionality to persist message sequence IDs
 *
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class LogSequence {

    public static int increaseSeqId(String id) {
        try {
            int val = 0;
            File file = new File(Config.PATH + id + ".seqid");

            file.getParentFile().mkdirs();

            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                if (scanner.hasNextInt()) {
                    val = scanner.nextInt();
                }
                scanner.close();
            }

            PrintWriter out = new PrintWriter(file);
            out.write(new Integer(val + 1).toString());
            out.close();

            return val;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

}
