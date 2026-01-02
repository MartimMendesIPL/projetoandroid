package pt.ipleiria.estg.dei.maislusitania_android.models;

public class TipoBilhete {
    private int id;
    private String nome;
    private String descricao;
    private String preco;

    public TipoBilhete(int id, String nome, String descricao, String preco) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
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
}
