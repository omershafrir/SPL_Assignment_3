package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.BaseServer;
import bgu.spl.net.srv.Messages.Message;
import bgu.spl.net.srv.Reactor;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String []args) {

        int port = 217;
        Server<Message> server = new Reactor<>(3, port,
                () -> new BidiMessagingProtocolImpl(),
                () -> new MessageEncoderDecoderImpl());

        server.serve();
    }
}
