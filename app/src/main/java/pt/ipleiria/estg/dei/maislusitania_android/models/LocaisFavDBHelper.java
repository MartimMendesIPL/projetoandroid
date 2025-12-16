package pt.ipleiria.estg.dei.maislusitania_android.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class LocaisFavDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "BDFavoritos";


    // força o onUpgrade a correr para recriar a tabela com as novas colunas.
    private static final int DB_VERSION = 2;

    private static final String TABLE_FAVORITOS = "favoritos";

    // --- COLUNAS ---
    private static final String ID = "id";
    private static final String NOME = "nome";
    private static final String IMAGEM = "imagem";
    // Novos campos para modo offline:
    private static final String MORADA = "morada";
    private static final String DISTRITO = "distrito";
    private static final String DESCRICAO = "descricao";
    private static final String AVALIACAO = "avaliacao_media";

    public LocaisFavDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criar tabela com TODOS os campos
        String sql = "CREATE TABLE " + TABLE_FAVORITOS + "(" +
                ID + " INTEGER PRIMARY KEY, " +
                NOME + " TEXT, " +
                MORADA + " TEXT, " +
                DISTRITO + " TEXT, " +
                DESCRICAO + " TEXT, " +
                IMAGEM + " TEXT, " +
                AVALIACAO + " REAL" + // REAL é o tipo para float/double em SQLite
                ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Apaga a tabela antiga e cria a nova (perdem-se os favoritos antigos, mas evita erros)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITOS);
        onCreate(db);
    }

    // --- MÉTODOS FAVORITOS ---

    public void adicionarFavorito(Local local) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Guardar TUDO o que vem do objeto Local
        values.put(ID, local.getId());
        values.put(NOME, local.getNome());
        values.put(MORADA, local.getMorada());
        values.put(DISTRITO, local.getDistrito());
        values.put(DESCRICAO, local.getDescricao());
        values.put(IMAGEM, local.getImagem());
        values.put(AVALIACAO, local.getAvaliacaoMedia());

        // Insere ou substitui se já existir (conflito de ID)
        db.insertWithOnConflict(TABLE_FAVORITOS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void removerFavorito(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITOS, ID + " = ?", new String[]{String.valueOf(id)});
    }

    public boolean isFavorito(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITOS, new String[]{ID}, ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public ArrayList<Local> getAllFavoritos() {
        ArrayList<Local> locais = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FAVORITOS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Local l = new Local(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NOME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MORADA)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DISTRITO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DESCRICAO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(IMAGEM)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(AVALIACAO))
                );

                l.setFavorite(true);
                locais.add(l);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return locais;
    }

    //deleteAllFavoritos
    public void deleteAllFavoritos() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITOS, null, null);
    }
}