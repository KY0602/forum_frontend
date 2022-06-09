package com.example.hw.Post;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.hw.MainActivity;
import com.example.hw.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String LOG_TAG = PostActivity.class.getSimpleName();
    private static final String TAG = PostActivity.class.getSimpleName();
    private String KEYS = "KEYS";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private EditText postTitle, postMsg;
    private ImageButton postButton;
    private ImageView profile_pic_edit;
    private AppCompatActivity activity;
    private SharedPreferences pref;
    private Button btnCamera;
    private ImageView imageView;
    private String mCurrentFilePath;
    private String cur_location;
    private File file = null;
    private String posttype;
    private String user_id, draft_id;
    private double cur_lat = 10;
    private double cur_lon = 19;
    private HashSet<String> draftset;
    private TextView fileSizeView;
    String img_src;
    public static final int REQUEST_TAKE_PHOTO = 100;
    public static final int REQUEST_SELECT_PICTURE = 200;
    public static final int REQUEST_SELECT_VIDEO = 300;
    public static final int REQUEST_TAKE_VIDEO = 400;

    public PostActivity() {
        // require a empty public constructor
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "Destroy");
        saveDraft();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "Pause");
        //saveDraft();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View v = inflater.inflate(R.layout.fragment_post, container, false);
        super.onCreate(savedInstanceState);

        //tstLocation();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Log.d(LOG_TAG, formatter.format(new Date()));
        Date curDate = new Date(System.currentTimeMillis());
        Log.d(LOG_TAG, curDate.toString());
        Log.d("lat=", String.valueOf(cur_lat));
        Log.d("lon=", String.valueOf(cur_lon));
        setContentView(R.layout.fragment_post);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        posttype = extras.getString("type");
        user_id = extras.getString("user_id");
        Log.d("posttype", posttype);
        btnCamera = (Button) findViewById(R.id.postPhotoButton);
        fileSizeView = findViewById(R.id.filesize);
        profile_pic_edit = findViewById(R.id.postPic_edit);
        profile_pic_edit.setOnClickListener(this::chooseImage);
        if (posttype.equals("txtandimg")) {
            profile_pic_edit.setVisibility(View.VISIBLE);
            profile_pic_edit.setOnClickListener(this::chooseImage);
            btnCamera.setText("拍照");
        } else if (posttype.equals("audio")) {
            profile_pic_edit.setVisibility(View.VISIBLE);
            profile_pic_edit.setImageResource(R.drawable.img);
            profile_pic_edit.setOnClickListener(this::chooseAudio);
            btnCamera.setText("录音");
        } else if (posttype.equals("video")) {
            profile_pic_edit.setVisibility(View.VISIBLE);
            profile_pic_edit.setImageResource(R.drawable.img_2);
            profile_pic_edit.setOnClickListener(this::chooseVideo);
            btnCamera.setText("录像");
        }
        //imageView = (ImageView) findViewById(R.id.imageView);

        postTitle = (EditText) findViewById(R.id.postTitle);
        postMsg = (EditText) findViewById(R.id.postMsg);
        postButton = (ImageButton) findViewById(R.id.postButton);
        postButton.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        pref = getSharedPreferences("Draft", 0);
        loadDraft();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 카메라 촬영을 하면 이미지뷰에 사진 삽입
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                File file = new File(mCurrentFilePath);
                this.file = file;
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media
                            .getBitmap(getContentResolver(), Uri.fromFile(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (bitmap != null) {
                    Log.d("bitmap", bitmap.toString());
                    ExifInterface ei = null;
                    try {
                        ei = new ExifInterface(mCurrentFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);
                    Bitmap rotatedBitmap = null;
                    switch (orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(bitmap, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(bitmap, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(bitmap, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = bitmap;
                    }
                    Log.d("rotated", rotatedBitmap.toString());
                    //Rotate한 bitmap을 ImageView에 저장
                    profile_pic_edit.setImageBitmap(rotatedBitmap);


                }
            } else if (requestCode == REQUEST_TAKE_VIDEO) {
                File file = new File(mCurrentFilePath);
                this.file = file;
            } else if (requestCode == REQUEST_SELECT_PICTURE) {
                if (data == null) {
                    Log.d("", "exit");
                } else {
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        profile_pic_edit.setImageURI(selectedImageUri);

                        // Get actual file URI
                        String wholeID = DocumentsContract.getDocumentId(selectedImageUri);
                        String id = wholeID.split(":")[1];
                        String[] column = {MediaStore.Images.Media.DATA};
                        String sel = MediaStore.Images.Media._ID + "=?";

                        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                column, sel, new String[]{id}, null);

                        int columnIndex = cursor.getColumnIndex(column[0]);

                        if (cursor.moveToFirst()) {
                            img_src = cursor.getString(columnIndex);
                            Log.d(LOG_TAG, img_src);
                        }
                        cursor.close();

                        // Upload
                        file = new File(img_src);
                        //Log.d("file",file);
                    }
                }

            } else if (requestCode == REQUEST_SELECT_VIDEO) {
                Log.d(LOG_TAG, "video here");
                if (data == null) {
                    Log.d("", "exit");
                } else {
                    Uri selectedVideoUri = data.getData();
                    if (selectedVideoUri != null) {
                        //profile_pic_edit.setImageURI(selectedVideoUri);

                        // Get actual file URI
                        String wholeID = DocumentsContract.getDocumentId(selectedVideoUri);
                        String id = wholeID.split(":")[1];
                        String[] column = {MediaStore.Video.Media.DATA};
                        String sel = MediaStore.Video.Media._ID + "=?";

                        Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                column, sel, new String[]{id}, null);

                        int columnIndex = cursor.getColumnIndex(column[0]);

                        if (cursor.moveToFirst()) {
                            img_src = cursor.getString(columnIndex);
                            Log.d(LOG_TAG, img_src);
                        }
                        cursor.close();
                        Log.d("ing_src=", img_src);
                        // Upload
                        file = new File(img_src);

                        //fileSizeView.setText((int) file.length());
                        //Log.d("file",file);
                    }
                }
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void chooseImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, REQUEST_SELECT_PICTURE);
    }

    private void chooseAudio(View view) {
        MediaRecorder recorder = new MediaRecorder();
//设置音频资源的来源包括：麦克风，通话上行，通话下行等；程序中设定音频来源为麦克风

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//设置输出文件的格式如3gp、mpeg4等
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//设置音频编码器，程序中设定音频编码为AMR窄带编码
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//设置文件输出路径，程序中的PATH_NAME要用实际路径替换掉
        recorder.setOutputFile(Environment.getExternalStorageDirectory().getPath()+"/Music/KakaoTalk/nevergonna.mp3");
//准备开始，这就在start前，必须调用
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recorder.start();   // Recording is now started
//开始后调用，但是如果刚刚开始就停止会抛出异常
        recorder.stop();
//将MediaRecorder置于空闲状况，如果要重新启动MediaRecorder需要重新配置参数
        recorder.reset();   // You can reuse the object by going back to setAudioSource() step
//释放MediaRecorder相关资源，如果不再调用MediaRecorder就要把资源释放掉。

        recorder.release(); // Now the object cannot be reused
    }

    private void chooseVideo(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, REQUEST_SELECT_VIDEO);
    }

    // 保存草稿，只能保存一个
    public void saveDraft() {
        String title = postTitle.getText().toString();
        String msg = postMsg.getText().toString();
        pref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if(title.equals("")&&msg.equals("")&&mCurrentFilePath==null)
        {
            Log.d(TAG, "saveDraft: nodraft");
        }else{
            String jsonStr = "{\"title\":\"" + title + "\",\"type\":\"" + posttype
                    + "\",\"text\":\"" + msg + "\",\"text\":\"" + msg + "\",\"media\":\"" + mCurrentFilePath + "\"}";
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            //Log.d(LOG_TAG, formatter.format(new Date()));
            String id = formatter.format(new Date());
            editor.putString(id, jsonStr);
            if(!pref.contains(KEYS))
            {
                Set tmpSet = new HashSet() {};
                editor.putStringSet(KEYS, tmpSet);
                editor.apply();
                Log.d(TAG, "saveDraft: NO!!!!!!!!!!!!!!!!!!");
            }
            if (pref == null && pref.contains(KEYS)){
                Log.d(LOG_TAG, "saveDraft: error");
                Toast.makeText(getApplicationContext(), "pref null", Toast.LENGTH_SHORT).show();
            }
            else {
                Log.d(TAG, "saveDraft: pref!!");
                draftset = (HashSet<String>) pref.getStringSet(KEYS, null);
                if (!draftset.contains(id)) {
                    draftset.add(id);
                }
                editor.putStringSet(KEYS, draftset);
                //editor.putString("MESSAGE", msg);
                editor.apply();
                Toast.makeText(getApplicationContext(), "草稿保存成功", Toast.LENGTH_SHORT).show();
            }
        }

        //
    }

    // 打开页面时，读入草稿
    public void loadDraft() {
        if (pref.contains("TITLE") || pref.contains("MESSAGE")) {
//            Toast.makeText(getActivity().getApplicationContext(), "草稿加载成功", Toast.LENGTH_SHORT).show();
            if (pref.contains("TITLE")) {
                postTitle.setText(pref.getString("TITLE", ""));
            }
            if (pref.contains("MESSAGE")) {
                postMsg.setText(pref.getString("MESSAGE", ""));
            }
        }
    }

    public void tstLocation() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 23 &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(LOG_TAG, "FailedPermmison");
            return;
        }
        Location location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double latitude = location.getLatitude();
        Log.d("???", "???");
        if (location != null) {
            //double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String message = "최근 위치 -> Latitude : " + latitude + "\nLongitude : " + longitude;
            Log.d("?", message);
            //tv_location.setText(message);
        }
    }

    public void getCurrentLocation() {
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (permissionGranted) {
            Log.d("permission", "1");
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//마지막 위치 받아오기
            Log.d("ssssss", locationManager.toString());
            Location loc_Current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            loc_Current = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (loc_Current == null && locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) == null) {
                Log.d("wait", "wait");
                loc_Current = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } else {
                Log.d("sss", loc_Current.toString());
                cur_lat = loc_Current.getLatitude(); //위도
                cur_lon = loc_Current.getLongitude(); //경도
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = null;
                Log.d("JJJJJ", "JJJJJJ");
                try {
                    Log.d("JJJJJ", "JJJJJJ");
                    addresses = geocoder.getFromLocation(
                            cur_lat,
                            cur_lon,
                            7);
                    cur_location = addresses.get(0).getAddressLine(0);
                    Log.d("address", cur_location);

                    //Log.d("locality",addresses.get(0).);
                } catch (IOException ioException) {

                    //네트워크 문제
                    Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
                } catch (IllegalArgumentException illegalArgumentException) {
                    Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
                    if (addresses == null || addresses.size() == 0) {
                        Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
                        //return "주소 미발견";

                    }
                    //cur_location = address.getAddressLine(0);

                }
            }


        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // {Some Code}
                }
            }
        }
    }

    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.postButton:
                Log.d(LOG_TAG, "Post");
                clickPost(file);
                break;
            case R.id.postPhotoButton: {
                if (posttype.equals("txtandimg"))
                    capture();
                else if (posttype.equals("audio")) {

                } else if (posttype.equals("video")) {
                    recordVideo();
                }
            }
            break;
            default:
                Log.d(LOG_TAG, "No match");
                break;
        }
    }

    public void recordVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        File tempDir = getCacheDir();
        File videoFile = null;
        //임시촬영파일 세팅

        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String videoFileName = "Capture_" + timeStamp + "_"; //ex) Capture_20201206_
            File tempfile = File.createTempFile(
                    videoFileName,  /* 파일이름 */
                    ".mp4",         /* 파일형식 */
                    tempDir      /* 경로 */

            );
            mCurrentFilePath = tempfile.getAbsolutePath();

            videoFile = tempfile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (videoFile != null) {
            //Uri 가져오기
            Uri videoURI = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider",
                    videoFile);
            //인텐트에 Uri담기
            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);

            //인텐트 실행
            startActivityForResult(takeVideoIntent, REQUEST_TAKE_PHOTO);
        }
    }

    public void capture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File tempDir = getCacheDir();
        File photoFile = null;
        //임시촬영파일 세팅

        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String imageFileName = "Capture_" + timeStamp + "_"; //ex) Capture_20201206_
            File tempfile = File.createTempFile(
                    imageFileName,  /* 파일이름 */
                    ".jpg",         /* 파일형식 */
                    tempDir      /* 경로 */

            );
            mCurrentFilePath = tempfile.getAbsolutePath();

            photoFile = tempfile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (photoFile != null) {
            //Uri 가져오기
            Uri photoURI = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider",
                    photoFile);
            //인텐트에 Uri담기
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            //인텐트 실행
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    private void clickPost(File file) {
        String title = postTitle.getText().toString();
        getCurrentLocation();
        //cur_location = "sss";
        String type;
        String msg = postMsg.getText().toString();
        if (title.isEmpty() || msg.isEmpty()) {
            Toast.makeText(getApplicationContext(), "动态标题或内容不能为空", Toast.LENGTH_LONG).show();
        } else {
            if (posttype.equals("txtandimg")) {
                Log.d("here1", posttype);
                if (file == null) {
                    type = "TEXT";
                    file = new File("null");
                } else {
                    type = "IMAGE";
                }
                Log.d("type=", type);

            } else if (posttype.equals("audio")) {
                type = "AUDIO";
            } else if (posttype.equals("video")) {
                type = "VIDEO";
            } else {
                Log.e("typeError", posttype);
                return;
            }

//            String jsonStr = "{\"user_id\":\"" + user_id + "\",\"type\":\"" + type
//                    + "\",\"title\":\"" + title + "\",\"text\":\"" + msg + "\",\"media\":\"" + "null" + "\",\"location\":\"" + "hh" + "\"}";
            String requestUrl = getResources().getString(R.string.backend_url) + "create-status";

            try {
                OkHttpClient client = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
                RequestBody requestBody;
                if (type.equals("TEXT")) {
                    if (cur_location == null) {
                        requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("user_id", user_id)
                                .addFormDataPart("type", type)
                                .addFormDataPart("title", title)
                                .addFormDataPart("text", msg)
                                .build();
                    } else {
                        requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("user_id", user_id)
                                .addFormDataPart("type", type)
                                .addFormDataPart("title", title)
                                .addFormDataPart("text", msg)
                                .addFormDataPart("location", cur_location)
                                .build();
                    }
                } else {
                    if (cur_location == null) {
                        requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("user_id", user_id)
                                .addFormDataPart("type", type)
                                .addFormDataPart("title", title)
                                .addFormDataPart("text", msg)
                                .addFormDataPart("media", file.getName(),
                                        RequestBody.create(MediaType.parse("image/*"), file))
                                .build();
                    } else {
                        requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("user_id", user_id)
                                .addFormDataPart("type", type)
                                .addFormDataPart("title", title)
                                .addFormDataPart("text", msg)
                                .addFormDataPart("media", file.getName(),
                                        RequestBody.create(MediaType.parse("image/*"), file))
                                .addFormDataPart("location", cur_location)
                                .build();
                    }
                }
                Request request = new Request.Builder()
                        .url(requestUrl).post(requestBody)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                        final String responseStr = response.body().string();
                        try {
                            JSONObject jObject = new JSONObject(responseStr);
                            boolean status = jObject.getBoolean("status");
                            if (status) {
                            } else {

                                Log.d("messagr", jObject.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }


            Toast.makeText(getApplicationContext(), "发布成功", Toast.LENGTH_LONG).show();
            postTitle.getText().clear();
            postMsg.getText().clear();

            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.commit();

            switchContent(title, msg);
        }
    }

    // 用以在点击“发布”后跳转到动态列表页面，通过switchHome来添加动态
    private void switchContent(String title, String msg) {
        if (activity == null) {
            return;
        } else if (activity instanceof MainActivity) {
            Log.d(LOG_TAG, "Switch");
            MainActivity mainActivity = (MainActivity) activity;
            mainActivity.switchHome(title, msg);
        }
    }
}