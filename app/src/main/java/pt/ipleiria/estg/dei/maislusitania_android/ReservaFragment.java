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
import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentReservaBinding;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.LocaisListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.ReservaListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.Local;
import pt.ipleiria.estg.dei.maislusitania_android.models.Reserva;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;
import pt.ipleiria.estg.dei.maislusitania_android.models.TipoBilhete;

public class ReservaFragment extends Fragment implements LocaisListener, ReservaListener {

    private FragmentReservaBinding binding;
    private TipoBilheteAdapter adapter;
    private int localId;
    private Calendar selectedDate;
    private SimpleDateFormat dateFormatter;

    public ReservaFragment() {}

    public static ReservaFragment newInstance(int localId) {
        ReservaFragment fragment = new ReservaFragment();
        Bundle args = new Bundle();
        args.putInt("local_id", localId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            localId = getArguments().getInt("local_id");
        }
        selectedDate = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReservaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupDatePicker();
        setupRecyclerView();
        setupButton();

        SingletonLusitania singleton = SingletonLusitania.getInstance(requireContext());
        singleton.setLocaisListener(this);
        singleton.setReservaListener(this);

        // Carrega os dados do local (incluindo os tipos de bilhete)
        singleton.getLocalAPI(localId, requireContext());
    }

    private void setupDatePicker() {
        binding.etDate.setOnClickListener(v -> mostrarDatePicker());
        binding.etDate.setText(dateFormatter.format(selectedDate.getTime()));
    }

    private void setupRecyclerView() {
        binding.rvTicketTypes.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Inicializa o adapter
        adapter = new TipoBilheteAdapter(requireContext(), new ArrayList<>());

        // Configura o listener para atualizar o total quando a quantidade muda
        adapter.setOnQuantityChangeListener(this::atualizarTotal);

        binding.rvTicketTypes.setAdapter(adapter);
    }

    private void setupButton() {
        binding.btnContinue.setEnabled(false); // Desativado até haver valor > 0
        binding.btnContinue.setOnClickListener(v -> fazerReserva());
    }

    private void mostrarDatePicker() {
        Calendar dataMaxima = Calendar.getInstance();
        dataMaxima.add(Calendar.YEAR, 1);

        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
                .setEnd(dataMaxima.getTimeInMillis())
                .build();

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data da visita")
                .setSelection(selectedDate.getTimeInMillis())
                .setCalendarConstraints(constraints)
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedDate.setTimeInMillis(selection);
            binding.etDate.setText(dateFormatter.format(selectedDate.getTime()));
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void atualizarTotal() {
        double total = adapter.calcularTotal();
        binding.tvTotalPrice.setText(String.format(Locale.getDefault(), "%.2f €", total));

        // Ativa o botão apenas se o total for maior que 0
        binding.btnContinue.setEnabled(total > 0);
    }

    private void fazerReserva() {
        double total = adapter.calcularTotal();
        if (total <= 0) {
            Toast.makeText(requireContext(), "Selecione pelo menos um bilhete", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<TipoBilhete> tiposBilheteSelecionados = adapter.getTiposBilheteSelecionados();
        if (tiposBilheteSelecionados.isEmpty()) {
            Toast.makeText(requireContext(), "Erro: Lista de bilhetes vazia", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dataVisitaFormatada = apiDateFormat.format(selectedDate.getTime());

        // Feedback visual
        binding.btnContinue.setEnabled(false);
        Toast.makeText(requireContext(), "A processar reserva...", Toast.LENGTH_SHORT).show();

        SingletonLusitania.getInstance(requireContext()).createReservaAPI(
                requireContext(),
                localId,
                dataVisitaFormatada,
                tiposBilheteSelecionados
        );
    }

    @Override
    public void onLocalLoaded(Local local) {
        if (local != null && local.getTiposBilhete() != null) {
            adapter.updateTiposBilhete(local.getTiposBilhete());
            atualizarTotal(); // Recalcula (será 0 inicialmente)
        }
    }

    @Override
    public void onLocalError(String error) {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Erro ao carregar local: " + error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReservaCreated(Reserva reserva) {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Reserva criada com sucesso!", Toast.LENGTH_LONG).show();
            adapter.limparSelecoes();
            atualizarTotal();

            // Volta para a tela anterior
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public void onReservaError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Erro na reserva: " + message, Toast.LENGTH_LONG).show();
            binding.btnContinue.setEnabled(true); // Reativa o botão para tentar de novo
        }
    }

    // Métodos não utilizados mas obrigatórios pela interface
    @Override
    public void onLocaisLoaded(ArrayList<Local> locais) {}
    @Override
    public void onLocaisError(String error) {}
    @Override
    public void onReservasLoaded(ArrayList<Reserva> listaReservas) {}
    @Override
    public void onReservaLoaded(Reserva reserva) {}
    @Override
    public void onReservasError(String message) {} // Este método pode entrar em conflito se usar sobrecargas incorretas

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
