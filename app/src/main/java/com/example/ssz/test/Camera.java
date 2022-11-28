package com.example.ssz.test;

import static android.os.Environment.DIRECTORY_PICTURES;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.ssz.AppData;
import com.example.ssz.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Camera extends AppCompatActivity {

    private Button c_route;

    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private String imageFilePath;
    private Uri photoUri;
    private static Intent intent;

    ArrayList<String> roadAddressList;
    ArrayList<String> timestampList;
    ArrayList<String> storeNameList;

    TextRecognizer recognizer =
            TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        roadAddressList = new ArrayList<>();
        storeNameList = new ArrayList<>();
        timestampList = new ArrayList<>();

        c_route = findViewById(R.id.c_route);
        c_route.setOnClickListener(new View.OnClickListener() { //사진찍는곳으로 이동
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Camera.this, com.example.ssz.test.make_marker.class);
                ((AppData)getApplicationContext()).setRoadAddressGetCamera(roadAddressList);
                ((AppData)getApplicationContext()).setTimestampGetCamera(timestampList);
                ((AppData)getApplicationContext()).setStoreNameGetCamera(storeNameList);

                startActivity(intent);
            }
        });

        findViewById(R.id.btn_capture).setOnClickListener(new View.OnClickListener() { //사진찍는곳으로 이동
            @Override
            public void onClick(View view) {
                //권한 체크
                TedPermission.with(getApplicationContext())
                        .setPermissionListener(permissionListener)
                        .setRationaleMessage("카메라 권한이 필요합니다.")
                        .setDeniedMessage("거부하셨습니다.")
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .check();

                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        //사진 파일 생성
                        photoFile = createImageFile();
                    } catch (IOException e) {

                    }
                    if (photoFile != null) {
                        photoUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName(), photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST" + timeStamp + ".";
        File storageDir = getExternalFilesDir(DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
            InputImage image = InputImage.fromBitmap(bitmap, 0);
            ExifInterface exif = null;

            try {
                exif = new ExifInterface(imageFilePath);
                Task<Text> result =
                        recognizer.process(image)
                                .addOnSuccessListener(new OnSuccessListener<Text>() {
                                    String roadAdr = "";

                                    @Override
                                    public void onSuccess(Text visionText) {
                                        //시 찾기
                                        String[] city = {"인천"};
                                        for (Text.TextBlock block : visionText.getTextBlocks()) {
                                            String targetBlock = block.getText();
                                            int checkBit = -1;
                                            int checkspc = 0;
                                            for (int i = 0; i < city.length; i++) {
                                                checkBit = targetBlock.indexOf(city[i]);
                                                if (checkBit != -1) {
                                                    break;
                                                }
                                            }
                                            if (checkBit != -1) {
                                                for (; checkBit < targetBlock.length(); checkBit++) {
                                                    if (checkspc > 3) break;
                                                    char tmp = targetBlock.charAt(checkBit);
                                                    if (tmp == ' ' || tmp == '-' || tmp == '(' || tmp == ')') {
                                                        roadAdr += targetBlock.charAt(checkBit);
                                                        if (tmp == ' ') {
                                                            checkspc++;
                                                        }
                                                    } else if ((tmp >= 33 && tmp <= 47) || (tmp >= 58 && tmp <= 64)
                                                            || (tmp >= 91 && tmp <= 96) || (tmp >= 123 && tmp <= 126)) {

                                                    } else {
                                                        roadAdr += targetBlock.charAt(checkBit);
                                                    }
                                                }
                                                break;
                                            }
                                        }


                                        //전체 정보 뽑기
                                        String storeName = "";
                                        for (Text.TextBlock block : visionText.getTextBlocks()) {
                                            String targetBlock = block.getText();
                                            int st_idx = targetBlock.indexOf("점");
                                            if (st_idx < 0) {
                                                continue;
                                            }
                                            st_idx = targetBlock.indexOf(":") + 1;
                                            for (; st_idx < targetBlock.length(); st_idx++) {
                                                storeName += targetBlock.charAt(st_idx);
                                            }
                                            if (st_idx == targetBlock.length()) {
                                                break;
                                            }

                                        }
                                        //시간 정보 추출
                                        String timestamp = "";
                                        for (Text.TextBlock block : visionText.getTextBlocks()) {
                                            String targetBlock = block.getText();
                                            int st_idx = targetBlock.indexOf("판매일자");
                                            if (st_idx < 0) {
                                                continue;
                                            }
                                            st_idx = targetBlock.indexOf(":") + 1;
                                            for (; st_idx < targetBlock.length(); st_idx++) {
                                                timestamp += targetBlock.charAt(st_idx);
                                            }
                                            if (st_idx == targetBlock.length()) {
                                                break;
                                            }

                                        }

                                        //intent로 주소값 전달
                                        roadAddressList.add(roadAdr);
                                        timestampList.add(timestamp);
                                        storeNameList.add(storeName);
                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                            }
                                        });

            } catch (IOException e) {
                e.printStackTrace();
            }

            int exifOrientation;
            int exifDegree;

            if (exif != null) {
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientationToDegrees(exifOrientation);
            } else {
                exifDegree = 0;
            }
        }
    }


    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            //Toast.makeText(getApplicationContext(),"권한이 허용됨",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            //Toast.makeText(getApplicationContext(),"권한이 거부됨",Toast.LENGTH_SHORT).show();
        }
    };


}