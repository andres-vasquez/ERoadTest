package com.boliviaontouch.eroadtest.WsUtils;

/**
 * Created by andresvasquez on 3/28/15.
 */

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    // constructor
    public JSONParser() {

    }

    //Recibe la url, el tipo de método y los paramétros
    public JSONObject makeHttpRequest(String url, String method,
                                      List<NameValuePair> params)
    {

        //Hace una petición HTTP
        try {

            //Si el método es POST
            if(method == "POST")
            {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setHeader("Content-type", "application/json");
                httpPost.setHeader("Accept", "application/json");

                JSONObject obj = new JSONObject();
                try {
                    for(NameValuePair param : params)
                        obj.put(param.getName(), param.getValue());
                }
                catch (Exception e){
                    Log.e("ERROR", "Creando Json" + e.getMessage());}
                //httpPost.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
                httpPost.setEntity(new StringEntity(obj.toString(), "UTF-8"));

                //Inicializa la respuesta en null
                HttpResponse httpResponse=null;
                //Hace el POST
                httpResponse = httpClient.execute(httpPost);
                //Obtiene una respuesta
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }
            else if(method == "GET")
            {
                //Envía los parámetros via GET. url?clave=valor
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);
                //Obtiene una respuesta
                HttpResponse httpResponse=null;
                httpResponse= httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            //Hace una lectira de la respuesta
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            //Guarda todos los resultados en un string
            StringBuilder sb = new StringBuilder();
            String line = null;
            //Lee línea por linea el resultado
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
            json=null;
        }

        //Convierte la string de resultado en un objeto Jsan
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
            Log.e("JSON Parser", "Error parsing data " + json.toString().replace(" ", ""));
        }
        //Devuelve el objeto Json
        return jObj;

    }
}
