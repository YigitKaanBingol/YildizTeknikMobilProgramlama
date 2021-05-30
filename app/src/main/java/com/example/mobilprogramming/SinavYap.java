package com.example.mobilprogramming;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class SinavYap extends AppCompatActivity {

    private EditText puan,sure;
    private TextView zorluk;
    private Button kaydet;
    private Button mailAt;
    RecyclerView recyclerView;


    private String stringFile = Environment.getExternalStorageDirectory()+"/"+Environment.DIRECTORY_DOCUMENTS.toString()+"/sinav.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sinav_yap);
        puan=findViewById(R.id.editTextPuan);
        sure=findViewById(R.id.editTextSure);
        zorluk=findViewById(R.id.textViewZorluk);
        kaydet=findViewById(R.id.buttonKaydet);
        mailAt=findViewById(R.id.butonMail);
        recyclerView=findViewById(R.id.recyclerView2);

        Gson gson=new Gson();
        SharedPreferences appSharedPrefs =getApplicationContext().getSharedPreferences("ayar", MODE_PRIVATE);
        String json = appSharedPrefs.getString("sinavAyar", "");
        Sinav sinav = gson.fromJson(json,Sinav.class);

        if(sinav!=null){
            puan.setText(String.valueOf((sinav.getSoruPuani())));
            sure.setText(String.valueOf((sinav.getSinavSuresi())));
            zorluk.setText(String.valueOf(sinav.getZorlukDuzeyi()));
        }

        DatabaseHelper dbhelper=new DatabaseHelper(SinavYap.this);
        ArrayList<Sorular> sorularim=dbhelper.readSpecific(Integer.valueOf(sinav.getZorlukDuzeyi()));
        SinavAdapter sinavAdapter = new SinavAdapter(this, sorularim);
        recyclerView.setAdapter(sinavAdapter) ;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sinavAdapter.writeToFile(SinavYap.this,puan.getText().toString(),sure.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mailAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(stringFile);
                if (!file.exists()){
                    Toast.makeText(SinavYap.this, "File doesn't exists", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intentShare = new Intent(Intent.ACTION_SEND);
                intentShare.setType("text/plain");
                intentShare.putExtra(Intent.EXTRA_EMAIL,new String[]{"android@example.com"});
                intentShare.putExtra(Intent.EXTRA_SUBJECT,"EMAIL SUBJECT");
                intentShare.putExtra(Intent.EXTRA_TEXT,"message");
                intentShare.putExtra(Intent.EXTRA_STREAM, Uri.parse("sinav.txt"));
                startActivity(Intent.createChooser(intentShare, "Share the file ..."));

            }
        });
    }
}