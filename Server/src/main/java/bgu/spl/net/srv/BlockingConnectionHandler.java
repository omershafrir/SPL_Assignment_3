package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocol<T> protocol) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;

    }

    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            protocol.start(ConnectionsImpl.getInstance().addHandler(this) ,ConnectionsImpl.getInstance() );
            int read;
            in = new BufferedInputStream(sock.getInputStream());
            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
//                System.out.println("NEXT BYTE: "+read);               /////////////////////////////
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
//                    System.out.println("INCOMING MESSAGE IS: "+nextMessage.toString());
                    protocol.process(nextMessage);
                }
            }
        } catch (Exception ex){//IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    @Override
    public void send(T msg) {
        try {
            out = new BufferedOutputStream(sock.getOutputStream());
            System.out.println("MESSAGE RECIEVED AT CONNECTIONHANLDER: " + this);
            System.out.println("MSG OF TYPE: "+msg.getClass()+ " ,  MSG CONTENT IS: "+msg);
            out.write(encdec.encode(msg));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
