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

                // 1. Validação de campos vazios
                if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty()
                        || senhaPlana.isEmpty() || cpf.isEmpty() || endereco.isEmpty()) {
                    MostraMensagem("Por favor, preencha todos os campos.");
                    return;
                }

                // ✅ SEGURANÇA: Gera o hash SHA-256 da senha ANTES de enviar para o background
                final String senhaHash = CriptoUtil.hashSHA256(senhaPlana);

                // Desabilita o botão temporariamente para evitar que o usuário clique duas vezes rápido
                btcadastrar.setEnabled(false);

                // Inicia o processamento em Background (fora da thread principal)
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        // 2. VERIFICA SE O USUÁRIO EXISTE (agora rodando em background!)
                        final boolean usuarioExiste = dbHelper.verificarUsuarioExistente(cpf, email);

                        if (usuarioExiste) {
                            // Se o usuário já existe, volta para a Thread Principal (UI) para mostrar o erro
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    MostraMensagem("Erro: CPF ou E-mail já estão em uso no GoVacation!");
                                    edcpf.setText("");
                                    edemail.setText("");
                                    edcpf.requestFocus(); // Coloca o cursor de volta no CPF
                                    btcadastrar.setEnabled(true); // Reabilita o botão
                                }
                            });

                            return; // 🛑 INTERROMPE A THREAD AQUI. O código abaixo não será executado.
                        }

                        // 3. SE NÃO EXISTE, PROSSEGUE COM O INSERT
                        long newRowIdTemp = -1;
                        String erroTemp = null;

                        try {
                            SQLiteDatabase db = dbHelper.getWritableDatabase();

                            ContentValues values = new ContentValues();
                            values.put("tipousuario", 2);
                            values.put("nome", nome);
                            values.put("telefone", telefone);
                            values.put("email", email);
                            values.put("senha", senhaHash); // Salva o hash
                            values.put("cpf", cpf);
                            values.put("endereco", endereco);

                            newRowIdTemp = db.insert("usuario", null, values);

                        } catch (Exception e) {
                            erroTemp = e.toString();
                        }

                        final long newRowId = newRowIdTemp;
                        final String erroCapturado = erroTemp;

                        // 4. VOLTA PARA A THREAD PRINCIPAL PARA ATUALIZAR A TELA
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                btcadastrar.setEnabled(true); // Reabilita o botão

                                if (erroCapturado != null) {
                                    MostraMensagem("Erro no banco: " + erroCapturado);
                                } else if (newRowId == -1) {
                                    MostraMensagem("Erro ao cadastrar. Verifique os dados.");
                                } else {
                                    MostraMensagem("Cadastro realizado com sucesso!");
                                    finish(); // Fecha a tela e volta para a anterior
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
