package pt.ipleiria.estg.dei.maislusitania_android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentMapaBinding;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.MapaListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.Mapa;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;
import pt.ipleiria.estg.dei.maislusitania_android.utils.MapaJsonParser;

public class MapaFragment extends Fragment implements MapaListener {
    private FragmentMapaBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapaBinding.inflate(inflater, container, false);

        // Listener do ícone de perfil (código existente)
        binding.tilPesquisa.setEndIconOnClickListener(v ->
                startActivity(new Intent(requireActivity(), PerfilActivity.class))
        );

        // NOVO: Listener para abrir LocaisFragment ao clicar na barra de pesquisa
        binding.tilPesquisa.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new LocaisFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Também no EditText
        binding.etPesquisa.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new LocaisFragment())
                    .addToBackStack(null)
                    .commit();
        });

        final String assetName = "leaflet_map.html";

        SingletonLusitania.getInstance(requireContext()).setMapaListener(this);
        SingletonLusitania.getInstance(requireContext()).getAllMapasAPI(getContext());

        WebView webView = binding.webViewMap;
        //webView.addJavascriptInterface(new WebAppInterface(requireContext()), "Android");
        webView.setBackgroundColor(Color.TRANSPARENT);

        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setAllowFileAccess(true);
        ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        ws.setAllowFileAccessFromFileURLs(true);
        ws.setAllowUniversalAccessFromFileURLs(true);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("file:///android_asset/" + assetName);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        WebView w = binding.webViewMap;
        w.loadUrl("about:blank");
        w.stopLoading();
        w.setWebChromeClient(null);
        w.setWebViewClient(null);
        w.destroy();
        binding = null;
    }

    @Override
    public void onMapaLoaded(ArrayList<Mapa> mapaLocais) {
        String jsonString = MapaJsonParser.mapasListToJson(mapaLocais);

        if (binding != null && binding.webViewMap != null) {
            String safeJson = jsonString.replace("'", "\\'");

            binding.webViewMap.post(() ->
                    binding.webViewMap.evaluateJavascript("loadMarkers('" + safeJson + "')", null)
            );
        }

    }

    @Override
    public void onMapaError(String message) {

    }
}
