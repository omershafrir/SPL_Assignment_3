package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.BaseServer;
import bgu.spl.net.srv.Messages.Message;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String []args){
//        int port = Integer.parseInt(args[0]);
        int port = 217 ;
        Server<Message> server = BaseServer.threadPerClient(port ,
                () ->  new BidiMessagingProtocolImpl() ,
                () ->  new MessageEncoderDecoderImpl());

        server.serve();

    }
}
