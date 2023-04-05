package com.example.shared_calendar;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Friends_page extends AppCompatActivity {
    private static final String camera = "Friends_page";
    public static final int REQUEST_TAKE_ALBUM = 9;
    public static final int REQUEST_TAKE_PHOTO = 10;
    public static final int REQUEST_PERMISSION = 11;
    private String mCurrentPhotoPath;
    ImageView upload_Picture;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_friends_page);

        Button friends_button = findViewById(R.id.friends_page_friends_button);
        Button calendar_button = findViewById(R.id.friends_page_calendar_button);
        Button upload_Picture_button = findViewById(R.id.upload_Picture_button);


        upload_Picture = findViewById(R.id.my_profile_image);

        /// 내 정보 들고오기
        String Sign_up_data;
        String now_ID;
        TextView get_Name = findViewById(R.id.my_name);
        TextView get_Email = findViewById(R.id.my_email);
        //리사이클
        ArrayList<Sign_up_item> friends_dataList = new ArrayList<>();
        Adapter_friends_list friends_adapter = new Adapter_friends_list(friends_dataList);
        RecyclerView recyclerView = findViewById(R.id.recycle_profile);
        recyclerView.setAdapter(friends_adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, recyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new ItemDecoration());


        SharedPreferences sharedPreferences = getSharedPreferences("Signup_info", MODE_PRIVATE);
        now_ID = sharedPreferences.getString("login_ID", "default");
        Sign_up_data = sharedPreferences.getString(now_ID + "_info", "default");

        try {
            JSONObject data = new JSONObject(Sign_up_data);
            get_Name.setText(data.get("NAME").toString());
            get_Email.setText(data.get("Email").toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //////////////////친구 목록 넣기
        String ID_unsplit;
        String[] ID_split;
        int length;

        ID_unsplit = get_signup_ID();
        ID_split = ID_unsplit.split("@#@");
        length = ID_split.length;
        friends_dataList.clear();
        try {
            now_ID = get_now_ID();

            Log.d("loge", "now_ID=" + now_ID);
            for (int i = 0; i < length; i++) {
                String img_path = get_image_path(ID_split[i]);
                if (ID_split[i].equals(now_ID)) {
                    Log.d("loge", "in for");
                } else {
                    friends_dataList.add(new Sign_up_item(ID_split[i], get_signup_name(ID_split[i]), get_signup_Email(ID_split[i]),img_path));  //리사이클러뷰 바로 출력
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        friends_adapter.notifyDataSetChanged();
        //////////////////////////////////////////////////////////사진 넣기

        String filename = now_ID + "프로필사진.jpg";
        File storageDir = new File(getFilesDir() + "/capture");

        try {
            String imgpath = storageDir + "/" + filename;   // 내부 저장소에 저장되어 있는 이미지 경로
            Bitmap bm = BitmapFactory.decodeFile(imgpath);
            Log.d("to check null", "bm"+bm);
            if(bm == null){
                upload_Picture.setImageResource(R.drawable.ic_launcher_foreground);
            }else{
                upload_Picture.setImageBitmap(bm);   // 내부 저장소에 저장된 이미지를 이미지뷰에 셋
                Toast.makeText(getApplicationContext(), "파일 로드 성공", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "파일 로드 실패", Toast.LENGTH_SHORT).show();
        }


        friends_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Friends_page.this, Friends_page.class);
                startActivity(intent);
                // onStop();
            }
        });

        calendar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Friends_page.this, Calendar_page.class);
                startActivity(intent);
                //  onStop();
            }
        });

        upload_Picture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(); //권한체크
                PopupMenu pop = new PopupMenu(getApplicationContext(), v);
                getMenuInflater().inflate(R.menu.menu, pop.getMenu());

                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.one: //카메라
                                captureCamera();
                                break;

                            case R.id.two: // 앨범
                                captureAlbum();
                                break;

                        }

                        return true;
                    }
                });
                pop.show();
            }
        });

    }


    String get_signup_ID() {
        String ID;
        SharedPreferences sharedPreferences = getSharedPreferences("Signup_info", MODE_PRIVATE);
        ID = sharedPreferences.getString("Signup_ID", "default");
        return ID;
    }

    String get_now_ID() throws JSONException {

        String now_ID;
        SharedPreferences signup_sharedPreferences = getSharedPreferences("Signup_info", MODE_PRIVATE);

        now_ID = signup_sharedPreferences.getString("login_ID", "default");

        return now_ID;
    }

    String get_signup_name(String ID) throws JSONException {
        String name;
        SharedPreferences sharedPreferences = getSharedPreferences("Signup_info", MODE_PRIVATE);
        ID = sharedPreferences.getString(ID + "_info", "default");
        JSONObject data = new JSONObject(ID);
        name = data.get("NAME").toString();

        return name;
    }

    String get_signup_Email(String ID) throws JSONException {
        String Email;
        SharedPreferences sharedPreferences = getSharedPreferences("Signup_info", MODE_PRIVATE);
        ID = sharedPreferences.getString(ID + "_info", "default");
        JSONObject data = new JSONObject(ID);
        Email = data.get("Email").toString();

        return Email;
    }

    private void captureAlbum() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);

    }

    private void captureCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 인텐트를 처리 할 카메라 액티비티가 있는지 확인
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // 촬영한 사진을 저장할 파일 생성
            File photoFile = null;

            try {
                //임시로 사용할 파일이므로 경로는 캐시폴더로
                File tempDir = getCacheDir();

                //임시촬영파일 세팅
                String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
                String imageFileName = "Capture_" + timeStamp + "_"; //ex) Capture_20201206_

                File tempImage = File.createTempFile(
                        imageFileName,  /* 파일이름 */
                        ".jpg",         /* 파일형식 */
                        tempDir      /* 경로 */
                );

                // ACTION_VIEW 인텐트를 사용할 경로 (임시파일의 경로)
                mCurrentPhotoPath = tempImage.getAbsolutePath();

                photoFile = tempImage;

            } catch (IOException e) {
                //에러 로그는 이렇게 관리하는 편이 좋다.
                Log.d("camera123", "파일 생성 에러!");
            }

            //파일이 정상적으로 생성되었다면 계속 진행
            if (photoFile != null) {
                Log.d("camera123", "들어옴");
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        try {
            //after capture
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: {
                    if (resultCode == RESULT_OK) {

                        File file = new File(mCurrentPhotoPath);
                        Bitmap bitmap = MediaStore.Images.Media
                                .getBitmap(getContentResolver(), Uri.fromFile(file));

                        if (bitmap != null) {
                            ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
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

                            //Rotate한 bitmap을 ImageView에 저장
                            upload_Picture.setImageBitmap(rotatedBitmap);
                            saveImg();

                        }
                    }
                    break;
                }
                case REQUEST_TAKE_ALBUM: {
                    if (resultCode == RESULT_OK) {
                        Uri fileUri = data.getData();
                        ContentResolver resolver = getContentResolver();
                        try {
                            InputStream instream = resolver.openInputStream(fileUri);
                            Bitmap imgBitmap = BitmapFactory.decodeStream(instream);
                            upload_Picture.setImageBitmap(imgBitmap);    // 선택한 이미지 이미지뷰에 셋
                            instream.close();   // 스트림 닫아주기
                            saveBitmapToJpeg(imgBitmap);    // 내부 저장소에 저장
                            Toast.makeText(getApplicationContext(), "파일 불러오기 성공", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "파일 불러오기 실패", Toast.LENGTH_SHORT).show();
                        }

                        // Glide.with(this).load(data.getData()).into(upload_Picture);

                    }
                }
            }

        } catch (Exception e) {
            Log.w(camera, "onActivityResult Error !", e);
        }

    }

    //권한 확인
    public void checkPermission() {
        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permissionRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //권한이 없으면 권한 요청
        if (permissionCamera != PackageManager.PERMISSION_GRANTED
                || permissionRead != PackageManager.PERMISSION_GRANTED
                || permissionWrite != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "이 앱을 실행하기 위해 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                // 권한이 취소되면 result 배열은 비어있다.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "권한 확인", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();
                    finish(); //권한이 없으면 앱 종료
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermission(); //권한체크
    }


    public void saveBitmapToJpeg(Bitmap bitmap) throws JSONException {   // 선택한 이미지 내부 저장소에 저장
        String now_ID = get_now_ID();
        String filename = now_ID + "프로필사진.jpg";
        File storageDir = new File(getFilesDir() + "/capture");
        if (!storageDir.exists()) //폴더가 없으면 생성.
            storageDir.mkdirs();

        File tempFile = new File(storageDir, filename);    // 파일 경로와 이름 넣기
        boolean deleted = tempFile.delete();
        Log.d("camera123", "Delete Dup Check : " + deleted);

        try {
          //  tempFile.createNewFile();   // 자동으로 빈 파일을 생성하기
            FileOutputStream out = new FileOutputStream(tempFile);  // 파일을 쓸 수 있는 스트림을 준비하기
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);   // compress 함수를 사용해 스트림에 비트맵을 저장하기
            out.close();    // 스트림 닫아주기
            Toast.makeText(getApplicationContext(), "파일 저장 성공", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "파일 저장 실패", Toast.LENGTH_SHORT).show();
        }
    }
    //이미지저장 메소드
    private void saveImg() {
        Log.d("camera123", "saveImg들어옴");

        try {
            //저장할 파일 경로
            Log.d("camera123", "getFilesDir=" + getFilesDir());
            File storageDir = new File(getFilesDir() + "/capture");
            if (!storageDir.exists()) //폴더가 없으면 생성.
                storageDir.mkdirs();
            String now_ID = get_now_ID();
            String filename = now_ID + "프로필사진.jpg";

            // 기존에 있다면 삭제
            File file = new File(storageDir, filename);
            boolean deleted = file.delete();
            Log.d("camera123", "Delete Dup Check : " + deleted);
            FileOutputStream output = null;

            try {
                output = new FileOutputStream(file);
                Log.d("camera123", "output " + output);
                BitmapDrawable drawable = (BitmapDrawable) upload_Picture.getDrawable();
                Log.d("camera123", "drawable " + drawable);
                Bitmap bitmap = drawable.getBitmap();
                Log.d("camera123", "bitmap " + bitmap);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output); //해상도에 맞추어 Compress
            } catch (FileNotFoundException e) {
                Log.d("camera123", "FileNotFoundException e : ");
                e.printStackTrace();
            } finally {
                try {
                    assert output != null;
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Log.d("camera123", "Captured Saved");
            Toast.makeText(this, "Capture Saved ", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.d("camera123", "Capture Saving Error!", e);
            Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();

        }


    }
    private String get_image_path(String id){
        String filename = id + "프로필사진.jpg";
        File storageDir = new File(getFilesDir() + "/capture");

        try {
            String imgpath = storageDir + "/" + filename;   // 내부 저장소에 저장되어 있는 이미지 경로
            return imgpath;
        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "파일 로드 실패", Toast.LENGTH_SHORT).show();
            return "none";
        }


    }

}