package com.example.sistemadecompras;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    CheckBox chkarroz, chkleite, chkcarne, chkfeijao;
    Button bttotal;
    Intent paymentTela;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        chkarroz = (CheckBox) findViewById(R.id.chkarroz);
        chkleite = (CheckBox) findViewById(R.id.chkleite);
        chkcarne = (CheckBox) findViewById(R.id.chkcarne);
        chkfeijao = (CheckBox) findViewById(R.id.chkfeijao);
        Button bttotal = (Button) findViewById(R.id.bttotal);

        bttotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                double total = 0;

                if (chkarroz.isChecked())
                    total += 2.69;
                if (chkleite.isChecked())
                    total += 5.00;
                if (chkcarne.isChecked())
                    total += 10.90;
                if (chkfeijao.isChecked())
                    total += 2.30;

                AlertDialog.Builder dialogo = new AlertDialog.Builder(MainActivity.this);
                dialogo.setTitle("Confirmar Compra");
                dialogo.setMessage("Valor total da compra: R$ " + String.format("%.2f", total));

                dialogo.setPositiveButton("Ir para o pagamento", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        paymentTela = new Intent(MainActivity.this, MainPayment.class);
                        startActivity(paymentTela);
                    }
                });

                dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                dialogo.create();
                dialogo.show();
            }
        });
    }
}