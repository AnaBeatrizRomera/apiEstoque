package com.example.apiestoque.controllers;

import com.example.apiestoque.models.Produto;
import com.example.apiestoque.services.ProdutosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    private final ProdutosService produtosService;

    @Autowired
    public ProdutoController(ProdutosService produtosService) {
        this.produtosService = produtosService;
    }

    @GetMapping("/selecionar")
    @Operation(summary = "Lista todos os produtos", description = "Retorna uma lista de todos os produtos disponíveis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Lista de produtos retornada com sucesso",content = @Content(mediaType = "application/json",schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500",description = "Erro interno no servidor",content = @Content)
    })
    public ResponseEntity<List<Produto>> listarProdutos() {
        return ResponseEntity.ok(produtosService.buscarTodosOsProdutos());
    }


    @GetMapping("/selecionaNomeEPreco")
    @Operation(summary = "Lista os produtos por nome e preço", description = "Retorna uma lista de todos os produtos que tenham nome e preço passados como parametro"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Lista de produtos retordana com sucesso", content = @Content(mediaType = "application/json",schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "400",description = "Produto não encontrado",content = @Content)
    })
    public ResponseEntity<?> listarProdutos(@Parameter(description = "Nome do produto a ser procurado") @RequestParam  String nome,@Parameter(description = "Preço do produto a ser procurado") @RequestParam BigDecimal preco) {
        List<Produto>produtos=produtosService.encotrarPorNomeEPreco(nome,preco);
        if (produtos.isEmpty()){
            return ResponseEntity.status(400).body("Não foi possível encontrar o que foi requisitado");
        }else {
            return ResponseEntity.status(200).body(produtos);
        }
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handler(MethodArgumentNotValidException mven) {
        return mven.getBindingResult().getFieldError().getDefaultMessage();
    }

    @PostMapping("/inserir" )
    @Operation(summary = "Inserir produto",description = "Insere um produto no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Produto inserido com sucesso",content = @Content),
            @ApiResponse(responseCode = "400",description = "Campos com entrada inesperada: Erro no campo preço:preço deve ser númerico",content = @Content)
    })
    public ResponseEntity<String> inserirProduto(@Valid @RequestBody Produto produto, BindingResult resultado) {
        if(resultado.hasErrors()){
            for (FieldError error : resultado.getFieldErrors()){
                String campoComErro = error.getField();
                String mensagemDeErro = error.getDefaultMessage()+", ";
                return ResponseEntity.status(400).body("Erro no campo '" + campoComErro + "': " + mensagemDeErro);
            }
        }
//        if (produto.getQuantidadeEstoque()<0 || produto.getPreco()<0){
//            return ResponseEntity.status(400).body("Não foi possível inserir");
//        }
//        if(produto.getDescricao()!=null && produto.getNome()!=null &&
//        produto.getPreco()!=0 && produto.getQuantidadeEstoque()!=0){

//            if (produtoIns.getId()!=0){
//                return ResponseEntity.ok("Produto inserido com sucesso");
//            }else {
//                return ResponseEntity.status(500).body("Não foi possível inserir");
//            }
//        }else {
//
//        }
        Produto produtoIns = produtosService.salvarProduto(produto);
        return ResponseEntity.status(200).body("Produto inserido com sucesso");


    }

    @DeleteMapping("/excluir/{id}")
    @Operation(summary = "Excluir produto por ID",description = "Remove um produto do sistema pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Produto excluído com sucesso",content = @Content),
            @ApiResponse(responseCode = "404",description = "Produto não encontrado",content = @Content)
    })
    public ResponseEntity<String> excluirProduto(@Parameter(description = "ID do produto a ser excluído")@PathVariable Long id) {
        Produto produto = produtosService.buscarProdutoPorId(id);
        if (produto!=null){
            produtosService.excluirProduto(id);
            return ResponseEntity.ok("Produto excluído com sucesso");

        }else {
            return ResponseEntity.status(404).body("Este id não existe");
        }

    }

    @PutMapping("/atualizar/{id}")
    @Operation(summary = "Atualizar produto por ID",description = "Atualiza um produto no sistema pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Produto atualizado com sucesso",content = @Content),
            @ApiResponse(responseCode = "404",description = "Produto não encontrado",content = @Content)
    })
    public ResponseEntity<String> atualizarProduto(@Parameter(description = "ID do produto a ser atualizado")@PathVariable Long id, @RequestBody @Valid Produto produtoAtualizado) {
        Produto produtoExistente = produtosService.buscarProdutoPorId(id);

            if (produtoExistente!=null){
                Produto produto = produtoExistente;
                produto.setNome(produtoAtualizado.getNome());
                produto.setDescricao(produtoAtualizado.getDescricao());
                produto.setPreco(produtoAtualizado.getPreco());
                produto.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());
                produtosService.salvarProduto(produto);
                return ResponseEntity.ok("Produto atualizado com sucesso");
            }else {
                return ResponseEntity.notFound().build();
            }

    }

    @PatchMapping("/atualizarParcial/{id}")
    @Operation(summary = "Atualiza um produto parciamente por ID",description = "Atualiza apenas os campos que o usuário quer de um produto por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Produto alterado com sucesso",content = @Content),
            @ApiResponse(responseCode = "400",description = "Campo com valor inesperado",content = @Content)
    })
    public ResponseEntity<String> atualizarProdutoParcial(@Parameter(description = "ID do produto a ser alterado")@PathVariable Long id,@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Mapeamento de campos a serem atualizados com os novos valores",
            content = @Content(mediaType = "application/json",
            schema = @Schema(type = "object",example="{\"nome\":\"Novo nome\",\"descricao\":\"Nova descricao\",\"preco\":10.0,\"quantidadeEstoque\":100}"))
    ) @RequestBody Map<String, Object> updates) {
        Produto produtoExistente = produtosService.buscarProdutoPorId(id);
        String campo=null;
//        if (produtoExistente!=null) {
            Produto produto = produtoExistente;
        // Criar um Validator
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();


//        Atualiza apenas os campos que foram passados no corpo da requisição
            if (updates.containsKey("nome")) {
                campo="nome";
                produto.setNome((String) updates.get("nome"));
//            }else{
//                return ResponseEntity.status(400).body("Não foi possível atualizar pois campo está nulo");
            }
                if (updates.containsKey("descricao")) {
                    campo="descricao";
                    produto.setDescricao((String) updates.get("descricao"));
//            }else{
//                return ResponseEntity.status(400).body("Não foi possível atualizar pois campo está nulo");
                }
                if (updates.containsKey("preco")) {
                    campo="preco";
                    try{
                        produto.setPreco((Double) updates.get("preco"));
                    }catch (ClassCastException cne){
                        int precoInt = (Integer) updates.get("preco");
                        produto.setPreco(Double.parseDouble(String.valueOf(precoInt)));
                    }

//            }else{
//                return ResponseEntity.status(400).body("Não foi possível atualizar pois campo está nulo");
                }
                if (updates.containsKey("quantidadeEstoque")) {
                    campo="quantidadeEstoque";
                    produto.setQuantidadeEstoque((Integer) updates.get("quantidadeEstoque"));
//            }else{
//                return ResponseEntity.status(400).body("Não foi possível atualizar pois campo está nulo");
                }
        Set<ConstraintViolation<Produto>> violations = validator.validate(produtoExistente);
        if (!violations.isEmpty()) {
            // Se houver violações de validação, lance uma exceção ou trate conforme necessário
            StringBuilder errorMessage = new StringBuilder("Erros de validação:");
            for (ConstraintViolation<Produto> violation : violations) {
                errorMessage.append(" /  ").append(violation.getMessage());
//            throw new RuntimeException("Erro de validação no campo '" + campo + "': " + violations.iterator().next().getMessage());
            }
            return ResponseEntity.status(400).body(errorMessage.toString());
        }

                produtosService.salvarProduto(produto);


                return ResponseEntity.status(200).body("Produto alterado com sucesso");
            }

        }

//    }

