package com.example.govacation;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ListarLocs extends AppCompatActivity {

    ListView listViewLocacoes;
    Button btVoltarListar;
    BDHelper dbHelper;
    ArrayList<Locacao> listaLocacoes;
    LocacaoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_locs);

        dbHelper = new BDHelper(this);
        listViewLocacoes = findViewById(R.id.listViewLocacoes);
        btVoltarListar = findViewById(R.id.btVoltarListar);

        // Botão para voltar à tela anterior (GerenciamentoLocs)
        btVoltarListar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Fecha a activity atual
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Carrega (ou recarrega) as locações toda vez que a tela
        // fica visível (ex: depois de alterar ou cadastrar)
        carregarLocacoes();
    }

    private void carregarLocacoes() {
        listaLocacoes = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            // Query para buscar todas as locações
            cursor = db.query("locacoes", null, null, null, null, null, "titulo ASC"); // Ordena por título

            if (cursor.moveToFirst()) {
                do {
                    // Pega os dados de cada coluna da linha atual
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow("idloc"));
                    String tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipoloc"));
                    String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                    String img = cursor.getString(cursor.getColumnIndexOrThrow("imagem"));
                    String descr = cursor.getString(cursor.getColumnIndexOrThrow("descr"));
                    double preco = cursor.getDouble(cursor.getColumnIndexOrThrow("preco"));
                    String local = cursor.getString(cursor.getColumnIndexOrThrow("localizacao"));
                    int hospedes = cursor.getInt(cursor.getColumnIndexOrThrow("qtdhospedes"));
                    String disp = cursor.getString(cursor.getColumnIndexOrThrow("disp"));

                    // Cria um objeto Locacao e adiciona na lista
                    listaLocacoes.add(new Locacao(id, tipo, titulo, img, descr, preco, local, hospedes, disp));

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao carregar locações: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }

        // Configura o Adapter
        if (adapter == null) {
            // Passa o layout de CADA ITEM da lista (list_item_locacao.xml)
            adapter = new LocacaoListAdapter(this, R.layout.list_item_locacao, listaLocacoes);
            listViewLocacoes.setAdapter(adapter);
        } else {
            // Se o adapter já existe, apenas limpa e atualiza os dados
            adapter.clear();
            adapter.addAll(listaLocacoes);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Este método é chamado pelo Adapter (LocacaoListAdapter)
     * @param id O ID da locação a ser alterada
     */
    public void alterarLocacao(long id) {
        // TODO: Criar a Activity "AlterarLocacaoActivity.java" e seu layout
        Intent intent = new Intent(this, AlterarLocs.class);
        // Passa o ID da locação para a próxima tela
        intent.putExtra("ID_LOCACAO", id);
        startActivity(intent);
    }

    /**
     * Este método é chamado pelo Adapter (LocacaoListAdapter)
     * @param id O ID da locação a ser excluída
     * @param titulo O título (apenas para mostrar na confirmação)
     */
    public void excluirLocacao(long id, String titulo) {
        // Cria um pop-up de confirmação para evitar exclusão acidental
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar Exclusão")
                .setMessage("Tem certeza que deseja excluir a locação '" + titulo + "'?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Se o usuário clicar "Sim", executa a exclusão
                        realizarExclusao(id);
                    }
                })
                .setNegativeButton("Não", null) // "Não" apenas fecha o diálogo
                .show();
    }

    private void realizarExclusao(long id) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            // Define a cláusula WHERE
            String selection = "idloc = ?";
            String[] selectionArgs = {String.valueOf(id)};

            // Executa o delete
            int count = db.delete("locacoes", selection, selectionArgs);

            if (count > 0) {
                Toast.makeText(this, "Locação excluída com sucesso!", Toast.LENGTH_SHORT).show();
                carregarLocacoes(); // Recarrega a lista para mostrar a mudança
            } else {
                Toast.makeText(this, "Erro: Nenhuma locação foi excluída.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao excluir: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null && db.isOpen()) db.close();
        }
    }
}