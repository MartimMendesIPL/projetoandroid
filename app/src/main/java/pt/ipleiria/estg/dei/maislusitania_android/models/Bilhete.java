package pt.ipleiria.estg.dei.maislusitania_android.models;

public class Bilhete {
    private String codigo; // Ex: "000105-001"
    private int reserva_id;
    private int local_id;
    private String local_nome;
    private String data_visita;
    private int tipo_bilhete_id;
    private String tipo_bilhete_nome; // Ex: "Adulto", "Criança"
    private double preco;
    private String estado;

    public Bilhete(String codigo, int reserva_id, int local_id, String local_nome, String data_visita, int tipo_bilhete_id, String tipo_bilhete_nome, double preco, String estado) {
        this.codigo = codigo;
        this.reserva_id = reserva_id;
        this.local_id = local_id;
        this.local_nome = local_nome;
        this.data_visita = data_visita;
        this.tipo_bilhete_id = tipo_bilhete_id;
        this.tipo_bilhete_nome = tipo_bilhete_nome;
        this.preco = preco;
        this.estado = estado;
    }

    // Getters
    public String getCodigo() { return codigo; }
    public int getReservaId() { return reserva_id; }
    public int getLocalId() { return local_id; }
    public String getLocalNome() { return local_nome; }
    public String getDataVisita() { return data_visita; }
    public int getTipoBilheteId() { return tipo_bilhete_id; }
    public String getTipoBilheteNome() { return tipo_bilhete_nome; }
    public double getPreco() { return preco; }
    public String getEstado() { return estado; }
}