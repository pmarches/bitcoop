/**
 * <p>Title: LoopSocketFactory.java</p>
 * <p>Description: A loopback pair of sockets userfull for testing.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class LoopSocketFactory extends Thread{
    public static final int DEFAULT_PORT = 8475;
    private volatile boolean ready = false;
    private Socket socket;
    
    public LoopSocketFactory(){
        start();
    }
    
    synchronized public void start(){
        super.start();
        try {
            while(!ready){
                wait(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void run(){
        try {
            ServerSocket sScoket = new ServerSocket(DEFAULT_PORT);
            ready = true;
            synchronized(this){
                notifyAll();
            }
            socket = sScoket.accept();
            sScoket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        try{
            if(socket == null){
                Socket s = new Socket("127.0.0.1", DEFAULT_PORT);
                join();
                return s;
            }
            return socket;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
