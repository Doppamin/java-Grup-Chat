import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    static ArrayList<ClientHandler>clientHandlers = new ArrayList<>();// Send message from each clients
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUserName;

    public ClientHandler(Socket socket){
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUserName = bufferedReader.readLine();
            clientHandlers.add(this);// ** add client in arraylist **
            broadcastMessage("SERVER: "+ clientUserName+ " has entered the chat !");
        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }

    }

    @Override
    public void run() {
        String messageFromClient;
        while(socket.isConnected()){
            try {
                messageFromClient =  bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            }catch (IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
                break;
            }
        }
    }
    void broadcastMessage(String messageToSend){
        for(ClientHandler clientHandler:clientHandlers){
            try{
                if(!clientHandler.clientUserName.equals(clientUserName)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }catch (IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
            }
        }
    }
    void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage("SERVER: "+clientUserName+ " has left chat ");
    }
    void closeEverything(Socket socket,BufferedReader bR,BufferedWriter bW){
        removeClientHandler();
        try {
            if(bufferedReader != null)bR.close();
            if(bufferedWriter != null)bW.close();
            if (socket != null)socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
