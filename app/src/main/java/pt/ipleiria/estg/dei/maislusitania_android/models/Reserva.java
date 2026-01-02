package pt.ipleiria.estg.dei.maislusitania_android.models;

public class Reserva {
    private int id;
    private int local_id;
    private String local_nome;
    private String data_visita;
    private double preco_total;
    private String estado;
    private String data_criacao;
    private String imagem_local;

    public Reserva(int id, int local_id, String local_nome, String data_visita, double preco_total, String estado, String data_criacao, String imagem_local) {
        this.id = id;
        this.local_id = local_id;
        this.local_nome = local_nome;
        this.data_visita = data_visita;
        this.preco_total = preco_total;
        this.estado = estado;
        this.data_criacao = data_criacao;
        this.imagem_local = imagem_local;
    }

    // Getters
    public int getId() { return id; }
    public int getLocalId() { return local_id; }
    public String getLocalNome() { return local_nome; }
    public String getDataVisita() { return data_visita; }
    public double getPrecoTotal() { return preco_total; }
    public String getEstado() { return estado; }
    public String getDataCriacao() { return data_criacao; }
    public String getImagemLocal() { return imagem_local; }
}