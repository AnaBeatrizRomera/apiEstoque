package com.example.apiestoque.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Schema(description = "Represente um produto no sistema")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do produto",example = "1234")
    private Long id;
    @NotNull(message = "O nome não pode ser nulo")
    @Size(min = 2, message = "O nome deve ter pelo menos 2 carcteres")
    @Schema(description = "Nome do produto",example = "Hamburguer de frango")
    private String nome;
    @Schema(description = "Descrição detalhada do produto" , example = "Hamburguer de frango congelado 500g")
    private String descricao;
    @NotNull(message = "O preço não pode ser nulo")
    @Min(value = 0, message = "O preço deve ser pelo menos 0")
    @Schema(description = "Preço do produto",example = "1999.99")
    private double preco;
    @NotNull(message = "A quantidade não pode ser nulo")
    @Min(value = 0, message = "A quantidade deve ser pelo menos 0")
    @Column(name="quantidadeestoque")
    @Schema(description = "Quantidade disponível em estoque", example = "50")
    private int quantidadeEstoque;

    public Produto() {
    }

    public Produto(Long id, String nome, String descricao, double preco, int quantidadeEstoque) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", preco=" + preco +
                ", quantidadEstoque=" + quantidadeEstoque +
                '}';
    }


}
