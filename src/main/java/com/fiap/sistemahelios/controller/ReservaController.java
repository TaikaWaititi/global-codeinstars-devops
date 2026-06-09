package com.fiap.sistemahelios.controller;

import com.fiap.sistemahelios.dto.request.ReservaRequestDTO;
import com.fiap.sistemahelios.dto.response.ReservaResponseDTO;
import com.fiap.sistemahelios.service.ReservaService;
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
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Endpoints para gerenciamento de reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @PostMapping
    @Operation(
            summary = "Criar reserva",
            description = "Cria uma nova reserva associada a um usuário."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Reserva criada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            )
    })
    public ResponseEntity<ReservaResponseDTO> criar(@Valid @RequestBody ReservaRequestDTO requestDTO) {
        ReservaResponseDTO novaReserva = reservaService.salvar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaReserva);
    }

    @GetMapping
    @Operation(
            summary = "Listar reservas",
            description = "Retorna uma lista completa de todas as reservas cadastrados."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de reservas retornada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaResponseDTO.class)
            )
    )
    public ResponseEntity<Page<ReservaResponseDTO>> listarTodos(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<ReservaResponseDTO> reservas = reservaService.listarTodos(pageable);
        return ResponseEntity.ok(reservas);
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar reserva por ID",
            description = "Retorna uma reserva específica baseado no ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reserva encontrada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reserva não encontrado"
            )
    })
    public ResponseEntity<ReservaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.buscarPorId(id));
    }


    //buscarReservasPorOcupante
    @GetMapping("/ocupante/{idOcupante}")
    @Operation(
            summary = "Buscar reservas por ocupante",
            description = "Retorna todos as reservas associados a um ocupante específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservas encontrados com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reserva não encontrada"
            )
    })
    public ResponseEntity<Page<ReservaResponseDTO>> buscarReservasPorOcupante(
            @PathVariable Long idOcupante,

            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<ReservaResponseDTO> reservasOcupante = reservaService.buscarReservasPorOcupante(idOcupante, pageable);
        return ResponseEntity.ok(reservasOcupante);
    }


    //buscarReservasPorModulo
    @GetMapping("/modulo/{idModulo}")
    @Operation(
            summary = "Buscar reservas por modulo",
            description = "Retorna todos as reservas associados a um modulo específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservas encontrados com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reserva não encontrada"
            )
    })
    public ResponseEntity<Page<ReservaResponseDTO>> buscarReservasPorModulo(
            @PathVariable Long idModulo,
            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<ReservaResponseDTO> reservasModulo = reservaService.buscarReservasPorModulo(idModulo, pageable);
        return ResponseEntity.ok(reservasModulo);
    }



    @PutMapping("/{id}")
    @Operation(

            summary = "Atualizar reserva",
            description = "Atualiza os dados de uma reserva existente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reserva atualizada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reserva não encontrada"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            )
    })
    public ResponseEntity<ReservaResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ReservaRequestDTO requestDTO) {
        ReservaResponseDTO reservaAtualizada = reservaService.atualizar(id, requestDTO);
        return ResponseEntity.ok(reservaAtualizada);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar reserva",
            description = "Remove uma reserva do sistema baseado no ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Reserva removida com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reserva não encontrada"
            )
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        reservaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
