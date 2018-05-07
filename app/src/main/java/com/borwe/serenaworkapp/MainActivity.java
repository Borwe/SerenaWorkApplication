package com.borwe.serenaworkapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import database.AppDatabase;
import model.Task;
import network.NetworkManager;

public class MainActivity extends AppCompatActivity {

    EditText task_input;
    Button add_task_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        task_input=findViewById(R.id.task_input);
        add_task_button=findViewById(R.id.add_task_button);

        add_task_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UserSubmit().execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sync:
                new CheckAndSendTask().execute();
                break;

        }
        return true;
    }

    class CheckAndSendTask extends AsyncTask<Void,Void,Void>{
        ProgressDialog dialog;

        //to hold error message;
        String error_message=null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(MainActivity.this);
            dialog.setMessage("Syncing...");
            dialog.setTitle("Please wait");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //get all tasks current on waiting
            List<Task> tasks=AppDatabase.getAppDatabase(MainActivity.this).taskDao().getTasks();

            //check if tasks are available for sending
            boolean tasks_can_send=false;
            for(Task t:tasks){
                if(t.isTask_sent()==false){
                    tasks_can_send=true;
                }
            }

            //if no tasks to send, save error
            if(tasks_can_send==false){
                error_message="Sorry no tasks to send, please add one before trying to send again";
                return null;
            }

            //check if user has network access
            try {
                boolean network_works= NetworkManager.checkIfHasInternet();
                if(network_works==false){
                    throw new IOException();
                }
            } catch (IOException e) {
                e.printStackTrace();
                error_message="Sorry, no network access, please retry when you have one.";
                return null;
            }

            //if network exists then send tasks data to server and wait for reply
            try {
                String reply=NetworkManager.sendTasks(tasks);
                if(reply==null){
                    throw new RuntimeException();
                }
            } catch (Exception e) {
               error_message="Sorry, something went wrong, data not sent to server fully";
               e.printStackTrace();
               Log.e("FUCK_ERROR",e.getMessage());
               return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.hide();
            dialog.dismiss();

            //if error message not null, then show it
            if(error_message!=null){
                AlertDialog.Builder error=new AlertDialog.Builder(MainActivity.this);
                error.setTitle("Error:");
                error.setMessage(error_message);
                error.setPositiveButton("Okay, let me try",null);
                error.setCancelable(false);
                error.create().show();
            }
        }
    }

    class UserSubmit extends AsyncTask<Void,Void,Void>{
        ProgressDialog dialog;

        //show error messages;
        String error_message=null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(MainActivity.this);
            dialog.setMessage("Loading...");
            dialog.setTitle("Please wait");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //get input from task_input
            String input=task_input.getText().toString().trim();
            if(input==null || input.length()<1){
                error_message="Please enter something in your task input,then retry\nThank You.";
                return null;
            }

            //get time of message
            Calendar date=Calendar.getInstance();
            String time=date.getTime().toString();

            Task task=new Task(time,input);
            AppDatabase.getAppDatabase(MainActivity.this).taskDao().putTask(task);

            //testing to see DB
            List<Task> tasks=AppDatabase.getAppDatabase(MainActivity.this).taskDao().getTasks();
            Log.d("FUCKING_TASKS",tasks.toString());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.hide();
            dialog.dismiss();

            //if error message not null, then show it
            if(error_message!=null){
                AlertDialog.Builder error=new AlertDialog.Builder(MainActivity.this);
                error.setTitle("Error:");
                error.setMessage(error_message);
                error.setPositiveButton("Okay, let me try",null);
                error.setCancelable(false);
                error.create().show();
            }
        }
    }
}
