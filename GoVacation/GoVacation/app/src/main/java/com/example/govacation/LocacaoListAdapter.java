package com.example.govacation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Locale;

public class LocacaoListAdapter extends ArrayAdapter<Locacao> {

    private Context mContext;
    private int mResource;
    private ListarLocs mActivity;

    public LocacaoListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Locacao> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;

        if (context instanceof ListarLocs) {
            this.mActivity = (ListarLocs) context;
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

        TextView tvTitulo = convertView.findViewById(R.id.tvTituloItem);
        TextView tvLocalizacao = convertView.findViewById(R.id.tvLocalizacaoItem);
        TextView tvPreco = convertView.findViewById(R.id.tvPrecoItem);
        ImageButton btnAlterar = convertView.findViewById(R.id.btnAlterarItem);
        ImageButton btnExcluir = convertView.findViewById(R.id.btnExcluirItem);

        if (locacao != null) {
            tvTitulo.setText(locacao.getTitulo());
            tvLocalizacao.setText(locacao.getLocalizacao());

            String precoFormatado = String.format(new Locale("pt", "BR"), "R$ %.2f", locacao.getPreco());
            tvPreco.setText(precoFormatado);

            long locacaoId = locacao.getIdloc();

            btnAlterar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mActivity != null) {
                        mActivity.alterarLocacao(locacaoId);
                    }
                }
            });

            btnExcluir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mActivity != null) {
                        mActivity.excluirLocacao(locacaoId, locacao.getTitulo());
                    }
                }
            });
        }

        return convertView;
    }
}