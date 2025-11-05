package com.example.govacation;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DetalhesReserva extends AppCompatActivity {

    TextView tvDetalheTitulo, tvDetalheLocal, tvDetalhePreco, tvDetalheHospedes,
            tvDetalheDescricao, tvDataCheckin, tvDataCheckout;
    EditText edMetodoPag;
    Button btConfirmarReserva, btVoltarDetalhes;
    BDHelper dbHelper;
    private long idLocacao = -1;
    private long idUsuario = -1;
    private Calendar calendarioCheckin;
    private Calendar calendarioCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_reserva);

        dbHelper = new BDHelper(this);
        calendarioCheckin = Calendar.getInstance();
        calendarioCheckout = Calendar.getInstance();

        inicializarComponentes();

        Intent intent = getIntent();
        idLocacao = intent.getLongExtra("ID_LOCACAO", -1);

        idUsuario = intent.getLongExtra("ID_USUARIO", -1);

        if (idLocacao == -1 || idUsuario == -1) {
            exibirAviso("Erro Crítico", "ID da Locação ou do Usuário não encontrado. Voltando...");
            finish();
            return;
        }

        carregarDetalhesLocacao();

        configurarListeners();
    }

    private void inicializarComponentes() {
        tvDetalheTitulo = findViewById(R.id.tvDetalheTitulo);
        tvDetalheLocal = findViewById(R.id.tvDetalheLocal);
        tvDetalhePreco = findViewById(R.id.tvDetalhePreco);
        tvDetalheHospedes = findViewById(R.id.tvDetalheHospedes);
        tvDetalheDescricao = findViewById(R.id.tvDetalheDescricao);
        tvDataCheckin = findViewById(R.id.tvDataCheckin);
        tvDataCheckout = findViewById(R.id.tvDataCheckout);
        edMetodoPag = findViewById(R.id.edMetodoPag);
        btConfirmarReserva = findViewById(R.id.btConfirmarReserva);
        btVoltarDetalhes = findViewById(R.id.btVoltarDetalhes);
    }

    private void carregarDetalhesLocacao() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query("locacoes", null, "idloc = ?", new String[]{String.valueOf(idLocacao)}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                tvDetalheTitulo.setText(cursor.getString(cursor.getColumnIndexOrThrow("titulo")));
                tvDetalheLocal.setText(cursor.getString(cursor.getColumnIndexOrThrow("localizacao")));
                tvDetalheDescricao.setText(cursor.getString(cursor.getColumnIndexOrThrow("descr")));

                double preco = cursor.getDouble(cursor.getColumnIndexOrThrow("preco"));
                tvDetalhePreco.setText(String.format(new Locale("pt", "BR"), "R$ %.2f", preco));

                int hospedes = cursor.getInt(cursor.getColumnIndexOrThrow("qtdhospedes"));
                tvDetalheHospedes.setText("Até " + hospedes + " hóspedes");
            }
        } catch (Exception e) {
            exibirAviso("Erro", "Não foi possível carregar os detalhes: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
    }

    private void configurarListeners() {
        btVoltarDetalhes.setOnClickListener(v -> finish()); // Apenas fecha esta tela

        btConfirmarReserva.setOnClickListener(v -> confirmarReserva());

        configurarDatePickers();
    }

    private void configurarDatePickers() {
        DatePickerDialog.OnDateSetListener dateSetListenerCheckin = (view, year, month, dayOfMonth) -> {
            calendarioCheckin.set(Calendar.YEAR, year);
            calendarioCheckin.set(Calendar.MONTH, month);
            calendarioCheckin.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            atualizarLabelData(tvDataCheckin, calendarioCheckin);
        };

        DatePickerDialog.OnDateSetListener dateSetListenerCheckout = (view, year, month, dayOfMonth) -> {
            calendarioCheckout.set(Calendar.YEAR, year);
            calendarioCheckout.set(Calendar.MONTH, month);
            calendarioCheckout.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            atualizarLabelData(tvDataCheckout, calendarioCheckout);
        };

        tvDataCheckin.setOnClickListener(v -> {
            new DatePickerDialog(DetalhesReserva.this, dateSetListenerCheckin,
                    calendarioCheckin.get(Calendar.YEAR),
                    calendarioCheckin.get(Calendar.MONTH),
                    calendarioCheckin.get(Calendar.DAY_OF_MONTH)).show();
        });

        tvDataCheckout.setOnClickListener(v -> {
            new DatePickerDialog(DetalhesReserva.this, dateSetListenerCheckout,
                    calendarioCheckout.get(Calendar.YEAR),
                    calendarioCheckout.get(Calendar.MONTH),
                    calendarioCheckout.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void atualizarLabelData(TextView tv, Calendar calendario) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
        tv.setText(sdf.format(calendario.getTime()));
    }

    private String formatarDataParaSQLite(Calendar calendario) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(calendario.getTime());
    }

    private void confirmarReserva() {
        String dataCheckin = tvDataCheckin.getText().toString();
        String dataCheckout = tvDataCheckout.getText().toString();
        String metodoPag = edMetodoPag.getText().toString().trim();

        if (dataCheckin.equals("Selecionar data") || dataCheckout.equals("Selecionar data")) {
            exibirAviso("Datas Inválidas", "Por favor, selecione as datas de check-in e check-out.");
            return;
        }
        if (metodoPag.isEmpty()) {
            edMetodoPag.setError("Método de pagamento é obrigatório.");
            edMetodoPag.requestFocus();
            return;
        }
        if (!calendarioCheckout.after(calendarioCheckin)) {
            exibirAviso("Datas Inválidas", "A data de check-out deve ser após a data de check-in.");
            return;
        }

        String checkinSQLite = formatarDataParaSQLite(calendarioCheckin);
        String checkoutSQLite = formatarDataParaSQLite(calendarioCheckout);

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("idusuario", this.idUsuario);
            values.put("idloc", this.idLocacao);
            values.put("metodopag", metodoPag);
            values.put("datacheckin", checkinSQLite);
            values.put("datacheckout", checkoutSQLite);

            long newRowId = db.insert("reservas", null, values);

            if (newRowId != -1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Reserva Confirmada!")
                        .setMessage("Sua reserva para " + tvDetalheTitulo.getText() + " foi realizada com sucesso.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            finish();
                        })
                        .setCancelable(false)
                        .show();
            } else {
                exibirAviso("Erro", "Não foi possível completar a reserva.");
            }

        } catch (Exception e) {
            exibirAviso("Erro no Banco", "Falha ao salvar reserva: " + e.getMessage());
            Log.e("DetalhesReserva", "Erro ao inserir reserva", e);
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