package com.fiap.sistemahelios.controller;

import com.fiap.sistemahelios.dto.request.LeituraSensorRequestDTO;
import com.fiap.sistemahelios.dto.response.LeituraSensorResponseDTO;
import com.fiap.sistemahelios.service.LeituraSensorService;
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
@RequestMapping("/api/leituras")
@Tag(name = "LeiturasSensor", description = "Endpoints para gerenciamento de leituras do sensor")
public class LeituraSensorController {

    @Autowired
    private LeituraSensorService leituraSensorService;

    @PostMapping
    @Operation(
            summary = "Criar leitura do sensor",
            description = "Cria um novo leitura do sensor no sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Leitura do sensor criado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            )
    })
    public ResponseEntity<LeituraSensorResponseDTO> criar(@Valid @RequestBody LeituraSensorRequestDTO requestDTO) {
        LeituraSensorResponseDTO novaLeituraSensor = leituraSensorService.salvar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaLeituraSensor);
    }

    @GetMapping
    @Operation(
            summary = "Listar leituras do sensor",
            description = "Retorna uma lista completa de todos os leituras do sensor cadastrados."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de leituras do sensor retornada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LeituraSensorResponseDTO.class)
            )
    )
    public ResponseEntity<Page<LeituraSensorResponseDTO>> listarTodos(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<LeituraSensorResponseDTO> leituras = leituraSensorService.listarTodos(pageable);
        return ResponseEntity.ok(leituras);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar leitura do sensor por ID",
            description = "Retorna um leitura do sensor específico baseado no ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "leitura do sensor encontrado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "leitura do sensor não encontrado"
            )
    })
    public ResponseEntity<LeituraSensorResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(leituraSensorService.buscarPorId(id));
    }


    //buscarLeiturasPorSensor
    @GetMapping("/sensor/{idSensor}")
    @Operation(
            summary = "Buscar leituras por ID do sensor",
            description = "Retorna leituras específicas baseado no ID do sensor"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Leituras encontrado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Leituras não encontrado"
            )
    })
    public ResponseEntity<Page<LeituraSensorResponseDTO>> buscarPorSensor(
            @PathVariable Long idSensor,
            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<LeituraSensorResponseDTO> leiturasSensor = leituraSensorService.buscarLeiturasPorSensor(idSensor, pageable);
        return ResponseEntity.ok(leiturasSensor);
    }


    // listarStatusSensores
    @GetMapping("/status")
    @Operation(
            summary = "Listar status dos sensores",
            description = "Retorna informações dos sensores com suas respectivas leituras"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Status dos sensores retornado com sucesso"
            )
    })
    public ResponseEntity<Page<LeituraSensorResponseDTO>> listarStatusSensores(

            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {

        Page<LeituraSensorResponseDTO> sensores =
                leituraSensorService.listarStatusSensores(pageable);

        return ResponseEntity.ok(sensores);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar leitura do sensor",
            description = "Atualiza os dados de um leitura do sensor existente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Leitura do sensor atualizado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Leitura do sensor não encontrado"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            )
    })
    public ResponseEntity<LeituraSensorResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody LeituraSensorRequestDTO requestDTO) {
        return ResponseEntity.ok(leituraSensorService.atualizar(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar leitura do sensor",
            description = "Remove um leitura do sensor do sistema baseado no ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "leitura do sensor removido com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "leitura do sensor não encontrado"
            )
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        leituraSensorService.deletar(id);
        return ResponseEntity.noContent().build();
    }



}
