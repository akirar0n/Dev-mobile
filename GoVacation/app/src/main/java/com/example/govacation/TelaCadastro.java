package com.example.govacation;

import android.app.AlertDialog;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.*;

public class TelaCadastro extends Activity {
    Button btcadastrar;
    Button btvoltar;
    EditText ednome, edtelefone, edemail, edsenha, edcpf, edendereco;

    BDHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro);

        dbHelper = new BDHelper(this);

        btcadastrar = (Button) findViewById(R.id.btcadastrar);
        btvoltar = (Button) findViewById(R.id.btvoltar);

        ednome = (EditText) findViewById(R.id.ednome);
        edtelefone = (EditText) findViewById(R.id.edtelefone);
        edemail = (EditText) findViewById(R.id.edemail);

        edsenha = (EditText) findViewById(R.id.edsenha);
        edcpf = (EditText) findViewById(R.id.edcpf);
        edendereco = (EditText) findViewById(R.id.edendereco);

        btcadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = ednome.getText().toString();
                String telefone = edtelefone.getText().toString();
                String email = edemail.getText().toString();
                String senha = edsenha.getText().toString();
                String cpf = edcpf.getText().toString();
                String endereco = edendereco.getText().toString();

                if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty() || senha.isEmpty() || cpf.isEmpty() || endereco.isEmpty()) {
                    MostraMensagem("Por favor, preencha todos os campos.");
                    return;
                }

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put("tipousuario", 2);
                values.put("nome", nome);
                values.put("telefone", telefone);
                values.put("email", email);
                values.put("senha", senha);
                values.put("cpf", cpf);
                values.put("endereco", endereco);

                try {
                    long newRowId = db.insert("usuario", null, values);

                    if (newRowId == -1) {
                        MostraMensagem("Erro ao cadastrar. " +
                                "(Verifique se faltam campos obrigatórios como senha, cpf, etc)");
                    } else {
                        MostraMensagem("Dados cadastrados com sucesso (ID: " + newRowId + ")");
                        ednome.setText("");
                        edtelefone.setText("");
                        edemail.setText("");
                        edsenha.setText("");
                        edcpf.setText("");
                        edendereco.setText("");
                    }
                } catch(Exception e) {
                    MostraMensagem("Erro : " + e.toString());
                }
            }
        });

        btvoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TelaCadastro.this.finish();
            }
        });
    }

    public void MostraMensagem(String str){
        AlertDialog.Builder dialogo = new
                AlertDialog.Builder(TelaCadastro.this);
        dialogo.setTitle("Aviso");
        dialogo.setMessage(str);
        dialogo.setNeutralButton("Ok", null);
        dialogo.show();
    }
}