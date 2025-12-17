package pt.ipleiria.estg.dei.maislusitania_android.models;

public class Noticia {
    private int id;
    private String titulo;
    private String conteudo;
    private String resumo;
    private String imagem;
    private String dataPublicacao;
    private boolean ativo;
    private int localId;
    private boolean destaque;

    public Noticia(int id, String titulo, String conteudo, String resumo, String imagem, String dataPublicacao, boolean ativo, int localId, boolean destaque) {
        this.id = id;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.resumo = resumo;
        this.imagem = imagem;
        this.dataPublicacao = dataPublicacao;
        this.ativo = ativo;
        this.localId = localId;
        this.destaque = destaque;
    }

    // Getters
    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getConteudo() { return conteudo; }
    public String getResumo() { return resumo; }
    public String getImagem() { return imagem; }
    public String getDataPublicacao() { return dataPublicacao; }
    public boolean isAtivo() { return ativo; }
    public int getLocalId() { return localId; }
    public boolean isDestaque() { return destaque; }
}
