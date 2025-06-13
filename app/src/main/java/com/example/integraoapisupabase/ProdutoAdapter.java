package com.example.integraoapisupabase;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.ViewHolder> {
    private static final String TAG = "ProdutoAdapter";

    private List<Produto> produtos;
    private OnProdutoClickListener listener;

    public interface OnProdutoClickListener {
        void onDeleteClick(Produto produto);
    }

    public ProdutoAdapter(List<Produto> produtos, OnProdutoClickListener listener) {
        this.produtos = new ArrayList<>(produtos);
        this.listener = listener;
        Log.d(TAG, "🔨 Adapter criado com " + this.produtos.size() + " produtos");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "📦 Criando ViewHolder para posição " + getItemCount());
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_produto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG, "🔗 Fazendo bind da posição " + position + " de " + getItemCount());

        if (position >= produtos.size()) {
            Log.e(TAG, "❌ ERRO: Posição " + position + " >= tamanho da lista " + produtos.size());
            return;
        }

        Produto produto = produtos.get(position);
        Log.d(TAG, "📱 Produto: " + produto.getNome() + " (ID: " + produto.getId() + ")");

        holder.textNome.setText(produto.getNome());
        holder.textPreco.setText("R$ " + String.format("%.2f", produto.getPreco()));
        holder.textCategoria.setText(produto.getCategoria());

        holder.buttonDelete.setOnClickListener(v -> {
            Log.d(TAG, "🗑️ Botão delete clicado para: " + produto.getNome());
            if (listener != null) {
                listener.onDeleteClick(produto);
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = produtos.size();
        Log.v(TAG, "📊 getItemCount() retornando: " + count);
        return count;
    }

    public void updateProdutos(List<Produto> newProdutos) {
        Log.d(TAG, "");
        Log.d(TAG, "🔄 ===== UPDATE PRODUTOS INICIADO =====");
        Log.d(TAG, "📊 Produtos ANTES: " + produtos.size());
        Log.d(TAG, "📊 Produtos NOVOS: " + (newProdutos != null ? newProdutos.size() : "NULL"));

        if (newProdutos == null) {
            Log.e(TAG, "❌ Lista nova é NULL! Abortando update.");
            return;
        }

        // Imprimir produtos atuais
        Log.d(TAG, "📋 Lista ATUAL:");
        for (int i = 0; i < produtos.size(); i++) {
            Log.d(TAG, "  " + i + ": " + produtos.get(i).getNome() + " (ID: " + produtos.get(i).getId() + ")");
        }

        // Imprimir produtos novos
        Log.d(TAG, "📋 Lista NOVA:");
        for (int i = 0; i < newProdutos.size(); i++) {
            Log.d(TAG, "  " + i + ": " + newProdutos.get(i).getNome() + " (ID: " + newProdutos.get(i).getId() + ")");
        }

        this.produtos.clear();
        Log.d(TAG, "🗑️ Lista limpa. Tamanho: " + produtos.size());

        this.produtos.addAll(newProdutos);
        Log.d(TAG, "➕ Produtos adicionados. Tamanho: " + produtos.size());

        Log.d(TAG, "🔔 Chamando notifyDataSetChanged()...");
        notifyDataSetChanged();
        Log.d(TAG, "✅ notifyDataSetChanged() chamado!");

        Log.d(TAG, "🔄 ===== UPDATE PRODUTOS CONCLUÍDO =====");
        Log.d(TAG, "");
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNome, textPreco, textCategoria;
        Button buttonDelete;

        ViewHolder(View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.textNome);
            textPreco = itemView.findViewById(R.id.textPreco);
            textCategoria = itemView.findViewById(R.id.textCategoria);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            Log.v("ProdutoAdapter", "🏗️ ViewHolder criado");
        }
    }
}