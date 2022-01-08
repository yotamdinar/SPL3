#include <stdlib.h>
#include <connectionHandler.h>
#include <thread>

int main (int argc, char *argv[]) {
    ConnectionHandler ch("127.0.0.1",53);
    std::string nextMessage;
    std::cout << ">";
    // getline(std::cin,nextMessage);
    ch.processNotificationMessage("091Shlomi\nhey There my dude\n");

}