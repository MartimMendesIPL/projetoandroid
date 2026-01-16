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

/**
 * Fragment que mostra os detalhes de um evento específico
 */
public class DetalhesEventoFragment extends Fragment implements EventoListener {

    // Chave para passar o ID do evento através de argumentos
    private static final String EVENTO_ID = "evento_id";

    // Binding para acesso aos elementos da UI
    private FragmentDetalhesEventoBinding binding;

    // Objeto evento atual
    private Evento item;

    /**
     * Cria a view do fragment e carrega os dados do evento
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inicializa o binding da view
        binding = FragmentDetalhesEventoBinding.inflate(inflater, container, false);

        // Obtém os argumentos passados ao fragment
        Bundle args = getArguments();
        if (args != null) {
            // Extrai o ID do evento
            int eventoId = args.getInt(EVENTO_ID);

            // Registra este fragment como listener e carrega o evento da API
            SingletonLusitania.getInstance(requireContext()).setEventoListener(this);
            SingletonLusitania.getInstance(requireContext()).getEventoAPI(eventoId, getContext());
        }

        return binding.getRoot();
    }

    /**
     * Limpa as referências quando a view é destruída
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Callback quando múltiplos eventos são carregados (não utilizado aqui)
     */
    @Override
    public void onEventosLoaded(ArrayList<Evento> listaEventos) {
        // Não é necessário implementar este método neste fragment de detalhes
    }

    /**
     * Callback quando um evento é carregado com sucesso
     */
    @Override
    public void onEventoLoaded(Evento evento) {
        // Verifica se o evento é nulo
        if (evento == null) {
            Toast.makeText(getContext(), "Notícia não encontrada", Toast.LENGTH_SHORT).show();
            return; // Sai sem atualizar a UI
        }

        // Armazena o evento
        item = evento;

        // Preenche os campos de texto com os dados do evento
        binding.tvDNome.setText(item.getTitulo());
        binding.tvDDataInicio.setText(item.getDataInicio());
        binding.tvDDataFim.setText(item.getDataFim());
        binding.tvDConteudo.setText(item.getDescricao());

        // Obtém a URL da imagem
        String urlImagem = item.getImagem();

        // Carrega a imagem se existir, caso contrário mostra placeholder
        if (urlImagem != null && !urlImagem.isEmpty()) {
            Glide.with(getContext())
                    .load(urlImagem)
                    .placeholder(R.drawable.ic_launcher_background) // Imagem enquanto carrega
                    .error(R.drawable.ic_launcher_background)       // Imagem em caso de erro
                    .diskCacheStrategy(DiskCacheStrategy.ALL)       // Cache em disco
                    .into(binding.ivDImagem);
        } else {
            // Define imagem de placeholder se a URL não existir
            binding.ivDImagem.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    /**
     * Callback quando ocorre um erro ao carregar o evento
     */
    @Override
    public void onEventoError(String message) {
        // Mostra mensagem de erro ao utilizador
        Toast.makeText(getContext(), "Erro: " + message, Toast.LENGTH_SHORT).show();
    }
}
