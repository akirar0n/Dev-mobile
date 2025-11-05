package com.example.govacation;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CadastrarLocs extends AppCompatActivity {

    EditText edTipoLocCad, edTituloLocCad, edImagemLocCad, edDescrLocCad,
            edPrecoLocCad, edLocalLocCad, edHospedesLocCad, edDispLocCad;
    Button btSalvarCadastro, btCancelarCadastro;

    BDHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_locs);

        dbHelper = new BDHelper(this);

        inicializarComponentes();

        configurarListeners();
    }

    private void inicializarComponentes() {
        edTipoLocCad = findViewById(R.id.edTipoLocCad);
        edTituloLocCad = findViewById(R.id.edTituloLocCad);
        edImagemLocCad = findViewById(R.id.edImagemLocCad);
        edDescrLocCad = findViewById(R.id.edDescrLocCad);
        edPrecoLocCad = findViewById(R.id.edPrecoLocCad);
        edLocalLocCad = findViewById(R.id.edLocalLocCad);
        edHospedesLocCad = findViewById(R.id.edHospedesLocCad);
        edDispLocCad = findViewById(R.id.edDispLocCad);
        btSalvarCadastro = findViewById(R.id.btSalvarCadastro);
        btCancelarCadastro = findViewById(R.id.btCancelarCadastro);
    }

    private void configurarListeners() {
        btSalvarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarLocacao();
            }
        });

        btCancelarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void salvarLocacao() {
        String tipo = edTipoLocCad.getText().toString().trim();
        String titulo = edTituloLocCad.getText().toString().trim();
        String imagem = edImagemLocCad.getText().toString().trim();
        String descr = edDescrLocCad.getText().toString().trim();
        String precoStr = edPrecoLocCad.getText().toString().trim();
        String local = edLocalLocCad.getText().toString().trim();
        String hospedesStr = edHospedesLocCad.getText().toString().trim();
        String disp = edDispLocCad.getText().toString().trim();

        if (tipo.isEmpty() || titulo.isEmpty() || precoStr.isEmpty() || local.isEmpty() || hospedesStr.isEmpty() || disp.isEmpty()) {
            exibirAviso("Campos Vazios", "Por favor, preencha todos os campos.");
            return;
        }

        double preco;
        int hospedes;

        try {
            preco = Double.parseDouble(precoStr);
        } catch (NumberFormatException e) {
            exibirAviso("Erro de Formato", "O valor do Preço é inválido.");
            edPrecoLocCad.requestFocus();
            return;
        }

        try {
            hospedes = Integer.parseInt(hospedesStr);
        } catch (NumberFormatException e) {
            exibirAviso("Erro de Formato", "A Quantidade de Hóspedes é inválida.");
            edHospedesLocCad.requestFocus();
            return;
        }

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("tipoloc", tipo);
            values.put("titulo", titulo);
            values.put("imagem", imagem); // Idealmente, seria um caminho ou URL
            values.put("descr", descr);
            values.put("preco", preco);
            values.put("localizacao", local);
            values.put("qtdhospedes", hospedes);
            values.put("disp", disp);

            long newRowId = db.insert("locacoes", null, values);

            if (newRowId != -1) {
                Toast.makeText(this, "Locação cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                exibirAviso("Erro", "Falha ao cadastrar locação.");
            }

        } catch (Exception e) {
            exibirAviso("Erro no Banco", "Falha ao salvar: " + e.getMessage());
        } finally {
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