package com.example.apiestoque.services;

import com.example.apiestoque.models.Produto;
import com.example.apiestoque.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutosService {
    private  final ProdutoRepository produtoRepository;

    @Autowired
    public ProdutosService(ProdutoRepository produtoRepository){
        this.produtoRepository=produtoRepository;
    }
    public List<Produto> buscarTodosOsProdutos(){
        return produtoRepository.findAll();
    }
    public Produto salvarProduto(Produto produto){
        return produtoRepository.save(produto);
    }

    public Produto buscarProdutoPorId(Long id){
        return produtoRepository.findById(id).orElseThrow(()->
                new RuntimeException("Produto não encontrado"));
    }
    public Produto excluirProduto(Long id){
        Optional <Produto> prod = produtoRepository.findById(id);
        if (prod.isPresent()){
            produtoRepository.deleteById(id);
            return prod.get();
        }
        return null;
    }

    public List<Produto> encotrarPorNomeEPreco(String nome, BigDecimal preco){
        return produtoRepository.findByNomeLikeIgnoreCaseAndPrecoLessThan(nome,preco);
    }
}
