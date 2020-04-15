package com.example.parserxml;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String URL = "https://xml-data.000webhostapp.com/buku.xml";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new DownloadXmlTask().execute(URL);
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return "IOException : "+e.getMessage();
            } catch (XmlPullParserException e) {
                return "XmlPullParserException : "+e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            setContentView(R.layout.activity_main);
            TextView tv = (TextView) findViewById(R.id.textView);
            tv.setText("DAFTAR BUKU\n");
            tv.setText(tv.getText() + result);
            Log.d("DEBUG", result);
        }
    }

    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        // Instantiate the parser
        BukuXmlParser bukuXmlParser = new BukuXmlParser();
        List<Buku> bukus = null;
        String res = "";

        try {
            stream = downloadUrl(urlString);
            bukus = bukuXmlParser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        for (Buku buku : bukus){
            String ISBN = buku.kode;
            String judul = buku.judul;
            String pengarang = buku.pengarang;
            res += "\nISBN :" + ISBN;
            res += "\nJudul :" + judul;
            res += "\nPengarang :" + pengarang;
        }

        return res;
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        java.net.URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}
