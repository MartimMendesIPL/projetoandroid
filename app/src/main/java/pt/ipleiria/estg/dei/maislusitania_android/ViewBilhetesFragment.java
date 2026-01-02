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

// FIX: Implement BilhetesListener, not ReservaListener
public class ViewBilhetesFragment extends Fragment implements BilheteListener {

    private FragmentViewBilhetesBinding binding;
    private BilheteAdapter bilheteAdapter;
    private ArrayList<Bilhete> listaBilhetes;
    private int idReserva = -1;

    public ViewBilhetesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentViewBilhetesBinding.inflate(inflater, container, false);
        listaBilhetes = new ArrayList<>();

        binding.btnVoltar.setOnClickListener(v -> requireActivity().onBackPressed());

        setupRecyclerView();

        if (getArguments() != null) {
            idReserva = getArguments().getInt("ID_RESERVA", -1);
        }

        SingletonLusitania.getInstance(requireContext()).setBilheteListener(this);
        SingletonLusitania.getInstance(requireContext()).getAllBilhetesAPI(requireContext(), idReserva);

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewBilhetes;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Adapter
        bilheteAdapter = new BilheteAdapter(listaBilhetes, new BilheteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Bilhete bilhete) {
                // Action: Show QR Code or details
                Toast.makeText(getContext(), "Código: " + bilhete.getCodigo(), Toast.LENGTH_SHORT).show();

                // Exemplo para abrir Activity do QR Code:
                // Intent intent = new Intent(getContext(), QrCodeActivity.class);
                // intent.putExtra("CODIGO", bilhete.getCodigo());
                // startActivity(intent);
            }
        });

        recyclerView.setAdapter(bilheteAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // --- Listener Methods (from BilhetesListener) ---

    @Override
    public void onBilhetesLoaded(ArrayList<Bilhete> bilhetesAPI) {
        // Atualiza a lista local e notifica o adapter
        listaBilhetes.clear();
        listaBilhetes.addAll(bilhetesAPI);

        if (bilheteAdapter != null) {
            bilheteAdapter.updateBilhetes(listaBilhetes);
        }
    }

    @Override
    public void onBilhetesError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Erro ao carregar bilhetes: " + message, Toast.LENGTH_SHORT).show();
        }
    }
}