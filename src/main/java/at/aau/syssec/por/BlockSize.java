package at.aau.syssec.por;

import java.io.Serializable;

/**
 * Bean to store data about a file
 *
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class BlockSize implements Serializable {

    private static final long serialVersionUID = -8715974386080032117L;
    public static final int MAX_BLOCKS = 1024;

    /**
     * bytes of the actual file
     */
    public int fileSize;

    /**
     * depth of the merkle tree
     */
    public int depth;

    /**
     * size of a block in bytes
     */
    public int blockSize;

    /**
     * amount of blocks
     */
    public int blockCount;

    public BlockSize(int length, int depth, int blockSize, int blockCount) {
        this.fileSize = length;
        this.depth = depth;
        this.blockSize = blockSize;
        this.blockCount = blockCount;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[fileSize: " + fileSize + ", ");
        sb.append("depth: " + depth + ", ");
        sb.append("blockSize: " + blockSize + ", ");
        sb.append("blockCount: " + blockCount + "]");
        return sb.toString();
    }
}
