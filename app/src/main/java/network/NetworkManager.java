package network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import model.Task;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkManager {

    //to handle http connection
    private static final OkHttpClient HTTP_CLIENT=new OkHttpClient();

    //urls
    private static final String IP="10.10.1.29";
    private static final String URL="http://"+IP+":8080/WorkServer/";
    private static final String URL_SERVER_RECIEVE=URL+"recieve_task";

    public static String sendTasks(List<Task> tasks) throws JSONException, IOException {

        //packager for tasks to JSON
        JSONObject tasks_to_send=new JSONObject();

        ArrayList<Task> not_sent_tasks=new ArrayList<>();
        //get all tasks not sent
        for(Task t:tasks){
            if(t.isTask_sent()==false){
                not_sent_tasks.add(t);
            }
        }

        //put tasks not sent to tasks_to_send
        tasks_to_send.putOpt("tasks",packageTaskToJSonArray(not_sent_tasks));

        //generate Request to send
        RequestBody requestBody=new FormBody.Builder().add("tasks",tasks_to_send.toString())
                .build();
        Request request=new Request.Builder().url(URL_SERVER_RECIEVE).post(requestBody)
                .build();

        //get response from server
        Response response=HTTP_CLIENT.newCall(request).execute();
        return response.body().string();
    }

    private static JSONArray packageTaskToJSonArray(List<Task> tasks) throws JSONException {
        JSONArray jsonArray=new JSONArray();

        //get all tasks, turn them to jsonObject, and add it to JsonArray
        for(Task task:tasks){
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("task_id",task.getTask_id());
            jsonObject.put("task_date",task.getTask_date());
            jsonObject.put("task_text",task.getTask_text());
            jsonObject.put("task_sent",task.isTask_sent());
            jsonArray.put(jsonObject);
        }

        return jsonArray;
    }

    public static boolean checkIfHasInternet() throws IOException {
        InetAddress address=InetAddress.getByName(IP);
        if(address.isReachable(6000)){
            return true;
        }else{
            return false;
        }
    }
}
