## cmd-chat

### Purpose
Design a chat program involving multiple clients and a host-server. Clients connected may broadcast messages to all users, or send private messages to a specific user. Before being able to send messages, users must login with a valid username and password combination. The server will run the chat service, manage clients, and handle user authentication.

A full description of the requirements can be found [here](https://github.com/PDirks/cli-chat/blob/master/requirements/lab3_v2.pdf)

### Client
The client interface uses multithreading to handle input from the user, as well as listening to the server. This is done with the *clientReadThread* class and the *clientListenThread* class respectivel.

### Server

### Running it
Both server and client runs from the command line interface. I developed using linux, so any terminal should work fine. Be sure to have java properly installed on your machine. The programs require the same port number to be given as input for there to be a connection (port numbers must be 5 digits long too) 

To run:

1. cd into the server directory and run 

  ```$java EchoServer [port num]``` 
2. in another terminal window cd into the client directory and run 

  ```$java EchoClient [port num]``` 

### Client-side commands
Chat clients can do a few basic commands, most of which require login first.

* **login [username] [password]** - attempts to login with the server. User will be notified on sucess or failure to login.
* **quit** - disconnects user from server and exits client out of program
* **send all [message]** -
* **send [username] [message]** -
* **who** -
