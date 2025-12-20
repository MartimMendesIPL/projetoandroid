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

import pt.ipleiria.estg.dei.maislusitania_android.adapters.EventoAdapter;
import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentEventosBinding;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.EventoListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.Evento;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;


public class EventosFragment extends Fragment implements EventoListener {

    private FragmentEventosBinding binding;
    private EventoAdapter eventoAdapter;
    private ArrayList<Evento> items;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEventosBinding.inflate(inflater, container, false);

        items = new ArrayList<>();

        // Listener para o ícone de perfil (ícone à direita)
        binding.tilPesquisa.setEndIconOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Abrir activity de perfil
                Intent intent = new Intent(getActivity(), PerfilActivity.class);
                startActivity(intent);
            }
        });

        binding.tilPesquisa.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH ||
                    actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {

                String query = binding.tilPesquisa.getEditText().getText().toString().trim();

                // Verificar se a query está vazia
                if (query.isEmpty()) {
                    // Se estiver recarregar os mapas
                    SingletonLusitania.getInstance(requireContext()).getAllEventosAPI(getContext());
                } else {
                    // Chamar o metodo de pesquisa
                    SingletonLusitania.getInstance(requireContext()).searchEventoAPI(getContext(), query);
                }

                // Esconder o teclado
                hideKeyboard();
                return true;
            }
            return false;
        });

        setupRecyclerView();

        // Configurar o listener e pedir os dados à API
        SingletonLusitania.getInstance(requireContext()).setEventoListener(this);
        SingletonLusitania.getInstance(requireContext()).getAllEventosAPI(getContext());

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewEventos;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        eventoAdapter = new EventoAdapter(getContext(), items, new EventoAdapter.OnEventoListener() {
            @Override
            public void onEventoClick(int position) {
                Evento item = items.get(position);

                DetalhesEventoFragment fragment = new DetalhesEventoFragment();

                Bundle args = new Bundle();
                args.putInt("evento_id", item.getId());
                fragment.setArguments(args);

                // Navegar para o fragmento
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        recyclerView.setAdapter(eventoAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onEventosLoaded(ArrayList<Evento> listaEventos) {
        // Atualizar a lista local e o adapter
        items.clear();
        items.addAll(listaEventos);

        if (eventoAdapter != null) {
            // Se tiver criado um metodo updateEventos no adapter, use-o, senão notifyDataSetChanged
            eventoAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onEventoLoaded(Evento evento) {
        // Este metodo é usado para carregar um único evento(detalhes),
    }

    @Override
    public void onEventoError(String message) {
        // Corrigido o nome do metodo (era onErrorEventos)
        Toast.makeText(getContext(), "Erro: " + message, Toast.LENGTH_SHORT).show();
    }

    private void hideKeyboard() {
        View view = this.getView();
        if (view != null) {
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager)
                    requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}