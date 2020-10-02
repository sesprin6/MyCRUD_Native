package com.mycrud.library;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

public class RequestHandler
{
    public static String sendPostRequest(String requestURL, HashMap<String, String> postData)
    {
        URL url;

        StringBuilder builder = new StringBuilder();
        try
        {
            url = new URL(requestURL);

            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();

            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream stream = connection.getOutputStream();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8));
            writer.write(getPostDataString(postData));

            writer.flush();
            writer.close();
            stream.close();

            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK)
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                builder = new StringBuilder();
                String response;

                while ((response = reader.readLine()) != null)
                    builder.append(response);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static String sendGetRequest(String requestURL)
    {
        StringBuilder builder = new StringBuilder();
        try
        {
            URL url = new URL(requestURL);
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response;

            while ((response = reader.readLine()) != null)
                builder.append(response).append('\n');
        }
        catch (Exception ignored) {}
        return builder.toString();
    }

    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException
    {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet())
        {
            if (first)
                first = false;
            else
                builder.append("&");

            builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            builder.append('=');
            builder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return builder.toString();
    }
}
