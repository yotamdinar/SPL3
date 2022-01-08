#include <connectionHandler.h>

using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;
 
ConnectionHandler::ConnectionHandler(string host, short port):currMessage_(""), host_(host), port_(port), io_service_(), socket_(io_service_), shouldTerminate_(false){}
    
// ConnectionHandler::~ConnectionHandler() {
//     close();
// }
 
bool ConnectionHandler::connect() {
    std::cout << "Starting connect to " 
        << host_ << ":" << port_ << std::endl;
    try {
		tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
		boost::system::error_code error;
		socket_.connect(endpoint, error);
		if (error)
			throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}


void ConnectionHandler::encodeAndSend(std::string message){
    //encode and send the message according to the message name
    if(message.substr(0,2) == "PM")
        sendPmMessage(message);
    else if(message.substr(0,4) == "POST")
        sendPostMessage(message);
    else if(message.substr(0,4) == "STAT")
        sendStatMessage(message);
    else if(message.substr(0,5) == "LOGIN")
        sendLoginMessage(message);
    else if(message.substr(0,5) == "BLOCK")
        sendBlockMessage(message);
    else if(message.substr(0,6) == "LOGOUT")
        sendLogoutMessage(message);
    else if(message.substr(0,6) == "FOLLOW")
        sendFollowMessage(message);
    else if(message.substr(0,7) == "LOGSTAT")
        sendLogstatMessage(message);
    else if(message.substr(0,8) == "REGISTER")
        sendRegisterMessage(message);
    else
        std::cout << "Sorry! Illegal input! please check your syntax" << std::endl;   
}

void ConnectionHandler::getAndProcess(){
    char nextByte[1];
    getBytes(nextByte,1);
    std::cout << "(CH.getAndProcess) nextByte = " << nextByte[0] << std::endl;
    if(nextByte[0] == ';'){
        std::cout << "(CH.getAndProcess) currMessage = " << currMessage_ << std::endl;
        process(currMessage_);
        currMessage_ = "";
    }
    else if(nextByte[0] == '\0'){
        currMessage_+="\n";
    } else{
        currMessage_ += nextByte[0];
    }

}

bool ConnectionHandler::shouldTerminate(){
    return shouldTerminate_;
}
 

// Close down the connection properly.
void ConnectionHandler::close() {
    try{
        socket_.close();
        shouldTerminate_ = true;
        std::cout << "successfully disconnected, client will now terminate" << std::endl;
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}



void ConnectionHandler::sendPmMessage(std::string message){
    //size = message/size() - 3("PM ".size) + 4(opCode + ';' + '0') = message.size() + 1  
    char bytes[message.size()+1];
    bytes[0] = '0';
    bytes[1] = '6';
    size_t i = 2;
    while(i < message.size()){
        if(message[i+1] != ' '){
             bytes[i] = message[i+1];
             i++;   
        } else{
            bytes[i] = '\0';
            i++;
            break;
        }
    }

    for(size_t j = i; j < message.size()-1; j++){
        bytes[j] = message[j+1];
    }

    bytes[message.size()-1] = '\0';
    bytes[message.size()] = ';';

    // for(size_t i = 0; i < message.size() + 1; i++){
    //     std::cout << bytes[i];
    // }
    // std::cout << std::endl;

    
    sendBytes(bytes, message.size());




}

void ConnectionHandler::sendPostMessage(std::string message){

    // bytes_size = message.size() +2(opcode) -5('POST ') +2('0;')=message.size()-1
    size_t bytes_size = message.size()-1;
    char bytes[bytes_size];
    bytes[0] = '0';
    bytes[1] = '5';
    for(size_t i = 5; i < message.size(); i++){
        bytes[i-3] = message[i];
    }
    bytes[bytes_size-2] = '\0';
    bytes[bytes_size-1] = ';';


    // for(size_t i = 0; i < bytes_size; i++){
    //     std::cout << bytes[i];
    // }
    // std::cout << std::endl;


    sendBytes(bytes, bytes_size);

}

void ConnectionHandler::sendStatMessage(std::string message){
    // bytes_size = message.size - 4("stat ") +2(opcode) +2("0;") = message.size
    size_t bytes_size = message.size();
    char bytes[message.size()];
    bytes[0] = '0';
    bytes[1] = '8';
    for(size_t i = 5; i < message.size(); i++){
        bytes[i-3] = message[i];
    }

    bytes[bytes_size-2] = '\0';
    bytes[bytes_size-1] = ';';

    // printCharArray(bytes,bytes_size);

    sendBytes(bytes, bytes_size);

    
}

void ConnectionHandler::sendLoginMessage(std::string message){
    //bytes_size = message.size -6("LOGIN ") +2(opcode) +3('0' +captcha + ';') + 1('0') = message.size
    size_t bytes_size = message.size();
    char bytes[bytes_size];
    bytes[0] = '0';
    bytes[1] = '2';
    size_t i = 6;
    while(i < message.size()){
        if(message[i]!= ' '){
            bytes[i-4] = message[i];
            i++;
        } else{
            bytes[i-4] = '\0';
            i++;
        }
    }
    bytes[bytes_size-4] = '\0';
    bytes[bytes_size-3] = '1';
    bytes[bytes_size-2] = '\0';
    bytes[bytes_size-1] = ';';

//    printCharArray(bytes,bytes_size);

    sendBytes(bytes, bytes_size);

    


}

void ConnectionHandler::sendBlockMessage(std::string message){
    //bytes_size = message.size -6("BLOCK ") +2(opcode) +2("0;") = message.size - 2
    size_t bytes_size = message.size()-2;
    char bytes[bytes_size];
    bytes[0] = '1';
    bytes[1] = '2';
    for(size_t i = 6; i < message.size(); i++){
        bytes[i-4] = message[i];
    }
    bytes[bytes_size-2] = '\0';
    bytes[bytes_size-1] = ';';


    // printCharArray(bytes,bytes_size);

    sendBytes(bytes,bytes_size);



}

void ConnectionHandler::sendLogoutMessage(std::string message){
    // opcode
    char bytes[3] = {'0','3',';'};
    boost::system::error_code error;
    
    // logout has no other parameters so just send
    sendBytes(bytes,3);

}

void ConnectionHandler::sendFollowMessage(std::string message){
    //bytes_size = message.size - 8("FOLLOW " + ' ') +2(opcode) + 2("0;")= mesage.size-4
    size_t bytes_size = message.size() - 3;
    char bytes[bytes_size];
    bytes[0] = '0';
    bytes[1] = '4';
    bytes[2] = message[7];
    bytes[3] = '\0';
    for(size_t i = 9; i < message.size(); i++){
        bytes[i-5] = message[i];
    }

    bytes[bytes_size-2] = '\0';
    bytes[bytes_size-1] = ';';

    // printCharArray(bytes,bytes_size);
    sendBytes(bytes,bytes_size);
}

void ConnectionHandler::sendLogstatMessage(std::string message){
    //bytes size = "07;".size = 3
    size_t bytes_size = 3;
    char bytes[3] = {'0','7',';'};

    printCharArray(bytes,bytes_size);
    sendBytes(bytes,bytes_size);

}

void ConnectionHandler::sendRegisterMessage(std::string message){
    //bytes_size = message.size - 9("REGISTER ") +2(opcode) +2("0;") = message.size - 5
    size_t bytes_size = message.size()-5;
    char bytes[bytes_size];
    bytes[0] = '0';
    bytes[1] = '1';
    for(size_t i = 9; i < message.size(); i++){
        if(message[i] != ' ')
            bytes[i-7] = message[i];
        else
            bytes[i-7] = '\0';
    }

    bytes[bytes_size-2] = '\0';
    bytes[bytes_size-1] = ';';

    // printCharArray(bytes,bytes_size);
    sendBytes(bytes,bytes_size);
        

}

void ConnectionHandler::process(std::string message){
    if(message.substr(0,2) == "09"){
        processNotificationMessage(message);
    } else if(message.substr(0,2) == "10"){
        processAckMessage(message);
    } else if(message.substr(0,2) == "11"){
        processErrorMessage(message);
    } else{
        std::cout << "cannot process message from server, opCode not recognized" << std::endl;
    }
}

void ConnectionHandler::processNotificationMessage(std::string message){
    //figure out username size and content size
    int userSize = 0;
    size_t i = 3;
    while(i < message.size()){
        if(message[i] == '\n'){
            i++;
            break;
        } else{
            i++;
            userSize++;
        }
    }
    int contentSize = message.size()-userSize-5;


    //build message
    std::string type;
    if(message[3] == '0')
        type = "PM";
    else
        type = "Public";
    std::string postingUser = message.substr(3,userSize);
    std::string content = message.substr(3 + userSize + 1, contentSize);
    std::string output = "NOTIFICATION " + type + " " + postingUser + " " + content;


    //print to screen
    std::cout << output << std::endl;
}

void ConnectionHandler::processAckMessage(std::string message){
    std::string output = "ACK " + message.substr(2);
    std::cout << output << std::endl;
    if(message.substr(2,2) == "03") {
        close();
    }
}

void ConnectionHandler::processErrorMessage(std::string message){
    std::cout << "ERROR " << message.substr(2,2) << std::endl;
}

void ConnectionHandler::printCharArray(char bytes[], size_t bytes_size){
    for(size_t i = 0; i < bytes_size; i++){
        std::cout << bytes[i];
    }
    std::cout << std::endl;
}

















bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
			tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);			
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
            std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        exit(0);
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
			tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}









