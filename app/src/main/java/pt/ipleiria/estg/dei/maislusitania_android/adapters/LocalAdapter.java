package pt.ipleiria.estg.dei.maislusitania_android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.estg.dei.maislusitania_android.R;
import pt.ipleiria.estg.dei.maislusitania_android.models.Local;

// Adaptador para apresentar uma lista de locais numa RecyclerView
public class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.ViewHolder> {

    private List<Local> items;
    private OnItemClickListener listener;

    // Interface para tratar cliques nos itens e no botão de favorito
    public interface OnItemClickListener {
        void onItemClick(Local item);
        void onFavoriteClick(Local item, int position);
    }

    // Construtor que inicializa o adaptador com lista de locais e listener de cliques
    public LocalAdapter(List<Local> items, OnItemClickListener listener) {
        this.items = items != null ? items : new ArrayList<>();
        this.listener = listener;
    }

    // Cria um novo ViewHolder quando necessário
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_local, parent, false);
        return new ViewHolder(view);
    }

    // Associa os dados do local aos elementos visuais do ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Local item = items.get(position);

        // Preenche os textos e classificação
        holder.tvTitle.setText(item.getNome());
        holder.tvCategory.setText(item.getDistrito());
        holder.ratingBar.setRating(item.getAvaliacaoMedia());

        // Carrega a imagem com Glide
        Glide.with(holder.itemView.getContext())
                .load(item.getImagem())
                .into(holder.ivImage);

        // Define o ícone de favorito conforme o estado
        holder.ivFavorite.setImageResource(
                item.isFavorite() ? R.drawable.ic_fav_on : R.drawable.ic_fav_off
        );

        // Listener para clique no item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });

        // Listener para clique no botão de favorito
        holder.ivFavorite.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFavoriteClick(item, holder.getAdapterPosition());
            }
        });
    }

    // Retorna o número total de locais
    @Override
    public int getItemCount() {
        return items.size();
    }

    // Atualiza a lista de locais e notifica o adaptador
    public void updateList(List<Local> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    // ViewHolder que representa cada item na lista
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;          // Imagem do local
        ImageView ivFavorite;       // Ícone de favorito
        TextView tvTitle;           // Nome do local
        TextView tvCategory;        // Distrito do local
        RatingBar ratingBar;        // Classificação em estrelas

        // Inicializa as referências aos elementos do layout
        ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivItemImage);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvCategory = itemView.findViewById(R.id.tvItemCategory);
            ratingBar = itemView.findViewById(R.id.ratingBar);

            ivFavorite.setClickable(true);
            ivFavorite.setFocusable(true);
        }
    }
}
