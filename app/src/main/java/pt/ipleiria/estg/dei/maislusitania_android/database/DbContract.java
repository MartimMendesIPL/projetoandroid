package pt.ipleiria.estg.dei.maislusitania_android.database;

import android.provider.BaseColumns;

public class DbContract {
    private DbContract(){}

    //Tabela Reservas
    public static class ReservaEntry implements BaseColumns {
        public static final String TABLE_NAME = "reservas";
        public static final String COLUMN_ID = "id"; //Chave Primaria
        public static final String COLUMN_LOCAL_ID = "local_id";
        public static final String COLUMN_LOCAL_NOME = "local_nome";
        public static final String COLUMN_DATA_VISITA = "data_visita";
        public static final String COLUMN_PRECO_TOTAL = "preco_total";
        public static final String COLUMN_ESTADO = "estado";
        public static final String COLUMN_DATA_CRIACAO = "data_criacao";
        public static final String COLUMN_IMAGEM_LOCAL = "imagem_local";
    }

    //Tabela Bilhetes
    public static class BilheteEntry implements BaseColumns {
        public static final String TABLE_NAME = "bilhetes";
        public static final String COLUMN_CODIGO = "codigo"; //Chave Primaria
        public static final String COLUMN_RESERVA_ID = "reserva_id"; // Chave Estrangeira
        public static final String COLUMN_LOCAL_ID = "local_id";
        public static final String COLUMN_LOCAL_NOME = "local_nome";
        public static final String COLUMN_DATA_VISITA = "data_visita";
        public static final String COLUMN_TIPO_BILHETE_ID = "tipo_bilhete_id";
        public static final String COLUMN_TIPO_BILHETE_NOME = "tipo_bilhete_nome";
        public static final String COLUMN_PRECO = "preco";
        public static final String COLUMN_ESTADO = "estado";
    }
}
