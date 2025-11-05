package com.example.govacation;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class AlterarLocs extends AppCompatActivity {

    // Componentes do Layout
    EditText edTipoLocAlt, edTituloLocAlt, edImagemLocAlt, edDescrLocAlt,
            edPrecoLocAlt, edLocalLocAlt, edHospedesLocAlt, edDispLocAlt;
    Button btSalvarAlteracoes, btCancelarAlterar;

    // Banco de Dados
    BDHelper dbHelper;
    private long locacaoId = -1; // Armazena o ID da locação a ser alterada

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_locs);

        dbHelper = new BDHelper(this);

        // 1. Linkar componentes do XML
        inicializarComponentes();

        // 2. Pegar o ID enviado pela ListarLocacoesActivity
        Intent intent = getIntent();
        locacaoId = intent.getLongExtra("ID_LOCACAO", -1);

        if (locacaoId == -1) {
            // Se o ID não foi passado, é um erro.
            Toast.makeText(this, "Erro: ID da locação não encontrado.", Toast.LENGTH_LONG).show();
            finish(); // Fecha a activity
            return;
        }

        // 3. Carregar os dados existentes no formulário
        carregarDadosLocacao();

        // 4. Configurar os cliques dos botões
        configurarListeners();
    }

    private void inicializarComponentes() {
        edTipoLocAlt = findViewById(R.id.edTipoLocAlt);
        edTituloLocAlt = findViewById(R.id.edTituloLocAlt);
        edImagemLocAlt = findViewById(R.id.edImagemLocAlt);
        edDescrLocAlt = findViewById(R.id.edDescrLocAlt);
        edPrecoLocAlt = findViewById(R.id.edPrecoLocAlt);
        edLocalLocAlt = findViewById(R.id.edLocalLocAlt);
        edHospedesLocAlt = findViewById(R.id.edHospedesLocAlt);
        edDispLocAlt = findViewById(R.id.edDispLocAlt);
        btSalvarAlteracoes = findViewById(R.id.btSalvarAlteracoes);
        btCancelarAlterar = findViewById(R.id.btCancelarAlterar);
    }

    private void carregarDadosLocacao() {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = "idloc = ?";
            String[] selectionArgs = {String.valueOf(locacaoId)};

            // Busca a locação específica pelo ID
            cursor = db.query("locacoes", null, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                // Popula os campos com os dados do banco
                edTipoLocAlt.setText(cursor.getString(cursor.getColumnIndexOrThrow("tipoloc")));
                edTituloLocAlt.setText(cursor.getString(cursor.getColumnIndexOrThrow("titulo")));
                edImagemLocAlt.setText(cursor.getString(cursor.getColumnIndexOrThrow("imagem")));
                edDescrLocAlt.setText(cursor.getString(cursor.getColumnIndexOrThrow("descr")));

                // Formata o preço e qtd de hóspedes para String
                double preco = cursor.getDouble(cursor.getColumnIndexOrThrow("preco"));
                int hospedes = cursor.getInt(cursor.getColumnIndexOrThrow("qtdhospedes"));

                // Usamos Locale.US (ponto) para garantir que Double.parseDouble funcione
                edPrecoLocAlt.setText(String.format(Locale.US, "%.2f", preco));
                edHospedesLocAlt.setText(String.valueOf(hospedes));

                edLocalLocAlt.setText(cursor.getString(cursor.getColumnIndexOrThrow("localizacao")));
                edDispLocAlt.setText(cursor.getString(cursor.getColumnIndexOrThrow("disp")));
            } else {
                Toast.makeText(this, "Locação não encontrada.", Toast.LENGTH_SHORT).show();
                finish(); // Volta se não encontrar a locação
            }
        } catch (Exception e) {
            exibirAviso("Erro", "Falha ao carregar dados: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
    }

    private void configurarListeners() {
        // Botão Salvar
        btSalvarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarAlteracoes();
            }
        });

        // Botão Cancelar
        btCancelarAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Simplesmente fecha a tela e volta para a lista
            }
        });
    }

    private void salvarAlteracoes() {
        // 1. Pegar todos os dados dos EditTexts
        String tipo = edTipoLocAlt.getText().toString().trim();
        String titulo = edTituloLocAlt.getText().toString().trim();
        String imagem = edImagemLocAlt.getText().toString().trim();
        String descr = edDescrLocAlt.getText().toString().trim();
        String precoStr = edPrecoLocAlt.getText().toString().trim();
        String local = edLocalLocAlt.getText().toString().trim();
        String hospedesStr = edHospedesLocAlt.getText().toString().trim();
        String disp = edDispLocAlt.getText().toString().trim();

        // 2. Validação (simples, igual ao cadastro)
        if (tipo.isEmpty() || titulo.isEmpty() || precoStr.isEmpty() || local.isEmpty() || hospedesStr.isEmpty() || disp.isEmpty()) {
            exibirAviso("Campos Vazios", "Por favor, preencha todos os campos obrigatórios.");
            return;
        }

        double preco;
        int hospedes;

        try {
            preco = Double.parseDouble(precoStr);
            hospedes = Integer.parseInt(hospedesStr);
        } catch (NumberFormatException e) {
            exibirAviso("Erro de Formato", "Preço ou Qtd. Hóspedes inválidos.");
            return;
        }

        // 3. Lógica de UPDATE
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("tipoloc", tipo);
            values.put("titulo", titulo);
            values.put("imagem", imagem);
            values.put("descr", descr);
            values.put("preco", preco);
            values.put("localizacao", local);
            values.put("qtdhospedes", hospedes);
            values.put("disp", disp);

            // Define a cláusula WHERE para o UPDATE
            String whereClause = "idloc = ?";
            String[] whereArgs = {String.valueOf(locacaoId)};

            // Executa o UPDATE
            int rowsAffected = db.update("locacoes", values, whereClause, whereArgs);

            if (rowsAffected > 0) {
                Toast.makeText(this, "Locação alterada com sucesso!", Toast.LENGTH_SHORT).show();
                finish(); // Fecha a tela e volta para a lista (que vai se atualizar)
            } else {
                exibirAviso("Erro", "Nenhuma locação foi alterada. Verifique os dados.");
            }

        } catch (Exception e) {
            exibirAviso("Erro no Banco", "Falha ao salvar alterações: " + e.getMessage());
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