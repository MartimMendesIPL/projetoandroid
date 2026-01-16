package pt.ipleiria.estg.dei.maislusitania_android.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pt.ipleiria.estg.dei.maislusitania_android.R;
import pt.ipleiria.estg.dei.maislusitania_android.models.Reserva;

// Adaptador para apresentar uma lista de reservas numa RecyclerView
public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder> {

    private List<Reserva> reservas;                   // Lista de reservas
    private OnItemClickListener onItemClickListener;  // Listener de cliques
    private Context context;                          // Contexto da aplicação

    // Interface para tratar cliques nos itens da lista
    public interface OnItemClickListener {
        void onItemClick(Reserva reserva);
    }

    // Construtor que inicializa o adaptador com contexto, lista de reservas e listener de cliques
    public ReservaAdapter(Context context, List<Reserva> reservas, OnItemClickListener listener) {
        this.context = context;
        this.reservas = reservas != null ? reservas : new ArrayList<>();
        this.onItemClickListener = listener;
    }

    // ViewHolder que representa cada item na lista
    public static class ReservaViewHolder extends RecyclerView.ViewHolder {
        ImageView ivReservaImagem;    // Imagem do local
        TextView tvReservaLocal;      // Nome do local
        TextView tvReservaData;       // Data da visita
        TextView tvReservaPreco;      // Preço total da reserva
        TextView tvReservaEstado;     // Estado da reserva

        // Inicializa as referências aos elementos do layout
        public ReservaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivReservaImagem = itemView.findViewById(R.id.ivReservaImagem);
            tvReservaLocal = itemView.findViewById(R.id.tvReservaLocal);
            tvReservaData = itemView.findViewById(R.id.tvReservaData);
            tvReservaPreco = itemView.findViewById(R.id.tvReservaPreco);
            tvReservaEstado = itemView.findViewById(R.id.tvReservaEstado);
        }
    }

    // Cria um novo ViewHolder quando necessário
    @NonNull
    @Override
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_reserva, parent, false);
        return new ReservaViewHolder(view);
    }

    // Associa os dados da reserva aos elementos visuais do ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {
        Reserva reserva = reservas.get(position);

        // Carrega a imagem com Glide
        Glide.with(context)
                .load(reserva.getImagemLocal())
                .into(holder.ivReservaImagem);

        // Preenche o nome do local
        holder.tvReservaLocal.setText(reserva.getLocalNome());

        // Preenche a data de visita
        holder.tvReservaData.setText(formatarData(reserva.getDataVisita()));

        // Preenche o preço total formatado em Euro (2 casas decimais)
        holder.tvReservaPreco.setText(String.format(Locale.getDefault(), "Total: %.2f€", reserva.getPrecoTotal()));

        // Define o estado e cor associada
        String estado = reserva.getEstado() != null ? reserva.getEstado() : "";
        holder.tvReservaEstado.setText(estado);

        int cor;
        switch (estado) {
            case "Confirmada":
                cor = Color.parseColor("#28A745"); // Verde
                break;
            case "Pendente":
            case "Expirado":
                cor = Color.parseColor("#FFC107"); // Amarelo
                break;
            case "Cancelada":
                cor = Color.parseColor("#DC3545"); // Vermelho
                break;
            default:
                cor = Color.GRAY;
                break;
        }
        holder.tvReservaEstado.setTextColor(cor);

        // Define o listener de clique no item
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(reserva);
            }
        });
    }

    // Retorna o número total de reservas
    @Override
    public int getItemCount() {
        return reservas.size();
    }

    // Atualiza a lista de reservas e notifica o adaptador
    public void updateReservas(List<Reserva> newReservas) {
        this.reservas = newReservas != null ? newReservas : new ArrayList<>();
        notifyDataSetChanged();
    }

    // Formata a data da reserva
    private String formatarData(String data) {
        return data;
    }
}
