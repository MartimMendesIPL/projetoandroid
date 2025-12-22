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
import pt.ipleiria.estg.dei.maislusitania_android.models.Favorito;

public class FavoritoAdapter extends RecyclerView.Adapter<FavoritoAdapter.ViewHolder> {

    private List<Favorito> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Favorito item);
        void onFavoriteClick(Favorito item, int position);
    }

    public FavoritoAdapter(List<Favorito> items, OnItemClickListener listener) {
        this.items = items != null ? items : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_favorito, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Favorito item = items.get(position);

        holder.tvTitle.setText(item.getLocalNome());
        holder.tvCategory.setText(item.getLocalDistrito());
        holder.ratingBar.setRating(item.getAvaliacaoMedia());

        Glide.with(holder.itemView.getContext())
                .load(item.getLocalImagem())
                .into(holder.ivImage);

        holder.ivFavorite.setImageResource(
                item.isFavorite() ? R.drawable.ic_fav_on : R.drawable.ic_fav_off
        );

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });

        holder.ivFavorite.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFavoriteClick(item, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateList(List<Favorito> newItems) {
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

            ivFavorite.setClickable(true);
            ivFavorite.setFocusable(true);
        }
    }
}
