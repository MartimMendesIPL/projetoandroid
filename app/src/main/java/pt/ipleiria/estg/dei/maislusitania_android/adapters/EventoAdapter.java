package pt.ipleiria.estg.dei.maislusitania_android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.R;
import pt.ipleiria.estg.dei.maislusitania_android.models.Evento;

// Adaptador para apresentar uma lista de eventos numa RecyclerView
public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Evento> eventos;
    private OnEventoListener onEventoListener;

    // Interface para tratar cliques nos itens da lista
    public interface OnEventoListener {
        void onEventoClick(int position);
    }

    // Construtor que inicializa o adaptador com contexto, lista de eventos e listener de cliques
    public EventoAdapter(Context context, ArrayList<Evento> eventos, EventoAdapter.OnEventoListener onEventoListener) {
        this.context = context;
        this.eventos = eventos;
        this.onEventoListener = onEventoListener;
    }

    // Cria um novo ViewHolder quando necessário
    @NonNull
    @Override
    public EventoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_evento, parent, false);
        return new EventoAdapter.ViewHolder(view, onEventoListener);
    }

    // Associa os dados do evento aos elementos visuais do ViewHolder
    @Override
    public void onBindViewHolder(@NonNull EventoAdapter.ViewHolder holder, int position) {
        Evento evento = eventos.get(position);

        // Preenche os textos do evento
        holder.tvTitulo.setText(evento.getTitulo());
        holder.tvResumo.setText(evento.getDescricao());
        holder.tvDataInicio.setText(evento.getDataInicio());
        holder.tvDataFim.setText(evento.getDataFim());

        // Carrega a imagem com Glide
        String urlImagem = evento.getImagem();
        if (urlImagem != null && !urlImagem.isEmpty()) {
            Glide.with(context)
                    .load(urlImagem)
                    .placeholder(R.drawable.ic_launcher_background) // Imagem temporária enquanto carrega
                    .error(R.drawable.ic_launcher_background)       // Imagem em caso de erro
                    .diskCacheStrategy(DiskCacheStrategy.ALL)       // Guarda em cache
                    .into(holder.ivImagem);
        } else {
            holder.ivImagem.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    // Retorna o número total de eventos
    @Override
    public int getItemCount() {
        return eventos != null ? eventos.size() : 0;
    }

    // Atualiza a lista de eventos e notifica o adaptador
    public void updateEventos(ArrayList<Evento> novosEventos) {
        this.eventos = novosEventos;
        notifyDataSetChanged();
    }

    // ViewHolder que representa cada item na lista
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivImagem;
        TextView tvTitulo, tvResumo, tvDataInicio, tvDataFim;
        OnEventoListener onEventoListener;

        // Inicializa as referências aos elementos do layout
        public ViewHolder(@NonNull View itemView, OnEventoListener onEventoListener) {
            super(itemView);

            // Vincula os IDs do layout aos atributos
            ivImagem = itemView.findViewById(R.id.ivItemImage);
            tvTitulo = itemView.findViewById(R.id.tvItemTitle);
            tvResumo = itemView.findViewById(R.id.tvItemSummary);
            tvDataInicio = itemView.findViewById(R.id.tvItemDateInicio);
            tvDataFim = itemView.findViewById(R.id.tvItemDateFim);

            this.onEventoListener = onEventoListener;
            itemView.setOnClickListener(this);
        }

        // Trata o clique no item
        @Override
        public void onClick(View v) {
            if (onEventoListener != null) {
                onEventoListener.onEventoClick(getAdapterPosition());
            }
        }
    }
}
