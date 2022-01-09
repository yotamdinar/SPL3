#include <stdlib.h>
#include <connectionHandler.h>
#include <thread>



//this class will run on a thread that receives messages from the server
class Task{
private:
    ConnectionHandler* connectionHandlerPtr_;

public:
    Task(ConnectionHandler* connectionHandlerPtr):connectionHandlerPtr_(connectionHandlerPtr){}

    /**
     * this method will run on it's own thread
     * it will listen to messages from the server and will respond accordingly
     */
    void run(){
        while(true){
            connectionHandlerPtr_->getAndProcess();
            if(connectionHandlerPtr_->shouldTerminate()){
                exit(0);
            }
        }
    }
};



/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/

/**
 * the main thread will get messages from the keyboard
 * we will have another thread that receives messages from the server
 */
int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    
    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    
	Task listenToServer(&connectionHandler);
    std::thread t(&Task::run, listenToServer);
	

    while (true) {

        std::string nextMessage;
        getline(std::cin,nextMessage);
        connectionHandler.encodeAndSend(nextMessage);


    }

    return 0;
}
