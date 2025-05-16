
# SimplePeerChat

Unimore project for Distributed algorithms course. 
The idea is to develop a distributed peer protocol to chat directly each other.
It is divided in two parts: Client/Peer (NoMitmChat) and Server (NoMitmChat).

The server only purpose is to keep a list of registered users, in form of ip address and username. \
The clients are able to communicate directly using UDP sockets in one of these two ways: already knowing the other peer, or registering to the server (through a TCP connection) and asking for a list of registrated users, which it can then contact.

## PeerChat protocol
### Client-server
Client contacts server on default port 6969 with a HELLO message. \
Client can ask list of online users.

Client: HELLO \
Server: HELLO \
Client: REGISTER user1 \
Server: REGISTER user1 OK \
Client: LISTUSERS \
Server: STARTUSERS \
Server: user2 \
Server: user3 \
Server: ... (all registrated users online other than user1) \
Server: ENDUSERS

Client asks the server to chat with a specified user. \
Client: CHAT user2 \
The server decides randomly a high port number, and asks to the interested users if that port is available to start a chat. If not, it generate a new number and asks again, until a max of 20 retries happen. \
If both clients send OK message, it then tells both to start a new chat, giving ip and usernames of each other

Server: PORT 55000 \
user1: OK \
user2: OK \
Server -> user1: STARTCHAT (user2 ip) 55000 \
Server -> user2: STARTCHAT (user1 ip) 55000 

Peer can now start chatting.

Errors are notified from server with a message in the form: ERROR infoerror

### Peer - Peer 
Peers use both the same port number. The reason for this, is to bypass NAT rules with UDP Hole Punching. To keep alive the session and not close the temporary nat rule, they keep exchanging a keep-alive token (keep-alive token not implemented here), that is useful also for a timeout to signal a peer has stopped chatting.

A message is formed as: usernameSender type_of_msg text \
Three types: CONNECT, MSG, CLOSE (four with keep-alive implementation: KEEPALIVE)

To start a connection, each peer starts sending the other with a CONNECT msg, untile receiving the other.

user1: user1 CONNECT \
user1: user1 CONNECT \
... \
user2: user2 CONNECT \

Once connected, they can exchange messages: \
user1: user1 MSG "hello there" \
user2: user2 MSG "General Kenobi" \

To manually stop a conversation, a peer can send a CLOSE msg \
user1: user1 CLOSE \

## Security Issues
Connections are not encrypted, everything is in plaintext.
Vulerable to Man in the middle attacks, both in client-server connections and in peer-peer chats. \
To solve these issues: \
1) TLS/SSL connections should be implemented instead plain TCP towards server \
2) Each client should register to server providing a personal public key. \
3) At beginning of the chat, there should be a challenge to verify authenticity of other peer using its public key.
4) Each Peer should keep a contact list with trusted public keys.
