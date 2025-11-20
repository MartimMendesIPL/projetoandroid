package pt.ipleiria.estg.dei.maislusitania_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentEventosBinding;

public class EventosFragment extends Fragment {

    private FragmentEventosBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEventosBinding.inflate(inflater, container, false);

        // Listener para o ícone de perfil (ícone à direita)
        binding.tilPesquisa.setEndIconOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Abrir activity de perfil
                Intent intent = new Intent(getActivity(), PerfilActivity.class);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}