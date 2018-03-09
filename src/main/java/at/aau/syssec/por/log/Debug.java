package at.aau.syssec.por.log;

import at.aau.syssec.por.Config;
import at.aau.syssec.por.Util;

import java.io.PrintStream;
import java.math.BigInteger;

/**
 * class to make debugging easier
 *
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class Debug {
    /**
     * Print out a list of objects with class and method name on System.out
     *
     * @param msgs list of objects to print
     */
    public static void info(Object... msgs) {
        println(System.out, msgs);
    }

    /**
     * Print out a list of objects with class and method name on System.err
     *
     * @param msgs list of objects to print
     */
    public static void err(Object... msgs) {
        println(System.err, msgs);
    }

    private static void println(PrintStream stream, Object[] msgs) {
        if (Config.getBoolValue(Config.DEBUG)) {
            StackTraceElement current = Thread.currentThread().getStackTrace()[3];
            String cName = current.getClassName().substring(current.getClassName().lastIndexOf(".") + 1);
            String mName = current.getMethodName();
            int line = current.getLineNumber();

            stream.println(cName + "." + mName + ":" + line + merge(msgs));
        }
    }

    private static String merge(Object[] msgs) {
        StringBuilder builder = new StringBuilder();
        for (Object msg : msgs) {
            if (msg instanceof byte[]) {
                builder.append(" " + Util.shorten((byte[]) msg));
            } else if (msg instanceof BigInteger) {
                builder.append(" " + Util.shorten(((BigInteger) msg)));
            } else {
                builder.append(" " + msg);
            }
        }
        return builder.toString();
    }
}
