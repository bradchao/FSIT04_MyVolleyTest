package tw.org.iii.myvolleytest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sketchproject.infogue.modules.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    private TextView cont;
    private ImageView img;
    private File sdroot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    0);

        }else{
            init();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            init();
        }
    }

    private void init(){
        sdroot = Environment.getExternalStorageDirectory();

        cont = findViewById(R.id.main_cont);
        img = findViewById(R.id.main_img);

        requestQueue = Volley.newRequestQueue(this);
    }



    public void test1(View view) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "http://www.google.com",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("brad", response);
                        cont.setText(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String mesg = error.getMessage();
                NetworkResponse resp = error.networkResponse;
                int statusCode = resp.statusCode;

                Log.v("brad", "error:" + mesg + "" +
                        ":" + statusCode);
            }
        });
        requestQueue.add(stringRequest);
    }

    public void test2(View view) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "http://data.coa.gov.tw/Service/OpenData/ODwsv/ODwsvTravelFood.aspx",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.v("brad", response);
                        //cont.setText(response);

                        parseJSON(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String mesg = error.getMessage();
                NetworkResponse resp = error.networkResponse;
                int statusCode = resp.statusCode;

                Log.v("brad", "error:" + mesg + "" +
                        ":" + statusCode);
            }
        });
        requestQueue.add(stringRequest);

    }

    private void parseJSON(String json) {
        try {
            JSONArray root = new JSONArray(json);
            for (int i=0; i<root.length(); i++){
                JSONObject row = root.getJSONObject(i);
                String name = row.getString("Name");
                String addr = row.getString("Address");
                Log.v("brad", name + ":" + addr);
            }

        }catch (Exception e){
            Log.v("brad", e.toString());
        }
    }

    public void test3(View view) {
        ImageRequest imageRequest = new ImageRequest(
"https://storage.googleapis.com/gweb-uniblog-publish-prod/images/android_ambassador_v1_cmyk_200px.max-2800x2800.png",
                new Response.Listener<Bitmap>(){
            @Override
            public void onResponse(Bitmap response) {
                img.setImageBitmap(response);
            }
        },0,0, ImageView.ScaleType.CENTER,
        Bitmap.Config.ARGB_8888,
        null);

        requestQueue.add(imageRequest);

    }

    // post
    public void test4(View view) {
        String url = "https://www.bradchao.com/iii/brad02.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("brad", response);
                        cont.setText(response);

                    }
                }, null){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                params.put("account", "bradiii");
                params.put("passwd", "123456");

                return params;
            }
        };

        requestQueue.add(stringRequest);



    }

    // post + upload
    public void test5(View view) {
        String uploadUrl = "https://www.bradchao.com/iii/brad03.php";

        File uploadFile = new File(sdroot, "brad.txt");
        final byte[] data = new byte[(int) uploadFile.length()];
        try {
            FileInputStream fin = new FileInputStream(uploadFile);
            fin.read(data);
            fin.close();
        }catch (Exception e){

        }


        VolleyMultipartRequest multipartRequest =
                new VolleyMultipartRequest(
                        Request.Method.POST,
                        uploadUrl,
                new Response.Listener<NetworkResponse>(){
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Log.v("brad", "code: " + response.statusCode);
                    }
                },
                null){
                    @Override
                    protected Map<String, DataPart> getByteData()
                            throws AuthFailureError {

                        HashMap<String,DataPart> params = new HashMap<>();
                        params.put("upload",
                                new DataPart("iii01.txt", data));

                        return params;
                    }
                };

        requestQueue.add(multipartRequest);
    }

}
