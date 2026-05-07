package com.example.govacation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class GerenciamentoLocs extends AppCompatActivity {

    ImageView ivLogoAdmin;
    MaterialCardView cardCadastrarLocs, cardListarLocs;
    Button btLogoutAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciamento_locs);

        ivLogoAdmin = findViewById(R.id.ivLogoAdmin);
        cardCadastrarLocs = findViewById(R.id.cardCadastrarLocs);
        cardListarLocs = findViewById(R.id.cardListarLocs);
        btLogoutAdmin = findViewById(R.id.btLogoutAdmin);

        configurarListeners();
    }

    private void configurarListeners() {
        cardCadastrarLocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GerenciamentoLocs.this, CadastrarLocs.class);
                startActivity(intent);
            }
        });

        cardListarLocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GerenciamentoLocs.this, ListarLocs.class);
                startActivity(intent);
            }
        });

        btLogoutAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GerenciamentoLocs.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); 
                startActivity(intent);
                finish(); 
            }
        });
    }
}