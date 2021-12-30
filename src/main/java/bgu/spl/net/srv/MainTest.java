package bgu.spl.net.srv;

import bgu.spl.net.info.impl.echo.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.function.Supplier;

public class MainTest {
    public static void main(String []args) {




        Server<String> rockerServer = Server.threadPerClient(6666 , () -> new EchoProtocol(), () -> new LineMessageEncoderDecoder());
            Thread x = new Thread(()->rockerServer.serve());
            x.start();




    }

}



