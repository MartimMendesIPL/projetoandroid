package pt.ipleiria.estg.dei.maislusitania_android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.R;
import pt.ipleiria.estg.dei.maislusitania_android.models.Noticia;

// Adaptador para apresentar uma lista de notícias numa RecyclerView
public class NoticiaAdapter extends RecyclerView.Adapter<NoticiaAdapter.ViewHolder> {

    private Context context;                          // Contexto da aplicação
    private ArrayList<Noticia> noticias;              // Lista de notícias
    private OnNoticiaListener onNoticiaListener;      // Listener de cliques

    // Interface para tratar cliques nos itens da lista
    public interface OnNoticiaListener {
        void onNoticiaClick(int position);
    }

    // Construtor que inicializa o adaptador com contexto, lista de notícias e listener de cliques
    public NoticiaAdapter(Context context, ArrayList<Noticia> noticias, OnNoticiaListener onNoticiaListener) {
        this.context = context;
        this.noticias = noticias;
        this.onNoticiaListener = onNoticiaListener;
    }

    // Cria um novo ViewHolder quando necessário
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_noticia, parent, false);
        return new ViewHolder(view, onNoticiaListener);
    }

    // Associa os dados da notícia aos elementos visuais do ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Noticia noticia = noticias.get(position);

        // Preenche os textos da notícia
        holder.tvNome.setText(noticia.getNome());
        holder.tvLocalNome.setText(noticia.getLocal_nome());
        holder.tvDataPublicacao.setText(noticia.getDataPublicacao());

        // Carrega a imagem com Glide
        String urlImagem = noticia.getImagem();
        if (urlImagem != null && !urlImagem.isEmpty()) {
            Glide.with(context)
                    .load(urlImagem)
                    .placeholder(R.drawable.ic_launcher_background) // Imagem enquanto carrega
                    .error(R.drawable.ic_launcher_background)       // Imagem em caso de erro
                    .diskCacheStrategy(DiskCacheStrategy.ALL)       // Guarda em cache
                    .into(holder.ivImagem);
        } else {
            holder.ivImagem.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    // Retorna o número total de notícias
    @Override
    public int getItemCount() {
        return noticias != null ? noticias.size() : 0;
    }

    // Atualiza a lista de notícias e notifica o adaptador
    public void updateNoticias(ArrayList<Noticia> novasNoticias) {
        this.noticias = novasNoticias;
        notifyDataSetChanged();
    }

    // ViewHolder que representa cada item na lista
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvNome;                // Título da notícia
        TextView tvLocalNome;           // Nome do local
        TextView tvDataPublicacao;      // Data de publicação
        ImageView ivImagem;             // Imagem da notícia
        OnNoticiaListener onNoticiaListener;

        // Inicializa as referências aos elementos do layout
        public ViewHolder(@NonNull View itemView, OnNoticiaListener onNoticiaListener) {
            super(itemView);

            // Vincula os IDs do layout aos atributos
            ivImagem = itemView.findViewById(R.id.ivImagem);
            tvNome = itemView.findViewById(R.id.tvNome);
            tvLocalNome = itemView.findViewById(R.id.tvResumo);
            tvDataPublicacao = itemView.findViewById(R.id.tvDataPublicacao);

            this.onNoticiaListener = onNoticiaListener;
            itemView.setOnClickListener(this);
        }

        // Trata o clique no item
        @Override
        public void onClick(View v) {
            if (onNoticiaListener != null) {
                onNoticiaListener.onNoticiaClick(getAdapterPosition());
            }
        }
    }
}
