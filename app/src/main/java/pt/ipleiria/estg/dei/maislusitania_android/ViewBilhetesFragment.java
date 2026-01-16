package pt.ipleiria.estg.dei.maislusitania_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.adapters.BilheteAdapter;
import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentViewBilhetesBinding;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.BilheteListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.Bilhete;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;

/**
 * Fragment que exibe a lista de bilhetes de uma reserva específica
 * Permite visualizar detalhes e códigos de acesso aos bilhetes
 */
public class ViewBilhetesFragment extends Fragment implements BilheteListener {

    // Binding para acesso aos elementos da UI
    private FragmentViewBilhetesBinding binding;

    // Adaptador para a RecyclerView de bilhetes
    private BilheteAdapter bilheteAdapter;

    // Lista de bilhetes carregados da API
    private ArrayList<Bilhete> listaBilhetes;

    // ID da reserva a qual pertencem os bilhetes
    private int idReserva = -1;

    /**
     * Construtor vazio obrigatório para fragmentos
     */
    public ViewBilhetesFragment() {
        // Required empty public constructor
    }

    /**
     * Cria a view do fragment e carrega os bilhetes da API
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentViewBilhetesBinding.inflate(inflater, container, false);
        listaBilhetes = new ArrayList<>();

        // Configura o botão voltar para retroceder na navegação
        binding.btnVoltar.setOnClickListener(v -> requireActivity().onBackPressed());

        // Configura a RecyclerView e o adaptador
        setupRecyclerView();

        // Obtém o ID da reserva dos argumentos do bundle
        if (getArguments() != null) {
            idReserva = getArguments().getInt("ID_RESERVA", -1);
        }

        // Registra este fragment como listener de eventos de bilhetes
        SingletonLusitania.getInstance(requireContext()).setBilheteListener(this);

        // Carrega todos os bilhetes da reserva especificada
        SingletonLusitania.getInstance(requireContext()).getAllBilhetesAPI(requireContext(), idReserva);

        return binding.getRoot();
    }

    /**
     * Configura a RecyclerView com o adaptador e listeners de clique
     */
    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewBilhetes;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Cria o adaptador com listener para cliques em bilhetes
        bilheteAdapter = new BilheteAdapter(listaBilhetes, new BilheteAdapter.OnItemClickListener() {
            /**
             * Chamado quando um bilhete é clicado na lista
             * Exibe o código do bilhete num toast
             */
            @Override
            public void onItemClick(Bilhete bilhete) {
                Toast.makeText(getContext(), "Código: " + bilhete.getCodigo(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(bilheteAdapter);
    }

    /**
     * Limpa recursos quando a view é destruída
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Callback quando a lista de bilhetes é carregada com sucesso
     * Atualiza a RecyclerView com os novos dados
     */
    @Override
    public void onBilhetesLoaded(ArrayList<Bilhete> bilhetesAPI) {
        // Limpa a lista anterior e adiciona os novos bilhetes
        listaBilhetes.clear();
        listaBilhetes.addAll(bilhetesAPI);

        // Notifica o adaptador sobre as alterações
        if (bilheteAdapter != null) {
            bilheteAdapter.updateBilhetes(listaBilhetes);
        }
    }

    /**
     * Callback quando ocorre erro ao carregar os bilhetes
     */
    @Override
    public void onBilhetesError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Erro ao carregar bilhetes: " + message, Toast.LENGTH_SHORT).show();
        }
    }
}
