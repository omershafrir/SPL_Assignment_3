package bgu.spl.net.info.impl.echo;

import bgu.spl.net.srv.Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class EchoClient {

    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            args = new String[]{"localhost", "hello"};
        }

        if (args.length < 2) {
            System.out.println("you must supply two arguments: host, message");
            System.exit(1);
        }

        //BufferedReader and BufferedWriter automatically using UTF-8 encoding
//        try (Socket sock = new Socket(args[0], 7777);
//        InetAddress address = InetAddress.getByName("192.168.1.15");
        Server<String> rockerServer = Server.threadPerClient(6666 , () -> new EchoProtocol(), () -> new LineMessageEncoderDecoder());
            Thread x = new Thread(()->rockerServer.serve());
            x.start();

        try (Socket sock = new Socket("127.0.0.1", 6666);
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()))) {

            System.out.println("sending message to server");
//            out.write(args[1]);
            out.write("hi MOTHERFUCKERRRRRRRRRRRRR");
            out.newLine();
            out.flush();

            System.out.println("awaiting response");
            String line = in.readLine();
            System.out.println("message from server: " + line);
        }
    }
}
