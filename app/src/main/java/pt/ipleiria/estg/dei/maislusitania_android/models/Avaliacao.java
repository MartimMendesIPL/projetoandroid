package pt.ipleiria.estg.dei.maislusitania_android.models;

public class Avaliacao {
    private int id;
    private String utilizador;
    private float classificacao;
    private String comentario;
    private String dataAvaliacao;
    private int localId;
private int utilizadorId;


    public Avaliacao(int id, int localId, int utilizadorId, String utilizador, float classificacao, String comentario, String dataAvaliacao) {
        this.id = id;
        this.localId = localId;
        this.utilizadorId = utilizadorId;
        this.utilizador = utilizador;
        this.classificacao = classificacao;
        this.comentario = comentario;
        this.dataAvaliacao = dataAvaliacao;
    }

    public int getId() {
        return id;
    }

    public String getUtilizador() {
        return utilizador;
    }

    public float getClassificacao() {
        return classificacao;
    }

    public String getComentario() {
        return comentario;
    }

    public String getDataAvaliacao() {
        return dataAvaliacao;
    }
    public int getLocalId() {
        return localId;
    }
    public int getUtilizadorId() {
        return utilizadorId;
    }
}
