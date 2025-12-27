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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.estg.dei.maislusitania_android.adapters.BilheteAdapter;
import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentBilhetesBinding;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.BilheteListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.Bilhete;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;

public class BilhetesFragment extends Fragment implements BilheteListener {
    private FragmentBilhetesBinding binding;

    private BilheteAdapter bilheteAdapter;
    private ArrayList<Bilhete> bilhetes; // Alterado para ArrayList para bater certo com o EventosFragment

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBilhetesBinding.inflate(inflater, container, false);
        bilhetes = new ArrayList<>();

        // Listener para o ícone de perfil
        binding.tilPesquisa.setEndIconOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PerfilActivity.class);
            startActivity(intent);
        });

        // Configurar a RecyclerView
        setupRecyclerView();

        // Definir o listener e chamar a API
        // Usar requireContext() em ambos para garantir que o contexto é válido
        SingletonLusitania.getInstance(requireContext()).setBilhetesListener(this);
        SingletonLusitania.getInstance(requireContext()).getAllBilhetesAPI(requireContext());

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewBilhetes;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Passamos a lista 'bilhetes' para o adapter. O adapter guarda uma referência para esta lista.
        bilheteAdapter = new BilheteAdapter(bilhetes, new BilheteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Bilhete bilhete) {
                Toast.makeText(getContext(), "Clicou em: " + bilhete.getLocal().getNome(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(bilheteAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onBilhetesLoaded(ArrayList<Bilhete> listaBilhetes) {
        // LÓGICA IGUAL AO EVENTOSFRAGMENT:
        // Em vez de substituir a lista no adapter, limpamos a nossa lista local e adicionamos os novos itens.
        // Como o adapter tem uma referência para esta lista 'bilhetes', ele vai ver as mudanças.

        bilhetes.clear();
        bilhetes.addAll(listaBilhetes);

        if (bilheteAdapter != null) {
            bilheteAdapter.notifyDataSetChanged();
            // Toast para debug (podes remover depois)
            // Toast.makeText(getContext(), "Carregados " + listaBilhetes.size() + " bilhetes", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBilhetesError(String message) {
        Toast.makeText(getContext(), "Erro: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBilheteLoaded(Bilhete bilhete) {
        // Não usado na lista
    }
}
