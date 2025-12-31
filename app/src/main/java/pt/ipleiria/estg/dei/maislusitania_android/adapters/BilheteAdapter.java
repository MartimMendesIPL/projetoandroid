package pt.ipleiria.estg.dei.maislusitania_android.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pt.ipleiria.estg.dei.maislusitania_android.R;
import pt.ipleiria.estg.dei.maislusitania_android.models.Bilhete;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import android.graphics.Bitmap;

public class BilheteAdapter extends RecyclerView.Adapter<BilheteAdapter.BilheteViewHolder> {

    private List<Bilhete> bilhetes;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Bilhete bilhete);
    }

    public BilheteAdapter(List<Bilhete> bilhetes, OnItemClickListener listener) {
        this.bilhetes = bilhetes != null ? bilhetes : new ArrayList<>();
        this.onItemClickListener = listener;
    }

    public static class BilheteViewHolder extends RecyclerView.ViewHolder {
        // Headers
        ImageView ivBilheteIcon;
        TextView tvBilheteLocal;
        TextView tvBilheteTipo;
        TextView tvBilheteEstado;
        TextView tvBilheteData;
        TextView tvBilheteCodigo;
        TextView tvBilheteTipoDetalhe;
        TextView tvBilhetePreco;
        ImageView ivQrCode;


        public BilheteViewHolder(@NonNull View itemView) {
            super(itemView);
            // Header IDs
            ivBilheteIcon = itemView.findViewById(R.id.ivBilheteIcon);
            tvBilheteLocal = itemView.findViewById(R.id.tvBilheteLocal);
            tvBilheteTipo = itemView.findViewById(R.id.tvBilheteTipo);
            tvBilheteEstado = itemView.findViewById(R.id.tvBilheteEstado);
            tvBilheteData = itemView.findViewById(R.id.tvBilheteData);
            tvBilheteCodigo = itemView.findViewById(R.id.tvBilheteCodigo);
            tvBilheteTipoDetalhe = itemView.findViewById(R.id.tvBilheteTipoDetalhe);
            tvBilhetePreco = itemView.findViewById(R.id.tvBilhetePreco);
            ivQrCode = itemView.findViewById(R.id.ivQrCode);
        }
    }

    @NonNull
    @Override
    public BilheteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_bilhete, parent, false);
        return new BilheteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BilheteViewHolder holder, int position) {
        Bilhete bilhete = bilhetes.get(position);


        holder.tvBilheteLocal.setText(bilhete.getLocalNome());
        holder.tvBilheteTipo.setText(bilhete.getTipoBilheteNome());

        String estadoRaw = bilhete.getEstado();
        String estado = estadoRaw != null ? estadoRaw.trim() : "";

        holder.tvBilheteEstado.setText(estado);

        int cor;
        switch (estado.toLowerCase()) {
            case "confirmada":
                cor = Color.parseColor("#28A745"); // Green
                break;
            case "pendente":
                cor = Color.parseColor("#FFC107"); // Yellow
                break;
            case "cancelada":
            case "expirada":
                cor = Color.parseColor("#DC3545"); // Red
                break;
            default:
                cor = Color.GRAY;
                break;
        }

        holder.tvBilheteEstado.setTextColor(cor);

        if (bilhete.getDataVisita() != null) {
            holder.tvBilheteData.setText(bilhete.getDataVisita());
        } else {
            holder.tvBilheteData.setText("--/--/----");
        }

        holder.tvBilheteCodigo.setText(bilhete.getCodigo());
        holder.tvBilheteTipoDetalhe.setText(bilhete.getTipoBilheteNome());
        holder.tvBilhetePreco.setText(String.format(Locale.getDefault(), "%.2f€", bilhete.getPreco()));

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(bilhete);
            }
        });

        String codigoParaQR = bilhete.getCodigo();
        if (codigoParaQR != null && !codigoParaQR.isEmpty()) {
            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                //Gerar o QR Code
                Bitmap bitmap = barcodeEncoder.encodeBitmap(codigoParaQR, BarcodeFormat.QR_CODE, 600, 600);
                holder.ivQrCode.setImageBitmap(bitmap);
                //Tornar vivibel o QR Code
                holder.ivQrCode.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
                holder.ivQrCode.setImageResource(android.R.drawable.stat_notify_error);
            }
        } else {
            holder.ivQrCode.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return bilhetes.size();
    }

    public void updateBilhetes(List<Bilhete> newBilhetes) {
        this.bilhetes = newBilhetes != null ? newBilhetes : new ArrayList<>();
        notifyDataSetChanged();
    }
}
