package com.example.getlocationdraw;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PaintView paintView;
    private Button btnLoad1;
    private Button btnSave;
    private Button btnLoad2;
    private Button btnRedo;
    private FrameLayout fmLayout;
   // private CircleView circleView;
    int DefaultColor;

    private Bitmap bm;

    SeekBar size;

    private static final int GET_FILE_REQUEST_CODE = 101;
    private static final int REQUEST_PERMISSION = 102;
    private String mCameraPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        paintView = findViewById(R.id.paint_view);
      //  circleView=findViewById(R.id.circle);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
      //  circleView.init(metrics);
        init();
    }
    public void onClickSelectPhoto(View view) {
        checkPermissionOS6();
    }


    private void checkPermissionOS6() {
        if (PermissionUtil.isCameraPermissionOn(this)
                && PermissionUtil.isReadExternalPermissionOn(this)
                && PermissionUtil.isWriteExternalPermissionOn(this)) {
            getPhoto();
            return;
        }
        String[] permissions = {
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);
    }

    private void getPhoto() {
        // Camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = FileUtil.createImageFile(this);
            mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
        } catch (IOException e) {
            mCameraPhotoPath = null;
        }
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

        // Gallery
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.addCategory(Intent.CATEGORY_OPENABLE);
        gallery.setType("image/*");

        Intent[] intents;
        if (cameraIntent != null && mCameraPhotoPath != null) {
            intents = new Intent[]{cameraIntent};
        } else {
            intents = new Intent[0];
        }

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, gallery);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);

        startActivityForResult(chooserIntent, GET_FILE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            onClickSelectPhoto(null);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != GET_FILE_REQUEST_CODE || resultCode != RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            mCameraPhotoPath = null;
            return;
        }
        Uri[] results = null;

        if (data == null || data.getData() == null) {
            // If there is not data, then we may have taken a photo
            if (mCameraPhotoPath != null) {
                results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                try {
                    bm= MediaStore.Images.Media.getBitmap(this.getContentResolver(), results[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCameraPhotoPath = null;
            }
        } else {
            Uri dataUri = data.getData();
            Hashtable<String, Object> info = FileUtil.getFileInfo(this, dataUri);
            String imagePath = (String) info.get(FileUtil.ARG_PATH);
           // results = new Uri[]{Uri.fromFile(new File(imagePath))};
            bm = BitmapFactory.decodeFile(imagePath);

        }
        paintView.setbm(bm,fmLayout.getWidth(),fmLayout.getHeight());

    }

    private void init() {
        size= findViewById(R.id.size);
        btnLoad1= findViewById(R.id.btn_load1);
        btnSave = findViewById(R.id.btn_save);
        fmLayout=findViewById(R.id.frm_paint);
        btnLoad2=findViewById(R.id.btn_load2);
        btnRedo=findViewById(R.id.btn_redo);
        btnSave.setOnClickListener(this);
        btnRedo.setOnClickListener(this);
        size.setMax(100);
        size.setProgress(25);
        size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
               // int i = size.getProgress();
              //  paintView.resize(i - 25);
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                saveFrameLayout(fmLayout);
                break;
            case R.id.btn_load2:

                break;
            case R.id.btn_redo:

        }
    }
    //Save

    public  void saveFrameLayout(FrameLayout frameLayout) {
       // frameLayout.setDrawingCacheEnabled(true);
       // frameLayout.buildDrawingCache();
        Bitmap cache = paintView.getBMRS();
        File file = Environment.getExternalStorageDirectory();
        File dir = new File(file.getAbsolutePath() + "/DCIM/DemoSVMC");
        dir.mkdir();
        String NameFile = "" + System.currentTimeMillis();
        File newFile = new File(dir, NameFile + ".png");
        try {
            OutputStream fileOutputStream = new FileOutputStream(newFile);
            cache.setHasAlpha(true);
            cache.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(MainActivity.this,
                    "Save success: " + newFile.getName(),
                    Toast.LENGTH_LONG).show();
            System.out.println("Name: " + NameFile);
            //quét hình ảnh để hiển thị trong album
            MediaScannerConnection.scanFile(this,
                    new String[]{newFile.getAbsolutePath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,
                    "Something wrong 1: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,
                    "Something wrong 2: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}
