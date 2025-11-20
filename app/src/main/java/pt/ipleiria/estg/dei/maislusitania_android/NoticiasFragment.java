package pt.ipleiria.estg.dei.maislusitania_android;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentEventosBinding;
import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentNoticiasBinding;

public class NoticiasFragment extends Fragment {
    private FragmentNoticiasBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNoticiasBinding.inflate(inflater, container, false);


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