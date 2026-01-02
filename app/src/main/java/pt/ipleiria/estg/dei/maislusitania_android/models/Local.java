package pt.ipleiria.estg.dei.maislusitania_android.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Local {
    private int id;
    private String nome;
    private String morada;
    private String distrito;
    private String descricao;
    private String imagem;
    private float avaliacaoMedia;
    private boolean isFavorite;
    private int favorito_id;

    // Novos campos para detalhes
    private String telefone;
    private String email;
    private String website;
    private Map<String, String> horario; // Mapa para guardar "segunda" -> "10:00 - 18:00"
    private ArrayList<Avaliacao> avaliacoes;
    private ArrayList<TipoBilhete> tiposBilhete;

    public Local(int id, String nome, String morada, String distrito,
                 String descricao, String imagem, float avaliacaoMedia, int favorito_id) {
        this.id = id;
        this.nome = nome;
        this.morada = morada;
        this.distrito = distrito;
        this.descricao = descricao;
        this.imagem = imagem;
        this.avaliacaoMedia = avaliacaoMedia;
        this.favorito_id = favorito_id;
        this.isFavorite = false;

        // Inicializar as listas e mapas para evitar NullPointerException
        this.horario = new HashMap<>();
        this.avaliacoes = new ArrayList<>();
        this.tiposBilhete = new ArrayList<>();
    }

    // --- Getters Existentes ---
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getMorada() { return morada; }
    public String getDistrito() { return distrito; }
    public String getDescricao() { return descricao; }
    public String getImagem() { return imagem; }
    public float getAvaliacaoMedia() { return avaliacaoMedia; }
    public boolean isFavorite() { return isFavorite; }
    public int getFavoritoId() { return favorito_id; }

    // --- Setters Existentes ---
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public void setFavoritoId(int favorito_id) { this.favorito_id = favorito_id; }

    // --- NOVOS Getters e Setters para Detalhes ---

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    // Horários
    public Map<String, String> getHorario() { return horario; }

    // Método auxiliar usado no parser: local.addHorario("segunda", "...")
    public void addHorario(String dia, String horas) {
        if (this.horario == null) {
            this.horario = new HashMap<>();
        }
        this.horario.put(dia, horas);
    }

    // Avaliações
    public ArrayList<Avaliacao> getAvaliacoes() { return avaliacoes; }
    public void setAvaliacoes(ArrayList<Avaliacao> avaliacoes) { this.avaliacoes = avaliacoes; }

    // Bilhetes
    public ArrayList<TipoBilhete> getTiposBilhete() { return tiposBilhete; }
    public void setTiposBilhete(ArrayList<TipoBilhete> tiposBilhete) { this.tiposBilhete = tiposBilhete; }
}
