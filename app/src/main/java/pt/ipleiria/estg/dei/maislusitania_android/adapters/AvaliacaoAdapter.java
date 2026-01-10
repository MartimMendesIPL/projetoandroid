package pt.ipleiria.estg.dei.maislusitania_android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView; // Import TextView

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pt.ipleiria.estg.dei.maislusitania_android.R;
import pt.ipleiria.estg.dei.maislusitania_android.models.Avaliacao;

public class AvaliacaoAdapter extends RecyclerView.Adapter<AvaliacaoAdapter.AvaliacaoViewHolder> {

    private List<Avaliacao> avaliacoes;
    private final LayoutInflater mInflater;

    public AvaliacaoAdapter(Context context, List<Avaliacao> avaliacoes) {
        this.mInflater = LayoutInflater.from(context);
        this.avaliacoes = avaliacoes;
    }

    // ViewHolder class with corrected view types
    public static class AvaliacaoViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsername;
        TextView tvComentario;
        RatingBar ratingBar;
        TextView tvData;

        public AvaliacaoViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvComentario = itemView.findViewById(R.id.tvComment);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvData = itemView.findViewById(R.id.tvDate);
        }
    }

    @NonNull
    @Override
    public AvaliacaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_list_avaliacao, parent, false);
        return new AvaliacaoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AvaliacaoViewHolder holder, int position) {
        Avaliacao currentAvaliacao = avaliacoes.get(position);

        holder.tvUsername.setText(currentAvaliacao.getUtilizador());
        holder.tvComentario.setText(currentAvaliacao.getComentario());
        holder.ratingBar.setRating(currentAvaliacao.getClassificacao());
        holder.tvData.setText(currentAvaliacao.getDataAvaliacao());
    }


    @Override
    public int getItemCount() {
        return avaliacoes != null ? avaliacoes.size() : 0;
    }

    public void updateAvaliacoes(List<Avaliacao> newAvaliacoes) {
        this.avaliacoes = newAvaliacoes;
        notifyDataSetChanged();
    }
}
