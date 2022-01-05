package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
//import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.Messages.Message;
import bgu.spl.net.srv.Messages.clientToServer.*;
import bgu.spl.net.srv.Messages.serverToClient.ACKMessage;
import bgu.spl.net.srv.Messages.serverToClient.ERRORMessage;
import bgu.spl.net.srv.Messages.serverToClient.NotificationMessage;

import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Period;
import java.time.chrono.ChronoLocalDate;
import java.util.*;

public class MainTest {

    public static int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
    }
    public static void main(String []args) throws UnknownHostException {

        Server<Message> server = BaseServer.threadPerClient(5555 ,
                                        () ->  new BidiMessagingProtocolImpl() ,
                                        () ->  new MessageEncoderDecoderImpl());

        server.serve();

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



