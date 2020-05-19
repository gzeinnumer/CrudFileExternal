package com.gzeinnumer.crudfileexternal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText enterText = findViewById(R.id.enterText);

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!enterText.getText().toString().isEmpty()) {
                    makeFile(enterText.getText().toString());
                } else {
                    enterText.requestFocus();
                    enterText.setError("Tidak Boleh Kosong");
                }
            }
        });

        final EditText readText = findViewById(R.id.readText);
        findViewById(R.id.read).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermissions()){
                    readText.setText(readFile());
                }
            }
        });

        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    if (deleteFile()) {
                        enterText.setText("");
                        readText.setText("");

                        Toast.makeText(MainActivity.this, "Success hapus file", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Gagal hapus file", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    private void makeFile(String text) {
        //untuk android 10, pakai ini
        //android:requestLegacyExternalStorage="true"
        if (checkPermissions()) {

            File file = new File(Environment.getExternalStorageDirectory().toString() + "/CrudFileExternal");
            if (!file.exists()) {
                file.mkdirs();
            }
            try {
                File gpxfile = new File(file, "sample.txt");
                FileWriter writer = new FileWriter(gpxfile);
                writer.append(text);
                writer.flush();
                writer.close();
                Toast.makeText(MainActivity.this, "Saved your text", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Add text to file " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Beri izin dulu", Toast.LENGTH_SHORT).show();
        }
    }

    private String readFile() {
        String myData = "";
        File myExternalFile = new File(Environment.getExternalStorageDirectory().toString() + "/CrudFileExternal", "sample.txt");
        try {
            FileInputStream fis = new FileInputStream(myExternalFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String strLine;
            while ((strLine = br.readLine()) != null) {
                myData = myData + strLine + "\n";
            }
            br.close();
            in.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Read text to file " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return myData;
    }

    private boolean deleteFile() {
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/CrudFileExternal", "sample.txt");
        return file.delete();
    }


    int MULTIPLE_PERMISSIONS = 1;

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                StringBuilder perStr = new StringBuilder();
                for (String per : permissions) {
                    perStr.append("\n").append(per);
                }
            }
        }
    }
}
