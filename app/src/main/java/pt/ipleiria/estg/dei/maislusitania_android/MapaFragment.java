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
import pt.ipleiria.estg.dei.maislusitania_android.utils.UtilParser;

/**
 * Fragment que exibe um mapa interativo com marcadores de locais
 * Inclui pesquisa dinâmica e integração com WebView usando Leaflet
 */
public class MapaFragment extends Fragment implements MapaListener {

    // Binding para acesso aos elementos da UI
    private FragmentMapaBinding binding;

    // Handler e Runnable para atrasar a pesquisa e evitar chamadas excessivas à API
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    // Variável de controlo para evitar chamadas repetidas ao JavaScript
    private boolean areMarkersLoaded = false;

    /**
     * Cria a view do fragment e inicializa componentes
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapaBinding.inflate(inflater, container, false);

        // Configura os listeners da pesquisa dinâmica
        setupSearchListeners();

        // Define ação do ícone de fim da barra de pesquisa (navega para Perfil)
        binding.tilPesquisa.setEndIconOnClickListener(v -> {
            if (SingletonLusitania.getInstance(requireContext()).isGuestMode(requireContext())) {
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                startActivity(intent);
                Toast.makeText(requireContext(), "Faça login para aceder ao perfil", Toast.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(requireContext(), PerfilActivity.class);
                startActivity(intent);
            }

        });

        // Configura a WebView com as definições necessárias
        setupWebView(binding.webViewMap);

        // Registra este fragment como listener de eventos do mapa
        SingletonLusitania.getInstance(requireContext()).setMapaListener(this);

        return binding.getRoot();
    }

    /**
     * Carrega o conteúdo da WebView quando o fragment fica visível
     */
    @Override
    public void onResume() {
        super.onResume();
        loadWebViewContent();
    }

    /**
     * Remove callbacks pendentes da pesquisa quando o fragment fica oculto
     */
    @Override
    public void onPause() {
        super.onPause();
        searchHandler.removeCallbacks(searchRunnable);
    }

    /**
     * Carrega o mapa ou aviso de sem internet consoante a ligação disponível
     */
    private void loadWebViewContent() {
        if (getContext() == null || binding == null) return;

        // Reseta o estado dos marcadores para nova carga
        areMarkersLoaded = false;

        if (UtilParser.isConnectionInternet(getContext())) {
            // ONLINE: Carrega o mapa e desativa a pesquisa
            binding.tilPesquisa.getEditText().setEnabled(false);
            binding.tilPesquisa.setAlpha(0.5f);
            binding.webViewMap.loadUrl("file:///android_asset/leaflet_map.html");
        } else {
            // OFFLINE: Ativa a pesquisa e carrega página de sem internet
            binding.tilPesquisa.getEditText().setEnabled(true);
            binding.tilPesquisa.setAlpha(1.0f);
            binding.webViewMap.loadUrl("file:///android_asset/no_internet.html");
        }
    }

    /**
     * Configura a pesquisa dinâmica com delay de 500ms para evitar chamadas excessivas
     */
    private void setupSearchListeners() {
        binding.tilPesquisa.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Remove a pesquisa anterior se ainda estiver pendente
                searchHandler.removeCallbacks(searchRunnable);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Define a nova pesquisa com delay
                searchRunnable = () -> {
                    // Verifica se o fragment ainda está ligado
                    if (getContext() == null) return;

                    // Verifica ligação à internet antes de pesquisar
                    if (!UtilParser.isConnectionInternet(getContext())) {
                        Toast.makeText(getContext(), "Sem ligação à Internet para pesquisar.", Toast.LENGTH_SHORT).show();
                        loadWebViewContent();
                        return;
                    }

                    // Reseta o estado dos marcadores para nova pesquisa
                    areMarkersLoaded = false;

                    // Obtém o texto de pesquisa
                    String query = s.toString().trim();

                    // Carrega todos os mapas ou faz pesquisa conforme necessário
                    if (query.isEmpty()) {
                        SingletonLusitania.getInstance(requireContext()).getAllMapasAPI(getContext());
                    } else {
                        SingletonLusitania.getInstance(requireContext()).searchMapaAPI(getContext(), query);
                    }
                };
                // Executa a pesquisa após 500ms de inatividade
                searchHandler.postDelayed(searchRunnable, 500);
            }
        });
    }

    /**
     * Configura a WebView com as definições necessárias para renderizar o mapa Leaflet
     */
    private void setupWebView(WebView webView) {
        // Define cor de fundo transparente
        webView.setBackgroundColor(Color.TRANSPARENT);

        // Obtém as definições da WebView
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setAllowFileAccessFromFileURLs(true);
        ws.setAllowUniversalAccessFromFileURLs(true);
        ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // Define a interface JavaScript para comunicação entre Android e WebView
        webView.addJavascriptInterface(new WebAppInterface(), "Android");
        webView.setWebChromeClient(new WebChromeClient());

        // Define o cliente da WebView para controlar o carregamento
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // Quando o mapa Leaflet termina de carregar
                if (url.endsWith("leaflet_map.html")) {
                    // Ativa a barra de pesquisa
                    binding.tilPesquisa.getEditText().setEnabled(true);
                    binding.tilPesquisa.setAlpha(1.0f);

                    // Carrega os dados dos mapas da API
                    SingletonLusitania.getInstance(requireContext()).getAllMapasAPI(getContext());
                }
            }
        });
    }

    /**
     * Interface JavaScript para comunicação entre o código Android e o mapa HTML
     */
    public class WebAppInterface {
        /**
         * Abre o detalhe de um local quando o utilizador clica num marcador no mapa
         */
        @JavascriptInterface
        public void openDetails(String localId) {
            new Handler(Looper.getMainLooper()).post(() -> {
                // Verifica se a view e o fragment manager ainda existem
                if (getView() == null || getParentFragmentManager() == null) {
                    return;
                }
                try {
                    // Converte o ID para inteiro e abre o detalhe do local
                    int id = Integer.parseInt(localId);
                    Fragment destinationFragment = DetalhesLocalFragment.newInstance(id);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, destinationFragment)
                            .addToBackStack(null)
                            .commit();
                } catch (NumberFormatException e) {
                    // Mostra erro se o ID não é válido
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "ID do local inválido.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * Callback quando a lista de mapas é carregada com sucesso
     * Adiciona os marcadores ao mapa Leaflet via JavaScript
     */
    @Override
    public void onMapaLoaded(ArrayList<Mapa> mapaLocais) {
        // Verifica se a view ainda existe e se os marcadores já foram carregados
        if (binding == null || binding.webViewMap == null || areMarkersLoaded) return;

        // Converte a lista de mapas para formato JSON
        String jsonString = MapaJsonParser.mapasListToJson(mapaLocais);
        // Escapa as aspas simples para evitar erros no JavaScript
        String safeJson = jsonString.replace("'", "\\'");

        // Executa JavaScript na WebView para carregar os marcadores
        binding.webViewMap.post(() -> {
            binding.webViewMap.evaluateJavascript("loadMarkers('" + safeJson + "')", null);
            areMarkersLoaded = true;
        });
    }

    /**
     * Callback quando ocorre erro ao carregar os mapas
     */
    @Override
    public void onMapaError(String message) {
        // Mostra mensagem de erro apenas se há ligação à internet
        if (getContext() != null) {
            if (UtilParser.isConnectionInternet(getContext())) {
                Toast.makeText(getContext(), "Erro ao carregar mapa: " + message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Limpa recursos quando a view é destruída
     */
    @Override
    public void onDestroyView() {
        // Destroi a WebView para liberar memória
        if (binding != null && binding.webViewMap != null) {
            binding.webViewMap.destroy();
        }
        super.onDestroyView();
        binding = null;
    }
}
