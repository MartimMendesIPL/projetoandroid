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

import pt.ipleiria.estg.dei.maislusitania_android.adapters.ReservaAdapter;
import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentBilhetesBinding;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.ReservaListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.Reserva;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;

public class ReservasFragment extends Fragment implements ReservaListener {

    private FragmentBilhetesBinding binding;
    private ReservaAdapter reservaAdapter;
    private ArrayList<Reserva> reservas;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBilhetesBinding.inflate(inflater, container, false);
        reservas = new ArrayList<>();

        // Listener para o ícone de perfil (Barra de pesquisa/topo)
        binding.tilPesquisa.setEndIconOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PerfilActivity.class);
            startActivity(intent);
        });

        // Configurar a RecyclerView
        setupRecyclerView();

        // Configurar o Singleton e pedir os dados à API
        SingletonLusitania.getInstance(requireContext()).setReservaListener(this);
        SingletonLusitania.getInstance(requireContext()).getAllReservasAPI(requireContext());

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewReservas;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        reservaAdapter = new ReservaAdapter(requireContext(), reservas, new ReservaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Reserva reserva) {
                ViewBilhetesFragment fragment = new ViewBilhetesFragment();

                Bundle args = new Bundle();
                args.putInt("ID_RESERVA", reserva.getId());
                fragment.setArguments(args);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        recyclerView.setAdapter(reservaAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onReservasLoaded(ArrayList<Reserva> listaReservas) {
        // Atualizar a lista local
        reservas.clear();
        reservas.addAll(listaReservas);

        // Notificar o adapter que os dados mudaram
        if (reservaAdapter != null) {
            reservaAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onReservasError(String message) {
        // Verificar se o fragmento ainda está anexado antes de mostrar o Toast (evita crashes)
        if (getContext() != null) {
            Toast.makeText(getContext(), "Erro: " + message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReservaLoaded(Reserva reserva) {
        // Este método não é usado na lista principal (usado apenas em detalhes/create)
    }
}