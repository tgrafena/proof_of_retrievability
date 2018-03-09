package at.aau.syssec.por;

import at.aau.syssec.por.log.LogInputStream;
import at.aau.syssec.por.log.LogOutputStream;
import at.aau.syssec.por.msg.ChallengeMsg;
import at.aau.syssec.por.msg.DeleteMsg;
import at.aau.syssec.por.msg.ErrorMsg;
import at.aau.syssec.por.msg.Message;
import at.aau.syssec.por.prover.ClientHandler;
import at.aau.syssec.por.verifier.PoRStruct;
import at.aau.syssec.por.verifier.VerifierFileInfo;
import at.aau.syssec.por.verifier.handler.*;
import at.aau.syssec.por.verifier.handler.AbstractHandler.RETURN_TYPE;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
/**
 * Main function to execute all PoR-operations
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class PoR implements MessageHandler {

    private boolean terminate = false;
    private PoRStruct porStruct;
    private Socket socket;
    private LogOutputStream out;
    private int t;

    public PoR(int t) {
        this.t = t;
    }

    /**
     * prints a the usage-manual for the PoR
     */
    public static void usage() {
        System.out
                .println("usage:\n"
                        + "por <command> <file-hash>\n"
                        + "por create <filepath>\n"
                        + "por verify <file-hash> <amount-of-challenges>\n"
                        + "por update <file-hash> <block-to-update> <file>\n"
                        + "por download <file-hash> <filepath>\n"
                        + "por config <key> [<value>]\n"
                        + "client commands:\n"
                        + "create     Uploads the file to the Provider and creates verification information\n"
                        + "update     Updates modified blocks at the Provider\n"
                        // + "extend     Doubles the size of the hash tree and uploads the file again\n"
                        + "verify     Uses <count> challenges to verify the file\n"
                        + "download   Downloads the whole file\n"
                        + "delete     Removes the whole file at the provider\n"
                        + "list       Shows all stored files and their hashes\n"
                        + "config     Configure parameters, e.g. provider-url\n\n"
                        + "server commands:\n"
                        + "server     Starts the server with a given data path");
    }


    public static void main(String[] args) {
        Config.setup();

        PoR por = new PoR(Config.getIntValue(Config.SEC_PARAM));

        try {
            if (args.length == 0 || args[0].equals("usage")) {
                usage();
                return;
            } else if (args[0].equals("server")) {
                if (args[0].equals("server") && args.length == 1) {
                    por.startServer();
                } else {
                    usage();
                }
            } else if (args[0].equals("config")) {
                switch (args.length) {
                    case 1:
                        Config.printValues();
                        break;
                    case 2:
                        Config.printValue(args[1]);
                        break;
                    case 3:
                        Config.setValue(args[1], args[2]);
                        break;
                    default:
                        usage();
                }
            } else if (args[0].equals("list") && args.length == 1) {
                por.list();
            } else {
                if (!por.startClient()) return;

                String hash = args[1];

                if (hash.length() == 8) {
                    String[] hashes = Config.getArrayValue("files");

                    if (hashes != null) {
                        for (String h : hashes) {
                            if (h.startsWith(hash)) {
                                hash = h;
                                break;
                            }
                        }

                        if(hash.length() != 64) {
                            System.out.println("No files found!");
                            return;
                        }
                    } else {
                        System.out.println("No files found!");
                        return;
                    }
                }

                if (args[0].equals("create") && args.length == 2) {
                    por.create(args[1]);
                } else if (args[0].equals("update") && args.length == 4) {
                    por.update(hash, Integer.parseInt(args[2]), args[3]);
                /* } else if (args[0].equals("extend") && args.length == 3) {
                    por.extend(hash, args[2]); */
                } else if (args[0].equals("verify") && args.length == 3) {
                    por.verify(hash, Integer.parseInt(args[2]));
                } else if (args[0].equals("download") && args.length == 3) {
                    por.download(hash, args[2]);
                } else if (args[0].equals("delete") && args.length == 2) {
                    por.delete(hash, false);
                } else {
                    usage();
                }

                por.stop();
            }

        } catch (IOException e) {
            System.err.println("Problem with server-communication: "
                    + e.getMessage());
        }
    }

    /**
     * create - uploads a new file to the prover
     * @param path - location of file to be uploaded
     * @throws IOException
     */
    public void create(String path) throws IOException {
        porStruct = PoRStruct.setup(t, null);
        byte[] hash = porStruct.encode(path, this);
        if (hash != null) {
            handleMsg(new EncodeHandler(hash, porStruct.getFileInfo()));
        }
    }

    /**
     * updates a file at the prover
     * @param hash - filename at prover (file to be updated)
     * @param pos - block of the file to be updated (same for update and original-file)
     * @param blockFile - file-path of the update-file
     * @throws IOException
     */
    public void update(String hash, int pos, String blockFile)
            throws IOException {
        porStruct = PoRStruct.setup(t, VerifierFileInfo.readFile(hash));

        ChallengeMsg challengeMsg = new ChallengeMsg(hash);
        challengeMsg.addChallenge(pos, porStruct.getFileInfo().getChallenges().getChallenge(pos).getChallenge());
        challengeMsg.setKeepConnection(true);

        File file = new File(blockFile);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        byte[] data = new byte[bis.available()];
        bis.read(data);
        bis.close();

        this.sendMsg(challengeMsg);

        handleMsg(new UpdateHandler(porStruct, this, Util.hexToBytes(hash), pos, data));
    }

    /* public void extend(String hash, String path) throws IOException {
        delete(hash, true);
        create(path);
    }*/

    /**
     * verifies for random blocks if they still are consistent at the prover
     * @param hash - filename at prover (file to be verified)
     * @param challengeAmount - amount of random challenges to be sent
     * @throws IOException
     */
    public void verify(String hash, int challengeAmount) throws IOException {
        porStruct = PoRStruct.setup(t, VerifierFileInfo.readFile(hash));

        ChallengeMsg challengeMsg = porStruct.challenge(hash, challengeAmount);
        out.writeObject(challengeMsg);
        handleMsg(new ChallengeHandler(porStruct, challengeMsg.getKeys()));
    }

    /**
     * downloads the whole file from the prover
     * @param hash - filename at prover (file to be downloaded)
     * @param file - file-path + file-name (target directory)
     * @throws IOException
     */
    public void download(String hash, String file) throws IOException {
        porStruct = PoRStruct.setup(t, VerifierFileInfo.readFile(hash));

        ChallengeMsg downloadMsg = porStruct.extract(hash);
        out.writeObject(downloadMsg);
        handleMsg(new DownloadHandler(file, downloadMsg.getKeys(), porStruct.getFileInfo().getBlockSize()));
    }

    /**
     * delete file at prover
     * @param hash - filename at prover (file to be deleted)
     * @param keepConnection
     * @throws IOException
     */
    public void delete(String hash, boolean keepConnection) throws IOException {
        DeleteMsg msg = new DeleteMsg(hash);
        msg.setKeepConnection(keepConnection);
        out.writeObject(msg);
        handleMsg(new DeleteHandler(Util.hexToBytes(hash)));
    }

    /**
     * lists all filenames and file-hashes stored at the prover
     */
    public void list() {
        String[] files = Config.getArrayValue("files");

        if (files == null) {
            System.out.println("No stored files found!");
        } else {
            System.out.println("Stored files:");

            for (String hash : files) {
                String path = Config.FILES_PATH + hash;

                try {
                    KeyValueFile file = new KeyValueFile(path);
                    if (hash.length() < 8) {
                        System.out.println(hash + " - \""
                                + file.getValue("name") + "\"");
                    } else {
                        System.out.println(Util.shorten(hash) + " - \""
                                + file.getValue("name") + "\"");
                    }
                } catch (IOException e) {
                    System.err.println(path + " not found!");
                }
            }
        }
    }

    /**
     * sends given messages on the LogOutPutstream
     */
    public void sendMsg(Message msg) throws IOException {
        out.writeObject(msg);
    }

    /**
     * handles incoming messages from the server (prover)
     * @param handler - handler-type (challenge, delete, download, encode, update)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void handleMsg(AbstractHandler handler) throws IOException {

        try {
            LogInputStream in = new LogInputStream("client", new ObjectInputStream(socket.getInputStream()));
            Object o;
            while ((o = in.readObject()) != null) {

                if (o instanceof Message) {
                    Message msg = (Message) o;

                    if (msg instanceof ErrorMsg) {
                        ErrorMsg error = (ErrorMsg) msg;
                        System.out.println(error.getError());
                        break;
                    } else {
                        RETURN_TYPE ret = handler.handleMessage(msg);

                        if (ret == RETURN_TYPE.FINISHED) {
                            System.out.println(handler.getMessage());
                            break;
                        } else if (ret == RETURN_TYPE.ERROR) {
                            System.err.println(handler.getMessage());
                            break;
                        } // else continue
                    }
                } else {
                    System.err.println("received object is no message!");
                    break;
                }
            }

            System.out.println(in.getBytesTransfered() + " bytes input traffic");

            in.close();
        } catch (ClassNotFoundException e) {
            System.err.println("Message unknown: " + e.getMessage());
        }
    }

    /**
     * starts a new client (verifier)
     * @return true if the client could be established successfully
     */
    public boolean startClient() {
        try {
            socket = new Socket(Config.getValue(Config.PROVIDER),
                    Config.getIntValue(Config.SERVER_PORT));
            out = new LogOutputStream("client", new ObjectOutputStream(socket.getOutputStream()));

            return true;
        } catch (UnknownHostException e) {
            System.err.println("Provider not found: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("Problem with server-communication: "
                    + e.getMessage());
            return false;
        }
    }

    /**
     * closes client-socket
     * @throws IOException
     */
    private void stop() throws IOException {
        System.out.println(out.getBytesTransfered()+" bytes output traffic");

        this.socket.close();
    }

    /**
     * starts a new server (prover)
     */
    public void startServer() {
        try {
            int port = Config.getIntValue(Config.SERVER_PORT);
            ServerSocket socket = new ServerSocket(port);

            System.out.println("PoR-server started on port " + port + " ...");

            while (!terminate) {
                ClientHandler client = new ClientHandler(socket.accept());
                client.start();
            }

            socket.close();
        } catch (IOException e) {
            System.err.println("Problem with client-communication: "
                    + e.getMessage());
        }
    }
}
