package pt.ipleiria.estg.dei.maislusitania_android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.models.Bilhete;
import pt.ipleiria.estg.dei.maislusitania_android.models.Favorito;
import pt.ipleiria.estg.dei.maislusitania_android.models.Reserva;

public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 4;
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
                    DbContract.ReservaEntry.TABLE_NAME + "(" + DbContract.ReservaEntry.COLUMN_ID + ") ON DELETE CASCADE)"; // Adicionado ON DELETE CASCADE

    private static final String SQL_CREATE_FAVORITOS =
            "CREATE TABLE " + DbContract.FavoritoEntry.TABLE_NAME + " (" +
                    DbContract.FavoritoEntry.COLUMN_ID + " INTEGER," +
                    DbContract.FavoritoEntry.COLUMN_UTILIZADOR_ID + " INTEGER, " +
                    DbContract.FavoritoEntry.COLUMN_LOCAL_ID + " INTEGER, " +
                    DbContract.FavoritoEntry.COLUMN_NOME + " TEXT, " +
                    DbContract.FavoritoEntry.COLUMN_DISTRITO + " TEXT, " +
                    DbContract.FavoritoEntry.COLUMN_IMAGEM + " TEXT, " +
                    DbContract.FavoritoEntry.COLUMN_AVALIACAO + " REAL, " +
                    DbContract.FavoritoEntry.COLUMN_DATA_ADICAO + " TEXT, " +
                    DbContract.FavoritoEntry.COLUMN_IS_FAVORITE + " INTEGER DEFAULT 1, " +
                    "PRIMARY KEY (" + DbContract.FavoritoEntry.COLUMN_UTILIZADOR_ID + ", " + DbContract.FavoritoEntry.COLUMN_LOCAL_ID + ")" +
                    ");";

    private static final String SQL_DELETE_RESERVAS = "DROP TABLE IF EXISTS " + DbContract.ReservaEntry.TABLE_NAME;
    private static final String SQL_DELETE_BILHETES = "DROP TABLE IF EXISTS " + DbContract.BilheteEntry.TABLE_NAME;
    private static final String SQL_DELETE_FAVORITOS = "DROP TABLE IF EXISTS " + DbContract.FavoritoEntry.TABLE_NAME;



    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_RESERVAS);
        db.execSQL(SQL_CREATE_BILHETES);
        db.execSQL(SQL_CREATE_FAVORITOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_BILHETES);
        db.execSQL(SQL_DELETE_RESERVAS);
        db.execSQL(SQL_DELETE_FAVORITOS);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    //region Métodos para Reservas e Bilhetes

    public void sincronizarReservas(ArrayList<Reserva> reservas) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Delete bilhetes first (child records) before deleting reservas
            db.delete(DbContract.BilheteEntry.TABLE_NAME, null, null);

            // Now delete reservas - no constraint violation since bilhetes are gone
            db.delete(DbContract.ReservaEntry.TABLE_NAME, null, null);

            for (Reserva reserva : reservas) {
                ContentValues values = new ContentValues();
                values.put(DbContract.ReservaEntry.COLUMN_ID, reserva.getId());
                values.put(DbContract.ReservaEntry.COLUMN_LOCAL_ID, reserva.getLocalId());
                values.put(DbContract.ReservaEntry.COLUMN_LOCAL_NOME, reserva.getLocalNome());
                values.put(DbContract.ReservaEntry.COLUMN_DATA_VISITA, reserva.getDataVisita());
                values.put(DbContract.ReservaEntry.COLUMN_PRECO_TOTAL, reserva.getPrecoTotal());
                values.put(DbContract.ReservaEntry.COLUMN_ESTADO, reserva.getEstado());
                values.put(DbContract.ReservaEntry.COLUMN_DATA_CRIACAO, reserva.getDataCriacao());
                values.put(DbContract.ReservaEntry.COLUMN_IMAGEM_LOCAL, reserva.getImagemLocal());
                db.insert(DbContract.ReservaEntry.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void sincronizarBilhetes(int reservaId, ArrayList<Bilhete> bilhetes) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Limpa os bilhetes antigos APENAS para esta reserva
            db.delete(DbContract.BilheteEntry.TABLE_NAME, DbContract.BilheteEntry.COLUMN_RESERVA_ID + " = ?", new String[]{String.valueOf(reservaId)});

            for (Bilhete bilhete : bilhetes) {
                ContentValues values = new ContentValues();
                values.put(DbContract.BilheteEntry.COLUMN_CODIGO, bilhete.getCodigo());
                values.put(DbContract.BilheteEntry.COLUMN_RESERVA_ID, bilhete.getReservaId());
                values.put(DbContract.BilheteEntry.COLUMN_LOCAL_ID, bilhete.getLocalId());
                values.put(DbContract.BilheteEntry.COLUMN_LOCAL_NOME, bilhete.getLocalNome());
                values.put(DbContract.BilheteEntry.COLUMN_DATA_VISITA, bilhete.getDataVisita());
                values.put(DbContract.BilheteEntry.COLUMN_TIPO_BILHETE_ID, bilhete.getTipoBilheteId());
                values.put(DbContract.BilheteEntry.COLUMN_TIPO_BILHETE_NOME, bilhete.getTipoBilheteNome());
                values.put(DbContract.BilheteEntry.COLUMN_PRECO, bilhete.getPreco());
                values.put(DbContract.BilheteEntry.COLUMN_ESTADO, bilhete.getEstado());
                db.insert(DbContract.BilheteEntry.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<Reserva> getAllReservas() {
        ArrayList<Reserva> reservas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + DbContract.ReservaEntry.TABLE_NAME, null)) {
            if (cursor.moveToFirst()) {
                do {
                    reservas.add(new Reserva(
                            cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.ReservaEntry.COLUMN_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.ReservaEntry.COLUMN_LOCAL_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.ReservaEntry.COLUMN_LOCAL_NOME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.ReservaEntry.COLUMN_DATA_VISITA)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(DbContract.ReservaEntry.COLUMN_PRECO_TOTAL)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.ReservaEntry.COLUMN_ESTADO)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.ReservaEntry.COLUMN_DATA_CRIACAO)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.ReservaEntry.COLUMN_IMAGEM_LOCAL))
                    ));
                } while (cursor.moveToNext());
            }
        }
        return reservas;
    }

    public ArrayList<Bilhete> getBilhetesByReserva(int idReserva) {
        ArrayList<Bilhete> bilhetes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(DbContract.BilheteEntry.TABLE_NAME, null,
                DbContract.BilheteEntry.COLUMN_RESERVA_ID + " = ?",
                new String[]{String.valueOf(idReserva)}, null, null, null)) {

            if (cursor.moveToFirst()) {
                do {
                    bilhetes.add(new Bilhete(
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.BilheteEntry.COLUMN_CODIGO)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.BilheteEntry.COLUMN_RESERVA_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.BilheteEntry.COLUMN_LOCAL_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.BilheteEntry.COLUMN_LOCAL_NOME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.BilheteEntry.COLUMN_DATA_VISITA)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.BilheteEntry.COLUMN_TIPO_BILHETE_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.BilheteEntry.COLUMN_TIPO_BILHETE_NOME)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(DbContract.BilheteEntry.COLUMN_PRECO)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.BilheteEntry.COLUMN_ESTADO))
                    ));
                } while (cursor.moveToNext());
            }
        }
        return bilhetes;
    }

    //remover reservas/bilhetes

    public void removerReservasBilhetes() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DbContract.ReservaEntry.TABLE_NAME, null, null);
        db.delete(DbContract.BilheteEntry.TABLE_NAME, null, null);
    }
    //endregion

    // --- MÉTODOS FAVORITOS ---

    public void adicionarFavorito(Favorito favorito) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // Usar as constantes de DbContract.FavoritoEntry
        values.put(DbContract.FavoritoEntry.COLUMN_ID, favorito.getId());
        values.put(DbContract.FavoritoEntry.COLUMN_UTILIZADOR_ID, favorito.getUtilizadorId());
        values.put(DbContract.FavoritoEntry.COLUMN_LOCAL_ID, favorito.getLocalId());
        values.put(DbContract.FavoritoEntry.COLUMN_NOME, favorito.getLocalNome());
        values.put(DbContract.FavoritoEntry.COLUMN_DISTRITO, favorito.getLocalDistrito());
        values.put(DbContract.FavoritoEntry.COLUMN_IMAGEM, favorito.getLocalImagem());
        values.put(DbContract.FavoritoEntry.COLUMN_AVALIACAO, favorito.getAvaliacaoMedia());
        values.put(DbContract.FavoritoEntry.COLUMN_DATA_ADICAO, favorito.getDataAdicao());
        values.put(DbContract.FavoritoEntry.COLUMN_IS_FAVORITE, favorito.isFavorite() ? 1 : 0);

        // Usar o nome da tabela de DbContract
        db.insertWithOnConflict(DbContract.FavoritoEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void sincronizarFavoritos(ArrayList<Favorito> favoritos) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Limpa todos os favoritos antigos
            db.delete(DbContract.FavoritoEntry.TABLE_NAME, null, null);

            // Insere os novos favoritos
            for (Favorito favorito : favoritos) {
                ContentValues values = new ContentValues();
                values.put(DbContract.FavoritoEntry.COLUMN_ID, favorito.getId());
                values.put(DbContract.FavoritoEntry.COLUMN_UTILIZADOR_ID, favorito.getUtilizadorId());
                values.put(DbContract.FavoritoEntry.COLUMN_LOCAL_ID, favorito.getLocalId());
                values.put(DbContract.FavoritoEntry.COLUMN_NOME, favorito.getLocalNome());
                values.put(DbContract.FavoritoEntry.COLUMN_DISTRITO, favorito.getLocalDistrito());
                values.put(DbContract.FavoritoEntry.COLUMN_IMAGEM, favorito.getLocalImagem());
                values.put(DbContract.FavoritoEntry.COLUMN_AVALIACAO, favorito.getAvaliacaoMedia());
                values.put(DbContract.FavoritoEntry.COLUMN_DATA_ADICAO, favorito.getDataAdicao());
                values.put(DbContract.FavoritoEntry.COLUMN_IS_FAVORITE, favorito.isFavorite() ? 1 : 0);

                db.insert(DbContract.FavoritoEntry.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }


    public void removerFavorito(int localid, int utilizadorid) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = DbContract.FavoritoEntry.COLUMN_LOCAL_ID + " = ? AND " + DbContract.FavoritoEntry.COLUMN_UTILIZADOR_ID + " = ?";
        String[] selectionArgs = {String.valueOf(localid), String.valueOf(utilizadorid)};
        db.delete(DbContract.FavoritoEntry.TABLE_NAME, selection, selectionArgs);
    }

    public boolean isFavorito(int localId, int utilizadorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {DbContract.FavoritoEntry.COLUMN_ID};
        String selection = DbContract.FavoritoEntry.COLUMN_LOCAL_ID + " = ? AND " + DbContract.FavoritoEntry.COLUMN_UTILIZADOR_ID + " = ?";
        String[] selectionArgs = {String.valueOf(localId), String.valueOf(utilizadorId)};

        // Usar try-with-resources para garantir que o cursor é fechado
        try (Cursor cursor = db.query(
                DbContract.FavoritoEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        )) {
            return cursor.getCount() > 0;
        }
    }

    public ArrayList<Favorito> getAllFavoritos() {
        ArrayList<Favorito> favoritos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Usar try-with-resources para garantir que o cursor é fechado
        try (Cursor cursor = db.query(DbContract.FavoritoEntry.TABLE_NAME, null, null, null, null, null, null)) {
            if (cursor.moveToFirst()) {
                do {
                    Favorito fav = new Favorito(
                            cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.FavoritoEntry.COLUMN_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.FavoritoEntry.COLUMN_UTILIZADOR_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.FavoritoEntry.COLUMN_LOCAL_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.FavoritoEntry.COLUMN_IMAGEM)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.FavoritoEntry.COLUMN_NOME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.FavoritoEntry.COLUMN_DISTRITO)),
                            cursor.getFloat(cursor.getColumnIndexOrThrow(DbContract.FavoritoEntry.COLUMN_AVALIACAO)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.FavoritoEntry.COLUMN_DATA_ADICAO)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.FavoritoEntry.COLUMN_IS_FAVORITE)) == 1
                    );
                    favoritos.add(fav);
                } while (cursor.moveToNext());
            }
        } // O cursor é fechado automaticamente aqui
        return favoritos;
    }

    //deleteAllFavoritos
    public void deleteAllFavoritos() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DbContract.FavoritoEntry.TABLE_NAME, null, null);
    }

    public void deleteAllFavoritosByUser(int utilizadorid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DbContract.FavoritoEntry.TABLE_NAME,
                DbContract.FavoritoEntry.COLUMN_UTILIZADOR_ID + " = ?", new String[]{String.valueOf(utilizadorid)});
    }

}
