package com.example.sistemadeclassificacao;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.DateTimePatternGenerator;
import android.media.Rating;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    TextView txtstatus;
    RatingBar rtbvotacao;
    Intent telaRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        txtstatus = (TextView) findViewById(R.id.txtstatus);
        rtbvotacao = (RatingBar) findViewById(R.id.rtbvotacao);
        txtstatus.setText("Status: Ruim");

        rtbvotacao.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating <= 1)
                    txtstatus.setText("Status: Ruim");
                else if (rating <= 2)
                    txtstatus.setText("Status: Regular");
                else if (rating <= 3)
                    txtstatus.setText("Status: Bom");
                else if (rating <= 4)
                    txtstatus.setText("Status: Ótimo");
                else if (rating <= 5)
                    txtstatus.setText("Status: Espetacular");

                AlertDialog.Builder confirm = new AlertDialog.Builder(MainActivity.this);

                confirm.setTitle(txtstatus.getText());

                confirm.setMessage("Você avaliou este jogo como: " + txtstatus.getText().toString().replace("Status: ", ""));
                confirm.setNeutralButton("Ok", null);
                confirm.show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}