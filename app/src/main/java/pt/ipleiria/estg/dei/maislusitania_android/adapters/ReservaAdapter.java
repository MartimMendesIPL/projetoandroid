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

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder> {

    private List<Reserva> reservas;
    private OnItemClickListener onItemClickListener;
    private Context context;

    // Interface para clicks na lista
    public interface OnItemClickListener {
        void onItemClick(Reserva reserva);
    }

    public ReservaAdapter(Context context, List<Reserva> reservas, OnItemClickListener listener) {
        this.context = context;
        this.reservas = reservas != null ? reservas : new ArrayList<>();
        this.onItemClickListener = listener;
    }

    // ViewHolder: mantém as referências das views do layout item_list_reserva
    public static class ReservaViewHolder extends RecyclerView.ViewHolder {
        ImageView ivReservaImagem;
        TextView tvReservaLocal;
        TextView tvReservaData;
        TextView tvReservaPreco; // Mudado de Tipo para Preço
        TextView tvReservaEstado;


        public ReservaViewHolder(@NonNull View itemView) {
            super(itemView);
            // Certifique-se que estes IDs existem no seu item_list_reserva.xml
            ivReservaImagem = itemView.findViewById(R.id.ivReservaImagem);
            tvReservaLocal = itemView.findViewById(R.id.tvReservaLocal);
            tvReservaData = itemView.findViewById(R.id.tvReservaData);
            tvReservaPreco = itemView.findViewById(R.id.tvReservaPreco);
            tvReservaEstado = itemView.findViewById(R.id.tvReservaEstado);
        }
    }

    @NonNull
    @Override
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout de RESERVA
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_reserva, parent, false);
        return new ReservaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {
        Reserva reserva = reservas.get(position);

        // Carregar imagem com Glide
        Glide.with(context)
                .load(reserva.getImagemLocal())
                .into(holder.ivReservaImagem);


        // 1. Nome do Local (Vem direto do JSON/Model agora)
        holder.tvReservaLocal.setText(reserva.getLocalNome());

        // 2. Data
        holder.tvReservaData.setText(formatarData(reserva.getDataVisita()));

        // 3. Preço Total (Formatado para Euro)
        // String.format("%.2f€") garante 2 casas decimais (ex: 12.50€)
        holder.tvReservaPreco.setText(String.format(Locale.getDefault(), "Total: %.2f€", reserva.getPrecoTotal()));

        // 4. Estado e Cor
        holder.tvReservaEstado.setText(reserva.getEstado());

        int cor;
        String estado = reserva.getEstado() != null ? reserva.getEstado() : "";

        switch (estado) {
            case "Confirmada":
                cor = Color.parseColor("#28A745"); // Verde
                break;
            case "Pendente":
            case "Expirado": // Assumindo lógica do PHP onde Expirado é tratado visualmente similar
                cor = Color.parseColor("#FFC107"); // Amarelo/Laranja
                break;
            case "Cancelada":
                cor = Color.parseColor("#DC3545"); // Vermelho
                break;
            default:
                cor = Color.GRAY;
                break;
        }
        holder.tvReservaEstado.setTextColor(cor);

        // 5. Imagem (Opcional - Usando Glide)
        // Exemplo: Carregar imagem baseada no ID do local se tiveres URL
        // String imageUrl = "http://teu-ip/api/locais/" + reserva.getLocalId() + "/image";
        /*
        Glide.with(context)
             .load(R.drawable.placeholder_image) // Imagem padrão
             .into(holder.ivReservaImagem);
        */

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(reserva);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservas.size();
    }

    // Atualizar lista
    public void updateReservas(List<Reserva> newReservas) {
        this.reservas = newReservas != null ? newReservas : new ArrayList<>();
        notifyDataSetChanged();
    }

    // Formatar data (pode melhorar usando SimpleDateFormat se necessário)
    private String formatarData(String data) {
        return data;
    }
}