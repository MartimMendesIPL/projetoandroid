package pt.ipleiria.estg.dei.maislusitania_android.models;

public class Bilhete {
    private String codigo;
    private int reserva_id;
    private Local local;
    private String data_visita;
    private String tipo_bilhete;
    private String preco;
    private String estado;
    private String data_criacao;

    // Classe interna para Local
    public static class Local {
        private int id;
        private String nome;

        public Local(int id, String nome) {
            this.id = id;
            this.nome = nome;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }
    }

    // Construtor vazio
    public Bilhete() {
    }

    // Construtor completo
    public Bilhete(String codigo, int reserva_id, Local local, String data_visita,
                   String tipo_bilhete, String preco, String estado, String data_criacao) {
        this.codigo = codigo;
        this.reserva_id = reserva_id;
        this.local = local;
        this.data_visita = data_visita;
        this.tipo_bilhete = tipo_bilhete;
        this.preco = preco;
        this.estado = estado;
        this.data_criacao = data_criacao;
    }

    // Getters e Setters
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getReservaId() {
        return reserva_id;
    }

    public void setReservaId(int reserva_id) {
        this.reserva_id = reserva_id;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public String getDataVisita() {
        return data_visita;
    }

    public void setDataVisita(String data_visita) {
        this.data_visita = data_visita;
    }

    public String getTipoBilhete() {
        return tipo_bilhete;
    }

    public void setTipoBilhete(String tipo_bilhete) {
        this.tipo_bilhete = tipo_bilhete;
    }

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDataCriacao() {
        return data_criacao;
    }

    public void setDataCriacao(String data_criacao) {
        this.data_criacao = data_criacao;
    }
}
