package pt.ipleiria.estg.dei.maislusitania_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentDetalhesNoticiaBinding;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.NoticiaListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.Noticia;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;


public class DetalhesNoticiaFragment extends Fragment implements NoticiaListener {
    // Constante para a chave do argumento
    private static final String NOTICIA_ID = "noticia_id";
    private FragmentDetalhesNoticiaBinding binding;
    private Noticia item;

    @Nullable
    @Override
    // Inflar o layout do fragmento
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        // Inflar o layout usando View Binding
        binding = FragmentDetalhesNoticiaBinding.inflate(inflater, container, false);
        Bundle args = getArguments();
        // Verificar se há argumentos
        if (args != null)
        {
            // Obter o ID da notícia dos argumentos
            int noticiaId = args.getInt(NOTICIA_ID);
            // Configurar o listener e solicitar os detalhes da notícia
            SingletonLusitania.getInstance(requireContext()).setNoticiaListener(this);
            SingletonLusitania.getInstance(requireContext()).getNoticiaAPI(noticiaId, getContext());
        }
        return binding.getRoot();
    }
    @Override
    // Limpar o binding quando a view for destruída
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onNoticiasLoaded(ArrayList<Noticia> listaNoticias) {
        // Não é necessário implementar este metodo neste fragmento de detalhes
    }
    @Override
    // Metodo chamado quando os detalhes da notícia são carregados
    public void onNoticiaLoaded(Noticia noticia)
    {
        // Verificar se a notícia é nula
        if (noticia == null)
        {
            Toast.makeText(getContext(), "Notícia não encontrada", Toast.LENGTH_SHORT).show();
            return; // Sai do metodo sem tentar atualizar a UI
        }
        // Atualizar a UI com os detalhes da notícia
        item = noticia;
        binding.tvDNome.setText(item.getNome());
        binding.tvDDataPublicacao.setText(item.getDataPublicacao());
        binding.tvDConteudo.setText(item.getConteudo());
        String urlImagem = item.getImagem();
        // Se a imagem não for nula ou vazia, carrega
        if (urlImagem != null && !urlImagem.isEmpty())
        {
            Glide.with(getContext())
                    .load(urlImagem)
                    .placeholder(R.drawable.ic_launcher_background) // Imagem de placeholder enquanto carrega
                    .error(R.drawable.ic_launcher_background)       // Imagem em caso de erro
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.ivDImagem);
        }
        else
        {
            // Definir imagem padrão se a URL for nula ou vazia
            binding.ivDImagem.setImageResource(R.drawable.ic_launcher_background);
        }
    }
    @Override
    // Metodo chamado em caso de erro ao carregar a notícia
    public void onNoticiaError(String message) {
        Toast.makeText(getContext(), "Erro: " + message, Toast.LENGTH_SHORT).show();
    }
}