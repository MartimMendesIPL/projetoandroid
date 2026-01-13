package pt.ipleiria.estg.dei.maislusitania_android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
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

    // Variáveis de controlo do Mapa
    private boolean isMapReady = false;
    private ArrayList<Mapa> pendingMapas = null;

    // Variáveis para a Pesquisa Dinâmica
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapaBinding.inflate(inflater, container, false);

        //Configurar Listeners da Interface (Pesquisa e Botões)
        setupSearchListeners();

        binding.tilPesquisa.setEndIconOnClickListener(v ->
                startActivity(new Intent(requireActivity(), PerfilActivity.class))
        );

        // Configurar a WebView (Mapa)
        setupWebView(binding.webViewMap);

        // Iniciar os dados
        SingletonLusitania.getInstance(requireContext()).setMapaListener(this);
        SingletonLusitania.getInstance(requireContext()).getAllMapasAPI(getContext());

        return binding.getRoot();
    }

    /**
     * Configura a pesquisa dinâmica com delay para evitar chamadas excessivas à API.
     */
    private void setupSearchListeners() {
        binding.tilPesquisa.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Se o utilizador continuar a escrever, é apagada a pesquisa feita anteriormente
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchRunnable = () -> {
                    String query = s.toString().trim();
                    if (query.isEmpty()) {
                        SingletonLusitania.getInstance(requireContext()).getAllMapasAPI(getContext());
                    } else {
                        SingletonLusitania.getInstance(requireContext()).searchMapaAPI(getContext(), query);
                    }
                };
                // Aguarda 500ms após a última tecla antes de pesquisar
                searchHandler.postDelayed(searchRunnable, 500);
            }
        });
    }

    /**
     * Centraliza todas as configurações da WebView para manter o código organizado.
     */
    private void setupWebView(WebView webView) {
        webView.setBackgroundColor(Color.TRANSPARENT);

        WebSettings ws = webView.getSettings();
        // ligar o js
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        // Permissões de ficheiros necessárias para carregar o HTML local e recursos associados
        ws.setAllowFileAccessFromFileURLs(true);
        ws.setAllowUniversalAccessFromFileURLs(true);
        // As imagens estao em http então é preciso isto
        ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // Adiciona a interface JavaScript
        webView.addJavascriptInterface(new WebAppInterface(), "Android");

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                isMapReady = true;

                // Se os dados da API chegaram antes do mapa carregar, mostramos agora
                if (pendingMapas != null) {
                    loadMarkersOnMap(pendingMapas);
                    pendingMapas = null;
                }
            }
        });

        webView.loadUrl("file:///android_asset/leaflet_map.html");
    }

    public class WebAppInterface {
        @JavascriptInterface
        public void openDetails(String localId) {
            new Handler(Looper.getMainLooper()).post(() -> {
                if (getView() == null || getParentFragmentManager() == null) {
                    return;
                }

                try {
                    int id = Integer.parseInt(localId);

                    Fragment destinationFragment = DetalhesLocalFragment.newInstance(id);

                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, destinationFragment)
                            .addToBackStack(null)
                            .commit();

                } catch (NumberFormatException e) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "ID do local inválido.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        // Limpar callbacks de pesquisa pendentes para evitar memory leaks ou crashes
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }

        // Limpeza padrão da WebView
        if (binding != null && binding.webViewMap != null) {
            WebView w = binding.webViewMap;
            // remover a interface para evitar leaks
            w.removeJavascriptInterface("Android");
            w.loadUrl("about:blank");
            w.stopLoading();
            w.setWebChromeClient(null);
            w.setWebViewClient(null);
            w.destroy();
        }

        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onMapaLoaded(ArrayList<Mapa> mapaLocais) {
        if (isMapReady) {
            loadMarkersOnMap(mapaLocais);
        } else {
            // Guarda para quando o mapa acabar de carregar
            pendingMapas = mapaLocais;
        }
    }

    @Override
    public void onMapaError(String message) {
        Toast.makeText(getContext(), "Erro Mapas: " + message, Toast.LENGTH_SHORT).show();
    }

    private void loadMarkersOnMap(ArrayList<Mapa> mapaLocais) {
        if (binding == null || binding.webViewMap == null || mapaLocais == null) return;

        String jsonString = MapaJsonParser.mapasListToJson(mapaLocais);
        // Escapar aspas simples para não partir o JavaScript
        String safeJson = jsonString.replace("'", "\\'");

        binding.webViewMap.post(() ->
                binding.webViewMap.evaluateJavascript("loadMarkers('" + safeJson + "')", null)
        );
    }
}
