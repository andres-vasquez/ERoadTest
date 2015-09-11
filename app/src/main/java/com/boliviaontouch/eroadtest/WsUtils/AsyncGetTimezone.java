package com.boliviaontouch.eroadtest.WsUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.boliviaontouch.eroadtest.Clases.TimezoneResponse;
import com.boliviaontouch.eroadtest.R;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresvasquez on 9/3/15.
 */
public class AsyncGetTimezone extends AsyncTask<String, Integer, TimezoneResponse> {

    static final String LOG = "AsyncGetTimezone";
    static final String RESULT_STATUS="OK";

    Context context;
    Receiver receiver;
    JSONParser jParser = new JSONParser();
    private ProgressDialog barProgressDialog;


    public AsyncGetTimezone(Context context, Receiver receiver) {
        this.context = context;
        this.receiver = receiver;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        barProgressDialog = new ProgressDialog(this.context);
        barProgressDialog.setTitle("Getting Timezone");
        barProgressDialog.setMessage("Please wait");
        barProgressDialog.setIndeterminate(true);
        barProgressDialog.show();
    }

    @Override
    protected TimezoneResponse doInBackground(String... args) {

        //API Reference: https://developers.google.com/maps/documentation/timezone/intro
        String url = Parameters.URL_TIMEZONE_API;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("location", args[0]));
        params.add(new BasicNameValuePair("timestamp", args[1]));
        params.add(new BasicNameValuePair("key", context.getResources().getString(R.string.google_timezone_api)));

        TimezoneResponse result=null;

        try
        {
            JSONObject json = jParser.makeHttpRequest(url, "GET", params);
            TimezoneResponse jsonResult=new Gson().fromJson(json.toString(), TimezoneResponse.class);
            Log.e("El resultado en JSON: ", json.toString());

            if(jsonResult.getStatus().compareTo(RESULT_STATUS)==0)
                result=jsonResult;
        }
        catch (NullPointerException e)
        {
            Log.e(LOG, "Error WS Routing" + e.toString());
        }
        catch (Exception e)
        {
            Log.e(LOG, "Error WS" + e.toString());
        }

        return result;
    }

    @Override
    protected void onPostExecute(TimezoneResponse response) {
        super.onPostExecute(response);

        if(barProgressDialog.isShowing())
            barProgressDialog.dismiss();
        receiver.onLoad(response);
    }

    public interface Receiver {
        void onLoad(TimezoneResponse timezoneResponse);
    }
}
