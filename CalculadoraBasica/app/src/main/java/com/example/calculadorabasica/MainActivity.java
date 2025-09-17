package com.example.calculadorabasica;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.app.*;

public class MainActivity extends Activity {

    EditText ednumero1, ednumero2;
    Button btsomar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ednumero1 = (EditText) findViewById(R.id.ednumero1);
        ednumero2 = (EditText) findViewById(R.id.ednumero2);
        btsomar = (Button) findViewById(R.id.btsomar);

        btsomar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double num1 = Double.parseDouble(ednumero1.getText().toString());

                double num2 = Double.parseDouble(ednumero2.getText().toString());

                double soma = num1 + num2;

                AlertDialog.Builder dialogo = new AlertDialog.Builder(MainActivity.this);

                dialogo.setTitle("Resultado soma");

                dialogo.setMessage("A soma é " + soma);

                dialogo.setNeutralButton("OK", null);

                dialogo.show();
            }
        });
    }
}