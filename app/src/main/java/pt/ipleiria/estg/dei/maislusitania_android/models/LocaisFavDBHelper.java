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
    private static final int DB_VERSION = 3;

    private static final String TABLE_FAVORITOS = "favoritos";

    // --- COLUNAS ---
    private static final String ID = "id";
    private static final String UTILIZADORID = "utilizador_id";
    private static final String LOCALID = "local_id";
    private static final String NOME = "nome";
    private static final String IMAGEM = "imagem";
    // Novos campos para modo offline:
    private static final String DISTRITO = "distrito";
    private static final String AVALIACAO = "avaliacao_media";
    private static final String DATAADICAO = "data_adicao";
    private static final String ISFAVORITE = "is_favorite";


    public LocaisFavDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criar tabela com TODOS os campos
        String sql = "CREATE TABLE " + TABLE_FAVORITOS + "(" +
                ID + " INTEGER PRIMARY KEY, " +
                UTILIZADORID + " INTEGER, " +
                LOCALID + " INTEGER, " +
                NOME + " TEXT, " +
                DISTRITO + " TEXT, " +
                IMAGEM + " TEXT, " +
                AVALIACAO + " REAL," + // REAL é o tipo para float/double em SQLite
                DATAADICAO + " TEXT, " +
                ISFAVORITE + " INTEGER DEFAULT 1" +  // 1 para true, 0 para false
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

    public void adicionarFavorito(Favorito favorito) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Guardar TUDO o que vem do objeto Local
        values.put(ID, favorito.getId());
        values.put(UTILIZADORID, favorito.getUtilizadorId());
        values.put(LOCALID, favorito.getLocalId());
        values.put(NOME, favorito.getLocalNome());
        values.put(DISTRITO, favorito.getLocalDistrito());
        values.put(IMAGEM, favorito.getLocalImagem());
        values.put(AVALIACAO, favorito.getAvaliacaoMedia());
        values.put(DATAADICAO, favorito.getDataAdicao());
        values.put(ISFAVORITE, favorito.isFavorite() ? 1 : 0);

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

    public ArrayList<Favorito> getAllFavoritos() {
        ArrayList<Favorito> favoritos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FAVORITOS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Favorito fav = new Favorito(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(UTILIZADORID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(LOCALID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(IMAGEM)),  // localImagem
                        cursor.getString(cursor.getColumnIndexOrThrow(NOME)),    // localNome
                        cursor.getString(cursor.getColumnIndexOrThrow(DISTRITO)), // localDistrito
                        cursor.getFloat(cursor.getColumnIndexOrThrow(AVALIACAO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DATAADICAO)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(ISFAVORITE)) == 1
                );

                fav.setFavorite(true);
                favoritos.add(fav);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favoritos;
    }

    //deleteAllFavoritos
    public void deleteAllFavoritos() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITOS, null, null);
    }
}