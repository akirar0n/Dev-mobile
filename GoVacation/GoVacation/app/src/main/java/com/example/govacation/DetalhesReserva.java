package com.example.govacation;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetalhesReserva extends AppCompatActivity {

    // -----------------------------------------------------------------------
    // ⚙️  CONFIGURAÇÃO — coloque aqui sua chave real do AbacatePay
    // -----------------------------------------------------------------------
    private static final String ABACATEPAY_API_KEY = "abc_dev_MwqC1MGHgNeyatXMKUCUXHU5";
    private static final String ABACATEPAY_URL     = "https://api.abacatepay.com/v1/billing/create";
    // -----------------------------------------------------------------------

    TextView tvDetalheTituloLoc, tvDetalheLocal, tvDetalheHospedes, tvDetalhePreco, tvDetalheDescr;
    ImageView ivDetalheImagem;
    EditText etDataCheckin, etDataCheckout;
    Spinner spinnerMetodoPag;
    Button btnConfirmarReserva;
    BDHelper dbHelper;

    private long idLocacao  = -1;
    private long idUsuario  = -1;
    private Calendar dataCheckinSelecionada;
    private Calendar dataCheckoutSelecionada;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private double precoDiaria = 0.0;

    // Dados do cliente logado (carregados do BD para enviar à API)
    private String clienteNome     = "";
    private String clienteEmail    = "";
    private String clienteTelefone = "";
    private String clienteCpf      = "";

    private final ExecutorService executorDeRede = Executors.newSingleThreadExecutor();
    private final Handler handlerDaTela = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_reserva);

        dbHelper   = new BDHelper(this);
        idLocacao  = getIntent().getLongExtra("ID_LOCACAO", -1);
        idUsuario  = getIntent().getLongExtra("ID_USUARIO", -1);

        if (idLocacao == -1 || idUsuario == -1) {
            Toast.makeText(this, "Erro: ID de Locação ou Usuário não encontrado.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ivDetalheImagem      = findViewById(R.id.ivDetalheImagem);
        tvDetalheTituloLoc   = findViewById(R.id.tvDetalheTituloLoc);
        tvDetalheLocal       = findViewById(R.id.tvDetalheLocal);
        tvDetalheHospedes    = findViewById(R.id.tvDetalheHospedes);
        tvDetalhePreco       = findViewById(R.id.tvDetalhePreco);
        tvDetalheDescr       = findViewById(R.id.tvDetalheDescr);
        etDataCheckin        = findViewById(R.id.etDataCheckin);
        etDataCheckout       = findViewById(R.id.etDataCheckout);
        spinnerMetodoPag     = findViewById(R.id.spinnerMetodoPag);
        btnConfirmarReserva  = findViewById(R.id.btnConfirmarReserva);

        carregarDetalhesLocacao();
        carregarDadosCliente();      // ✅ Necessário para enviar ao AbacatePay
        configurarSpinnerPagamento();
        configurarSeletoresData();

        btnConfirmarReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tentarConfirmarReserva();
            }
        });
    }

    // -----------------------------------------------------------------------
    // Carregamento de dados
    // -----------------------------------------------------------------------

    private void carregarDetalhesLocacao() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query("locacoes", null, "idloc = ?",
                    new String[]{String.valueOf(idLocacao)}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                String titulo      = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String local       = cursor.getString(cursor.getColumnIndexOrThrow("localizacao"));
                int    hospedes    = cursor.getInt(cursor.getColumnIndexOrThrow("qtdhospedes"));
                double preco       = cursor.getDouble(cursor.getColumnIndexOrThrow("preco"));
                String descr       = cursor.getString(cursor.getColumnIndexOrThrow("descr"));
                String nomeImagem  = cursor.getString(cursor.getColumnIndexOrThrow("imagem"));

                this.precoDiaria = preco;
                tvDetalheTituloLoc.setText(titulo);
                tvDetalheLocal.setText(local);
                tvDetalheHospedes.setText("Até " + hospedes + " hóspedes");
                tvDetalhePreco.setText(String.format(new Locale("pt", "BR"), "R$ %.2f", preco));
                tvDetalheDescr.setText(descr);

                int idImagem = getResources().getIdentifier(nomeImagem, "drawable", getPackageName());
                ivDetalheImagem.setImageResource(idImagem != 0 ? idImagem : R.drawable.govac);

            } else {
                Toast.makeText(this, "Locação não encontrada.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Log.e("DetalhesReserva", "Erro ao carregar locação", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
    }

    /** Carrega nome, e-mail, telefone e CPF do usuário logado para enviar ao AbacatePay. */
    private void carregarDadosCliente() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query("usuario",
                    new String[]{"nome", "email", "telefone", "cpf"},
                    "idusuario = ?",
                    new String[]{String.valueOf(idUsuario)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                clienteNome     = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
                clienteEmail    = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                clienteTelefone = cursor.getString(cursor.getColumnIndexOrThrow("telefone"));
                clienteCpf      = cursor.getString(cursor.getColumnIndexOrThrow("cpf"));
            }
        } catch (Exception e) {
            Log.e("DetalhesReserva", "Erro ao carregar dados do cliente", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
    }

    // -----------------------------------------------------------------------
    // UI — Spinner e DatePickers
    // -----------------------------------------------------------------------

    private void configurarSpinnerPagamento() {
        // ✅ Apenas métodos suportados pela AbacatePay
        String[] metodos = {"Selecione um método...", "PIX", "Cartão de Crédito"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, metodos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMetodoPag.setAdapter(adapter);
    }

    private void configurarSeletoresData() {
        etDataCheckin.setOnClickListener(v -> mostrarDialogoCheckin());
        etDataCheckout.setOnClickListener(v -> mostrarDialogoCheckout());
    }

    private void mostrarDialogoCheckin() {
        Calendar hoje = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            dataCheckinSelecionada = Calendar.getInstance();
            dataCheckinSelecionada.set(year, month, day);
            etDataCheckin.setText(
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            .format(dataCheckinSelecionada.getTime()));
            etDataCheckout.setEnabled(true);
            etDataCheckout.setText("");
            dataCheckoutSelecionada = null;
        }, hoje.get(Calendar.YEAR), hoje.get(Calendar.MONTH), hoje.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void mostrarDialogoCheckout() {
        if (dataCheckinSelecionada == null) return;
        Calendar minCheckout = (Calendar) dataCheckinSelecionada.clone();
        minCheckout.add(Calendar.DAY_OF_MONTH, 1);

        DatePickerDialog dlg = new DatePickerDialog(this, (view, year, month, day) -> {
            dataCheckoutSelecionada = Calendar.getInstance();
            dataCheckoutSelecionada.set(year, month, day);
            etDataCheckout.setText(
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            .format(dataCheckoutSelecionada.getTime()));
        }, minCheckout.get(Calendar.YEAR), minCheckout.get(Calendar.MONTH),
                minCheckout.get(Calendar.DAY_OF_MONTH));

        dlg.getDatePicker().setMinDate(minCheckout.getTimeInMillis());
        dlg.show();
    }

    // -----------------------------------------------------------------------
    // Lógica de reserva e pagamento
    // -----------------------------------------------------------------------

    private double calcularValorTotal() {
        if (dataCheckinSelecionada == null || dataCheckoutSelecionada == null) return 0.0;
        long difMs   = dataCheckoutSelecionada.getTimeInMillis() - dataCheckinSelecionada.getTimeInMillis();
        long difDias = difMs / (1000 * 60 * 60 * 24);
        if (difDias <= 0) difDias = 1;
        return difDias * precoDiaria;
    }

    private void tentarConfirmarReserva() {
        if (dataCheckinSelecionada == null) {
            exibirAviso("Erro", "Selecione uma data de check-in.");
            return;
        }
        if (dataCheckoutSelecionada == null) {
            exibirAviso("Erro", "Selecione uma data de check-out.");
            return;
        }
        if (spinnerMetodoPag.getSelectedItemPosition() == 0) {
            exibirAviso("Erro", "Selecione um método de pagamento.");
            return;
        }

        String metodoPag   = spinnerMetodoPag.getSelectedItem().toString();
        double valorTotal  = calcularValorTotal();

        iniciarPagamentoAPI(metodoPag, valorTotal);
    }

    /**
     * Monta o payload correto para a AbacatePay e abre a URL de pagamento no navegador.
     *
     * Formato exigido pela API:
     *  - frequency, methods, products  → dados da cobrança
     *  - customer                      → obrigatório pelo Banco Central para PIX
     */
    private void iniciarPagamentoAPI(final String metodoPag, final double valorTotal) {
        Toast.makeText(this, "Gerando cobrança no AbacatePay…", Toast.LENGTH_SHORT).show();

        executorDeRede.execute(() -> {
            HttpURLConnection conexao = null;
            try {
                // ✅ Endpoint correto da AbacatePay
                URL url = new URL(ABACATEPAY_URL);
                conexao = (HttpURLConnection) url.openConnection();
                conexao.setRequestMethod("POST");
                conexao.setRequestProperty("Content-Type", "application/json");
                conexao.setRequestProperty("Authorization", "Bearer " + ABACATEPAY_API_KEY);
                conexao.setDoOutput(true);
                conexao.setConnectTimeout(15_000);
                conexao.setReadTimeout(15_000);

                // Mapeia o metodo do Spinner para o enum exato da AbacatePay
                String metodoApi;
                switch (metodoPag) {
                    case "Cartão de Crédito": metodoApi = "CARD"; break;
                    case "Cartão de Débito":  metodoApi = "DEBIT_CARD";  break;
                    default:                    metodoApi = "PIX";         break;
                }

                // Valor em centavos (padrao financeiro)
                int valorEmCentavos = (int) Math.round(valorTotal * 100);

                // CPF sem pontuacao (AbacatePay exige apenas digitos)
                String cpfLimpo = clienteCpf.replaceAll("[^0-9]", "");

                // Telefone sem pontuacao
                String telefoneLimpo = clienteTelefone.replaceAll("[^0-9]", "");

                JSONObject produto = new JSONObject();
                produto.put("externalId", "loc_" + idLocacao);
                produto.put("name", "Reserva GoVacation #" + idLocacao);
                produto.put("quantity", 1);
                produto.put("price", valorEmCentavos);

                JSONObject cliente = new JSONObject();
                cliente.put("name", clienteNome);
                cliente.put("email", clienteEmail);
                cliente.put("cellphone", telefoneLimpo);
                cliente.put("taxId", cpfLimpo);

                JSONObject payload = new JSONObject();
                payload.put("frequency", "ONE_TIME");
                payload.put("methods", new org.json.JSONArray().put(metodoApi));
                payload.put("products", new org.json.JSONArray().put(produto));
                payload.put("customer", cliente);
                // Redireciona o navegador apos o pagamento.
                // Pode ser qualquer URL https valida enquanto nao houver site proprio.
                payload.put("returnUrl",     "https://abacatepay.com");
                payload.put("completionUrl", "https://abacatepay.com");

                // Loga o payload exato para diagnostico
                Log.d("AbacatePay", "Payload enviado: " + payload.toString());

                // Envia o JSON
                try (OutputStream os = conexao.getOutputStream()) {
                    os.write(payload.toString().getBytes("UTF-8"));
                }

                int codigoResposta = conexao.getResponseCode();
                Log.d("AbacatePay", "HTTP " + codigoResposta);

                // Código -1 = conexão falhou antes de chegar ao servidor
                if (codigoResposta == -1) {
                    handlerDaTela.post(() ->
                            exibirAviso("Erro de Conexão",
                                    "A conexão com a AbacatePay falhou (código -1).\n\n" +
                                            "Verifique:\n" +
                                            "• Permissão INTERNET no AndroidManifest.xml\n" +
                                            "• Conexão Wi-Fi/dados do dispositivo\n" +
                                            "• Se a API key está correta"));
                    return;
                }

                if (codigoResposta >= 200 && codigoResposta < 300) {
                    // ✅ Sucesso — lê a URL de pagamento da resposta
                    InputStream is = conexao.getInputStream();
                    Scanner s = new Scanner(is).useDelimiter("\\A");
                    String respostaJson = s.hasNext() ? s.next() : "";
                    Log.d("AbacatePay", "Resposta: " + respostaJson);

                    JSONObject jsonResponse = new JSONObject(respostaJson);

                    // ✅ Campo correto da AbacatePay: data.url
                    final String urlPagamento = jsonResponse
                            .getJSONObject("data")
                            .getString("url");

                    handlerDaTela.post(() -> {
                        // ✅ Abre o navegador/app da AbacatePay para o cliente pagar
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlPagamento));
                        startActivity(browserIntent);

                        // Salva a reserva como pendente no BD local
                        efetivarReservaNoBanco(metodoPag);
                    });

                } else {
                    // Erro da API — loga o corpo para diagnóstico
                    InputStream err = conexao.getErrorStream();
                    Scanner s = new Scanner(err != null ? err : conexao.getInputStream())
                            .useDelimiter("\\A");
                    final String erroBody = s.hasNext() ? s.next() : "Sem detalhes";
                    Log.e("AbacatePay", "Erro HTTP " + codigoResposta + ": " + erroBody);

                    // Extrai mensagem de erro do JSON da AbacatePay, se houver
                    String mensagemApi = erroBody;
                    try {
                        JSONObject errJson = new JSONObject(erroBody);
                        if (errJson.has("error")) mensagemApi = errJson.getString("error");
                        else if (errJson.has("message")) mensagemApi = errJson.getString("message");
                    } catch (Exception ignored) {}

                    final int codigo = codigoResposta;
                    final String msgFinal = mensagemApi;
                    handlerDaTela.post(() ->
                            exibirAviso("Erro no Pagamento (HTTP " + codigo + ")",
                                    msgFinal + "\n\nSe o erro persistir, verifique sua API key no painel da AbacatePay."));
                }

            } catch (java.net.UnknownHostException e) {
                // DNS falhou — sem internet ou domínio errado
                Log.e("AbacatePay", "Sem internet ou domínio inválido", e);
                handlerDaTela.post(() ->
                        exibirAviso("Sem Conexão",
                                "Não foi possível alcançar a AbacatePay.\n\nVerifique sua conexão com a internet."));
            } catch (java.net.SocketTimeoutException e) {
                Log.e("AbacatePay", "Timeout na conexão", e);
                handlerDaTela.post(() ->
                        exibirAviso("Tempo Esgotado",
                                "A AbacatePay demorou demais para responder. Tente novamente."));
            } catch (Exception e) {
                Log.e("AbacatePay", "Exceção na chamada à API", e);
                handlerDaTela.post(() ->
                        exibirAviso("Erro Inesperado",
                                e.getClass().getSimpleName() + ": " + e.getMessage()));
            } finally {
                if (conexao != null) conexao.disconnect();
            }
        });
    }

    /** Persiste a reserva no SQLite e marca a locação como indisponível. */
    private void efetivarReservaNoBanco(String metodoPag) {
        String dataCheckinSQL  = sdf.format(dataCheckinSelecionada.getTime());
        String dataCheckoutSQL = sdf.format(dataCheckoutSelecionada.getTime());

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            ContentValues reservaValues = new ContentValues();
            reservaValues.put("idusuario",    idUsuario);
            reservaValues.put("idloc",        idLocacao);
            reservaValues.put("metodopag",    metodoPag);
            reservaValues.put("datacheckin",  dataCheckinSQL);
            reservaValues.put("datacheckout", dataCheckoutSQL);

            long idReserva = db.insertOrThrow("reservas", null, reservaValues);
            if (idReserva == -1) throw new Exception("Falha ao criar reserva no banco local.");

            ContentValues locacaoValues = new ContentValues();
            locacaoValues.put("disp", "Indisponível");
            int rows = db.update("locacoes", locacaoValues, "idloc = ?",
                    new String[]{String.valueOf(idLocacao)});
            if (rows == 0) throw new Exception("Falha ao atualizar status da locação.");

            db.setTransactionSuccessful();
            // A tela de pagamento já foi aberta — aqui apenas confirmamos o registro local
            Log.i("DetalhesReserva", "Reserva #" + idReserva + " salva com sucesso.");

        } catch (Exception e) {
            Log.e("DetalhesReserva", "Erro ao gravar reserva no BD", e);
            exibirAviso("Atenção",
                    "O pagamento foi iniciado, mas houve um erro interno ao salvar a reserva.\n" +
                            "Entre em contato com o suporte se o problema persistir.");
        } finally {
            if (db != null) {
                if (db.inTransaction()) db.endTransaction();
                if (db.isOpen()) db.close();
            }
        }
    }

    private void exibirAviso(String titulo, String mensagem) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensagem)
                .setPositiveButton("OK", null)
                .show();
    }
}