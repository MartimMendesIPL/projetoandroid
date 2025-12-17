package pt.ipleiria.estg.dei.maislusitania_android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentMapaBinding;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.MapaListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.Mapa;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;
import pt.ipleiria.estg.dei.maislusitania_android.utils.MapaJsonParser;

public class MapaFragment extends Fragment implements MapaListener {
    private FragmentMapaBinding binding;

    private boolean isMapReady = false;
    private ArrayList<Mapa> pendingMapas = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapaBinding.inflate(inflater, container, false);

        binding.tilPesquisa.setEndIconOnClickListener(v ->
                startActivity(new Intent(requireActivity(), PerfilActivity.class))
        );

        binding.tilPesquisa.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new LocaisFragment())
                    .addToBackStack(null)
                    .commit();
        });

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
        webView.setBackgroundColor(Color.TRANSPARENT);

        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setAllowFileAccess(true);
        ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        ws.setAllowFileAccessFromFileURLs(true);
        ws.setAllowUniversalAccessFromFileURLs(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                isMapReady = true;

                if (pendingMapas != null) {
                    loadMarkersOnMap(pendingMapas);
                    pendingMapas = null;
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("file:///android_asset/" + assetName);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding != null && binding.webViewMap != null) {
            WebView w = binding.webViewMap;
            w.loadUrl("about:blank");
            w.stopLoading();
            w.setWebChromeClient(null);
            w.setWebViewClient(null);
            w.destroy();
        }
        binding = null;
    }

    @Override
    public void onMapaLoaded(ArrayList<Mapa> mapaLocais) {
        if (isMapReady) {
            loadMarkersOnMap(mapaLocais);
        } else {
            pendingMapas = mapaLocais;
        }
    }

    private void loadMarkersOnMap(ArrayList<Mapa> mapaLocais) {
        if (binding == null || binding.webViewMap == null || mapaLocais == null) return;

        String jsonString = MapaJsonParser.mapasListToJson(mapaLocais);

        String safeJson = jsonString.replace("'", "\\'");

        binding.webViewMap.post(() ->
                binding.webViewMap.evaluateJavascript("loadMarkers('" + safeJson + "')", null)
        );
    }

    @Override
    public void onMapaError(String message) {
        Toast.makeText(getContext(), "Erro Mapas: " + message, Toast.LENGTH_SHORT).show();
    }
}