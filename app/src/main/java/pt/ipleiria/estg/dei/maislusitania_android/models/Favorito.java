package pt.ipleiria.estg.dei.maislusitania_android.models;

public class Favorito {
    private int id;
    private int utilizadorId;
    private int localId;
    private String localImagem;
    private String localNome;
    private String localDistrito;
    private float AvaliacaoMedia;
    private String dataAdicao;
    private boolean isFavorite;


    public Favorito(int id, int utilizadorId, int localId, String localImagem, String localNome, String localDistrito, float avaliacaoMedia, String dataAdicao, boolean isFavorite) {
        this.id = id;
        this.utilizadorId = utilizadorId;
        this.localId = localId;
        this.localImagem = localImagem;
        this.localNome = localNome;
        this.localDistrito = localDistrito;
        this.AvaliacaoMedia = avaliacaoMedia;
        this.dataAdicao = dataAdicao;
        this.isFavorite = isFavorite;
    }

    // Getters
    public int getId() { return id; }
    public int getUtilizadorId() { return utilizadorId; }
    public int getLocalId() { return localId; }
    public String getLocalImagem() { return localImagem; }
    public String getLocalNome() { return localNome; }
    public String getLocalDistrito() { return localDistrito; }
    public float getAvaliacaoMedia() { return AvaliacaoMedia; }
    public String getDataAdicao() { return dataAdicao; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
