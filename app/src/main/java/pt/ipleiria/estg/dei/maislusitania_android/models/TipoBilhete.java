package pt.ipleiria.estg.dei.maislusitania_android.models;

public class TipoBilhete {
    private int id;
    private String nome;
    private String descricao;
    private String preco;

    private int ativo;

    private int local_id;

    private int quantidade = 0;

    public TipoBilhete(int id, String nome, String descricao, String preco, int ativo, int local_id) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.ativo = ativo;
        this.local_id = local_id;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getPreco() {
        return preco;
    }

    public int getAtivo() {
        return ativo;
    }

    public int getLocal_id() {
        return local_id;
    }

    public void setAtivo(int ativo) {
        this.ativo = ativo;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
