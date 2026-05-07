package com.example.govacation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BDHelper extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "govacation";
    private static final int VERSAO_BANCO = 1;

    private static final String SQL_CREATE_TABLE_USUARIOS =
            "CREATE TABLE IF NOT EXISTS usuario (" +
                    "idusuario INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "tipousuario INT NOT NULL, " +
                    "email VARCHAR(100) NOT NULL, " +
                    "senha VARCHAR(255) NOT NULL, " +
                    "nome VARCHAR(70) NOT NULL, " +
                    "cpf VARCHAR(13) NOT NULL, " +
                    "endereco VARCHAR(100) NOT NULL, " +
                    "telefone VARCHAR(11) NOT NULL" +
                    ")";

    private static final String SQL_CREATE_TABLE_LOCACOES =
            "CREATE TABLE IF NOT EXISTS locacoes (" +
                    "idloc INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "tipoloc VARCHAR(20) NOT NULL, " +
                    "titulo VARCHAR(255) NOT NULL, " +
                    "imagem VARCHAR(255) NOT NULL, " +
                    "descr VARCHAR(255) NOT NULL, " +
                    "preco DECIMAL(10,2) NOT NULL, " +
                    "localizacao VARCHAR(100) NOT NULL, " +
                    "qtdhospedes INT NOT NULL, " +
                    "disp VARCHAR(15) NOT NULL" +
                    ")";

    private static final String SQL_CREATE_TABLE_RESERVAS =
            "CREATE TABLE IF NOT EXISTS reservas (" +
                    "idreserva INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "idusuario INT NOT NULL, " +
                    "idloc INT NOT NULL, " +
                    "metodopag VARCHAR(15), " +
                    "datacheckin DATE, " +
                    "datacheckout DATE, " +
                    "FOREIGN KEY (idusuario) REFERENCES usuario(idusuario), " +
                    "FOREIGN KEY (idloc) REFERENCES locacoes(idloc)" +
                    ")";

    public BDHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_USUARIOS);
        db.execSQL(SQL_CREATE_TABLE_LOCACOES);
        db.execSQL(SQL_CREATE_TABLE_RESERVAS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS reservas");
        db.execSQL("DROP TABLE IF EXISTS locacoes");
        db.execSQL("DROP TABLE IF EXISTS usuario");

        onCreate(db);
    }
}
