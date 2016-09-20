package de.daalx.javatestserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/* Server starten: "new TestServer().start();"  */
public class TestServer extends Thread {
    
    private static final boolean DEBUG = true;
    private static final int PORT = 80;
    
    @Override
    public void run() {
        startServer(PORT);
    }
    
    public static void startServer (int port) {
        try {          
            ServerSocket serverSocket = new ServerSocket(port);
            while(true) {
                Socket clientSocket = serverSocket.accept();
                InputStreamReader inputReader = new InputStreamReader(clientSocket.getInputStream());
                BufferedReader bufferReader = new BufferedReader(inputReader);                
                OutputStreamWriter outputWriter = new OutputStreamWriter(clientSocket.getOutputStream());
                BufferedWriter bufferWriter = new BufferedWriter(outputWriter);
                String inputLine;
                String header = "";
                int count = 0;

                // Request einlesen
                while((inputLine = bufferReader.readLine()) != null && inputLine.length() > 0 && count < 1) {
                    count++;
                    header += inputLine + "\n";
                }                
                if(DEBUG) {
                    System.out.println("----- TestServer (Begin of Request) -----");
                    System.out.println(header);
                    System.out.println("------ TestServer (End of Request) ------");
                }
                doResponse(header, bufferWriter);

                inputReader.close();
                outputWriter.close();
                clientSocket.close();
               // serverSocket.close();
            }
        } 
        catch (IOException ex) {
            Logger.getLogger(TestServer.class.getName()).log(Level.SEVERE, null, ex);            
        }        
    }
    
    private static void doResponse(String request, BufferedWriter bufferedWriter) throws IOException {    
        if (request.contains("GET /?test")) {            
            create200Header(bufferedWriter);            
            bufferedWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><message>This is a test</message>\n");
            bufferedWriter.flush();
        }
        else {
            bufferedWriter.write("HTTP/1.1 400\n");
            bufferedWriter.write("\n");
            bufferedWriter.flush();
            if(DEBUG) {
              System.out.println("----- TestServer (Request wurde nicht erkannt) -----");
            }       
        }
    }
    
    private static void create200Header(BufferedWriter bufferedWriter) throws IOException {
        bufferedWriter.write("HTTP/1.1 200 OK\n");
        bufferedWriter.write("Content-Type: text/xml\n");
        bufferedWriter.write("Server: TestServer 1.0\n");
        bufferedWriter.write("\n");
    }
    
}

