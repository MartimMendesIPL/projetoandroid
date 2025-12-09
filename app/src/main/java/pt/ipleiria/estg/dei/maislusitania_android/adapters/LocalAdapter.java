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

public class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.ViewHolder> {

    private List<Local> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Local item);
        void onFavoriteClick(Local item, int position);
    }

    public LocalAdapter(List<Local> items, OnItemClickListener listener) {
        this.items = items != null ? items : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_local, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Local item = items.get(position);

        holder.tvTitle.setText(item.getNome());
        holder.tvCategory.setText(item.getDistrito() + " • " + item.getMorada());
        holder.ratingBar.setRating(item.getAvaliacaoMedia());

        // Carregar imagem da API usando Glide (sem placeholder/error)
        Glide.with(holder.itemView.getContext())
                .load(item.getImagem())
                .into(holder.ivImage);

        // Atualizar ícone de favorito
        holder.ivFavorite.setImageResource(
                item.isFavorite() ? R.drawable.ic_fav_off : R.drawable.ic_fav_off
        );

        // Click no item inteiro
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });

        // Click no favorito
        holder.ivFavorite.setOnClickListener(v -> {
            if (listener != null) {
                item.setFavorite(!item.isFavorite());
                notifyItemChanged(position);
                listener.onFavoriteClick(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateList(List<Local> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage, ivFavorite;
        TextView tvTitle, tvCategory;
        RatingBar ratingBar;

        ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivItemImage);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvCategory = itemView.findViewById(R.id.tvItemCategory);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
