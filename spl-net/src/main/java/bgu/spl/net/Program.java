package bgu.spl.net;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.MessageEncoderDecoderImp;

public class Program {

    public static void main(String[] args){

        MessageEncoderDecoderImp messageEncoderDecoderImp = new MessageEncoderDecoderImp();
        System.out.println(messageEncoderDecoderImp.encode("5555faga")[0]);
        System.out.println(messageEncoderDecoderImp.encode("5555faga")[1]);

    }
}
