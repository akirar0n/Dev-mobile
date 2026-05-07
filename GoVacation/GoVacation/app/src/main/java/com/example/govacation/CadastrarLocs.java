package com.example.govacation;

import android.content.ContentValues;
import android.content.Intent;
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

public class CadastrarLocs extends AppCompatActivity {

    EditText edTipoLocCad, edTituloLocCad, edDescrLocCad,
            edPrecoLocCad, edLocalLocCad, edHospedesLocCad;
    Spinner spDispLocCad;
    Button btSalvarCadastro, btCancelarCadastro, btnEscolherImagem;
    ImageView ivPreviewLocacao;

    BDHelper dbHelper;

    // Variável para armazenar o caminho da imagem no celular
    private String stringUriImagem = "";

    // Lançador moderno para abrir a galeria e capturar a foto
    private final ActivityResultLauncher<String> abridorDeGaleria = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    // Pede permissão permanente ao Android para manter o acesso a essa foto
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    // Salva o endereço para o banco de dados
                    stringUriImagem = uri.toString();

                    // Exibe a miniatura da foto escolhida na tela
                    ivPreviewLocacao.setImageURI(uri);
                }
            }
    );

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
        edDescrLocCad = findViewById(R.id.edDescrLocCad);
        edPrecoLocCad = findViewById(R.id.edPrecoLocCad);
        edLocalLocCad = findViewById(R.id.edLocalLocCad);
        edHospedesLocCad = findViewById(R.id.edHospedesLocCad);
        spDispLocCad = findViewById(R.id.spDispLocCad);

        // Novos componentes da imagem
        ivPreviewLocacao = findViewById(R.id.ivPreviewLocacao);
        btnEscolherImagem = findViewById(R.id.btnEscolherImagem);

        btSalvarCadastro = findViewById(R.id.btSalvarCadastro);
        btCancelarCadastro = findViewById(R.id.btCancelarCadastro);
    }

    private void configurarListeners() {
        // Listener para o botão de abrir galeria
        btnEscolherImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre o seletor pedindo qualquer tipo de imagem (png, jpg, etc)
                abridorDeGaleria.launch("image/*");
            }
        });

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
        String descr = edDescrLocCad.getText().toString().trim();
        String precoStr = edPrecoLocCad.getText().toString().trim();
        String local = edLocalLocCad.getText().toString().trim();
        String hospedesStr = edHospedesLocCad.getText().toString().trim();
        String disp = spDispLocCad.getSelectedItem().toString();

        // Validação de textos vazios
        if (tipo.isEmpty() || titulo.isEmpty() || precoStr.isEmpty() || local.isEmpty() || hospedesStr.isEmpty() || disp.isEmpty()) {
            exibirAviso("Campos Vazios", "Por favor, preencha todos os campos.");
            return;
        }

//        // Validação da imagem
//        if (stringUriImagem.isEmpty()) {
//            exibirAviso("Imagem Ausente", "Por favor, escolha uma imagem da galeria.");
//            return;
//        }

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
            values.put("imagem", stringUriImagem); // Aqui vai o caminho capturado da galeria!
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