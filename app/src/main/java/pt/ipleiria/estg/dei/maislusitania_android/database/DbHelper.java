package pt.ipleiria.estg.dei.maislusitania_android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "MaisLusitania.db";

    private static final String SQL_CREATE_RESERVAS =
            "CREATE TABLE " + DbContract.ReservaEntry.TABLE_NAME + " (" +
                    DbContract.ReservaEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    DbContract.ReservaEntry.COLUMN_LOCAL_ID + " INTEGER," +
                    DbContract.ReservaEntry.COLUMN_LOCAL_NOME + " TEXT," +
                    DbContract.ReservaEntry.COLUMN_DATA_VISITA + " TEXT," +
                    DbContract.ReservaEntry.COLUMN_PRECO_TOTAL + " REAL," +
                    DbContract.ReservaEntry.COLUMN_ESTADO + " TEXT," +
                    DbContract.ReservaEntry.COLUMN_DATA_CRIACAO + " TEXT," +
                    DbContract.ReservaEntry.COLUMN_IMAGEM_LOCAL + " TEXT)";

    private static final String SQL_CREATE_BILHETES =
            "CREATE TABLE " + DbContract.BilheteEntry.TABLE_NAME + " (" +
                    DbContract.BilheteEntry.COLUMN_CODIGO + " TEXT PRIMARY KEY," +
                    DbContract.BilheteEntry.COLUMN_RESERVA_ID + " INTEGER," +
                    DbContract.BilheteEntry.COLUMN_LOCAL_ID + " INTEGER," +
                    DbContract.BilheteEntry.COLUMN_LOCAL_NOME + " TEXT," +
                    DbContract.BilheteEntry.COLUMN_DATA_VISITA + " TEXT," +
                    DbContract.BilheteEntry.COLUMN_TIPO_BILHETE_ID + " INTEGER," +
                    DbContract.BilheteEntry.COLUMN_TIPO_BILHETE_NOME + " TEXT," +
                    DbContract.BilheteEntry.COLUMN_PRECO + " REAL," +
                    DbContract.BilheteEntry.COLUMN_ESTADO + " TEXT, " +
                    "FOREIGN KEY(" + DbContract.BilheteEntry.COLUMN_RESERVA_ID + ") REFERENCES " +
                    DbContract.ReservaEntry.TABLE_NAME + "(" + DbContract.ReservaEntry.COLUMN_ID + "))";

    private static final String SQL_DELETE_RESERVAS = "DROP TABLE IF EXISTS " + DbContract.ReservaEntry.TABLE_NAME;
    private static final String SQL_DELETE_BILHETES = "DROP TABLE IF EXISTS " + DbContract.BilheteEntry.TABLE_NAME;


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_RESERVAS);
        db.execSQL(SQL_CREATE_BILHETES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Para simplicidade, vamos apagar e recriar. Numa app de produção, faríamos uma migração.
        db.execSQL(SQL_DELETE_BILHETES);
        db.execSQL(SQL_DELETE_RESERVAS);
        onCreate(db);
    }
}
