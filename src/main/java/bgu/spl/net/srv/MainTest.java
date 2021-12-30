package bgu.spl.net.srv;

import bgu.spl.net.info.impl.echo.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.Supplier;

public class MainTest {
    public static void main(String []args) throws UnknownHostException {
        System.out.println(InetAddress.getLocalHost());
        try(Server<String> rockerServer = Server.threadPerClient(6666 , () -> new EchoProtocol(), () -> new LineMessageEncoderDecoder());){
            rockerServer.serve();
        }    catch(Exception e){
            e.printStackTrace();
        }

    }

}



