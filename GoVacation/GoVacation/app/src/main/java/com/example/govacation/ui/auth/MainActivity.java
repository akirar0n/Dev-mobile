package com.example.govacation.ui.auth;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.govacation.ui.admin.GerenciamentoLocs;
import com.example.govacation.R;
import com.example.govacation.ui.client.TelaHome;
import com.example.govacation.data.BDHelper;
import com.example.govacation.util.CriptoUtil;

public class MainActivity extends AppCompatActivity {
    EditText edemail, edsenha;
    Button btentrar, btcadastro, btesquecisenha;
    BDHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new BDHelper(this);

        // Garante que o admin padrão existe com senha já hasheada
        inserirAdminPadrao();

        edemail = findViewById(R.id.edemail);
        edsenha = findViewById(R.id.edsenha);
        btentrar = findViewById(R.id.btentrar);
        btcadastro = findViewById(R.id.btcadastro);
        btesquecisenha = findViewById(R.id.btesquecisenha);

        configurarListeners();
    }

    private void inserirAdminPadrao() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String adminEmail    = "admin@govacation.com";
        String adminSenhaHash = CriptoUtil.hashSHA256("admin123");

        try {
            db = dbHelper.getWritableDatabase();

            String[] projection  = {"idusuario", "senha"};
            String   selection   = "email = ?";
            String[] selectionArgs = {adminEmail};

            cursor = db.query("usuario", projection, selection, selectionArgs, null, null, null);

            if (cursor == null || cursor.getCount() == 0) {
                // Admin não existe — cria com hash
                ContentValues adminValues = new ContentValues();
                adminValues.put("tipousuario", 1);
                adminValues.put("email", adminEmail);
                adminValues.put("senha", adminSenhaHash);
                adminValues.put("nome", "Administrador");
                adminValues.put("cpf", "000.000.000-00");
                adminValues.put("endereco", "Sede GoVacation");
                adminValues.put("telefone", "(00) 00000-0000");
                db.insertWithOnConflict("usuario", null, adminValues, SQLiteDatabase.CONFLICT_IGNORE);
                Log.i("MainActivity", "Admin criado com senha hasheada.");

            } else if (cursor.moveToFirst()) {
                String senhaSalva = cursor.getString(cursor.getColumnIndexOrThrow("senha"));

                // ✅ MIGRAÇÃO: se a senha salva NÃO é o hash esperado, atualiza agora
                // Isso corrige bancos criados antes da criptografia ser adicionada
                if (!adminSenhaHash.equals(senhaSalva)) {
                    ContentValues update = new ContentValues();
                    update.put("senha", adminSenhaHash);
                    db.update("usuario", update, "email = ?", new String[]{adminEmail});
                    Log.i("MainActivity", "Senha do admin migrada para hash SHA-256.");
                } else {
                    Log.i("MainActivity", "Admin já possui senha hasheada.");
                }
            }

        } catch (Exception e) {
            Log.e("MainActivity", "Erro ao configurar admin padrão", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
    }

    private void configurarListeners() {
        btentrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tentarLogin();
            }
        });

        btcadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TelaCadastro.class);
                startActivity(intent);
            }
        });

        btesquecisenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,
                        "Função 'Esqueci a senha' não implementada.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tentarLogin() {
        String email = edemail.getText().toString().trim();
        String senhaDigitada = edsenha.getText().toString().trim();

        if (email.isEmpty()) {
            edemail.setError("Email é obrigatório");
            edemail.requestFocus();
            return;
        }

        if (senhaDigitada.isEmpty()) {
            edsenha.setError("Senha é obrigatória");
            edsenha.requestFocus();
            return;
        }

        // ✅ SEGURANÇA: Converte a senha digitada para hash antes de comparar com o banco
        String senhaHash = CriptoUtil.hashSHA256(senhaDigitada);

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            String[] projection = {"idusuario", "nome", "tipousuario"};
            String selection = "email = ? AND senha = ?";
            String[] selectionArgs = {email, senhaHash}; // ✅ Compara o hash, nunca o texto puro

            cursor = db.query("usuario", projection, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                long idUsuario = cursor.getLong(cursor.getColumnIndexOrThrow("idusuario"));
                String nomeUsuario = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
                int tipoUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("tipousuario"));

                Toast.makeText(this, "Login com sucesso! Bem-vindo, " + nomeUsuario, Toast.LENGTH_LONG).show();

                Intent intent;
                if (tipoUsuario == 1) {
                    intent = new Intent(MainActivity.this, GerenciamentoLocs.class);
                } else if (tipoUsuario == 2) {
                    intent = new Intent(MainActivity.this, TelaHome.class);
                    intent.putExtra("ID_USUARIO", idUsuario);
                } else {
                    exibirAviso("Erro de Permissão", "Tipo de usuário desconhecido.");
                    return;
                }

                startActivity(intent);
                finish();

            } else {
                exibirAviso("Login Falhou", "Email ou senha incorretos. Tente novamente.");
            }

        } catch (Exception e) {
            Log.e("MainActivity", "Erro ao tentar logar", e);
            exibirAviso("Erro no Banco", "Ocorreu um erro ao tentar logar: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
    }

    private void exibirAviso(String titulo, String mensagem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo)
                .setMessage(mensagem)
                .setPositiveButton("OK", null)
                .show();
    }
}