package pt.ipleiria.estg.dei.maislusitania_android.database;

import android.provider.BaseColumns;

// Contrato da base de dados que define a estrutura das tabelas
public class DbContract {
    private DbContract(){}

    // Tabela Reservas - armazena informações sobre as reservas dos utilizadores
    public static class ReservaEntry implements BaseColumns {
        static final String TABLE_NAME = "reservas";
        static final String COLUMN_ID = "id";                          // Chave Primária
        static final String COLUMN_LOCAL_ID = "local_id";              // ID do local
        static final String COLUMN_LOCAL_NOME = "local_nome";          // Nome do local
        static final String COLUMN_DATA_VISITA = "data_visita";        // Data da visita agendada
        static final String COLUMN_PRECO_TOTAL = "preco_total";        // Preço total da reserva
        static final String COLUMN_ESTADO = "estado";                  // Estado da reserva (Confirmada, Pendente, etc)
        static final String COLUMN_DATA_CRIACAO = "data_criacao";      // Data de criação da reserva
        static final String COLUMN_IMAGEM_LOCAL = "imagem_local";      // Imagem do local
    }

    // Tabela Bilhetes - armazena informações sobre bilhetes emitidos
    public static class BilheteEntry implements BaseColumns {
        static final String TABLE_NAME = "bilhetes";
        static final String COLUMN_CODIGO = "codigo";                          // Chave Primária - código único do bilhete
        static final String COLUMN_RESERVA_ID = "reserva_id";                  // Chave Estrangeira - referência à reserva
        static final String COLUMN_LOCAL_ID = "local_id";                      // ID do local
        static final String COLUMN_LOCAL_NOME = "local_nome";                  // Nome do local
        static final String COLUMN_DATA_VISITA = "data_visita";                // Data da visita
        static final String COLUMN_TIPO_BILHETE_ID = "tipo_bilhete_id";        // ID do tipo de bilhete
        static final String COLUMN_TIPO_BILHETE_NOME = "tipo_bilhete_nome";    // Nome do tipo de bilhete
        static final String COLUMN_PRECO = "preco";                            // Preço unitário do bilhete
        static final String COLUMN_ESTADO = "estado";                          // Estado do bilhete (Válido, Usado, Expirado)
    }

    // Tabela Favoritos - armazena os locais marcados como favoritos pelo utilizador
    public static class FavoritoEntry implements BaseColumns {
        static final String TABLE_NAME = "favoritos";
        static final String COLUMN_ID = "id";                          // Chave Primária
        static final String COLUMN_UTILIZADOR_ID = "utilizador_id";    // ID do utilizador proprietário do favorito
        static final String COLUMN_LOCAL_ID = "local_id";              // ID do local
        static final String COLUMN_NOME = "nome";                      // Nome do local
        static final String COLUMN_IMAGEM = "imagem";                  // URL/caminho da imagem do local
        static final String COLUMN_DISTRITO = "distrito";              // Distrito onde se localiza o lugar
        static final String COLUMN_AVALIACAO = "avaliacao_media";      // Classificação média do local
        static final String COLUMN_DATA_ADICAO = "data_adicao";        // Data em que foi adicionado aos favoritos
        static final String COLUMN_IS_FAVORITE = "is_favorite";        // Flag booleana indicando se é favorito
    }
}
