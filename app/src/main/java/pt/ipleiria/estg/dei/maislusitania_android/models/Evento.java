package pt.ipleiria.estg.dei.maislusitania_android.models;

public class Evento {
    private int id;
    private String local;
    private String titulo;
    private String descricao;
    private String imagem;
    private String dataInicio;
    private String dataFim;

    public Evento(int id, String local, String titulo, String descricao, String imagem, String dataInicio, String dataFim){
        this.id = id;
        this.local = local;
        this.titulo = titulo;
        this.descricao = descricao;
        this.imagem = imagem;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    // Getters
    public int getId() {
        return id;
    }
    public String getLocal() {
        return local;
    }
    public String getTitulo() {
        return titulo;
    }
    public String getDescricao() {
        return descricao;
    }
    public String getImagem() {
        return imagem;
    }
    public String getDataInicio() {
        return dataInicio;
    }
    public String getDataFim() {
        return dataFim;
    }
}
