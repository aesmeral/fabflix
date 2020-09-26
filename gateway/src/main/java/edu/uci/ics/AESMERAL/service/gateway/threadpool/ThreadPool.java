package edu.uci.ics.AESMERAL.service.gateway.threadpool;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool
{
    private int numWorkers;

    private ArrayList<Worker> workers;
    private BlockingQueue<ClientRequest> queue;

    /*
     * BlockingQueue is a interface that allows us
     * to choose the type of implementation of the queue.
     * In this case we are using a LinkedBlockingQueue.
     *
     * BlockingQueue as the name implies will block
     * any thread requesting from it if the queue is empty
     * but only if you use the correct function
     */
    private ThreadPool(int numWorkers)
    {
        this.numWorkers = numWorkers;

        workers = new ArrayList<>();
        queue = new LinkedBlockingQueue<>();

        // TODO more work is needed to create the threads
            for (int i = 0; i < numWorkers; i++) {
                workers.add(Worker.CreateWorker(i, this));    // create our worker threads
            }
            for(int i = 0; i < numWorkers; i++){
                workers.get(i).start();
            }
    }

    public static ThreadPool createThreadPool(int numWorkers)
    {
        return new ThreadPool(numWorkers);
    }

    /*
     * Note that this function only has package scoped
     * as it should only be called with the package by
     * a worker
     * 
     * Make sure to use the correct functions that will
     * block a thread if the queue is unavailable or empty
     */
    private ClientRequest takeRequest()
    {
        // TODO *take* the request from the queue
        try {
            return queue.take();
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        return null;
    }

    // wrapper function to synchronize.
    public synchronized ClientRequest getRequest(){
        return takeRequest();
    }

    public void putRequest(ClientRequest request)
    {
        try {
            queue.put(request);                             // put the request into the blockingQueue
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        // find a thread to wake up that is currently waiting.
        // maybe do a while loop? counter % workers.getSize() until a request opens up.
        // find an available worker.
        for(int i = 0; i < numWorkers; i++){
            if(workers.get(i).getState().equals(Thread.State.WAITING)){
                synchronized (workers.get(i)) {
                    workers.get(i).notify();
                    break;
                }
            }
        }
    }

}
