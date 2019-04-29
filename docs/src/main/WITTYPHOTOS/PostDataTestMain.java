//package com.example.WITTYPHOTOS;
//
//import android.content.ContentValues;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import org.w3c.dom.Text;
//
//public class PostDataTestMain extends AppCompatActivity {
//
//    private TextView tv_outPut;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_postdata);
//
//        tv_outPut = (TextView) findViewById(R.id.tv_outPut);
//
//        String url = "http://jangjieun.pythonanywhere.com/returnTag";
//
//        NetworkTask networkTask = new NetworkTask(url, null);
//        networkTask.execute();
//
//    }
//
//    public class NetworkTask extends AsyncTask<Void, Void, String> {
//
//        String url;
//        ContentValues values;
//
//        NetworkTask(String url, ContentValues values) {
//
//            this.url = url;
//            this.values = values;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(Void... Params) {
//
//            String result;
//            PostDataTest postDataTest = new PostDataTest();
//            result = postDataTest.request(url, values);
//
//            return result; // ㄱ결과값이 담기는 공간
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            tv_outPut.setText(result);
//
////            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
//        }
//    }
//}
