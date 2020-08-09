package com.getStorage.android30;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 5;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_MANUAL = 5;//intent value used in onRequestPermissionsResult
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;//intent value used in ask permissions
    private FloatingActionButton fab;
    private ImageView mImgAddValue;
    private String mPart_image;
    private File mActualImageFile;

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         fab = findViewById(R.id.fab);
        mImgAddValue=findViewById(R.id.capturedImage);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {


            try {
                if (Build.VERSION.SDK_INT >= 19) {
                    mPart_image = ConvertUriToFilePath.getPathFromURI(this, data.getData());
                } else {
                    mPart_image = ConvertUriToFilePath.getImagePath(this, data.getData());
                }
                if (mPart_image != null) {
                    mActualImageFile = FileUtil.from(this, data.getData());

                    // Glide.with(this).load(mActualImageFile).into(mImgAddValueOfRest);
                    mImgAddValue.setImageBitmap(BitmapFactory.decodeFile(mActualImageFile.getAbsolutePath()));
                    Log.e(TAG, "mPartImageCategoryFragment = " + mPart_image);

                    // Toast.makeText(EitDataActivity.this,mPart_image,Toast.LENGTH_LONG).show();
                    //  cursor.close();

                    // mCircleImageViewHolder.setImageBitmap(BitmapFactory.decodeFile(mActualImageFile.getAbsolutePath()));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void galleryPermissionDialog() {/*this dialog will appear when first open
                                            to note user to give app permission to read device storage*/
        int hasWriteContactsPermission = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            hasWriteContactsPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
            //noinspection UnnecessaryReturnStatement
            return;

        }else {
            chooseImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Initial
        // Fill with results
        // Check for READ_EXTERNAL_STORAGE
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            Map<String, Integer> perms = new HashMap<>();
            perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            for (int i = 0; i < permissions.length; i++)
                perms.put(permissions[i], grantResults[i]);
            boolean showRationale;
            if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // All Permissions Granted
                galleryPermissionDialog();
            } else {
                showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (showRationale) {
                    showMessageOKCancel(getString(R.string.read_storage_permission),
                            (dialog, which) -> galleryPermissionDialog());
                } else {
                    showMessageOKCancel(getString(R.string.read_storage_permission),
                            (dialog, which) -> {
                                Toast.makeText(MainActivity.this, getString(R.string.enable_permission), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, REQUEST_CODE_MANUAL);
                            });

                }


            }


        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    private void initImage() {//to open image and ask permission for all  versions of android want permissions

        galleryPermissionDialog();


    }
    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
}