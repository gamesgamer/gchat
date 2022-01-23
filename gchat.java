import java.io.*;
import java.net.*;

class gchat extends Thread{
    // Declaring variables here so they can be used by all functions

    private boolean busy = false;
    private Socket socket;
    private DataOutputStream dout;
    private String host;
    private ServerSocket ss;

    public void run(){
        try {
            sender();
        }catch(IOException e){
            System.out.println("An error occured... Details below: ");
            System.out.println(e);
        }
    }

    public static void main(String[] args) throws IOException{ // Main function
        gchat gChat = new gchat();
        System.out.println("gChat. Type 1 to listen for incoming chats, type 2 to chat to someone");
        String choice = System.console().readLine();
        //System.out.println(choice + "a");
        if (choice.equals("1")){ // Listen
            gChat.ss = new ServerSocket(6666); // Start listening
            System.out.println("Listening for incoming connections...");
            gChat.socket = gChat.ss.accept(); // Communicating.
            gChat.dout = new DataOutputStream(gChat.socket.getOutputStream());
            gChat.host = gChat.socket.getLocalAddress().toString();

            System.out.println("Connected to " + gChat.host);

            gChat.busy = true;
            gChat.start();
    
            gChat.listener();
        }
        else if (choice.equals("2")){ // Speak
            System.out.println("Host IP Address?");
            gChat.host = System.console().readLine();

            gChat.connect(gChat.host, 6666);
    
            System.out.println("Connected to " + gChat.host);
            // Listener
            gChat.start();
    
            gChat.listener();
        }
        else { // If it isn't 1 or 2
            System.out.println("Invalid command");
        }

    }

    public void listener() throws IOException{ // Listen for messages from host
        if(busy == true){
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            String recievedMessage = dis.readUTF();
            System.out.println("["+host+"]: " + recievedMessage);
            listener();
        }
    }

    public void sender() throws IOException{ // Interface for communication between host
        String message = System.console().readLine();
        if (message.equals("disconnect")){
            // Disconnect
            disconnect();
        }else{
            sendMessage(message);
        }
        System.out.println("[You]: " + message);
        sender();
    }

    public void connect(String host, int port){ // Connect to host
        try{
            socket = new Socket(host, port);
            dout = new DataOutputStream(socket.getOutputStream());
            busy = true;
        }catch (Exception e) {
            System.out.println("Failed to connect... Host may not be using gChat or currently speaking to someone");
        }
    }

    public void sendMessage(String message) throws IOException { // Send message to host
        dout.writeUTF(message);
    }

    public void disconnect() throws IOException{ // Disconnect from host
        dout.flush();
        dout.close();
        socket.close();
        busy = false;
    }
}
