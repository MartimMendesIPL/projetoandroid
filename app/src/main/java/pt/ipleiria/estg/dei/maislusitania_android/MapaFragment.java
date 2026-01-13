package pt.ipleiria.estg.dei.maislusitania_android;

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
import pt.ipleiria.estg.dei.maislusitania_android.utils.UtilParser; // Make sure this is imported

public class MapaFragment extends Fragment implements MapaListener {

    private FragmentMapaBinding binding;
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    // Control variable to prevent spamming JS calls
    private boolean areMarkersLoaded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapaBinding.inflate(inflater, container, false);
        setupSearchListeners();
        binding.tilPesquisa.setEndIconOnClickListener(v ->
                startActivity(new Intent(requireActivity(), PerfilActivity.class))
        );

        setupWebView(binding.webViewMap);
        SingletonLusitania.getInstance(requireContext()).setMapaListener(this);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWebViewContent();
    }

    @Override
    public void onPause() {
        super.onPause();
        searchHandler.removeCallbacks(searchRunnable);
    }

    private void loadWebViewContent() {
        if (getContext() == null || binding == null) return;

        areMarkersLoaded = false; // Reset marker state

        if (UtilParser.isConnectionInternet(getContext())) {
            // ONLINE:
            binding.tilPesquisa.getEditText().setEnabled(false);
            binding.tilPesquisa.setAlpha(0.5f);
            binding.webViewMap.loadUrl("file:///android_asset/leaflet_map.html");
        } else {
            // OFFLINE:
            binding.tilPesquisa.getEditText().setEnabled(true);
            binding.tilPesquisa.setAlpha(1.0f);
            binding.webViewMap.loadUrl("file:///android_asset/no_internet.html");
        }
    }

    private void setupSearchListeners() {
        binding.tilPesquisa.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacks(searchRunnable);
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchRunnable = () -> {
                    if (getContext() == null) return;

                    if (!UtilParser.isConnectionInternet(getContext())) {
                        Toast.makeText(getContext(), "Sem ligação à Internet para pesquisar.", Toast.LENGTH_SHORT).show();
                        loadWebViewContent();
                        return;
                    }

                    areMarkersLoaded = false; // Reset for new search
                    String query = s.toString().trim();
                    if (query.isEmpty()) {
                        SingletonLusitania.getInstance(requireContext()).getAllMapasAPI(getContext());
                    } else {
                        SingletonLusitania.getInstance(requireContext()).searchMapaAPI(getContext(), query);
                    }
                };
                searchHandler.postDelayed(searchRunnable, 500);
            }
        });
    }

    private void setupWebView(WebView webView) {
        webView.setBackgroundColor(Color.TRANSPARENT);
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setAllowFileAccessFromFileURLs(true);
        ws.setAllowUniversalAccessFromFileURLs(true);
        ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.addJavascriptInterface(new WebAppInterface(), "Android");
        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.endsWith("leaflet_map.html")) {

                    binding.tilPesquisa.getEditText().setEnabled(true);
                    binding.tilPesquisa.setAlpha(1.0f);

                    SingletonLusitania.getInstance(requireContext()).getAllMapasAPI(getContext());
                }
            }
        });
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
    public void onMapaLoaded(ArrayList<Mapa> mapaLocais) {
        if (binding == null || binding.webViewMap == null || areMarkersLoaded) return;

        String jsonString = MapaJsonParser.mapasListToJson(mapaLocais);
        String safeJson = jsonString.replace("'", "\\'");

        binding.webViewMap.post(() -> {
            binding.webViewMap.evaluateJavascript("loadMarkers('" + safeJson + "')", null);
            areMarkersLoaded = true;
        });
    }

    @Override
    public void onMapaError(String message) {
        if (getContext() != null) {
            if (UtilParser.isConnectionInternet(getContext())) {
                Toast.makeText(getContext(), "Erro ao carregar mapa: " + message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        if (binding != null && binding.webViewMap != null) {
            binding.webViewMap.destroy();
        }
        super.onDestroyView();
        binding = null;
    }
}
