package com.bakbijbel.bakbijbel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutionException;

public class AddReceptActivity extends AppCompatActivity{
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    TextView receptTitle;
    TextView receptBereidingswijze;
    TextView receptIngredienten;
    Button addPicture;
    Button saveRecept;
    ImageView aimageView;
    Bitmap photo;
    String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_recept);
        receptTitle = findViewById(R.id.recept_title);
        receptBereidingswijze = findViewById(R.id.recept_bereidingswijze);
        receptIngredienten = findViewById(R.id.recept_ingredienten);
        addPicture = findViewById(R.id.add_picture);
        aimageView = findViewById(R.id.imageView3);
        saveRecept  = findViewById(R.id.recept_save);

        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPicture();
            }
        });
        saveRecept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Save();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void AddPicture()
    {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }
    void Save() throws ExecutionException, InterruptedException {
        //String output = new AsyncTaskRunnerAddRecept(photo).execute("http://10.0.2.2:8000/api/recept").get();
        //String output = new AsyncUploadPicture(path).execute("http://10.0.2.2:8000/api/fileupload").get();
        //Log.println(Log.DEBUG, "MINE", output);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            aimageView.setImageBitmap(photo);
        }
    }

 //  public Uri getImageUri(Context inContext, Bitmap inImage) {
 //      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
 //      inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
 //      String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
 //      return Uri.parse(path);
 //  }

 // public String getRealPathFromURI(Uri uri) {
 //     Cursor cursor = getContentResolver().query(uri, null, null, null, null);
 //     cursor.moveToFirst();
 //     int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
 //     return cursor.getString(idx);
 // }


 // private class AsyncUploadPicture extends AsyncTask<String, Void, String> {
 //     String sourceFileUri = "";

 //     public AsyncUploadPicture(String picture) {
 //         this.sourceFileUri = picture;
 //     }

 //     @Override
 //     protected String doInBackground(String... params) {

 //         try {

 //             HttpURLConnection conn = null;
 //             DataOutputStream dos = null;
 //             String lineEnd = "\r\n";
 //             String twoHyphens = "--";
 //             String boundary = "*****";
 //             int bytesRead, bytesAvailable, bufferSize;
 //             byte[] buffer;
 //             int maxBufferSize = 1 * 1024 * 1024;
 //             File sourceFile = new File(sourceFileUri);

 //             if (sourceFile.isFile()) {

 //                 try {
 //                     String upLoadServerUri = "http://10.0.2.2:8000/api/fileupload";

 //                     // open a URL connection to the Servlet
 //                     FileInputStream fileInputStream = new FileInputStream(
 //                             sourceFile);
 //                     URL url = new URL(upLoadServerUri);

 //                     // Open a HTTP connection to the URL
 //                     conn = (HttpURLConnection) url.openConnection();
 //                     conn.setDoInput(true); // Allow Inputs
 //                     conn.setDoOutput(true); // Allow Outputs
 //                     conn.setUseCaches(false); // Don't use a Cached Copy
 //                     conn.setRequestMethod("POST");
 //                     conn.setRequestProperty("Connection", "Keep-Alive");
 //                     conn.setRequestProperty("ENCTYPE",
 //                             "multipart/form-data");
 //                     conn.setRequestProperty("Content-Type",
 //                             "multipart/form-data;boundary=" + boundary);
 //                     conn.setRequestProperty("photo", sourceFileUri);

 //                     dos = new DataOutputStream(conn.getOutputStream());

 //                     dos.writeBytes(twoHyphens + boundary + lineEnd);
 //                     dos.writeBytes("Content-Disposition: form-data; name=\"bill\";filename=\""
 //                             + sourceFileUri + "\"" + lineEnd);

 //                     dos.writeBytes(lineEnd);

 //                     // create a buffer of maximum size
 //                     bytesAvailable = fileInputStream.available();

 //                     bufferSize = Math.min(bytesAvailable, maxBufferSize);
 //                     buffer = new byte[bufferSize];

 //                     // read file and write it into form...
 //                     bytesRead = fileInputStream.read(buffer, 0, bufferSize);

 //                     while (bytesRead > 0) {

 //                         dos.write(buffer, 0, bufferSize);
 //                         bytesAvailable = fileInputStream.available();
 //                         bufferSize = Math
 //                                 .min(bytesAvailable, maxBufferSize);
 //                         bytesRead = fileInputStream.read(buffer, 0,
 //                                 bufferSize);

 //                     }

 //                     // send multipart form data necesssary after file
 //                     // data...
 //                     dos.writeBytes(lineEnd);
 //                     dos.writeBytes(twoHyphens + boundary + twoHyphens
 //                             + lineEnd);

 //                     // Responses from the server (code and message)
 //                     int serverResponseCode = conn.getResponseCode();
 //                     String serverResponseMessage = conn
 //                             .getResponseMessage();

 //                     if (serverResponseCode == 200) {

 //                         // messageText.setText(msg);
 //                         //Toast.makeText(ctx, "File Upload Complete.",
 //                         //      Toast.LENGTH_SHORT).show();

 //                         // recursiveDelete(mDirectory1);

 //                     }

 //                     // close the streams //
 //                     fileInputStream.close();
 //                     dos.flush();
 //                     dos.close();

 //                 } catch (Exception e) {

 //                     // dialog.dismiss();
 //                     e.printStackTrace();

 //                 }
 //                 // dialog.dismiss();

 //             } // End else block


 //         } catch (Exception ex) {
 //             // dialog.dismiss();

 //             ex.printStackTrace();
 //         }
 //         return "Executed";
 //     }
 // }
}


