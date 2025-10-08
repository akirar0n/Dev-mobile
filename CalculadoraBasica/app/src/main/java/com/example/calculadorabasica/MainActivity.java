package com.example.calculadorabasica;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.app.*;

public class MainActivity extends Activity {

    EditText ednumero1, ednumero2;
    Button btsomar;
    Button btsubtrair;
    Button btmultilpicar;
    Button btdividir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ednumero1 = (EditText) findViewById(R.id.ednumero1);
        ednumero2 = (EditText) findViewById(R.id.ednumero2);
        btsomar = (Button) findViewById(R.id.btsomar);
        btsubtrair = (Button) findViewById(R.id.btsubtrair);
        btmultilpicar = (Button) findViewById(R.id.btmultiplicar);
        btdividir = (Button) findViewById(R.id.btdividir);

        btsomar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double num1 = Double.parseDouble(ednumero1.getText().toString());
                double num2 = Double.parseDouble(ednumero2.getText().toString());
                double soma = num1 + num2;

                AlertDialog.Builder dialogo = new AlertDialog.Builder(MainActivity.this);

                dialogo.setTitle("Resultado da soma");
                dialogo.setMessage("A soma é " + soma);
                dialogo.setNeutralButton("OK", null);

                dialogo.show();
            }
        });

        btsubtrair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double num1 = Double.parseDouble(ednumero1.getText().toString());
                double num2 = Double.parseDouble(ednumero2.getText().toString());
                double sub = num1 - num2;

                AlertDialog.Builder dialogo = new AlertDialog.Builder(MainActivity.this);

                dialogo.setTitle("Resultado da subtração");
                dialogo.setMessage("A subtração é " + sub);
                dialogo.setNeutralButton("OK", null);

                dialogo.show();
            }
        });

        btmultilpicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double num1 = Double.parseDouble(ednumero1.getText().toString());
                double num2 = Double.parseDouble(ednumero2.getText().toString());
                double mult = num1 * num2;

                AlertDialog.Builder dialogo = new AlertDialog.Builder(MainActivity.this);

                dialogo.setTitle("Resultado da multiplicação");
                dialogo.setMessage("A multiplicação é " + mult);
                dialogo.setNeutralButton("OK", null);

                dialogo.show();
            }
        });

        btdividir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double num1 = Double.parseDouble(ednumero1.getText().toString());
                double num2 = Double.parseDouble(ednumero2.getText().toString());
                double div = num1 / num2;

                if (num2 == 0){
                    AlertDialog.Builder dialogo = new AlertDialog.Builder(MainActivity.this);
                    dialogo.setTitle("Resultado da divisão");
                    dialogo.setMessage("Não existe divisão por zero.");
                    dialogo.setNeutralButton("OK", null);
                    dialogo.show();
                } else {
                    AlertDialog.Builder dialogo = new AlertDialog.Builder(MainActivity.this);
                    dialogo.setTitle("Resultado da divisão");
                    dialogo.setMessage("A divisão é " + div);
                    dialogo.setNeutralButton("OK", null);
                    dialogo.show();
                }
            }
        });
    }
}