package com.fiap.sistemahelios.controller;

import com.fiap.sistemahelios.dto.request.RegraAlertaRequestDTO;
import com.fiap.sistemahelios.dto.response.RegraAlertaResponseDTO;
import com.fiap.sistemahelios.service.RegraAlertaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/regras")
@Tag(name = "RegraAlertas", description = "Endpoints para gerenciamento de regras de alertas")
public class RegraAlertaController {

    @Autowired
    private RegraAlertaService regraAlertaService;

    @PostMapping
    @Operation(
            summary = "Criar regra de alerta",
            description = "Cria uma nova regra de alerta no sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Regra de alerta criado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            )
    })
    public ResponseEntity<RegraAlertaResponseDTO> criar(@Valid @RequestBody RegraAlertaRequestDTO requestDTO) {
        RegraAlertaResponseDTO novoOcupante = regraAlertaService.salvar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoOcupante);
    }

    @GetMapping
    @Operation(
            summary = "Listar regras de alerta",
            description = "Retorna uma lista completa de todas as regras de alerta cadastrados."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de regras de alerta retornada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegraAlertaResponseDTO.class)
            )
    )
    public ResponseEntity<Page<RegraAlertaResponseDTO>> listarTodos(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<RegraAlertaResponseDTO> regras = regraAlertaService.listarTodos(pageable);
        return ResponseEntity.ok(regras);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar regra de alerta por ID",
            description = "Retorna uma regra de alerta específico baseado no ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Regra de alerta encontrada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Regra de alerta não encontrada"
            )
    })
    public ResponseEntity<RegraAlertaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(regraAlertaService.buscarPorId(id));
    }


    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar regra de alerta",
            description = "Atualiza os dados de uma regra de alerta existente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Regra de alerta atualizado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Regra de alerta não encontrado"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            )
    })
    public ResponseEntity<RegraAlertaResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody RegraAlertaRequestDTO requestDTO) {
        return ResponseEntity.ok(regraAlertaService.atualizar(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar regra de alerta",
            description = "Remove um regra de alerta do sistema baseado no ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Regra de alerta removido com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Regra de alerta não encontrado"
            )
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        regraAlertaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
