package pt.ipleiria.estg.dei.maislusitania_android.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import pt.ipleiria.estg.dei.maislusitania_android.databinding.ItemTicketTypeBinding;
import pt.ipleiria.estg.dei.maislusitania_android.models.TipoBilhete;

public class TipoBilheteAdapter extends RecyclerView.Adapter<TipoBilheteAdapter.TipoBilheteViewHolder> {

    private final Context context;
    private ArrayList<TipoBilhete> tiposBilhete;
    private OnQuantityChangeListener listener;

    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }

    public TipoBilheteAdapter(Context context, ArrayList<TipoBilhete> tiposBilhete) {
        this.context = context;
        this.tiposBilhete = tiposBilhete != null ? tiposBilhete : new ArrayList<>();
    }

    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TipoBilheteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTicketTypeBinding binding = ItemTicketTypeBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new TipoBilheteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TipoBilheteViewHolder holder, int position) {
        holder.bind(tiposBilhete.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return tiposBilhete.size();
    }

    public void updateTiposBilhete(ArrayList<TipoBilhete> novosTiposBilhete) {
        this.tiposBilhete = novosTiposBilhete != null ? novosTiposBilhete : new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * Método auxiliar robusto para converter String de preço em double.
     * Remove símbolos de moeda, espaços e trata vírgulas.
     */
    private double parsePrecoUnitario(String precoStr) {
        if (precoStr == null || precoStr.isEmpty()) return 0.0;
        try {
            // 1. Remove tudo o que não for dígito, ponto ou vírgula
            String limpo = precoStr.replaceAll("[^0-9.,]", "");

            // 2. Substitui vírgula por ponto (para o Double.parse)
            limpo = limpo.replace(",", ".");

            // 3. Verifica se sobrou algo
            if (limpo.isEmpty()) return 0.0;

            return Double.parseDouble(limpo);
        } catch (Exception e) {
            Log.e("TipoBilheteAdapter", "Falha ao converter preço: " + precoStr);
            return 0.0;
        }
    }

    public double calcularTotal() {
        double total = 0;
        for (TipoBilhete tipo : tiposBilhete) {
            double preco = parsePrecoUnitario(tipo.getPreco());
            total += preco * tipo.getQuantidade();
        }
        return total;
    }

    public ArrayList<TipoBilhete> getTiposBilheteSelecionados() {
        ArrayList<TipoBilhete> selecionados = new ArrayList<>();
        for (TipoBilhete tipo : tiposBilhete) {
            if (tipo.getQuantidade() > 0) {
                selecionados.add(tipo);
            }
        }
        return selecionados;
    }

    public void limparSelecoes() {
        for (TipoBilhete tipo : tiposBilhete) {
            tipo.setQuantidade(0);
        }
        notifyDataSetChanged();
        if (listener != null) {
            listener.onQuantityChanged();
        }
    }

    // ViewHolder não estático para aceder ao método parsePrecoUnitario facilmente,
    // ou mantemos a lógica de parse dentro do adapter.
    class TipoBilheteViewHolder extends RecyclerView.ViewHolder {
        private final ItemTicketTypeBinding binding;

        public TipoBilheteViewHolder(@NonNull ItemTicketTypeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(TipoBilhete tipoBilhete, OnQuantityChangeListener listener) {
            binding.tvTicketName.setText(tipoBilhete.getNome());
            binding.tvTicketDescription.setText(tipoBilhete.getDescricao());

            // USAR O MÉTODO DE PARSE SEGURO
            double preco = parsePrecoUnitario(tipoBilhete.getPreco());

            if (preco > 0) {
                binding.tvTicketPrice.setText(String.format(Locale.getDefault(), "%.2f €", preco));
            } else {
                // Se der zero, mostra o valor original para DEBUG (assim vês o que está a vir da API)
                binding.tvTicketPrice.setText(tipoBilhete.getPreco());
            }

            binding.tvQuantity.setText(String.valueOf(tipoBilhete.getQuantidade()));

            binding.btnDecrease.setOnClickListener(v -> {
                if (tipoBilhete.getQuantidade() > 0) {
                    tipoBilhete.setQuantidade(tipoBilhete.getQuantidade() - 1);
                    binding.tvQuantity.setText(String.valueOf(tipoBilhete.getQuantidade()));

                    if (listener != null) listener.onQuantityChanged();
                }
            });

            binding.btnIncrease.setOnClickListener(v -> {
                tipoBilhete.setQuantidade(tipoBilhete.getQuantidade() + 1);
                binding.tvQuantity.setText(String.valueOf(tipoBilhete.getQuantidade()));

                if (listener != null) listener.onQuantityChanged();
            });
        }
    }
}
