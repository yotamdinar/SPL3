#ifndef CONNECTION_HANDLER__
#define CONNECTION_HANDLER__
                                           
#include <string>
#include <iostream>
#include <boost/asio.hpp>

using boost::asio::ip::tcp;

class ConnectionHandler {
private:
    std::string currMessage_;
	const std::string host_;
	const short port_;
	boost::asio::io_service io_service_;   // Provides core I/O functionality
	tcp::socket socket_; 
    bool shouldTerminate_;
 
public:
    ConnectionHandler(std::string host, short port);
    // virtual ~ConnectionHandler();
 
    // Connect to the remote machine
    bool connect();

    // get next byte from server and respond 
    void getAndProcess();

    // return true if the connection should be terminated and false otherwise
    bool shouldTerminate();

    // encodes a message and sends it to the server
    void encodeAndSend(std::string message);

    // sends bytesToWrite bytes to the server, returns true if was successful
    bool sendBytes(const char bytes[], int bytesToWrite);
 
    // reads bytesToRead bytes from the server, returns true if sucessful
    bool getBytes(char bytes[], unsigned int bytesToRead);



    // Close down the connection properly.
    void close();


    void process(std::string message);


    // encodes and sends different types of messages
    void sendPmMessage(std::string message);

    void sendPostMessage(std::string message);

    void sendStatMessage(std::string message);

    void sendLoginMessage(std::string message);

    void sendBlockMessage(std::string message);

    void sendLogoutMessage(std::string message);

    void sendFollowMessage(std::string message);

    void sendLogstatMessage(std::string message);

    void sendRegisterMessage(std::string message);



    //used for testing
    void printCharArray(char bytes[], size_t bytes_size);


    void processNotificationMessage(std::string message);
    void processAckMessage(std::string message);
    void processErrorMessage(std::string message);

 
}; //class ConnectionHandler
 
#endif