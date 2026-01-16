package pt.ipleiria.estg.dei.maislusitania_android.database;

import android.provider.BaseColumns;

public class DbContract {
    private DbContract(){}

    //Tabela Reservas
    public static class ReservaEntry implements BaseColumns {
        static final String TABLE_NAME = "reservas";
        static final String COLUMN_ID = "id"; //Chave Primaria
        static final String COLUMN_LOCAL_ID = "local_id";
        static final String COLUMN_LOCAL_NOME = "local_nome";
        static final String COLUMN_DATA_VISITA = "data_visita";
        static final String COLUMN_PRECO_TOTAL = "preco_total";
        static final String COLUMN_ESTADO = "estado";
        static final String COLUMN_DATA_CRIACAO = "data_criacao";
        static final String COLUMN_IMAGEM_LOCAL = "imagem_local";
    }

    //Tabela Bilhetes
    public static class BilheteEntry implements BaseColumns {
        static final String TABLE_NAME = "bilhetes";
        static final String COLUMN_CODIGO = "codigo"; //Chave Primaria
        static final String COLUMN_RESERVA_ID = "reserva_id"; // Chave Estrangeira
        static final String COLUMN_LOCAL_ID = "local_id";
        static final String COLUMN_LOCAL_NOME = "local_nome";
        static final String COLUMN_DATA_VISITA = "data_visita";
        static final String COLUMN_TIPO_BILHETE_ID = "tipo_bilhete_id";
        static final String COLUMN_TIPO_BILHETE_NOME = "tipo_bilhete_nome";
        static final String COLUMN_PRECO = "preco";
        static final String COLUMN_ESTADO = "estado";
    }

    //Tabela Favoritos
    public static class FavoritoEntry implements BaseColumns {
        static final String TABLE_NAME = "favoritos";
        static final String COLUMN_ID = "id";
        static final String COLUMN_UTILIZADOR_ID = "utilizador_id";
        static final String COLUMN_LOCAL_ID = "local_id";
        static final String COLUMN_NOME = "nome";
        static final String COLUMN_IMAGEM = "imagem";
        // Novos campos para modo offline:
        static final String COLUMN_DISTRITO = "distrito";
        static final String COLUMN_AVALIACAO = "avaliacao_media";
        static final String COLUMN_DATA_ADICAO = "data_adicao";
        static final String COLUMN_IS_FAVORITE = "is_favorite";
    }
}
