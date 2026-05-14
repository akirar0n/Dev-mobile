package com.example.govacation.ui.client;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.govacation.R;
import com.example.govacation.data.BDHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MinhasReservas extends AppCompatActivity {

    private ListView lvMinhasReservas;
    private Button btVoltarReservas;
    private BDHelper dbHelper;
    private long idUsuarioAtual = -1;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhas_reservas); // Layout da tela principal

        dbHelper = new BDHelper(this);

        // INICIALIZAÇÃO OBRIGATÓRIA (Removido o comentário)
        lvMinhasReservas = findViewById(R.id.lvMinhasReservas);
        btVoltarReservas = findViewById(R.id.btVoltarReservas);

        idUsuarioAtual = getIntent().getLongExtra("ID_USUARIO", -1);

        if (idUsuarioAtual == -1) {
            Toast.makeText(this, "Erro de autenticação.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btVoltarReservas.setOnClickListener(v -> finish());

        carregarListaDeReservas();
    }

    private void carregarListaDeReservas() {
        executor.execute(() -> {
            final List<HashMap<String, String>> dadosParaLista = new ArrayList<>();
            SQLiteDatabase db = null;
            Cursor cursor = null;

            try {
                db = dbHelper.getReadableDatabase();

                // Relação SQL: Busca dados de duas tabelas diferentes
                String query = "SELECT l.titulo, l.localizacao, r.datacheckin, r.datacheckout " +
                        "FROM reservas r " +
                        "INNER JOIN locacoes l ON r.idloc = l.idloc " +
                        "WHERE r.idusuario = ?";

                cursor = db.rawQuery(query, new String[]{String.valueOf(idUsuarioAtual)});

                if (cursor.moveToFirst()) {
                    do {
                        HashMap<String, String> item = new HashMap<>();
                        // Relacionando colunas do banco com chaves do Map
                        item.put("titulo", cursor.getString(0));
                        item.put("local", cursor.getString(1));
                        String periodo = "Check-in: " + cursor.getString(2) + " | Check-out: " + cursor.getString(3);
                        item.put("datas", periodo);

                        dadosParaLista.add(item);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                handler.post(() -> Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show());
            } finally {
                if (cursor != null) cursor.close();
                if (db != null) db.close();
            }

            handler.post(() -> {
                if (dadosParaLista.isEmpty()) {
                    Toast.makeText(this, "Nenhuma reserva encontrada.", Toast.LENGTH_SHORT).show();
                } else {
                    // RELAÇÃO FINAL: Map -> Layout da Linha
                    String[] chavesOrigem = {"titulo", "local", "datas"};
                    int[] idsDestino = {R.id.tvTituloReserva, R.id.tvLocalReserva, R.id.tvDatasReserva};

                    SimpleAdapter adapter = new SimpleAdapter(
                            MinhasReservas.this,
                            dadosParaLista,
                            R.layout.list_item_reserva, // USE O NOME DO ARQUIVO QUE VOCÊ CRIOU PARA A LINHA
                            chavesOrigem,
                            idsDestino
                    );

                    lvMinhasReservas.setAdapter(adapter);
                }
            });
        });
    }
}