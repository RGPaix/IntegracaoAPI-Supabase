package com.example.integraoapisupabase;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements ProdutoAdapter.OnProdutoClickListener {

    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private ProdutoAdapter adapter;
    private List<Produto> produtos = new ArrayList<>();
    private ApiService apiService;

    private EditText editNome, editPreco, editCategoria;
    private Button buttonAdicionar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "🚀 ===== APP INICIADO =====");

        initViews();
        setupRecyclerView();
        setupApiService();

        buttonAdicionar.setOnClickListener(v -> adicionarProduto());

        // Carregar produtos iniciais
        Log.d(TAG, "📱 Carregando produtos iniciais...");
        carregarProdutos();
    }

    private void initViews() {
        Log.d(TAG, "🔧 Inicializando views...");
        recyclerView = findViewById(R.id.recyclerView);
        editNome = findViewById(R.id.editNome);
        editPreco = findViewById(R.id.editPreco);
        editCategoria = findViewById(R.id.editCategoria);
        buttonAdicionar = findViewById(R.id.buttonAdicionar);
        Log.d(TAG, "✅ Views inicializadas");
    }

    private void setupRecyclerView() {
        Log.d(TAG, "🔧 Configurando RecyclerView...");
        Log.d(TAG, "📊 Lista inicial tem " + produtos.size() + " produtos");

        adapter = new ProdutoAdapter(produtos, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Log.d(TAG, "✅ RecyclerView configurado");
    }

    private void setupApiService() {
        Log.d(TAG, "🔧 Configurando ApiService...");
        apiService = ApiClient.getApiService();
        Log.d(TAG, "✅ ApiService configurado");
    }

    private void carregarProdutos() {
        Log.d(TAG, "");
        Log.d(TAG, "🌐 ===== CARREGANDO PRODUTOS =====");

        String authHeader = "Bearer " + ApiClient.API_KEY;
        Log.d(TAG, "🔑 Auth header configurado");

        Call<List<Produto>> call = apiService.getProdutos(ApiClient.API_KEY, authHeader);
        Log.d(TAG, "📞 Fazendo chamada GET...");

        call.enqueue(new Callback<List<Produto>>() {
            @Override
            public void onResponse(Call<List<Produto>> call, Response<List<Produto>> response) {
                Log.d(TAG, "");
                Log.d(TAG, "📨 ===== RESPOSTA GET RECEBIDA =====");
                Log.d(TAG, "📊 Código: " + response.code());
                Log.d(TAG, "✅ Sucesso: " + response.isSuccessful());
                Log.d(TAG, "📦 Body é null: " + (response.body() == null));

                if (response.isSuccessful() && response.body() != null) {
                    List<Produto> produtosRecebidos = response.body();
                    Log.d(TAG, "📊 Produtos recebidos do servidor: " + produtosRecebidos.size());

                    // Imprimir cada produto recebido
                    Log.d(TAG, "📋 Lista completa recebida:");
                    for (int i = 0; i < produtosRecebidos.size(); i++) {
                        Produto p = produtosRecebidos.get(i);
                        Log.d(TAG, "  " + i + ": " + p.getNome() + " (ID: " + p.getId() + ") - R$ " + p.getPreco());
                    }

                    // Verificar lista local ANTES
                    Log.d(TAG, "📊 Lista local ANTES: " + produtos.size() + " produtos");

                    produtos.clear();
                    produtos.addAll(produtosRecebidos);

                    // Verificar lista local DEPOIS
                    Log.d(TAG, "📊 Lista local DEPOIS: " + produtos.size() + " produtos");

                    // Chamar update do adapter
                    Log.d(TAG, "🔄 Chamando adapter.updateProdutos()...");
                    adapter.updateProdutos(new ArrayList<>(produtosRecebidos)); // Nova lista para evitar referência
                    Log.d(TAG, "✅ adapter.updateProdutos() concluído");

                    // Verificar estado do adapter
                    Log.d(TAG, "📊 Adapter agora tem: " + adapter.getItemCount() + " itens");

                    String mensagem = "✅ " + produtosRecebidos.size() + " produtos carregados";
                    Toast.makeText(MainActivity.this, mensagem, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "🍞 Toast exibido: " + mensagem);

                } else {
                    Log.e(TAG, "❌ Erro na resposta ou body null");
                    Log.e(TAG, "📊 Código: " + response.code());
                    Toast.makeText(MainActivity.this, "Erro ao carregar produtos", Toast.LENGTH_SHORT).show();
                }

                Log.d(TAG, "📨 ===== FIM RESPOSTA GET =====");
                Log.d(TAG, "");
            }

            @Override
            public void onFailure(Call<List<Produto>> call, Throwable t) {
                Log.e(TAG, "");
                Log.e(TAG, "💥 ===== FALHA NA REQUISIÇÃO GET =====");
                Log.e(TAG, "❌ Erro: " + t.getMessage());
                Log.e(TAG, "💥 ===== FIM FALHA GET =====");
                Log.e(TAG, "");

                Toast.makeText(MainActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void adicionarProduto() {
        String nome = editNome.getText().toString().trim();
        String precoStr = editPreco.getText().toString().trim();
        String categoria = editCategoria.getText().toString().trim();

        Log.d(TAG, "");
        Log.d(TAG, "➕ ===== ADICIONANDO PRODUTO =====");
        Log.d(TAG, "📝 Nome: '" + nome + "'");
        Log.d(TAG, "💰 Preço: '" + precoStr + "'");
        Log.d(TAG, "🏷️ Categoria: '" + categoria + "'");

        if (nome.isEmpty() || precoStr.isEmpty() || categoria.isEmpty()) {
            Log.w(TAG, "⚠️ Campos vazios detectados");
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double preco = Double.parseDouble(precoStr);
            Log.d(TAG, "💰 Preço convertido: " + preco);

            Map<String, Object> novoProduto = new HashMap<>();
            novoProduto.put("nome", nome);
            novoProduto.put("preco", preco);
            novoProduto.put("categoria", categoria);

            Log.d(TAG, "📦 Objeto criado: " + novoProduto);

            String authHeader = "Bearer " + ApiClient.API_KEY;
            Log.d(TAG, "🔑 Auth header configurado para POST");

            Call<Void> call = apiService.createProduto(ApiClient.API_KEY, authHeader, novoProduto);
            Log.d(TAG, "📞 Fazendo chamada POST...");

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {  // ← Void aqui também
                    Log.d(TAG, "");
                    Log.d(TAG, "📨 ===== RESPOSTA POST RECEBIDA =====");
                    Log.d(TAG, "📊 Código: " + response.code());
                    Log.d(TAG, "✅ Sucesso: " + response.isSuccessful());

                    if (response.isSuccessful()) {
                        Log.d(TAG, "🎉 Produto criado com sucesso!");

                        // ❌ REMOVER ESTAS LINHAS (não tem mais response.body())
                        // if (response.body() != null) {
                        //     Produto produtoCriado = response.body();
                        //     Log.d(TAG, "📱 Produto criado: " + produtoCriado.getNome());
                        // }

                        Toast.makeText(MainActivity.this, "✅ Produto '" + nome + "' adicionado!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "🧹 Limpando campos...");
                        limparCampos();

                        Log.d(TAG, "🔄 Iniciando recarregamento da lista...");
                        carregarProdutos();

                    } else {
                        Log.e(TAG, "❌ Erro ao criar produto - Código: " + response.code());
                        Toast.makeText(MainActivity.this, "❌ Erro ao adicionar: " + response.code(), Toast.LENGTH_SHORT).show();
                    }

                    Log.d(TAG, "📨 ===== FIM RESPOSTA POST =====");
                    Log.d(TAG, "");
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {  // ← Void aqui também
                    Log.e(TAG, "");
                    Log.e(TAG, "💥 ===== FALHA NA REQUISIÇÃO POST =====");
                    Log.e(TAG, "❌ Erro: " + t.getMessage());
                    Log.e(TAG, "💥 ===== FIM FALHA POST =====");
                    Log.e(TAG, "");

                    Toast.makeText(MainActivity.this, "🌐 Erro de conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (NumberFormatException e) {
            Log.e(TAG, "❌ Erro ao converter preço: " + e.getMessage());
            Toast.makeText(this, "⚠️ Preço inválido! Use formato: 29.90", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteClick(Produto produto) {
        Log.d(TAG, "");
        Log.d(TAG, "🗑️ ===== EXCLUINDO PRODUTO =====");
        Log.d(TAG, "📱 Produto: " + produto.getNome() + " (ID: " + produto.getId() + ")");

        String authHeader = "Bearer " + ApiClient.API_KEY;

        Call<Void> call = apiService.deleteProduto(ApiClient.API_KEY, authHeader, "eq." + produto.getId());
        Log.d(TAG, "📞 Fazendo chamada DELETE...");

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "📨 Resposta DELETE - Código: " + response.code());

                if (response.isSuccessful()) {
                    Log.d(TAG, "🎉 Produto excluído com sucesso!");
                    Toast.makeText(MainActivity.this, "Produto removido!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "🔄 Recarregando lista após exclusão...");
                    carregarProdutos();
                } else {
                    Log.e(TAG, "❌ Erro ao excluir - Código: " + response.code());
                    Toast.makeText(MainActivity.this, "Erro ao remover produto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "💥 Falha DELETE: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void limparCampos() {
        editNome.setText("");
        editPreco.setText("");
        editCategoria.setText("");
        Log.d(TAG, "🧹 Campos limpos");
    }
}