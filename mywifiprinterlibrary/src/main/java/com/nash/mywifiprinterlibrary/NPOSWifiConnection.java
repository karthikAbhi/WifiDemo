package com.nash.mywifiprinterlibrary;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class NPOSWifiConnection {

    private static NPOSWifiConnection sNPOSWifiConnection;

    private static final String TAG = "sNPOSWifiConnection";
    private static Socket mSocket;
    private static BufferedOutputStream out;
    private static BufferedInputStream in;
    public static RWThread t;
    private static String mHost;
    private static int mPort;
    private Context mContext;
    private boolean flag = false;

    /**
     * Singleton Constructor
     * @param context
     * @param host
     * @param port
     */
    private NPOSWifiConnection(Context context,String host, int port) {
        mContext = context;
        mHost = host;
        mPort = port;
        new InitializeConnection().execute();
        t = new RWThread();
    }

    /**
     * Singleton Pattern
     * @param context
     * @param host
     * @param port
     * @return
     */
    public synchronized static NPOSWifiConnection getInstance(Context context, String host, int port) {
        if (sNPOSWifiConnection == null) {
            sNPOSWifiConnection = new NPOSWifiConnection(context, host, port);
        }
        return sNPOSWifiConnection;
    }

    /**
     * Initialize the Wifi Socket Connection
     */
    private class InitializeConnection extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            //sNPOSWifiConnection.connect();
            try {
                if (mSocket == null) {

                    mSocket = new Socket(mHost, mPort);
                    // Print the Address info of the Device.
                    InetAddress inetSocketAddress = mSocket.getLocalAddress();
                    Log.i(TAG, inetSocketAddress.toString());
                }
                if(out == null) {
                    out = new BufferedOutputStream(mSocket.getOutputStream());
                }
                if(in == null) {
                    in = new BufferedInputStream(mSocket.getInputStream());
                }

                while(true){
                    t.read();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class RWThread {

        boolean flag = true;

        /**
         * Reader Method - Reads information from the inputstream
         * Printer acknowledgement is read for sending the next packet.i.e.,"PRCDONE"
         */
        public synchronized void read(){
            if(flag){
                try{
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Reader Thread\n");
            try {
                int bytesread = 0;
                String builder = "";
                byte[] prcdone = new byte[10];

                if((bytesread  = in.available()) != 0){

                    in.mark(7);
                    in.read(prcdone, 0, 10);

                    for(byte tmp : prcdone){
                        builder += (char)tmp;
                    }

                    if(builder.contains("PRCDONE")){
                        flag = true;
                    }

                    System.out.println(builder);
                }

                System.out.println("Read buffer length = " + bytesread + "\n");

            } catch (IOException e) {
                e.printStackTrace();
            }
            notify();
        }

        /**
         * Writer Method - Writes information to the outputstream
         * @param bytes
         */
        public synchronized void write(final byte[] bytes){

            if(!flag){
                try{
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Writer Thread");

            try {

                int totallength = bytes.length;

                if (totallength < 500) {
                    out.write(bytes);
                    out.flush();
                    flag = false;
                }
                else {

                    int offset = 0;
                    int length = 500;
                    int rem = totallength;

                    while (totallength != offset) {

                        out.write(bytes, offset, length);
                        out.flush();

                        offset += length;

                        rem = rem - length;

                        if (rem < length) {
                            length = rem;
                            rem = 0;
                        }
                        if(rem == 0){
                            flag = true;
                        }
                        else{
                            flag = false;
                            notify();
                            wait();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            notify();
        }
    }
}