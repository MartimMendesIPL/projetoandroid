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

import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentMapaBinding;

public class MapaFragment extends Fragment {
    private FragmentMapaBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapaBinding.inflate(inflater, container, false);

        binding.tilPesquisa.setEndIconOnClickListener(v ->
                startActivity(new Intent(requireActivity(), PerfilActivity.class))
        );

        final String assetName = "leaflet_map.html";

        WebView webView = binding.webViewMap;
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
}
