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

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Evento> eventos;
    private OnEventoListener onEventoListener;

    // Interface para lidar com cliques nos itens
    public interface OnEventoListener {
        void onEventoClick(int position);
    }

    public EventoAdapter(Context context, ArrayList<Evento> eventos, EventoAdapter.OnEventoListener onEventoListener) {
        this.context = context;
        this.eventos = eventos;
        this.onEventoListener = onEventoListener;
    }

    @NonNull
    @Override
    public EventoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_evento, parent, false);
        return new EventoAdapter.ViewHolder(view, onEventoListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EventoAdapter.ViewHolder holder, int position) {
        Evento evento = eventos.get(position);

        // Definir textos
        holder.tvTitulo.setText(evento.getTitulo());
        holder.tvResumo.setText(evento.getDescricao());

        // Formatar ou exibir a data como vem da API
        holder.tvDataInicio.setText(evento.getDataInicio());
        holder.tvDataFim.setText(evento.getDataFim());

        // Carregar imagem com Glide
        String urlImagem = evento.getImagem();

        // Se a imagem não for nula ou vazia, carrega
        if (urlImagem != null && !urlImagem.isEmpty()) {
            Glide.with(context)
                    .load(urlImagem)
                    .placeholder(R.drawable.ic_launcher_background) // Imagem de placeholder enquanto carrega
                    .error(R.drawable.ic_launcher_background)       // Imagem em caso de erro
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivImagem);
        } else {
            holder.ivImagem.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public int getItemCount() {
        return eventos != null ? eventos.size() : 0;
    }

    public void updateEventos(ArrayList<Evento> novosEventos) {
        this.eventos = novosEventos;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivImagem;
        TextView tvTitulo, tvResumo, tvDataInicio, tvDataFim;
        EventoAdapter.OnEventoListener onEventoListener;

        public ViewHolder(@NonNull View itemView, EventoAdapter.OnEventoListener onEventoListener) {
            super(itemView);

            // Vincular os IDs do layout
            ivImagem = itemView.findViewById(R.id.ivItemImage);
            tvTitulo = itemView.findViewById(R.id.tvItemTitle);
            tvResumo = itemView.findViewById(R.id.tvItemSummary);
            tvDataInicio = itemView.findViewById(R.id.tvItemDateInicio);
            tvDataFim = itemView.findViewById(R.id.tvItemDateFim);

            this.onEventoListener = onEventoListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onEventoListener != null) {
                onEventoListener.onEventoClick(getAdapterPosition());
            }
        }
    }
}