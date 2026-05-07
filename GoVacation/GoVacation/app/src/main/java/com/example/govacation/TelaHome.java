package com.example.govacation;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class TelaHome extends AppCompatActivity {

    ListView listViewLocacoesCliente;
    Button btLogoutHome;
    BDHelper dbHelper;
    ArrayList<Locacao> listaLocacoes;
    LocacaoClienteAdapter adapter;
    private long idUsuarioLogado = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_home);

        dbHelper = new BDHelper(this);
        listViewLocacoesCliente = findViewById(R.id.listViewLocacoesCliente);
        btLogoutHome = findViewById(R.id.btLogoutHome);
        idUsuarioLogado = getIntent().getLongExtra("ID_USUARIO", -1);

        btLogoutHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaHome.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarLocacoesDisponiveis();
    }

    private void carregarLocacoesDisponiveis() {
        listaLocacoes = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            String selection = "disp = ?";
            String[] selectionArgs = {"Disponível"};

            cursor = db.query("locacoes",
                    null,
                    selection,
                    selectionArgs,
                    null, null,
                    "preco ASC");

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
            adapter = new LocacaoClienteAdapter(this, R.layout.list_item_locacao_cliente, listaLocacoes);
            listViewLocacoesCliente.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(listaLocacoes);
            adapter.notifyDataSetChanged();
        }
    }

    public void verDetalhesReserva(long id) {
        Intent intent = new Intent(this, DetalhesReserva.class);
        intent.putExtra("ID_LOCACAO", id);
        intent.putExtra("ID_USUARIO", idUsuarioLogado);
        startActivity(intent);
    }
}