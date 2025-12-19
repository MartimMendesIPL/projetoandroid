package pt.ipleiria.estg.dei.maislusitania_android;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentDetalhesEventoBinding;
import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentDetalhesNoticiaBinding;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.EventoListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.Evento;
import pt.ipleiria.estg.dei.maislusitania_android.models.Noticia;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;

public class DetalhesEventoFragment extends Fragment implements EventoListener {

   private static final String EVENTO_ID = "evento_id";
    private FragmentDetalhesEventoBinding binding;
    private Evento item;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetalhesEventoBinding.inflate(inflater, container, false);

        Bundle args = getArguments();
        if (args != null) {
            int eventoId = args.getInt(EVENTO_ID);
            SingletonLusitania.getInstance(requireContext()).setEventoListener(this);
            SingletonLusitania.getInstance(requireContext()).getEventoAPI(eventoId, getContext());
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onEventosLoaded(ArrayList<Evento> listaEventos) {
        // Não é necessário implementar este metodo neste fragmento de detalhes
    }

    @Override
    public void onEventoLoaded(Evento evento) {
        if (evento == null) {
            Toast.makeText(getContext(), "Notícia não encontrada", Toast.LENGTH_SHORT).show();
            return; // Sai do método sem tentar atualizar a UI
        }
        item = evento;

        binding.tvDNome.setText(item.getTitulo());
        binding.tvDDataInicio.setText(item.getDataInicio());
        binding.tvDDataFim.setText(item.getDataFim());
        binding.tvDConteudo.setText(item.getDescricao());
        String urlImagem = item.getImagem();

        // Se a imagem não for nula ou vazia, carrega
        if (urlImagem != null && !urlImagem.isEmpty()) {
            Glide.with(getContext())
                    .load(urlImagem)
                    .placeholder(R.drawable.ic_launcher_background) // Imagem de placeholder enquanto carrega
                    .error(R.drawable.ic_launcher_background)       // Imagem em caso de erro
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.ivDImagem);
        } else {
            binding.ivDImagem.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public void onEventoError(String message) {
        // Corrigido o nome do metodo (era onErrorNoticias)
        Toast.makeText(getContext(), "Erro: " + message, Toast.LENGTH_SHORT).show();
    }
}