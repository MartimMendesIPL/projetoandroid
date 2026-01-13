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

import java.util.ArrayList;
import java.util.Map;

import pt.ipleiria.estg.dei.maislusitania_android.R;
import pt.ipleiria.estg.dei.maislusitania_android.ReservaFragment;
import pt.ipleiria.estg.dei.maislusitania_android.adapters.AvaliacaoAdapter;
import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentDetalhesLocalBinding;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.LocaisListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.Avaliacao;
import pt.ipleiria.estg.dei.maislusitania_android.models.Local;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;

public class DetalhesLocalFragment extends Fragment implements LocaisListener {

    private static final String ARG_LOCAL_ID = "local_id";
    private static final String ARG_RATING = "local_rating";

    private int localId;
    private float initialRating;
    private Local local;

    private AvaliacaoAdapter avaliacaoAdapter;

    private int avaliacaoId;
    private Avaliacao avaliacaoUser;
    private FragmentDetalhesLocalBinding binding;

    public DetalhesLocalFragment() {
    }

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

        // Setup RecyclerViews and Adapters here
        setupRecyclerViews();

        binding.ratingBar.setRating(initialRating);

        // Set listener and fetch data
        SingletonLusitania.getInstance(getContext()).setLocaisListener(this);
        SingletonLusitania.getInstance(getContext()).getLocalAPI(localId, getContext());

        binding.btnComprar.setOnClickListener(v -> {
            // Criar instância do ReservaFragment com o ID do local
            ReservaFragment reservaFragment = ReservaFragment.newInstance(localId);

            // Fazer a transação do fragment
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, reservaFragment) // Ajusta se necessário
                    .addToBackStack(null)
                    .commit();
        });

        binding.btnVoltar.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        binding.btnSubmitAvaliacao.setOnClickListener(v -> {
            String comentario = binding.etComentario.getText().toString();
            float novaAvaliacao = binding.rbAddAvaliacao.getRating();

            if (comentario.isEmpty() && novaAvaliacao == 0) {
                Toast.makeText(getContext(), "Preencha ao menos um dos campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (avaliacaoUser != null) {
                // Lógica de Edição: O utilizador já tem uma avaliação
                SingletonLusitania.getInstance(getContext()).editAvaliacao(getContext(),localId, avaliacaoId, novaAvaliacao, comentario);
                Toast.makeText(getContext(), "A editar a sua avaliação...", Toast.LENGTH_SHORT).show();

            } else {
                // Lógica de Adição: O utilizador está a criar uma nova avaliação
                SingletonLusitania.getInstance(getContext()).addAvaliacao(getContext(), localId, novaAvaliacao, comentario);
                Toast.makeText(getContext(), "A submeter a sua avaliação...", Toast.LENGTH_SHORT).show();
            }
        });


        binding.btnApagarAvaliacao.setOnClickListener(v -> {
            if (avaliacaoId != -1) {
                SingletonLusitania.getInstance(getContext()).deleteAvaliacao(getContext(), localId, avaliacaoId);
                Toast.makeText(getContext(), "Apagado com sucesso!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Nenhuma avaliação para apagar.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerViews() {
        binding.rvAvaliacoes.setLayoutManager(new LinearLayoutManager(getContext()));

        avaliacaoAdapter = new AvaliacaoAdapter(getContext(), new ArrayList<>());
        binding.rvAvaliacoes.setAdapter(avaliacaoAdapter);
    }

    @Override
    public void onLocalLoaded(Local local) {
        this.local = local;
        if (getContext() == null || binding == null) return;

        int userId = SingletonLusitania.getInstance(getContext()).getUserId(getContext());

        this.avaliacaoUser = null;
        this.avaliacaoId = -1;

        //Procurar a avaliação do utilizador
        if(local.getAvaliacoes() != null && userId != -1) {
            for (Avaliacao avaliacao : local.getAvaliacoes()) {
                if (avaliacao.getUtilizadorId() == userId) {
                    this.avaliacaoUser = avaliacao;
                    this.avaliacaoId = avaliacao.getId();
                    break;
                }
            }
        }

        preencherAvaliacaoExistente();
        // Update all UI components with the loaded data
        binding.tvNome.setText(local.getNome());

        String localizacao = local.getMorada();
        if (local.getDistrito() != null && !local.getDistrito().isEmpty()) {
            localizacao += ", " + local.getDistrito();
        }
        binding.tvLocalizacao.setText(localizacao);

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

        // Update Avaliacoes RecyclerView
        if (local.getAvaliacoes() != null) {
            avaliacaoAdapter.updateAvaliacoes(local.getAvaliacoes());
        }
    }

    private void preencherAvaliacaoExistente() {
        if (avaliacaoUser != null) {
            binding.etComentario.setText(avaliacaoUser.getComentario());
            binding.rbAddAvaliacao.setRating(avaliacaoUser.getClassificacao());

            binding.btnApagarAvaliacao.setVisibility(View.VISIBLE);
            binding.btnSubmitAvaliacao.setText("Editar");
        }
        else{
            binding.btnApagarAvaliacao.setVisibility(View.INVISIBLE);
            binding.btnSubmitAvaliacao.setText("Submeter");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
