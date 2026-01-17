package pt.ipleiria.estg.dei.maislusitania_android;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import pt.ipleiria.estg.dei.maislusitania_android.adapters.TipoBilheteAdapter;
import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentFazerReservaBinding;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.LocaisListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.ReservaListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.Local;
import pt.ipleiria.estg.dei.maislusitania_android.models.Reserva;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;
import pt.ipleiria.estg.dei.maislusitania_android.models.TipoBilhete;

/**
 * Fragment para criar reservas de bilhetes em locais
 */
public class FazerReservaFragment extends Fragment implements LocaisListener, ReservaListener {

    // Binding para acesso aos elementos da UI
    private FragmentFazerReservaBinding binding;

    // Adaptador para a lista de tipos de bilhetes
    private TipoBilheteAdapter adapter;

    // ID do local e data selecionada
    private int localId;
    private Calendar selectedDate;

    // Formatador de datas
    private SimpleDateFormat dateFormatter;

    /**
     * Construtor padrão
     */
    public FazerReservaFragment() {}

    /**
     * Factory method para criar o fragment com o ID do local
     */
    public static FazerReservaFragment newInstance(int localId) {
        FazerReservaFragment fragment = new FazerReservaFragment();
        Bundle args = new Bundle();
        args.putInt("local_id", localId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Extrai o ID do local dos argumentos e inicializa datas
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            localId = getArguments().getInt("local_id");
        }
        selectedDate = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    /**
     * Inicializa o binding da view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFazerReservaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Configura os elementos da UI e carrega os dados do local
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupDatePicker();
        setupRecyclerView();
        setupButton();

        // Faz o pedido à API para obter dados do local
        Log.d("ReservaFragment", "A pedir dados do local ID: " + localId);
        SingletonLusitania.getInstance(requireContext()).getLocalAPI(localId, requireContext());
    }

    /**
     * Religa os listeners ao fragment ficar visível (impede desconexões)
     */
    @Override
    public void onResume() {
        super.onResume();
        // --- CORREÇÃO FUNDAMENTAL ---
        // Força a ligação do listener sempre que o fragmento fica visível.
        // Isto impede que o 'onDestroyView' do fragmento anterior corte a comunicação.
        SingletonLusitania.getInstance(requireContext()).setLocaisListener(this);
        SingletonLusitania.getInstance(requireContext()).setReservaListener(this);
    }

    /**
     * Configura o selecionador de datas
     */
    private void setupDatePicker() {
        binding.etDate.setOnClickListener(v -> mostrarDatePicker());
        binding.etDate.setText(dateFormatter.format(selectedDate.getTime()));
    }

    /**
     * Configura a RecyclerView com o adaptador de tipos de bilhetes
     */
    private void setupRecyclerView() {
        binding.rvTicketTypes.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Inicializa o adaptador vazio
        adapter = new TipoBilheteAdapter(requireContext(), new ArrayList<>());

        // Configura o listener para atualizar o total quando a quantidade muda
        adapter.setOnQuantityChangeListener(this::atualizarTotal);

        binding.rvTicketTypes.setAdapter(adapter);
    }

    /**
     * Configura o botão de continuar
     */
    private void setupButton() {
        binding.btnContinue.setEnabled(false);
        binding.btnContinue.setOnClickListener(v -> fazerReserva());
    }

    /**
     * Mostra o diálogo de seleção de data com limitações
     */
    private void mostrarDatePicker() {
        // Define a data máxima (1 ano a partir de hoje)
        Calendar dataMaxima = Calendar.getInstance();
        dataMaxima.add(Calendar.YEAR, 1);

        // Configura restrições de datas (apenas datas futuras até 1 ano)
        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
                .setEnd(dataMaxima.getTimeInMillis())
                .build();

        // Cria o selecionador de datas
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data da visita")
                .setSelection(selectedDate.getTimeInMillis())
                .setCalendarConstraints(constraints)
                .build();

        // Atualiza a data quando o utilizador seleciona uma
        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedDate.setTimeInMillis(selection);
            binding.etDate.setText(dateFormatter.format(selectedDate.getTime()));
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    /**
     * Atualiza o preço total baseado nas quantidades selecionadas
     */
    private void atualizarTotal() {
        double total = adapter.calcularTotal();
        binding.tvTotalPrice.setText(String.format(Locale.getDefault(), "%.2f €", total));
        // Ativa o botão apenas se há bilhetes selecionados
        binding.btnContinue.setEnabled(total > 0);
    }

    /**
     * Submete a reserva à API
     */
    private void fazerReserva() {
        double total = adapter.calcularTotal();
        if (total <= 0) {
            Toast.makeText(requireContext(), "Selecione pelo menos um bilhete", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtém os bilhetes selecionados
        ArrayList<TipoBilhete> tiposBilheteSelecionados = adapter.getTiposBilheteSelecionados();
        if (tiposBilheteSelecionados.isEmpty()) {
            Toast.makeText(requireContext(), "Erro: Lista de bilhetes vazia", Toast.LENGTH_SHORT).show();
            return;
        }

        // Formata a data para o formato da API (yyyy-MM-dd)
        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dataVisitaFormatada = apiDateFormat.format(selectedDate.getTime());

        // Desativa o botão e mostra mensagem de progresso
        binding.btnContinue.setEnabled(false);
        Toast.makeText(requireContext(), "A processar reserva...", Toast.LENGTH_SHORT).show();

        // Envia a reserva para a API
        SingletonLusitania.getInstance(requireContext()).createReservaAPI(
                requireContext(),
                localId,
                dataVisitaFormatada,
                tiposBilheteSelecionados
        );
    }

    /**
     * Callback quando o local é carregado com sucesso
     */
    @Override
    public void onLocalLoaded(Local local) {
        Log.d("ReservaFragment", "onLocalLoaded chamado!");

        if (local != null) {
            ArrayList<TipoBilhete> bilhetes = local.getTiposBilhete();

            if (bilhetes != null && !bilhetes.isEmpty()) {
                // Atualiza o adaptador com os tipos de bilhetes disponíveis
                Log.d("ReservaFragment", "Bilhetes carregados: " + bilhetes.size());
                adapter.updateTiposBilhete(bilhetes);
                atualizarTotal();
            } else {
                // Mostra erro se não há bilhetes disponíveis
                Log.e("ReservaFragment", "Local carregado mas SEM bilhetes.");
                Toast.makeText(requireContext(), "Não há bilhetes disponíveis para este local.", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Callback quando ocorre erro ao carregar o local
     */
    @Override
    public void onLocalError(String error) {
        Log.e("ReservaFragment", "Erro ao carregar local: " + error);
        if (getContext() != null) {
            Toast.makeText(getContext(), "Erro: " + error, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Callback quando a reserva é criada com sucesso
     */
    @Override
    public void onReservaCreated(Reserva reserva) {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Reserva criada com sucesso!", Toast.LENGTH_LONG).show();
            // Limpa seleções e volta para o fragment anterior
            adapter.limparSelecoes();
            atualizarTotal();

            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        }
    }

    /**
     * Callback quando ocorre erro ao criar a reserva
     */
    @Override
    public void onReservaError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Erro na reserva: " + message, Toast.LENGTH_LONG).show();
            // Reativa o botão para permitir nova tentativa
            binding.btnContinue.setEnabled(true);
        }
    }

    /**
     * Callbacks não utilizados neste fragment
     */
    @Override
    public void onLocaisLoaded(ArrayList<Local> locais) {}
    @Override
    public void onLocaisError(String error) {}
    @Override
    public void onReservasLoaded(ArrayList<Reserva> listaReservas) {}
    @Override
    public void onReservaLoaded(Reserva reserva) {}
    @Override
    public void onReservasError(String message) {}

    /**
     * Limpa as referências quando a view é destruída
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
