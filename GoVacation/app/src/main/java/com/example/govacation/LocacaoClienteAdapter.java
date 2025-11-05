package com.example.govacation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Locale;

public class LocacaoClienteAdapter extends ArrayAdapter<Locacao> {

    private Context mContext;
    private int mResource;
    private TelaHome mActivity;

    public LocacaoClienteAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Locacao> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;

        if (context instanceof TelaHome) {
            this.mActivity = (TelaHome) context;
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Locacao locacao = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
        }

        TextView tvTitulo = convertView.findViewById(R.id.tvTituloItemCliente);
        TextView tvLocal = convertView.findViewById(R.id.tvLocalItemCliente);
        TextView tvHospedes = convertView.findViewById(R.id.tvHospedesItemCliente);
        TextView tvPreco = convertView.findViewById(R.id.tvPrecoItemCliente);
        Button btnReservar = convertView.findViewById(R.id.btnReservarItem);

        if (locacao != null) {
            tvTitulo.setText(locacao.getTitulo());
            tvLocal.setText(locacao.getLocalizacao());

            String precoFormatado = String.format(new Locale("pt", "BR"), "R$ %.2f", locacao.getPreco());
            tvPreco.setText(precoFormatado);

            String hospedesFormatado = "Até " + locacao.getQtdhospedes() + " hóspedes";
            tvHospedes.setText(hospedesFormatado);

            long locacaoId = locacao.getIdloc();

            btnReservar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mActivity != null) {
                        mActivity.verDetalhesReserva(locacaoId);
                    }
                }
            });
        }

        return convertView;
    }
}