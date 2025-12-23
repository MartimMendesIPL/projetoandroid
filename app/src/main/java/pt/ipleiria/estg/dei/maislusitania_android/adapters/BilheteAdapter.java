package pt.ipleiria.estg.dei.maislusitania_android.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.estg.dei.maislusitania_android.R;
import pt.ipleiria.estg.dei.maislusitania_android.models.Bilhete;

public class BilheteAdapter extends RecyclerView.Adapter<BilheteAdapter.BilheteViewHolder> {

    private List<Bilhete> bilhetes;
    private OnItemClickListener onItemClickListener;

    // Interface para clicks
    public interface OnItemClickListener {
        void onItemClick(Bilhete bilhete);
    }

    public BilheteAdapter(List<Bilhete> bilhetes, OnItemClickListener listener) {
        this.bilhetes = bilhetes != null ? bilhetes : new ArrayList<>();
        this.onItemClickListener = listener;
    }

    // ViewHolder: mantém as referências das views
    public static class BilheteViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBilheteImage;
        TextView tvBilheteLocal;
        TextView tvBilheteData;
        TextView tvBilheteTipo;
        TextView tvBilheteEstado;

        public BilheteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBilheteImage = itemView.findViewById(R.id.ivBilheteImage);
            tvBilheteLocal = itemView.findViewById(R.id.tvBilheteLocal);
            tvBilheteData = itemView.findViewById(R.id.tvBilheteData);
            tvBilheteTipo = itemView.findViewById(R.id.tvBilheteTipo);
            tvBilheteEstado = itemView.findViewById(R.id.tvBilheteEstado);
        }
    }

    @NonNull
    @Override
    public BilheteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_bilhete, parent, false);
        return new BilheteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BilheteViewHolder holder, int position) {
        Bilhete bilhete = bilhetes.get(position);

        // Preenche os dados
        holder.tvBilheteLocal.setText(bilhete.getLocal().getNome());
        holder.tvBilheteData.setText(formatarData(bilhete.getDataVisita()));
        holder.tvBilheteTipo.setText("Tipo: " + bilhete.getTipoBilhete());
        holder.tvBilheteEstado.setText("Estado: " + bilhete.getEstado());

        // Cor do estado
        int cor;
        switch (bilhete.getEstado()) {
            case "Confirmada":
                cor = Color.parseColor("#28A745");
                break;
            case "Pendente":
                cor = Color.parseColor("#FFC107");
                break;
            default:
                cor = Color.parseColor("#DC3545");
                break;
        }
        holder.tvBilheteEstado.setTextColor(cor);

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(bilhete);
            }
        });

        // Carregar imagem (com Glide ou Picasso)
        // Glide.with(holder.itemView.getContext()).load(imagemUrl).into(holder.ivBilheteImage);
    }

    @Override
    public int getItemCount() {
        return bilhetes.size();
    }

    // Atualizar lista
    public void updateBilhetes(List<Bilhete> newBilhetes) {
        this.bilhetes = newBilhetes != null ? newBilhetes : new ArrayList<>();
        notifyDataSetChanged();
    }

    // Formatar data
    private String formatarData(String data) {
        // Implementa a formatação "25 de novembro, 2025"
        return data; // Por agora retorna a data original
    }
}
