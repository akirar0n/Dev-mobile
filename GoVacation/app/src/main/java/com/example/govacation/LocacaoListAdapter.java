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
    private ListarLocs mActivity; // Referência para a Activity

    public LocacaoListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Locacao> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;

        // Salva a referência da Activity para poder chamar os métodos dela
        if (context instanceof ListarLocs) {
            this.mActivity = (ListarLocs) context;
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Pega o objeto Locacao da posição atual
        Locacao locacao = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
        }

        // Linka os componentes do XML (list_item_locacao.xml)
        TextView tvTitulo = convertView.findViewById(R.id.tvTituloItem);
        TextView tvLocalizacao = convertView.findViewById(R.id.tvLocalizacaoItem);
        TextView tvPreco = convertView.findViewById(R.id.tvPrecoItem);
        ImageButton btnAlterar = convertView.findViewById(R.id.btnAlterarItem);
        ImageButton btnExcluir = convertView.findViewById(R.id.btnExcluirItem);

        if (locacao != null) {
            // Popula os campos com os dados
            tvTitulo.setText(locacao.getTitulo());
            tvLocalizacao.setText(locacao.getLocalizacao());

            // Formata o preço para R$
            String precoFormatado = String.format(new Locale("pt", "BR"), "R$ %.2f", locacao.getPreco());
            tvPreco.setText(precoFormatado);

            // Pega o ID da locação
            long locacaoId = locacao.getIdloc();

            // --- Define os cliques dos botões ---
            btnAlterar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mActivity != null) {
                        // Chama o método "alterar" da Activity
                        mActivity.alterarLocacao(locacaoId);
                    }
                }
            });

            btnExcluir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mActivity != null) {
                        // Chama o método "excluir" da Activity
                        mActivity.excluirLocacao(locacaoId, locacao.getTitulo());
                    }
                }
            });
        }

        return convertView;
    }
}