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

public class NoticiaAdapter extends RecyclerView.Adapter<NoticiaAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Noticia> noticias;
    private OnNoticiaListener onNoticiaListener;

    // Interface para lidar com cliques nos itens
    public interface OnNoticiaListener {
        void onNoticiaClick(int position);
    }

    public NoticiaAdapter(Context context, ArrayList<Noticia> noticias, OnNoticiaListener onNoticiaListener) {
        this.context = context;
        this.noticias = noticias;
        this.onNoticiaListener = onNoticiaListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_noticia, parent, false);
        return new ViewHolder(view, onNoticiaListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Noticia noticia = noticias.get(position);

        // Definir textos
        holder.tvNome.setText(noticia.getNome());
        holder.tvLocalNome.setText(noticia.getLocal_nome());
        // Formatar ou exibir a data como vem da API
        holder.tvDataPublicacao.setText(noticia.getDataPublicacao());
        // Carregar imagem usando Glide
        String urlImagem = noticia.getImagem();
        // Se a imagem não for nula ou vazia, carrega
        if (urlImagem != null && !urlImagem.isEmpty()) {
            Glide.with(context)
                    .load(urlImagem)
                    .placeholder(R.drawable.ic_launcher_background) // Imagem de placeholder enquanto carrega
                    .error(R.drawable.ic_launcher_background)       // Imagem em caso de erro
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivImagem);
        } else {
            holder.ivImagem.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public int getItemCount() {
        return noticias != null ? noticias.size() : 0;
    }

    public void updateNoticias(ArrayList<Noticia> novasNoticias) {
        this.noticias = novasNoticias;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvNome,
                 tvLocalNome,
                 tvDataPublicacao;

        ImageView ivImagem;
        OnNoticiaListener onNoticiaListener;

        public ViewHolder(@NonNull View itemView, OnNoticiaListener onNoticiaListener) {
            super(itemView);

            // Vincular os IDs do layout item_list_noticia.xml
            ivImagem = itemView.findViewById(R.id.ivImagem);
            tvNome= itemView.findViewById(R.id.tvNome);
            tvLocalNome = itemView.findViewById(R.id.tvResumo);
            tvDataPublicacao = itemView.findViewById(R.id.tvDataPublicacao);

            this.onNoticiaListener = onNoticiaListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onNoticiaListener != null) {
                onNoticiaListener.onNoticiaClick(getAdapterPosition());
            }
        }
    }
}
