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

    private static final String NOTICIA_ID = "noticia_id";
    private FragmentDetalhesNoticiaBinding binding;
    private Noticia item;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetalhesNoticiaBinding.inflate(inflater, container, false);

        // Listener para o ícone de perfil (ícone à direita)
        binding.tilPesquisa.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir activity de perfil
                Intent intent = new Intent(getActivity(), PerfilActivity.class);
                startActivity(intent);
            }
        });

        Bundle args = getArguments();
        if (args != null) {
            int noticiaId = args.getInt(NOTICIA_ID);
            SingletonLusitania.getInstance(requireContext()).setNoticiaListener(this);
            SingletonLusitania.getInstance(requireContext()).getNoticiaAPI(noticiaId, getContext());
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onNoticiasLoaded(ArrayList<Noticia> listaNoticias) {
        // Não é necessário implementar este metodo neste fragmento de detalhes
    }

    @Override
    public void onNoticiaLoaded(Noticia noticia) {
        if (noticia == null) {
            Toast.makeText(getContext(), "Notícia não encontrada", Toast.LENGTH_SHORT).show();
            return; // Sai do método sem tentar atualizar a UI
        }
        item = noticia;

        binding.tvTituloNoticia.setText(item.getTitulo());
        binding.tvDataPublicacao.setText(item.getDataPublicacao());
        binding.tvConteudoNoticia.setText(item.getConteudo());
        String urlImagem = item.getImagem();
        Toast.makeText(getContext(), urlImagem, Toast.LENGTH_SHORT).show();

        // Se a imagem não for nula ou vazia, carrega
        if (urlImagem != null && !urlImagem.isEmpty()) {
            Glide.with(getContext())
                    .load(urlImagem)
                    .placeholder(R.drawable.ic_launcher_background) // Imagem de placeholder enquanto carrega
                    .error(R.drawable.ic_launcher_background)       // Imagem em caso de erro
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.ivNoticiaHeader);
        } else {
            binding.ivNoticiaHeader.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public void onNoticiaError(String message) {
        // Corrigido o nome do metodo (era onErrorNoticias)
        Toast.makeText(getContext(), "Erro: " + message, Toast.LENGTH_SHORT).show();
    }


}