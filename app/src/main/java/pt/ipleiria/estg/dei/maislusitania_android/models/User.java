package pt.ipleiria.estg.dei.maislusitania_android.models;

public class User {
    private int id;
    private String primeiro_nome;
    private String ultimo_nome;
    private String imagem_perfil;
    private int user_id;
    private String username;
    private String email;
    private String data_adesao;

    //Criar contrutor
    public User(int id, String primeiro_nome, String ultimo_nome, String imagem_perfil, int user_id, String username, String email, String data_adesao) {
        this.id = id;
        this.primeiro_nome = primeiro_nome;
        this.ultimo_nome = ultimo_nome;
        this.imagem_perfil = imagem_perfil;
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.data_adesao = data_adesao;
    }

    //Getters

    public int getId() {

        return id;
    }


    public String getPrimeiro_nome() {

        return primeiro_nome;
    }

    public String getUltimo_nome() {

        return ultimo_nome;
    }

    public String getImagem_perfil() {

        return imagem_perfil;
    }

    public int getUser_id() {

        return user_id;
    }


    public String getUsername() {

        return username;
    }

    public String getEmail() {

        return email;
    }

    public String getData_adesao() {

        return data_adesao;
    }

    //Setters
    public void setId(int id) {

        this.id = id;
    }


    public void setPrimeiro_nome(String primeiro_nome) {

        this.primeiro_nome = primeiro_nome;
    }

    public void setUltimo_nome(String ultimo_nome) {

        this.ultimo_nome = ultimo_nome;
    }

    public void setImagem_perfil(String imagem_perfil) {

        this.imagem_perfil = imagem_perfil;
    }

    public void setUser_id(int user_id) {

        this.user_id = user_id;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public void setData_adesao(String data_adesao) {

        this.data_adesao = data_adesao;
    }

}
