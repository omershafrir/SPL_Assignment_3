package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
//import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.srv.Messages.Message;
import bgu.spl.net.srv.Messages.clientToServer.*;
import bgu.spl.net.srv.Messages.serverToClient.ACKMessage;
import bgu.spl.net.srv.Messages.serverToClient.ERRORMessage;
import bgu.spl.net.srv.Messages.serverToClient.NotificationMessage;

import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

        MessageEncoderDecoderImpl encdec = new MessageEncoderDecoderImpl();
//
        Message[] array = new Message[12];
        array[0] = new RegisterMessage("om" , "1" , "3");
        array[1] = new LoginMessage("omer" , "123" , (byte)1);
        array[2] = new LogoutMessage();
        array[3] = new FollowMessage((byte)1,"patidi");
        array[4] = new PostMessage("yoyo watup");
        array[5] = new PMMessage("0tolerance" , "yoyohey" , "3,10,989");
        array[6] = new LogstatMessage();
        array[7] = new StatMessage(null);
        array[8] = new NotificationMessage((byte)10 , "R0CKER" , "good_evening");
        array[9] = new ACKMessage((short)12 , null);
        array[10] = new ERRORMessage((short)12);
        array[11] = new BlockMessage("POPER");


//        short s = 12;
//        short ss = (short) (0<<8 | 4 & 0xFF);
//        byte[] a = {0 , 2 , 3 , 5 };
//        byte[] b = {0 , -2 , -3 , -5 };
//        byte[] c = {0 , 1 , 10 , 100 };
//        Vector<byte[]> vec = new Vector<>();
//        vec.add(a); vec.add(b); vec.add(c);
//        System.out.println(Arrays.toString(Message.mergeArrays(vec)));





//        Message m = new ACKMessage((short)3,null);
//        Message k = new RegisterMessage(""+opcode,"","");

        for(int i = 0; i < 12; i++) {
            byte[] encoded = encdec.encode(array[i]);
            System.out.println(array[i]+" encoded: "+ Arrays.toString(encoded));
            decode(encoded , encdec);
        }


//        System.out.println(Arrays.toString((array[0]).encode()));

//       byte[] bbb=encdec.encode(array[3].toString());
//        System.out.println(Arrays.toString(bbb));
//       testEncode(bbb,encdec);
//        for (int i=0 ;i < bbb.length ; i++){
//            System.out.println(enc);
//        }
//        for (int i=0 ; i< array.length ; i++){
//            Message m = array[i];
//            System.out.println("Message type is : "+m.getClass());
//            System.out.println("String after encoding: "+m);
//            System.out.println(Arrays.toString(encdec.encode(m.toString())));
//            System.out.print("String afteer decoding: ");
//            testEncode(encdec.encode(m.toString()) , encdec);
//            System.out.println("---------------------------------------------------");
//        }
//        MessageEncDec tool = new MessageEncDec();
//        System.out.println("MESSAGE: "+tool.toMessage(tool.toString(array[0])));

//
//        for (int i=0 ; i< array.length ; i++) {
//            System.out.println("String:  "+tool.toString(array[i]));
//            System.out.println("MESSAGE: "+tool.toMessage(tool.toString(array[i])));
//            System.out.println("**************************************************");
//        }







//        String s1= "omer";
//        String s2="shafrir";
//        Vector<byte[]> vec = new Vector<>();
//        byte[] a = encdec.encode(s1);
//        byte[] b = encdec.encode(s2);
//        vec.add(a); vec.add(b);
//        byte[] c = combineArrays(vec);
//
//        System.out.println(Arrays.toString(combineArrays(vec)));
//        testEncode(c , encdec);
//        System.out.println(""+(char)6);


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
    public static void decode(byte[] arr , MessageEncoderDecoder<Message> encdec){
        for (int i=0 ; i< arr.length ; i++){
            Message res;
            res = (Message) encdec.decodeNextByte(arr[i]);
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
    public static byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
}



