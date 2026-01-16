package pt.ipleiria.estg.dei.maislusitania_android.models;

public class Noticia {
    // Atributos
    private int id;
    private String nome;
    private String local_nome;
    private String resumo;
    private String conteudo;
    private String imagem;
    private String dataPublicacao;
    // Construtor
    public Noticia(int id, String nome, String local_nome, String resumo, String conteudo, String imagem, String dataPublicacao) {
        this.id = id;
        this.nome = nome;
        this.local_nome = local_nome;
        this.resumo = resumo;
        this.conteudo = conteudo;
        this.imagem = imagem;
        this.dataPublicacao = dataPublicacao;
    }
    // Getters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getLocal_nome() { return local_nome; }
    public String getResumo() { return resumo; }
    public String getConteudo() { return conteudo; }
    public String getImagem() { return imagem; }
    public String getDataPublicacao() { return dataPublicacao; }

}
