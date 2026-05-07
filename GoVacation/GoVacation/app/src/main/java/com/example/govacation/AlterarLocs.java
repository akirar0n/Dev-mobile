package com.example.govacation;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class AlterarLocs extends AppCompatActivity {

    EditText edTipoLocAlt, edTituloLocAlt, edDescrLocAlt,
            edPrecoLocAlt, edLocalLocAlt, edHospedesLocAlt;
    Spinner spDispLocAlt;
    ImageView ivPreviewLocAlt;
    Button btSalvarAlteracoes, btCancelarAlterar, btnEscolherImagemAlt;

    BDHelper dbHelper;
    private long locacaoId = -1;

    // Variável para armazenar o caminho da imagem
    private String stringUriImagem = "";

    // Lançador moderno para abrir a galeria
    private final ActivityResultLauncher<String> abridorDeGaleria = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    stringUriImagem = uri.toString();
                    ivPreviewLocAlt.setImageURI(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_locs);

        dbHelper = new BDHelper(this);

        inicializarComponentes();

        Intent intent = getIntent();
        locacaoId = intent.getLongExtra("ID_LOCACAO", -1);

        if (locacaoId == -1) {
            Toast.makeText(this, "Erro: ID da locação não encontrado.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        carregarDadosLocacao();
        configurarListeners();
    }

    private void inicializarComponentes() {
        edTipoLocAlt = findViewById(R.id.edTipoLocAlt);
        edTituloLocAlt = findViewById(R.id.edTituloLocAlt);
        edDescrLocAlt = findViewById(R.id.edDescrLocAlt);
        edPrecoLocAlt = findViewById(R.id.edPrecoLocAlt);
        edLocalLocAlt = findViewById(R.id.edLocalLocAlt);
        edHospedesLocAlt = findViewById(R.id.edHospedesLocAlt);

        // Componentes atualizados para adequação ao novo XML
        spDispLocAlt = findViewById(R.id.spDispLocAlt);
        ivPreviewLocAlt = findViewById(R.id.ivPreviewLocAlt);
        btnEscolherImagemAlt = findViewById(R.id.btnEscolherImagemAlt);

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

            cursor = db.query("locacoes", null, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                edTipoLocAlt.setText(cursor.getString(cursor.getColumnIndexOrThrow("tipoloc")));
                edTituloLocAlt.setText(cursor.getString(cursor.getColumnIndexOrThrow("titulo")));
                edDescrLocAlt.setText(cursor.getString(cursor.getColumnIndexOrThrow("descr")));
                edLocalLocAlt.setText(cursor.getString(cursor.getColumnIndexOrThrow("localizacao")));

                double preco = cursor.getDouble(cursor.getColumnIndexOrThrow("preco"));
                int hospedes = cursor.getInt(cursor.getColumnIndexOrThrow("qtdhospedes"));

                edPrecoLocAlt.setText(String.format(Locale.US, "%.2f", preco));
                edHospedesLocAlt.setText(String.valueOf(hospedes));

                // 1. Carregando a Disponibilidade no Spinner
                String dispBanco = cursor.getString(cursor.getColumnIndexOrThrow("disp"));
                if ("Disponível".equalsIgnoreCase(dispBanco)) {
                    spDispLocAlt.setSelection(0);
                } else if ("Indisponível".equalsIgnoreCase(dispBanco)) {
                    spDispLocAlt.setSelection(1);
                }

                // 2. Carregando a Imagem de forma mista (Antiga dos drawables vs Nova da Galeria)
                String imagemBanco = cursor.getString(cursor.getColumnIndexOrThrow("imagem"));
                stringUriImagem = imagemBanco != null ? imagemBanco : "";

                if (stringUriImagem.startsWith("content://")) {
                    // É uma foto da galeria salva recentemente
                    ivPreviewLocAlt.setImageURI(Uri.parse(stringUriImagem));
                } else {
                    // É um texto antigo do seu protótipo (ex: "hotelpraia")
                    int idImagem = getResources().getIdentifier(stringUriImagem, "drawable", getPackageName());
                    if (idImagem != 0) {
                        ivPreviewLocAlt.setImageResource(idImagem);
                    }
                }

            } else {
                Toast.makeText(this, "Locação não encontrada.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            exibirAviso("Erro", "Falha ao carregar dados: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
    }

    private void configurarListeners() {
        btnEscolherImagemAlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abridorDeGaleria.launch("image/*");
            }
        });

        btSalvarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarAlteracoes();
            }
        });

        btCancelarAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void salvarAlteracoes() {
        String tipo = edTipoLocAlt.getText().toString().trim();
        String titulo = edTituloLocAlt.getText().toString().trim();
        String descr = edDescrLocAlt.getText().toString().trim();
        String precoStr = edPrecoLocAlt.getText().toString().trim();
        String local = edLocalLocAlt.getText().toString().trim();
        String hospedesStr = edHospedesLocAlt.getText().toString().trim();
        String disp = spDispLocAlt.getSelectedItem().toString(); // Coleta do Spinner

        if (tipo.isEmpty() || titulo.isEmpty() || precoStr.isEmpty() || local.isEmpty() || hospedesStr.isEmpty() || disp.isEmpty()) {
            exibirAviso("Campos Vazios", "Por favor, preencha todos os campos obrigatórios.");
            return;
        }

//        if (stringUriImagem.isEmpty()) {
//            exibirAviso("Imagem Ausente", "Por favor, defina uma imagem para a locação.");
//            return;
//        }

        double preco;
        int hospedes;

        try {
            preco = Double.parseDouble(precoStr);
            hospedes = Integer.parseInt(hospedesStr);
        } catch (NumberFormatException e) {
            exibirAviso("Erro de Formato", "Preço ou Qtd. Hóspedes inválidos.");
            return;
        }

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("tipoloc", tipo);
            values.put("titulo", titulo);
            values.put("imagem", stringUriImagem); // Salva o novo ou antigo endereço da imagem
            values.put("descr", descr);
            values.put("preco", preco);
            values.put("localizacao", local);
            values.put("qtdhospedes", hospedes);
            values.put("disp", disp);

            String whereClause = "idloc = ?";
            String[] whereArgs = {String.valueOf(locacaoId)};

            int rowsAffected = db.update("locacoes", values, whereClause, whereArgs);

            if (rowsAffected > 0) {
                Toast.makeText(this, "Locação alterada com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
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