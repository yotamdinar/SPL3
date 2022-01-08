package bgu.spl.net.api.bidi;

import bgu.spl.net.api.MessageEncoderDecoder;

import java.util.LinkedList;
import java.util.List;

public class MessageEncoderDecoderImp implements MessageEncoderDecoder<String> {

    private List<Byte> bytes = new LinkedList<Byte>();

    @Override
    public byte[] encode(String message) { //message = "1001"
        System.out.println("(encdec.encode)  before encoding string message = " + message);
        byte[] bytes = new byte[message.length()+1];
        short OpCode = Short.parseShort(message.substring(0, 2));
        System.out.println("(encdec.encode) short opcode = " + OpCode);
        bytes[0] = (byte)((OpCode >> 8) & 0xFF);
        bytes[1] = (byte)(OpCode & 0xFF);
        for(int i = 2; i < message.length(); i++){
            bytes[i] = (byte) message.charAt(i);
        }/////////////////////////////////////////////////////////need to add here the delimiters '0' or '\0'....
        bytes[bytes.length] = (byte) ';';
        System.out.println("(encedc.encode) after encoding resonse: " +bytes.toString());
        return bytes;
    }

    @Override
    public String decodeNextByte(byte nextByte) {
        if (nextByte == ';'){
            return decodeMessage(bytes);
        }
        bytes.add(nextByte);
        return null;
    }

    private String decodeMessage(List<Byte> bytesMessage){ //01Neymar0
        System.out.println("(encdec.encode)before decoding "+bytesMessage.get(0) + bytesMessage.get(1)+ bytesMessage.get(2)+" --->     " +bytesMessage.toString());

        String stringMessage = "";
        byte[] opBytes = new byte[2];
        opBytes[0] = bytesMessage.get(0);
        opBytes[1] = bytesMessage.get(1);
        //short opCode = bytesToShort(opBytes);
        String op = ""+(char)opBytes[0]+ (char) opBytes[1];
        System.out.println("op as string: "+op);
        int opCode = Integer.parseInt(op);
        System.out.println("(encde.decode)short: "+opCode);
        if(opCode<10)
            stringMessage+= "0";
        stringMessage += opCode; //adding here short to string how should i parse it in process?
        System.out.println("(encde.decode)opcode: "+stringMessage);


        for(int i = 2; i< bytesMessage.size(); i++){
            stringMessage += (char) ((byte)bytesMessage.get(i));
        }
        stringMessage += ';';
        bytes = new LinkedList<>();
        System.out.println("(encde.decode) after decoding: "+stringMessage);
        return stringMessage; /*01<Username>+'\0'+<Password>+'\0'+<Birthday>+'\0'*/
    }



    private byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    private short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }


}
