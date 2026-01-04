package pt.ipleiria.estg.dei.maislusitania_android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;

import java.util.Map;

import pt.ipleiria.estg.dei.maislusitania_android.R;
import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentDetalhesLocalBinding;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.LocaisListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.Local;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;

public class DetalhesLocalFragment extends Fragment implements LocaisListener {

    private static final String ARG_LOCAL_ID = "local_id";
    private static final String ARG_RATING = "local_rating"; // Novo argumento

    private int localId;
    private float initialRating; // Guarda o rating vindo da lista
    private Local local;

    private FragmentDetalhesLocalBinding binding;

    public DetalhesLocalFragment() {
        // Required empty public constructor
    }

    // Recebe ID e Rating
    public static DetalhesLocalFragment newInstance(int localId, float rating) {
        DetalhesLocalFragment fragment = new DetalhesLocalFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LOCAL_ID, localId);
        args.putFloat(ARG_RATING, rating);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            localId = getArguments().getInt(ARG_LOCAL_ID);
            initialRating = getArguments().getFloat(ARG_RATING, 0.0f);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetalhesLocalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rvBilhetes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAvaliacoes.setLayoutManager(new LinearLayoutManager(getContext()));

        // 1. Define o rating IMEDIATAMENTE com o valor seguro da lista
        binding.ratingBar.setRating(initialRating);

        SingletonLusitania.getInstance(getContext()).setLocaisListener(this);
        SingletonLusitania.getInstance(getContext()).getLocalAPI(localId, getContext());

        binding.btnComprar.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Funcionalidade de compra em breve", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onLocalLoaded(Local local) {
        this.local = local;
        if (getContext() == null || binding == null) return;

        binding.tvNome.setText(local.getNome());

        String localizacao = local.getMorada();
        if (local.getDistrito() != null && !local.getDistrito().isEmpty()) {
            localizacao += ", " + local.getDistrito();
        }
        binding.tvLocalizacao.setText(localizacao);

        // 2. Só atualiza o rating se a API trouxer um valor válido (> 0).
        // Se a API trouxer 0 (bug), mantemos o valor correto que veio da lista.
        if (local.getAvaliacaoMedia() > 0) {
            binding.ratingBar.setRating(local.getAvaliacaoMedia());
        }

        Glide.with(getContext())
                .load(local.getImagem())
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.ivImagem);

        binding.tvDescricao.setText(local.getDescricao());
        binding.tvInfoMorada.setText(local.getMorada());

        binding.tvTelefone.setText((local.getTelefone() != null && !local.getTelefone().isEmpty()) ? local.getTelefone() : "N/A");
        binding.tvEmail.setText((local.getEmail() != null && !local.getEmail().isEmpty()) ? local.getEmail() : "N/A");

        if (local.getHorario() != null && !local.getHorario().isEmpty()) {
            StringBuilder horarioTexto = new StringBuilder();
            Map<String, String> horarioMap = local.getHorario();
            String[] diasSemana = {"segunda", "terca", "quarta", "quinta", "sexta", "sabado", "domingo"};

            for (String dia : diasSemana) {
                if (horarioMap.containsKey(dia)) {
                    String diaFormatado = dia.substring(0, 1).toUpperCase() + dia.substring(1);
                    horarioTexto.append(diaFormatado).append(": ").append(horarioMap.get(dia)).append("\n");
                }
            }
            binding.tvHorario.setText(horarioTexto.toString().trim());
        } else {
            binding.tvHorario.setText("Horário não disponível");
        }
    }

    @Override
    public void onLocalError(String message) {
        if (getContext() != null)
            Toast.makeText(getContext(), "Erro ao carregar detalhes: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocaisLoaded(java.util.ArrayList<Local> listaLocais) {}

    @Override
    public void onLocaisError(String message) {}
}
