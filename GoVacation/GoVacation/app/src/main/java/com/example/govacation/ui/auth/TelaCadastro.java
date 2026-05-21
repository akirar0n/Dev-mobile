package com.example.govacation.ui.auth;

import android.app.AlertDialog;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

import com.example.govacation.R;
import com.example.govacation.data.BDHelper;
import com.example.govacation.util.CriptoUtil;

public class TelaCadastro extends Activity {
    Button btcadastrar;
    Button btvoltar;
    EditText ednome, edtelefone, edemail, edsenha, edcpf, edendereco;

    BDHelper dbHelper;

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro);

        dbHelper = new BDHelper(this);

        btcadastrar = (Button) findViewById(R.id.btcadastrar);
        btvoltar    = (Button) findViewById(R.id.btvoltar);

        ednome     = (EditText) findViewById(R.id.ednome);
        edtelefone = (EditText) findViewById(R.id.edtelefone);
        edemail    = (EditText) findViewById(R.id.edemail);
        edsenha    = (EditText) findViewById(R.id.edsenha);
        edcpf      = (EditText) findViewById(R.id.edcpf);
        edendereco = (EditText) findViewById(R.id.edendereco);

        btcadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nome      = ednome.getText().toString().trim();
                final String telefone  = edtelefone.getText().toString().trim();
                final String email     = edemail.getText().toString().trim();
                final String senhaPlana = edsenha.getText().toString().trim();
                final String cpf       = edcpf.getText().toString().trim();
                final String endereco  = edendereco.getText().toString().trim();

                if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty()
                        || senhaPlana.isEmpty() || cpf.isEmpty() || endereco.isEmpty()) {
                    MostraMensagem("Por favor, preencha todos os campos.");
                    return;
                }

                final String senhaHash = CriptoUtil.hashSHA256(senhaPlana);

                btcadastrar.setEnabled(false);

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        final boolean usuarioExiste = dbHelper.verificarUsuarioExistente(cpf, email);

                        if (usuarioExiste) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    MostraMensagem("Erro: CPF ou E-mail já estão em uso no GoVacation!");
                                    edcpf.setText("");
                                    edemail.setText("");
                                    edcpf.requestFocus();
                                    btcadastrar.setEnabled(true);
                                }
                            });

                            return;
                        }

                        long newRowIdTemp = -1;
                        String erroTemp = null;

                        try {
                            SQLiteDatabase db = dbHelper.getWritableDatabase();

                            ContentValues values = new ContentValues();
                            values.put("tipousuario", 2);
                            values.put("nome", nome);
                            values.put("telefone", telefone);
                            values.put("email", email);
                            values.put("senha", senhaHash);
                            values.put("cpf", cpf);
                            values.put("endereco", endereco);

                            newRowIdTemp = db.insert("usuario", null, values);

                        } catch (Exception e) {
                            erroTemp = e.toString();
                        }

                        final long newRowId = newRowIdTemp;
                        final String erroCapturado = erroTemp;

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                btcadastrar.setEnabled(true);

                                if (erroCapturado != null) {
                                    MostraMensagem("Erro no banco: " + erroCapturado);
                                } else if (newRowId == -1) {
                                    MostraMensagem("Erro ao cadastrar. Verifique os dados.");
                                } else {
                                    MostraMensagem("Cadastro realizado com sucesso!");
                                    finish();
                                }
                            }
                        });
                    }
                });
            }
        });

        btvoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TelaCadastro.this.finish();
            }
        });
    }

    public void MostraMensagem(String str) {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(TelaCadastro.this);
        dialogo.setTitle("Aviso");
        dialogo.setMessage(str);
        dialogo.setNeutralButton("Ok", null);
        dialogo.show();
    }
}
