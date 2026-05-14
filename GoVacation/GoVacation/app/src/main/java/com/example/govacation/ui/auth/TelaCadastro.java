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
                final String senhaPlana = edsenha.getText().toString().trim(); // Lida na Main Thread
                final String cpf       = edcpf.getText().toString().trim();
                final String endereco  = edendereco.getText().toString().trim();

                if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty()
                        || senhaPlana.isEmpty() || cpf.isEmpty() || endereco.isEmpty()) {
                    MostraMensagem("Por favor, preencha todos os campos.");
                    return;
                }
                BDHelper bdHelper = new BDHelper(TelaCadastro.this);

                // 3. EXECUTA A NOVA VALIDAÇÃO
                if (bdHelper.verificarUsuarioExistente(cpf, email)) {
                    // Se retornar TRUE, barra o cadastro e avisa o usuário
                    MostraMensagem("Erro: CPF ou E-mail já estão em uso no GoVacation!");

                    // Opcional: Limpa os campos para ele digitar de novo
                    edcpf.setText("");
                    edemail.setText("");
                } else {
                    // Se retornar FALSE, está tudo livre! Segue o fluxo normal de inserção.

                    // ... (Seu código original que faz o INSERT usando ContentValues ou BDHelper entra aqui) ...

                    MostraMensagem("Cadastro realizado com sucesso!");
                    finish(); // Fecha a tela de cadastro
                }

                // ✅ SEGURANÇA: Gera o hash SHA-256 da senha ANTES de enviar para o background
                // A senha em texto puro nunca é armazenada no banco de dados
                final String senhaHash = CriptoUtil.hashSHA256(senhaPlana);

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        long newRowIdTemp = -1;
                        String erroTemp = null;

                        try {
                            SQLiteDatabase db = dbHelper.getWritableDatabase();

                            ContentValues values = new ContentValues();
                            values.put("tipousuario", 2);
                            values.put("nome", nome);
                            values.put("telefone", telefone);
                            values.put("email", email);
                            values.put("senha", senhaHash); // ✅ Salva o hash, nunca o texto puro
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
                                if (erroCapturado != null) {
                                    MostraMensagem("Erro: " + erroCapturado);
                                } else if (newRowId == -1) {
                                    MostraMensagem("Erro ao cadastrar. Verifique se todos os campos estão preenchidos.");
                                } else {
                                    MostraMensagem("Cadastro realizado com sucesso!");
                                    ednome.setText("");
                                    edtelefone.setText("");
                                    edemail.setText("");
                                    edsenha.setText("");
                                    edcpf.setText("");
                                    edendereco.setText("");
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
