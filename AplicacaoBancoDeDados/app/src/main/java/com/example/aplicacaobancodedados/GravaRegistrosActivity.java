package com.example.aplicacaobancodedados;

import android.app.AlertDialog;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.*;

public class GravaRegistrosActivity extends Activity {
    Button btcadastrar;
    Button btvoltar;
    EditText ednome, edtelefone, edemail;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grava_registros);
        btcadastrar = (Button) findViewById(R.id.btcadastrar);
        btvoltar = (Button) findViewById(R.id.btvoltar);
        ednome = (EditText) findViewById(R.id.ednome);
        edtelefone = (EditText) findViewById(R.id.edemail);
        edemail = (EditText) findViewById(R.id.edtelefone);
        try {
            db = openOrCreateDatabase("banco_dados",
                    Context.MODE_PRIVATE, null);
        } catch (Exception e)
        {
            MostraMensagem("Erro : " + e.toString());
        }

        btcadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = ednome.getText().toString();
                String telefone = edtelefone.getText().toString();
                String email = edemail.getText().toString();
                try {
                    db.execSQL("insert into usuarios(nome," +
                                "telefone, email) values('" + nome + "','"
                                + telefone + "','" + email + "')");
                    MostraMensagem("Dados cadastrados com sucesso");
                } catch(Exception e) {
                    MostraMensagem("Erro : " + e.toString());
                }
            }
        });

        btvoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GravaRegistrosActivity.this.finish();
            }
        });
    }
    public void MostraMensagem(String str){
        AlertDialog.Builder dialogo = new
                AlertDialog.Builder(GravaRegistrosActivity.this);
        dialogo.setTitle("Aviso");
        dialogo.setMessage(str);
        dialogo.setNeutralButton("Ok", null);
        dialogo.show();
    }
}