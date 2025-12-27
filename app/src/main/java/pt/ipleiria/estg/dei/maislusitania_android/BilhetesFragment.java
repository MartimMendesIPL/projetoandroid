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
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import pt.ipleiria.estg.dei.maislusitania_android.listeners.BilheteListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.Bilhete;
import pt.ipleiria.estg.dei.maislusitania_android.adapters.BilheteAdapter;


import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentBilhetesBinding;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;

public class BilhetesFragment extends Fragment implements BilheteListener {
    private FragmentBilhetesBinding binding;

    private BilheteAdapter bilheteAdapter;
    private List<Bilhete> bilhetes;




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

        // IMPORTANTE: Definir o listener ANTES de fazer a chamada à API
        SingletonLusitania.getInstance(requireContext()).setBilhetesListener(this);
        SingletonLusitania.getInstance(requireContext()).getAllBilhetesAPI(getContext());

        return binding.getRoot();
    }


    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewBilhetes;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bilheteAdapter = new BilheteAdapter(bilhetes, new BilheteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Bilhete bilhete) {
                Toast.makeText(getContext(), "Clicou em: " + bilhete.getLocal().getNome(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(bilheteAdapter); // <- Passar a instância, não a classe
    }

    @Override
        public void onDestroyView() {
            super.onDestroyView();
            binding = null;
        }


        @Override
        public void onBilhetesLoaded(ArrayList<Bilhete> listaBilhetes) {

            // Atualizar a lista local e o adapter
            bilhetes.clear();
            bilhetes.addAll(listaBilhetes);

            if (bilheteAdapter != null) {
                bilheteAdapter.notifyDataSetChanged();
            }

        }

        @Override
        public void onBilheteLoaded(Bilhete bilhete) {

        }

        @Override
        public void onBilhetesError(String message) {

        }
    }