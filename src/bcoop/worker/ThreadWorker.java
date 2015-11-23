/**
 * <p>Title: ThreadWorker.java</p>
 * <p>Description: A base class that can be extended to provide a loop that runs in a thread</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.worker;

/**
 * @author pmarches
 *
 */
public abstract class ThreadWorker extends Thread{
    private volatile boolean keepRunning;
    
    public ThreadWorker() {
    }
    public ThreadWorker(String name) {
        super(name);
    }

    public void start(){
        this.keepRunning = true;
        super.start();
    }

    synchronized public void shutdown() {
        try{
            keepRunning = false;
            notifyAll();
            join();
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
    }
    
    synchronized public void run(){
        while(keepRunning){
            execute();
        }
    }

    public void stopCallingExecute() {
        keepRunning = false;        
    }

    abstract public void execute();
}
