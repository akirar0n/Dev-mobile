package com.example.govacation;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DetalhesReserva extends AppCompatActivity {

    TextView tvDetalheTituloLoc, tvDetalheLocal, tvDetalheHospedes, tvDetalhePreco, tvDetalheDescr;
    ImageView ivDetalheImagem;
    EditText etDataCheckin, etDataCheckout;
    Spinner spinnerMetodoPag;
    Button btnConfirmarReserva;
    BDHelper dbHelper;
    private long idLocacao = -1;
    private long idUsuario = -1;
    private Calendar dataCheckinSelecionada;
    private Calendar dataCheckoutSelecionada;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_reserva);

        dbHelper = new BDHelper(this);

        idLocacao = getIntent().getLongExtra("ID_LOCACAO", -1);
        idUsuario = getIntent().getLongExtra("ID_USUARIO", -1);

        if (idLocacao == -1 || idUsuario == -1) {
            Toast.makeText(this, "Erro: ID de Locação ou Usuário não encontrado.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ivDetalheImagem = findViewById(R.id.ivDetalheImagem);
        tvDetalheTituloLoc = findViewById(R.id.tvDetalheTituloLoc);
        tvDetalheLocal = findViewById(R.id.tvDetalheLocal);
        tvDetalheHospedes = findViewById(R.id.tvDetalheHospedes);
        tvDetalhePreco = findViewById(R.id.tvDetalhePreco);
        tvDetalheDescr = findViewById(R.id.tvDetalheDescr);

        etDataCheckin = findViewById(R.id.etDataCheckin);
        etDataCheckout = findViewById(R.id.etDataCheckout);
        spinnerMetodoPag = findViewById(R.id.spinnerMetodoPag);
        btnConfirmarReserva = findViewById(R.id.btnConfirmarReserva);

        carregarDetalhesLocacao();

        configurarSpinnerPagamento();

        configurarSeletoresData();

        btnConfirmarReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tentarConfirmarReserva();
            }
        });
    }

    private void carregarDetalhesLocacao() {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = "idloc = ?";
            String[] selectionArgs = {String.valueOf(idLocacao)};

            cursor = db.query(
                    "locacoes",
                    null,
                    selection,
                    selectionArgs,
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String local = cursor.getString(cursor.getColumnIndexOrThrow("localizacao"));
                int hospedes = cursor.getInt(cursor.getColumnIndexOrThrow("qtdhospedes"));
                double preco = cursor.getDouble(cursor.getColumnIndexOrThrow("preco"));
                String descr = cursor.getString(cursor.getColumnIndexOrThrow("descr"));
                String nomeImagem = cursor.getString(cursor.getColumnIndexOrThrow("imagem"));

                String precoFormatado = String.format(new Locale("pt", "BR"), "R$ %.2f", preco);
                String hospedesFormatado = "Até " + hospedes + " hóspedes";

                tvDetalheTituloLoc.setText(titulo);
                tvDetalheLocal.setText(local);
                tvDetalheHospedes.setText(hospedesFormatado);
                tvDetalhePreco.setText(precoFormatado);
                tvDetalheDescr.setText(descr);

                int idImagem = this.getResources().getIdentifier(
                        nomeImagem,
                        "drawable",
                        this.getPackageName()
                );

                if (idImagem != 0) {
                    ivDetalheImagem.setImageResource(idImagem);
                } else {
                    ivDetalheImagem.setImageResource(R.drawable.govac);
                }

            } else {
                Toast.makeText(this, "Locação não encontrada.", Toast.LENGTH_SHORT).show();
                finish();
            }

        } catch (Exception e) {
            Log.e("DetalhesReserva", "Erro ao carregar detalhes da locação", e);
            Toast.makeText(this, "Erro ao carregar detalhes: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
    }


    private void configurarSpinnerPagamento() {
        String[] metodos = new String[]{
                "Selecione um método...",
                "Cartão de Crédito",
                "Cartão de Débito",
                "PIX",
                "Boleto"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, metodos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMetodoPag.setAdapter(adapter);
    }

    private void configurarSeletoresData() {
        etDataCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoCheckin();
            }
        });

        etDataCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoCheckout();
            }
        });
    }

    private void mostrarDialogoCheckin() {
        Calendar calendario = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dataCheckinSelecionada = Calendar.getInstance();
                        dataCheckinSelecionada.set(year, month, dayOfMonth);

                        SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        etDataCheckin.setText(sdfDisplay.format(dataCheckinSelecionada.getTime()));

                        etDataCheckout.setEnabled(true);
                        etDataCheckout.setText("");
                        dataCheckoutSelecionada = null;
                    }
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void mostrarDialogoCheckout() {
        Calendar minCheckout = (Calendar) dataCheckinSelecionada.clone();
        minCheckout.add(Calendar.DAY_OF_MONTH, 1);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dataCheckoutSelecionada = Calendar.getInstance();
                        dataCheckoutSelecionada.set(year, month, dayOfMonth);

                        SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        etDataCheckout.setText(sdfDisplay.format(dataCheckoutSelecionada.getTime()));
                    }
                },
                minCheckout.get(Calendar.YEAR),
                minCheckout.get(Calendar.MONTH),
                minCheckout.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(minCheckout.getTimeInMillis());
        datePickerDialog.show();
    }

    private void tentarConfirmarReserva() {
        if (dataCheckinSelecionada == null) {
            exibirAviso("Erro", "Por favor, selecione uma data de check-in.");
            return;
        }
        if (dataCheckoutSelecionada == null) {
            exibirAviso("Erro", "Por favor, selecione uma data de check-out.");
            return;
        }
        if (spinnerMetodoPag.getSelectedItemPosition() == 0) {
            exibirAviso("Erro", "Por favor, selecione um método de pagamento.");
            return;
        }

        String metodoPag = spinnerMetodoPag.getSelectedItem().toString();
        String dataCheckinSQL = sdf.format(dataCheckinSelecionada.getTime());
        String dataCheckoutSQL = sdf.format(dataCheckoutSelecionada.getTime());

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            ContentValues reservaValues = new ContentValues();
            reservaValues.put("idusuario", idUsuario);
            reservaValues.put("idloc", idLocacao);
            reservaValues.put("metodopag", metodoPag);
            reservaValues.put("datacheckin", dataCheckinSQL);
            reservaValues.put("datacheckout", dataCheckoutSQL);
            long idReserva = db.insertOrThrow("reservas", null, reservaValues);

            if (idReserva == -1) {
                throw new Exception("Falha ao criar reserva.");
            }

            ContentValues locacaoValues = new ContentValues();
            locacaoValues.put("disp", "Indisponível");
            String selection = "idloc = ?";
            String[] selectionArgs = {String.valueOf(idLocacao)};
            int rowsAffected = db.update("locacoes", locacaoValues, selection, selectionArgs);

            if (rowsAffected == 0) {
                throw new Exception("Falha ao atualizar status da locação.");
            }

            db.setTransactionSuccessful();

            exibirAviso("Sucesso!", "Sua reserva foi confirmada com sucesso.");
            finish();

        } catch (Exception e) {
            Log.e("DetalhesReserva", "Erro ao confirmar reserva", e);
            exibirAviso("Erro no Banco", "Não foi possível completar a reserva: " + e.getMessage());
        } finally {
            if (db != null) {
                if (db.inTransaction()) {
                    db.endTransaction();
                }
                if (db.isOpen()) {
                    db.close();
                }
            }
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