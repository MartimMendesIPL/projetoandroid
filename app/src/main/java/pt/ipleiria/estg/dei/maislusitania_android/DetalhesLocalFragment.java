package pt.ipleiria.estg.dei.maislusitania_android;

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

import pt.ipleiria.estg.dei.maislusitania_android.adapters.AvaliacaoAdapter;
import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentDetalhesLocalBinding;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.LocaisListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.Avaliacao;
import pt.ipleiria.estg.dei.maislusitania_android.models.Local;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;

public class DetalhesLocalFragment extends Fragment implements LocaisListener {

    private static final String ARG_LOCAL_ID = "localId";
    private FragmentDetalhesLocalBinding binding;
    private AvaliacaoAdapter avaliacaoAdapter;
    private int localId = -1;
    private Avaliacao avaliacaoUser;

    public static DetalhesLocalFragment newInstance(int localId) {
        DetalhesLocalFragment fragment = new DetalhesLocalFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LOCAL_ID, localId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            localId = getArguments().getInt(ARG_LOCAL_ID, -1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetalhesLocalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupClickListeners();

        if (localId != -1) {
            SingletonLusitania.getInstance(requireContext()).setLocaisListener(this);
            SingletonLusitania.getInstance(requireContext()).getLocalAPI(localId, requireContext());
        } else {
            Toast.makeText(getContext(), "Erro: ID do local não encontrado.", Toast.LENGTH_LONG).show();
            requireActivity().onBackPressed();
        }
    }

    private void setupRecyclerView() {
        avaliacaoAdapter = new AvaliacaoAdapter(getContext(), new ArrayList<>());
        binding.rvAvaliacoes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAvaliacoes.setAdapter(avaliacaoAdapter);
    }

    private void setupClickListeners() {
        binding.btnComprar.setOnClickListener(v -> Toast.makeText(getContext(), "Funcionalidade de compra em breve", Toast.LENGTH_SHORT).show());
        binding.btnSubmitAvaliacao.setOnClickListener(v -> submitAvaliacao());
        binding.btnApagarAvaliacao.setOnClickListener(v -> deleteAvaliacao());
    }

    private void submitAvaliacao() {
        String comentario = binding.etComentario.getText().toString().trim();
        float classificacao = binding.rbAddAvaliacao.getRating();

        if (comentario.isEmpty() && classificacao == 0) {
            Toast.makeText(getContext(), "É necessário um comentário ou uma classificação.", Toast.LENGTH_SHORT).show();
            return;
        }

        SingletonLusitania singleton = SingletonLusitania.getInstance(requireContext());
        if (avaliacaoUser != null) {
            // Edit existing evaluation
            singleton.editAvaliacao(requireContext(), localId, avaliacaoUser.getId(), classificacao, comentario);
            Toast.makeText(getContext(), "A editar a sua avaliação...", Toast.LENGTH_SHORT).show();
        } else {
            // Add new evaluation
            singleton.addAvaliacao(requireContext(), localId, classificacao, comentario);
            Toast.makeText(getContext(), "A submeter a sua avaliação...", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteAvaliacao() {
        if (avaliacaoUser != null) {
            SingletonLusitania.getInstance(requireContext()).deleteAvaliacao(requireContext(), localId, avaliacaoUser.getId());
            Toast.makeText(getContext(), "A apagar a sua avaliação...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Nenhuma avaliação para apagar.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocalLoaded(Local local) {
        if (!isAdded() || binding == null) return;

        updateLocalInfo(local);
        updateAvaliacoes(local);
        updateUserAvaliacao(local);
    }

    private void updateLocalInfo(Local local) {
        binding.tvNome.setText(local.getNome());
        binding.tvLocalizacao.setText(local.getMorada() + ", " + local.getDistrito());
        binding.ratingBar.setRating(local.getAvaliacaoMedia());
        binding.tvDescricao.setText(local.getDescricao());
        binding.tvInfoMorada.setText(local.getMorada());
        binding.tvTelefone.setText(local.getTelefone() != null && !local.getTelefone().isEmpty() ? local.getTelefone() : "N/A");
        binding.tvEmail.setText(local.getEmail() != null && !local.getEmail().isEmpty() ? local.getEmail() : "N/A");

        Glide.with(requireContext())
                .load(local.getImagem())
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.ivImagem);

        updateHorario(local.getHorario());
    }

    private void updateHorario(Map<String, String> horarioMap) {
        if (horarioMap != null && !horarioMap.isEmpty()) {
            StringBuilder horarioTexto = new StringBuilder();
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

    private void updateAvaliacoes(Local local) {
        if (local.getAvaliacoes() != null) {
            avaliacaoAdapter.updateAvaliacoes(local.getAvaliacoes());
        }
    }

    private void updateUserAvaliacao(Local local) {
        int userId = SingletonLusitania.getInstance(requireContext()).getUserId(requireContext());
        this.avaliacaoUser = null;

        if (local.getAvaliacoes() != null && userId != -1) {
            for (Avaliacao avaliacao : local.getAvaliacoes()) {
                if (avaliacao.getUtilizadorId() == userId) {
                    this.avaliacaoUser = avaliacao;
                    break;
                }
            }
        }

        preencherFormularioAvaliacao();
    }

    private void preencherFormularioAvaliacao() {
        if (avaliacaoUser != null) {
            binding.etComentario.setText(avaliacaoUser.getComentario());
            binding.rbAddAvaliacao.setRating(avaliacaoUser.getClassificacao());
            binding.btnApagarAvaliacao.setVisibility(View.VISIBLE);
            binding.btnSubmitAvaliacao.setText("Editar");
        } else {
            binding.etComentario.setText("");
            binding.rbAddAvaliacao.setRating(0);
            binding.btnApagarAvaliacao.setVisibility(View.INVISIBLE);
            binding.btnSubmitAvaliacao.setText("Submeter");
        }
    }

    @Override
    public void onLocalError(String message) {
        if (isAdded()) {
            Toast.makeText(getContext(), "Erro ao carregar detalhes: " + message, Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        }
    }

    @Override
    public void onLocaisLoaded(ArrayList<Local> listaLocais) {
        // Not used in this fragment
    }

    @Override
    public void onLocaisError(String message) {
        // Not used in this fragment
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SingletonLusitania.getInstance(requireContext()).setLocaisListener(null);
        binding = null;
    }
}
