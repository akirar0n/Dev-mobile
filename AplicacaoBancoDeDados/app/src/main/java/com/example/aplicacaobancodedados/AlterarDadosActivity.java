package com.example.aplicacaobancodedados;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;



public class AlterarDadosActivity extends Activity {
    EditText txtnome, txttelefone, txtemail;
    TextView txtstatus_registro;
    SQLiteDatabase db;
    ImageView imgprimeiro, imganterior, imgproximo, imgultimo;
    Button btalterardados;
    int indice;
    int numreg;
    Cursor c;
    DialogInterface.OnClickListener diAlteraInformacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_dados);

        txtnome = (EditText) findViewById(R.id.txtnome);
        txttelefone = (EditText) findViewById(R.id.txttelefone);
        txtemail = (EditText) findViewById(R.id.txtemail);
        txtstatus_registro = (TextView) findViewById(R.id.txtstatus_registro);
        imgprimeiro = (ImageView) findViewById(R.id.imgprimeiro);
        imganterior = (ImageView) findViewById(R.id.imganterior);
        imgproximo = (ImageView) findViewById(R.id.imgproximo);
        imgultimo = (ImageView) findViewById(R.id.imgultimo);
        btalterardados = (Button)findViewById(R.id.btalterardados);

        try {
            db = openOrCreateDatabase("banco_dados",
                    Context.MODE_PRIVATE, null);
            c = db.query("usuarios",new String []
                            {"numreg","nome","telefone","email"},
                    null,null,null,null,null);

            if(c.getCount() > 0) { //Move para o primeiro registro
                c.moveToFirst();
                indice = 1;

                numreg = c.getInt(0); //Obtem o número de registro
                txtnome.setText(c.getString(1)); //Obtem o nome
                txttelefone.setText(c.getString(2)); //Obtém o telefone
                txtemail.setText(c.getString(3));//Obtém o e-mail
                txtstatus_registro.setText(indice + " / " +
                        c.getCount());
            }

            else {
                txtstatus_registro.setText("Nenhum Registro");
            }
        }
        catch(Exception e){
        }

        imgprimeiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(c.getCount() > 0)    {

                    //Move para o primeiro registro

                    c.moveToFirst();

                    indice = 1;

                    //Obtem o número de registro

                    numreg = c.getInt(0);

                    //Obtem o nome

                    txtnome.setText(c.getString(1));

                    //Obtém o telefone

                    txttelefone.setText(c.getString(2));

                    //Obtém o e-mail

                    txtemail.setText(c.getString(3));

                    txtstatus_registro.setText(indice + " / " +

                            c.getCount());

                }

            }

        });



        imganterior.setOnClickListener(new View.OnClickListener() {

            @Override   public void onClick(View v) {

                // TODO Auto-generated method stub

                if(c.getCount() > 0)    {

                    if(indice > 1) {

                        indice--;

                        //Move para o registro anterior

                        c.moveToPrevious();

                        numreg = c.getInt(0);

                        //Obtem o número de registro

                        txtnome.setText(c.getString(1));

                        //Obtem o nome

                        txttelefone.setText(c.getString(2));

                        //Obtém o telefone

                        txtemail.setText(c.getString(3));

                        //Obtém o e-mail

                        txtstatus_registro.setText(indice + " / " +

                                c.getCount());

                    }

                }

            }

        });



        imgproximo.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                // TODO Auto-generated method stub

                if(c.getCount() > 0)     {

                    if(indice != c.getCount()) {

                        indice++;

                        //Move para o proximo registro

                        c.moveToNext();

                        numreg = c.getInt(0); //Obtem o número de registro

                        txtnome.setText(c.getString(1));

                        //Obtem o nome

                        txttelefone.setText(c.getString(2));

                        //Obtém o telefone

                        //Obtém o e-mail

                        txtemail.setText(c.getString(3));

                        txtstatus_registro.setText(indice + " / " +

                                c.getCount());

                    }

                }

            }

        });



        imgultimo.setOnClickListener(new View.OnClickListener() {

            @Override  public void onClick(View v) {

                if(c.getCount() > 0)   {

                    //Move para o último registro

                    c.moveToLast();

                    indice = c.getCount();

                    numreg = c.getInt(0); //Obtem o número de registro

                    txtnome.setText(c.getString(1));//Obtem o nome

                    txttelefone.setText(c.getString(2));//Obtém o

                    //telefone

                    txtemail.setText(c.getString(3));//Obtém o e-mail

                    txtstatus_registro.setText(indice + " / " +

                            c.getCount());   }  }    });



        diAlteraInformacoes = new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which) {

                //Altera as informações do registro na tabela

                String nome = txtnome.getText().toString();

                String telefone = txttelefone.getText().toString();

                String email = txtemail.getText().toString();



                try {

                    db.execSQL("update usuarios set nome = '" + nome + "', "

                            + "telefone = '" + telefone + "', email = '" + email +

                            "' where numreg = " + numreg);

                    MostraMensagem("Dados alterados com sucesso.");

                }   catch(Exception e) {

                    MostraMensagem("Erro: " + e.toString());

                }

            }

        };



        btalterardados.setOnClickListener(new View.OnClickListener() {

            @Override  public void onClick(View v) {

                AlertDialog.Builder dialogo = new

                        AlertDialog.Builder(AlterarDadosActivity.this);

                dialogo.setTitle("Confirma");

                dialogo.setMessage("Deseja alterar as informações");

                dialogo.setNegativeButton("Não", null);

                dialogo.setPositiveButton("Sim", diAlteraInformacoes);

                dialogo.show();

            }

        });

    }

    public void MostraMensagem(String str)  {

        AlertDialog.Builder dialogo = new

                AlertDialog.Builder(AlterarDadosActivity.this);

        dialogo.setTitle("Aviso");

        dialogo.setMessage(str);

        dialogo.setNeutralButton("OK", null);

        dialogo.show();

    }

}