package com.example.aplicacaobancodedados;



import android.os.Bundle;

import android.app.Activity;

import android.app.AlertDialog;

//import android.content.ContentValues;

import android.content.Context;

import android.content.DialogInterface;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.view.View;

import android.widget.Button;

import android.widget.ImageView;

import android.widget.TextView;



public class ExcluirDados2Activity extends Activity {

    TextView txtnome, txttelefone, txtemail, txtstatus_registro;

    SQLiteDatabase db;

    ImageView imgprimeiro, imganterior, imgproximo, imgultimo;

    Button btexcluirdados;

    int indice;

    int numreg;

    Cursor c;

    DialogInterface.OnClickListener diExcluiRegistro;



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_excluir_dados);

        txtnome = (TextView) findViewById(R.id.txtnome);

        txttelefone = (TextView) findViewById(R.id.txttelefone);

        txtemail = (TextView) findViewById(R.id.txtemail);

        txtstatus_registro = (TextView) findViewById(R.id.txtstatus_registro);



        txtnome.setText("");

        txttelefone.setText("");

        txtemail.setText("");



        imgprimeiro = (ImageView) findViewById(R.id.imgprimeiro);

        imganterior = (ImageView) findViewById(R.id.imganterior);

        imgproximo = (ImageView) findViewById(R.id.imgproximo);



        imgultimo = (ImageView) findViewById(R.id.imgultimo);

        btexcluirdados = (Button) findViewById(R.id.btexcluirdados);



        try {

            //Abre o banco de dados

            db = openOrCreateDatabase("banco_dados",Context.MODE_PRIVATE, null);

            CarregaDados();



            imgprimeiro.setOnClickListener(new View.OnClickListener() {

                @Override

                public void onClick(View v) {

                    if(c.getCount() > 0)      {

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

                @Override     public void onClick(View v) {

                    // TODO Auto-generated method stub

                    if(c.getCount() > 0)    {

                        if(indice > 1) {

                            indice--;

                            //Move para o registro anterior

                            c.moveToPrevious();

                            numreg = c.getInt(0);

                            //Obtem o número de registro

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

                }

            });

            imgproximo.setOnClickListener(new View.OnClickListener() {

                @Override    public void onClick(View v) {

                    // TODO Auto-generated method stub

                    if(c.getCount() > 0)        {

                        if(indice != c.getCount()) {



                            indice++;

                            //Move para o proximo registro

                            c.moveToNext();

                            //Obtem o número de registro

                            numreg = c.getInt(0);

                            txtnome.setText(c.getString(1));//Obtem o nome

                            //Obtém o telefone

                            txttelefone.setText(c.getString(2));

                            //Obtém o e-mail

                            txtemail.setText(c.getString(3));

                            txtstatus_registro.setText(indice + " / " +

                                    c.getCount());

                        }

                    }

                }

            });

            imgultimo.setOnClickListener(new View.OnClickListener() {

                @Override

                public void onClick(View v) {

                    if(c.getCount() > 0)      {

                        //Move para o último registro

                        c.moveToLast();

                        indice = c.getCount();

                        numreg = c.getInt(0); //Obtem o número de registro

                        txtnome.setText(c.getString(1));//Obtem o nome

                        //Obtém o telefone

                        txttelefone.setText(c.getString(2));

                        txtemail.setText(c.getString(3));//Obtém o e-mail

                        txtstatus_registro.setText(indice + " / " + c.getCount());

                    }

                }

            });



            diExcluiRegistro = new DialogInterface.OnClickListener() {

                @Override

                public void onClick(DialogInterface dialog, int which) {

                    //Exclui as informações do registro na tabela

                    db.delete("usuarios","numreg=?" + numreg,null);

                    CarregaDados();

                    MostraMensagem("Dados excluidos com sucesso!");

                }

            };



            btexcluirdados.setOnClickListener(new View.OnClickListener() {

                @Override

                public void onClick(View v) {



                    if (c.getCount() > 0) {



                        AlertDialog.Builder dialogo = new

                                AlertDialog.Builder(ExcluirDados2Activity.this);

                        dialogo.setTitle("Confirma");

                        dialogo.setMessage("Deseja excluir esse registro ?");

                        dialogo.setNegativeButton("Não", null);

                        dialogo.setPositiveButton("Sim", diExcluiRegistro);

                        dialogo.show();

                    } else {

                        MostraMensagem("Não existem registros para excluir!");

                    }

                }

            });



        } catch(Exception e)

        {

            MostraMensagem("Erro: " + e.toString());

        }

    }



    public void CarregaDados(){

        c = db.query("usuarios",new String []

                        {"numreg","nome","telefone","email"},

                null,null,null,null,null);



        txtnome.setText("");

        txttelefone.setText("");

        txtemail.setText("");



        if(c.getCount() > 0) {

            //Move para o primeiro registro

            c.moveToFirst();

            indice = 1;

            numreg = c.getInt(0);

            //Obtem o número de registro

            txtnome.setText(c.getString(1));//Obtem o nome

            //Obtém o telefone

            txttelefone.setText(c.getString(2));

            //Obtém o e-mail

            txtemail.setText(c.getString(3));



            txtstatus_registro.setText(indice + " / " + c.getCount());

        }

        else {

            txtstatus_registro.setText("Nenhum Registro");

        }

    }



    public void MostraMensagem(String str)    {

        AlertDialog.Builder dialogo = new

                AlertDialog.Builder(ExcluirDados2Activity.this);

        dialogo.setTitle("Aviso");

        dialogo.setMessage(str);

        dialogo.setNeutralButton("OK", null);

        dialogo.show();

    }

}