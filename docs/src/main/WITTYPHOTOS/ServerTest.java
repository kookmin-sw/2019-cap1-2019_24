//package com.example.WITTYPHOTOS;
//
//
//import android.app.Activity;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.StrictMode;
//import android.provider.MediaStore;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import java.io.DataOutputStream;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class ServerTest extends AppCompatActivity {
//    ImageView imageView = null;
//    Button button = null;
//    private final int REQ_CODE_SELECT_IMAGE = 100;
//    private String img_path = new String();
//    private String serverURL = ""; //서버주소
//    private Bitmap image_bitmap_copy = null;
//    private Bitmap image_bitmap = null;
//    private String imageName = null;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_tag_add);
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//        .permitDiskReads()
//        .permitDiskWrites()
//        .permitNetwork().build());
//
//
//        imageView = (ImageView) findViewById(R.id.imageView);
//        // 이미지를 띄울 위젯
//        imageView.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
//            }
//        });
//
//        button = (Button) findViewById(R.id.btn_input);
//        // 이미지 전송 버튼
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DoFileUpload(serverURL, img_path);
//                Toast.makeText(getApplicationContext(), "이미지 전송 성공",
//                        Toast.LENGTH_SHORT).show();
//                Log.d("Send", "Success");
//            }
//        });
//
//    }
//
//
//
//    @Override
//    protected void onActicityResult(int requestCode, int resultCode, Intent data) {
//
//        Toast.makeText(getBaseContext(), "resultCode : " + data, Toast.LENGTH_SHORT).show();
//
//        if (requestCode == REQ_CODE_SELECT_IMAGE) {
//            if (resultCode == Activity.RESULT_OK) {
//                try {
//                    img_path = getImagePathToUri(data.getData());
//                    Toast.makeText(getBaseContext(), "img_path : " + img_path, Toast.LENGTH_SHORT).show();
//
//                    image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
//
//                    int reWidth = (int) (getWindowManager().getDefaultDisplay().getWidth());
//                    int reHeight = (int) (getWindowManager().getDefaultDisplay().getHeight());
//
//                    image_bitmap_copy = Bitmap.createScaledBitmap(image_bitmap, 400, 300, true);
//                    ImageView image = (ImageView) findViewById(R.id.imageView);
//                    image.setImageBitmap(image_bitmap_copy);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    public String getImagePathToUri(Uri data) {
//        // 사용자가 선택한 이미지의 정보를 받아옴
//        String[] proj = {MediaStore.Images.Media.DATA};
//        Cursor cursor = managedQuery(data, proj, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//
//        // 이미지 경로 값
//        String imgpath = cursor.getString(column_index);
//        Log.d("test", imgpath);
//
//        // 이미지 이름 값
//        String imgName = imgpath.substring(imgpath.lastIndexOf("/") + 1);
//        Toast.makeText(ServerTest.this, "이미지 이름 : " +imgName, Toast.LENGTH_SHORT).show();
//        this.imageName = imgName;
//
//        return imgpath;
//    }
//
//    public void DoFileUpload(String apiUrl, String absolutePath) {
//        HttpFileUpload(apiUrl, "", absolutePath);
//    }
//
//    String lineEnd = "\r\n";
//    String twoHyphens = "- -";
//    String boundary = "*****";
//
//    public void HttpFileUpload(String urlString, String params, String fileName) {
//
//        try {
//
//            FileInputStream mFileInputStream = new FileInputStream(fileName);
//            URL connectUrl = new URL(urlString);
//            Log.d("Test", "mFileInputStream is " + mFileInputStream);
//
//            // HttpURLConnection 통신
//            HttpURLConnection connection = (HttpURLConnection) connectUrl.openConnection();
//            connection.setDoInput(true);
//            connection.setDoOutput(true);
//            connection.setUseCaches(false);
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Connection", "Keep-Alive");
//            connection.setRequestProperty("Content-type", "multipart/form-data;boundary=" + boundary);
//
//            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
//            dos.writeBytes(twoHyphens + boundary + lineEnd);
//            dos.writeBytes("Content-Disposition: form-data; name =\"uploadedfile\";filename=\"" + fileName);
//            dos.writeBytes(lineEnd);
//
//            int bytesAvailable = mFileInputStream.available();
//            int maxBufferSize = 1024;
//            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
//
//            byte[] buffer = new byte[bufferSize];
//            int bytesRead = mFileInputStream.read(buffer, 0, bufferSize );
//
//            Log.d("Test", "image byte is " + bytesRead);
//
//            while (bytesRead > 0) {
//                dos.write(buffer, 0, bufferSize);
//                bytesAvailable = mFileInputStream.available();
//                bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
//
//            }
//
//            dos.writeBytes(lineEnd);
//            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//
//            Log.e("Test", "File is written");
//            mFileInputStream.close();
//            dos.flush();
//
//            InputStream is = connection.getInputStream();
//
//            StringBuffer b = new StringBuffer();
//            for (int ch = 0; (ch = is.read()) != -1;) {
//                b.append((char) ch);
//            }
//            is.close();
//            Log.e("Test", b.toString());
//        } catch (Exception e) {
//            Log.d("Test", "exception" + e.getMessage());
//        }
//    }
//

//}
