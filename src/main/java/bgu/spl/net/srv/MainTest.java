package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.MessageEncoderDecoderImpl;
import bgu.spl.net.info.impl.echo.*;
import bgu.spl.net.srv.Messages.Message;
import bgu.spl.net.srv.Messages.clientToServer.RegisterMessage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLOutput;
import java.util.*;
import java.util.function.Supplier;

public class MainTest {
    public static void main(String []args) throws UnknownHostException {
        /**
        System.out.println(InetAddress.getLocalHost());
        try(Server<String> rockerServer = Server.threadPerClient(6666 , () -> new EchoProtocol(), () -> new LineMessageEncoderDecoder());){
            rockerServer.serve();
        }    catch(Exception e){
            e.printStackTrace();
        }
         **/

        MessageEncoderDecoder encdec = new MessageEncoderDecoderImpl();
//
//        RegisterMessage m =new RegisterMessage("omer" , "123" , "3/10");
//        System.out.println(m);
//        System.out.println(Arrays.toString(encdec.encode(m.toString())));
//        testEncode(encdec.encode(m.toString()) , encdec);



        String s1= "omer";
        String s2="shafrir";
        Vector<byte[]> vec = new Vector<>();
        byte[] a = encdec.encode(s1);
        byte[] b = encdec.encode(s2);
        vec.add(a); vec.add(b);
        byte[] c = combineArrays(vec);

        System.out.println(Arrays.toString(combineArrays(vec)));
        testEncode(c , encdec);



        /**
        String x1 = "Hello  World";
        byte[] arr1 = encdec.encode(x1);
//        byte[] bytes = {72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100, 10};
        System.out.println("WITHOUT ZEROES:");
        System.out.println(Arrays.toString(arr1));
        testEncode(arr1 , encdec);

        arr1[5] = 0;

        System.out.println("WITH ZEROES:");
        System.out.println(Arrays.toString(arr1));
        testEncode(arr1 , encdec);
        **/
    }
    public static void testEncode(byte[] arr , MessageEncoderDecoder<String> encdec){
        for (int i=0 ; i< arr.length ; i++){
            String res;
            res = (String) encdec.decodeNextByte(arr[i]);
            if (res != null)
                System.out.println(res);
        }
    }

    public  static byte[] combineArrays(Vector<byte[]> vec){
        int size=0;
        int index=0;

        for (int arr=0; arr<vec.size() ; arr++){      //calculating returned array size
            size += vec.elementAt(arr).length;
        }

        byte[] output = new byte[size];

        for (byte[] arr : vec){                     //appending all bytes to to return value
            for(int i=0 ; i< arr.length ; i++){
                output[index] = arr[i];
                index++;
            }
        }
        return output;
    }

}



