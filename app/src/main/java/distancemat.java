import android.os.AsyncTask;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by john rarui on 10/9/2017.
 */

public class distancemat extends AsyncTask<String,Void,String> {
    private String ad,pickup;
    private static final String API_KEY = "AIzaSyCXEPdkGIjQJVXtfZOQwJuIMdlbNWyBEX8";

    @Override
    protected String doInBackground(String... url) {

        String url_request = "https://maps.googleapis.com/maps/api/distancematrix/json?" +
                "origins="+ad+
                "&destinations="+pickup+
                "&mode=driving" +
                "&key=" + API_KEY;
        try {
            String response=this.run(url_request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
        return null;
    }
    public String run(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
    protected void onPostExecute(String message) {
        //process message
    }



}
