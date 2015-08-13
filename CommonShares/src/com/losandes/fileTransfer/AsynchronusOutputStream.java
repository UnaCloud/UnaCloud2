/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.losandes.fileTransfer;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Clouder
 */
public class AsynchronusOutputStream extends FilterOutputStream{

    /**
     * Queue of byte arrays to be writed on this Managed ouputStream
     */
    private final ArrayBlockingQueue<Buffer> buffers;
    /**
     * Variable that determines if this output stream is closed or not
     */
    private volatile boolean closed;
    /**
     * Variable that determines if there was an error writing on the underlying output stream
     */
    private volatile boolean exception;
    /**
     * Exception thrown by the underlying outputstream
     */
    private IOException excep;

    /**
     * Constructs a new AsynchronusOutputStream. This method uses a new Thread that checks the state of the buffer queue and, if it is not empty, poll the first Buffer and writes it on the underlying socket.
     * @param os The underlying socket to be managed
     * @param size The maximum size of the queue to be used by this outpus stream
     */
    public AsynchronusOutputStream(OutputStream os,int size){
        super(os);
        buffers=new ArrayBlockingQueue<Buffer>(size,true);
        new Thread(){
            @Override
            public void run() {
                while(!exception){
                    try {
                        Buffer b = buffers.poll(10, TimeUnit.SECONDS);
                        if(b==null&&closed)break;
                        else if(b==null)continue;
                        try {
                            out.write(b.buffer, b.ini, b.length);
                            out.flush();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            excep=ex;
                            exception=true;
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(AsynchronusOutputStream.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                try {
                    out.close();
                } catch (IOException ex) {
                    excep=ex;
                    exception=true;
                }
            }
        }.start();
    }

    /**
     * Puts a new byte into the queue
     * @param b The byte to be writted
     * @throws IOException If there us a previous exception thrown by the underlying output stream
     */
    @Override
    public void write(int b) throws IOException {
        if(exception)throw excep;
        try {
            buffers.put(new Buffer(new byte[]{(byte) b}, 1));
        } catch (Exception ex) {
        }
    }

    /**
     * Puts a new byte array into the queue
     * @param b The byte array to be writed
     * @throws IOException If there us a previous exception thrown by the underlying output stream
     */
    @Override
    public void write(byte[] b) throws IOException {
        if(exception)throw excep;
        try {
            buffers.put(new Buffer(b,b.length));
        } catch (Exception ex) {
        }
    }

    /**
     * Puts a new byte array into the queue
     * @param b The byte array to be writed
     * @param off The offset to start reading bytes from the given byte array
     * @param len The number of bytes to be readed from the array
     * @throws IOException If there us a previous exception thrown by the underlying output stream
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if(exception)throw excep;
        try {
            buffers.put(new Buffer(b,off,len));
        } catch (Exception ex) {
        }
    }

    /**
     * Flushes this output stream
     * @throws IOException If there us a previous exception thrown by the underlying output stream
     */
    @Override
    public void flush() throws IOException {
        if(exception)throw excep;
    }

    /**
     * closed this output stream
     * @throws IOException If there us a previous exception thrown by the underlying output stream
     */
    @Override
    public void close() throws IOException {
        closed=true;
    }

    /**
     * Buffer that contains a byte array to be writed on a Asynchronus output stream
     */
    private class Buffer{
        byte[] buffer;
        int length,ini=0;

        public Buffer(byte[] buffer, int length) {
            this.buffer = buffer;
            this.length = length;
        }
        public Buffer(byte[] buffer, int off,int len) {
            this.buffer = buffer;
            this.length = len;
            this.ini=off;
        }

    }
}
