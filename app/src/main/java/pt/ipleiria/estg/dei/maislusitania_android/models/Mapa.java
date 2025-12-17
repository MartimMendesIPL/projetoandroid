package pt.ipleiria.estg.dei.maislusitania_android.models;

public class Mapa {
    private int id;
    private String nome;
    private String imagem;
    private String tipo;
    private Double latitude;
    private Double longitude;

    private String markerImagem;

    public Mapa(int id, String nome, String imagem, String tipo, String markerImagem, Double latitude, Double longitude) {
        this.id = id;
        this.nome = nome;
        this.imagem = imagem;
        this.tipo = tipo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.markerImagem = markerImagem;
    }

    public int getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }

    public String getImagem() {
        return imagem;
    }
    public String getTipo(){
        return tipo;
    }
    public Double getLatitude(){
        return latitude;
    }
    public Double getLongitude(){
        return longitude;
    }
    public String getMarkerImagem(){
        return markerImagem;
    }
}
