package com.example.aplicacaobancodedados;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.*;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    Button btcriarbanco;
    Button btcadastrardados;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btcriarbanco = findViewById(R.id.btcriarbanco);
        btcadastrardados = findViewById(R.id.btcadastrardados);

        btcadastrardados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gravaRegistrosActivity = new Intent(MainActivity.this,
                        GravaRegistrosActivity.class);
                        MainActivity.this.startActivity(gravaRegistrosActivity);
            }
        });

        btcriarbanco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    db = openOrCreateDatabase("banco_dados",
                            Context.MODE_PRIVATE, null);
                    db.execSQL("create table if not exists " +
                               " usuarios(numreg integer primary key " +
                               " autoincrement, nome text not null, telefone text " +
                               " not null, " + " email text not null) ");
                    AlertDialog.Builder dialogo = new
                            AlertDialog.Builder(MainActivity.this);
                    dialogo.setTitle("Aviso")
                            .setMessage("Banco de dados criado com sucesso!")
                            .setNeutralButton("OK", null)
                            .show();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}