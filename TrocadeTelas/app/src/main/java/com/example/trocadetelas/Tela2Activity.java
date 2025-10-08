package com.example.trocadetelas;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.Activity;
import android.content.Intent;
import android.widget.*;
import android.view.*;

public class Tela2Activity extends Activity {
        Button btTelaprincipal;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_tela2);
            btTelaprincipal = (Button) findViewById(R.id.bttelaprincipal);

            btTelaprincipal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Tela2Activity.this.finish();
                }
            });
        }
    }