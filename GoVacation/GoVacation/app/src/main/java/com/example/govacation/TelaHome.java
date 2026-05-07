package com.example.govacation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class TelaHome extends AppCompatActivity {

    // 1. Declare o botão e a variável do usuário logado (se já não estiver lá)
    Button btnVerMinhasReservas;
    long idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_home);

        // 2. Capture o ID do usuário que fez o login e acessou essa Home
        idUsuarioLogado = getIntent().getLongExtra("ID_USUARIO", -1);

        // 3. Conecte o botão do XML
        btnVerMinhasReservas = findViewById(R.id.btnVerMinhasReservas);

        // 4. Adicione a ação de clique para abrir a tela nova
        btnVerMinhasReservas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aqui nós chamamos a tela "MinhasReservas" que criamos antes
                Intent intent = new Intent(TelaHome.this, MinhasReservas.class);

                // Enviamos o ID na "mochila" do Intent para a tela nova saber de quem é a reserva
                intent.putExtra("ID_USUARIO", idUsuarioLogado);

                startActivity(intent);
            }
        });

        // O resto do seu código que carrega a lista usando o LocacaoClienteAdapter continua aqui para baixo...
        // carregarListaDeImoveis();
    }

    // Você já tem esse método, ele é chamado pelo Adapter quando clicam em "Reservar"
    public void verDetalhesReserva(long locacaoId) {
        Intent intent = new Intent(TelaHome.this, DetalhesReserva.class);
        intent.putExtra("ID_LOCACAO", locacaoId);
        intent.putExtra("ID_USUARIO", idUsuarioLogado);
        startActivity(intent);
    }
}