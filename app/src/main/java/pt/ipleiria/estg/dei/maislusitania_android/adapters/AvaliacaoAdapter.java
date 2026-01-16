package pt.ipleiria.estg.dei.maislusitania_android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pt.ipleiria.estg.dei.maislusitania_android.R;
import pt.ipleiria.estg.dei.maislusitania_android.models.Avaliacao;

// Adaptador para apresentar uma lista de avaliações numa RecyclerView
public class AvaliacaoAdapter extends RecyclerView.Adapter<AvaliacaoAdapter.AvaliacaoViewHolder> {

    private List<Avaliacao> avaliacoes;
    private final LayoutInflater mInflater;

    // Construtor que inicializa o adaptador com o contexto e lista de avaliações
    public AvaliacaoAdapter(Context context, List<Avaliacao> avaliacoes) {
        this.mInflater = LayoutInflater.from(context);
        this.avaliacoes = avaliacoes;
    }

    // ViewHolder que representa cada item na lista
    public static class AvaliacaoViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsername;        // Nome do utilizador
        TextView tvComentario;      // Comentário da avaliação
        RatingBar ratingBar;        // Classificação em estrelas
        TextView tvData;            // Data da avaliação

        public AvaliacaoViewHolder(@NonNull View itemView) {
            super(itemView);

            // Referencia os elementos do layout
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvComentario = itemView.findViewById(R.id.tvComment);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvData = itemView.findViewById(R.id.tvDate);
        }
    }

    // Cria um novo ViewHolder quando necessário
    @NonNull
    @Override
    public AvaliacaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_list_avaliacao, parent, false);
        return new AvaliacaoViewHolder(itemView);
    }

    // Associa os dados da avaliação aos elementos visuais do ViewHolder
    @Override
    public void onBindViewHolder(@NonNull AvaliacaoViewHolder holder, int position) {
        Avaliacao currentAvaliacao = avaliacoes.get(position);

        holder.tvUsername.setText(currentAvaliacao.getUtilizador());
        holder.tvComentario.setText(currentAvaliacao.getComentario());
        holder.ratingBar.setRating(currentAvaliacao.getClassificacao());
        holder.tvData.setText(currentAvaliacao.getDataAvaliacao());
    }

    // Retorna o número total de avaliações
    @Override
    public int getItemCount() {
        return avaliacoes != null ? avaliacoes.size() : 0;
    }

    // Atualiza a lista de avaliações e notifica o adaptador
    public void updateAvaliacoes(List<Avaliacao> newAvaliacoes) {
        this.avaliacoes = newAvaliacoes;
        notifyDataSetChanged();
    }
}
