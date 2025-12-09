package pt.ipleiria.estg.dei.maislusitania_android.models;

public class Local {
    private int id;
    private String nome;
    private String morada;
    private String distrito;
    private String descricao;
    private String imagem;
    private float avaliacaoMedia;
    private boolean isFavorite;

    public Local(int id, String nome, String morada, String distrito,
                 String descricao, String imagem, float avaliacaoMedia) {
        this.id = id;
        this.nome = nome;
        this.morada = morada;
        this.distrito = distrito;
        this.descricao = descricao;
        this.imagem = imagem;
        this.avaliacaoMedia = avaliacaoMedia;
        this.isFavorite = false;
    }

    // Getters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getMorada() { return morada; }
    public String getDistrito() { return distrito; }
    public String getDescricao() { return descricao; }
    public String getImagem() { return imagem; }
    public float getAvaliacaoMedia() { return avaliacaoMedia; }
    public boolean isFavorite() { return isFavorite; }

    // Métodos de compatibilidade (se necessário para o adapter existente)
    public String getTitle() { return nome; }
    public String getCategory() { return distrito; }
    public String getDistance() { return morada; }
    public String getDescription() { return descricao; }
    public float getRating() { return avaliacaoMedia; }
    public int getImageResId() { return 0; } // Não usado mais

    // Setter para favorito
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
