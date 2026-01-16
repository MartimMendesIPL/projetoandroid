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

// Adaptador para apresentar uma lista de tipos de bilhetes numa RecyclerView
public class TipoBilheteAdapter extends RecyclerView.Adapter<TipoBilheteAdapter.TipoBilheteViewHolder> {

    private final Context context;                              // Contexto da aplicação
    private ArrayList<TipoBilhete> tiposBilhete;               // Lista de tipos de bilhete
    private OnQuantityChangeListener listener;                 // Listener para mudanças de quantidade

    // Interface para tratar mudanças na quantidade de bilhetes
    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }

    // Construtor que inicializa o adaptador com contexto e lista de tipos de bilhete
    public TipoBilheteAdapter(Context context, ArrayList<TipoBilhete> tiposBilhete) {
        this.context = context;
        this.tiposBilhete = tiposBilhete != null ? tiposBilhete : new ArrayList<>();
    }

    // Define o listener para mudanças de quantidade
    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.listener = listener;
    }

    // Cria um novo ViewHolder quando necessário usando ViewBinding
    @NonNull
    @Override
    public TipoBilheteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTicketTypeBinding binding = ItemTicketTypeBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new TipoBilheteViewHolder(binding);
    }

    // Associa os dados do tipo de bilhete aos elementos visuais do ViewHolder
    @Override
    public void onBindViewHolder(@NonNull TipoBilheteViewHolder holder, int position) {
        holder.bind(tiposBilhete.get(position), listener);
    }

    // Retorna o número total de tipos de bilhete
    @Override
    public int getItemCount() {
        return tiposBilhete.size();
    }

    // Atualiza a lista de tipos de bilhete e notifica o adaptador
    public void updateTiposBilhete(ArrayList<TipoBilhete> novosTiposBilhete) {
        this.tiposBilhete = novosTiposBilhete != null ? novosTiposBilhete : new ArrayList<>();
        notifyDataSetChanged();
    }

    // Método auxiliar robusto para converter String de preço em double
    // Remove símbolos de moeda, espaços e trata vírgulas
    private double parsePrecoUnitario(String precoStr) {
        if (precoStr == null || precoStr.isEmpty()) return 0.0;
        try {
            // Remove tudo o que não for dígito, ponto ou vírgula
            String limpo = precoStr.replaceAll("[^0-9.,]", "");

            // Substitui vírgula por ponto para o Double.parse
            limpo = limpo.replace(",", ".");

            // Verifica se sobrou algo
            if (limpo.isEmpty()) return 0.0;

            return Double.parseDouble(limpo);
        } catch (Exception e) {
            Log.e("TipoBilheteAdapter", "Falha ao converter preço: " + precoStr);
            return 0.0;
        }
    }

    // Calcula o valor total de todas as compras (preço × quantidade)
    public double calcularTotal() {
        double total = 0;
        for (TipoBilhete tipo : tiposBilhete) {
            double preco = parsePrecoUnitario(tipo.getPreco());
            total += preco * tipo.getQuantidade();
        }
        return total;
    }

    // Retorna uma lista apenas com os bilhetes com quantidade > 0
    public ArrayList<TipoBilhete> getTiposBilheteSelecionados() {
        ArrayList<TipoBilhete> selecionados = new ArrayList<>();
        for (TipoBilhete tipo : tiposBilhete) {
            if (tipo.getQuantidade() > 0) {
                selecionados.add(tipo);
            }
        }
        return selecionados;
    }

    // Limpa todas as seleções definindo quantidade a 0
    public void limparSelecoes() {
        for (TipoBilhete tipo : tiposBilhete) {
            tipo.setQuantidade(0);
        }
        notifyDataSetChanged();
        if (listener != null) {
            listener.onQuantityChanged();
        }
    }

    // ViewHolder que representa cada item na lista de tipos de bilhete
    class TipoBilheteViewHolder extends RecyclerView.ViewHolder {
        private final ItemTicketTypeBinding binding;  // ViewBinding para aceder aos elementos

        // Inicializa o ViewHolder com ViewBinding
        public TipoBilheteViewHolder(@NonNull ItemTicketTypeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Vincula os dados do tipo de bilhete aos elementos visuais
        public void bind(TipoBilhete tipoBilhete, OnQuantityChangeListener listener) {
            binding.tvTicketName.setText(tipoBilhete.getNome());
            binding.tvTicketDescription.setText(tipoBilhete.getDescricao());

            // Converte o preço com segurança
            double preco = parsePrecoUnitario(tipoBilhete.getPreco());

            if (preco > 0) {
                binding.tvTicketPrice.setText(String.format(Locale.getDefault(), "%.2f €", preco));
            } else {
                // Mostra o valor original para debug caso haja erro na conversão
                binding.tvTicketPrice.setText(tipoBilhete.getPreco());
            }

            // Define a quantidade inicial
            binding.tvQuantity.setText(String.valueOf(tipoBilhete.getQuantidade()));

            // Listener para diminuir a quantidade
            binding.btnDecrease.setOnClickListener(v -> {
                if (tipoBilhete.getQuantidade() > 0) {
                    tipoBilhete.setQuantidade(tipoBilhete.getQuantidade() - 1);
                    binding.tvQuantity.setText(String.valueOf(tipoBilhete.getQuantidade()));

                    if (listener != null) listener.onQuantityChanged();
                }
            });

            // Listener para aumentar a quantidade
            binding.btnIncrease.setOnClickListener(v -> {
                tipoBilhete.setQuantidade(tipoBilhete.getQuantidade() + 1);
                binding.tvQuantity.setText(String.valueOf(tipoBilhete.getQuantidade()));

                if (listener != null) listener.onQuantityChanged();
            });
        }
    }
}
