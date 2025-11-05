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

        btVoltarListar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarLocacoes();
    }

    private void carregarLocacoes() {
        listaLocacoes = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query("locacoes", null, null, null, null, null, "titulo ASC"); // Ordena por título

            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow("idloc"));
                    String tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipoloc"));
                    String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                    String img = cursor.getString(cursor.getColumnIndexOrThrow("imagem"));
                    String descr = cursor.getString(cursor.getColumnIndexOrThrow("descr"));
                    double preco = cursor.getDouble(cursor.getColumnIndexOrThrow("preco"));
                    String local = cursor.getString(cursor.getColumnIndexOrThrow("localizacao"));
                    int hospedes = cursor.getInt(cursor.getColumnIndexOrThrow("qtdhospedes"));
                    String disp = cursor.getString(cursor.getColumnIndexOrThrow("disp"));

                    listaLocacoes.add(new Locacao(id, tipo, titulo, img, descr, preco, local, hospedes, disp));

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao carregar locações: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }

        if (adapter == null) {
            adapter = new LocacaoListAdapter(this, R.layout.list_item_locacao, listaLocacoes);
            listViewLocacoes.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(listaLocacoes);
            adapter.notifyDataSetChanged();
        }
    }

    public void alterarLocacao(long id) {
        Intent intent = new Intent(this, AlterarLocs.class);
        intent.putExtra("ID_LOCACAO", id);
        startActivity(intent);
    }

    public void excluirLocacao(long id, String titulo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar Exclusão")
                .setMessage("Tem certeza que deseja excluir a locação '" + titulo + "'?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        realizarExclusao(id);
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void realizarExclusao(long id) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String selection = "idloc = ?";
            String[] selectionArgs = {String.valueOf(id)};

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