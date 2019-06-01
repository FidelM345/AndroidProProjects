package com.example.thebeast.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class MyService extends Service {
    public MyService() {
    }



    final  class MyThreadClass implements Runnable{

        int service_id;

        public MyThreadClass(int service_id) {
            this.service_id = service_id;
        }

        @Override
        public void run() {

            int i=0;

            synchronized (this){


                while (i<10){


                    try {
                        wait(1500);
                        i++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                stopSelf(service_id);
            }


        }
    }






    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //used to implement the on starter service
        Toast.makeText(MyService.this,"Service started....",Toast.LENGTH_LONG).show();

        Thread thread=new Thread(new MyThreadClass(startId));
        thread.start();//starts the service on a different thread not the main ui thread





        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        //used for implementing the onBind service an it is the mandatory
        //method that must be overriden on all service classes.

        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //used for destroying the service
        Toast.makeText(MyService.this,"Service destroyed....",Toast.LENGTH_LONG).show();

    }
}
