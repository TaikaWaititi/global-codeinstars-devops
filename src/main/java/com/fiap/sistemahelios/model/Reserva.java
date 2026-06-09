package com.fiap.sistemahelios.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "reservas")
@Schema(
        name = "Reserva",
        description = "Representa uma reserva no sistema Helios"
)
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "ID único da reserva",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ocupante", nullable = false)
    @NotNull(message = "O ID do ocupante é obrigatório")
    private Ocupante ocupante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modulo", nullable = false)
    @NotNull(message = "O ID do modulo é obrigatório")
    private ModuloHabitacional modulo;

    @NotNull(message = "Data de início da reserva é obrigatória")
    @Column(name = "data_inicio")
    @Schema(
            description = "Data de início da reserva do usuário",
            example = "2026-05-04"
    )
    private LocalDate dataInicio;

    @NotNull(message = "Data de fim da reserva é obrigatória")
    @Column(name = "data_fim")
    @Schema(
            description = "Data de fim da reserva do usuário",
            example = "2026-05-14"
    )
    private LocalDate dataFim;


    @NotBlank(message = "Status da reserva é obrigatório")
    @Column(name = "status_reserva", nullable = false)
    @Schema(
            description = "Indica se a reserva ainda está ativa sistema",
            example = "Cancelada"
    )
    private String statusReserva;

    public Reserva() {
    }

    public Reserva(Ocupante ocupante, ModuloHabitacional modulo, LocalDate dataInicio, LocalDate dataFim, String statusReserva) {
        this.ocupante = ocupante;
        this.modulo = modulo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.statusReserva = statusReserva;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ocupante getOcupante() {
        return ocupante;
    }

    public void setOcupante(Ocupante ocupante) {
        this.ocupante = ocupante;
    }

    public ModuloHabitacional getModulo() {
        return modulo;
    }

    public void setModulo(ModuloHabitacional modulo) {
        this.modulo = modulo;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public String getStatusReserva() {
        return statusReserva;
    }

    public void setStatusReserva(String statusReserva) {
        this.statusReserva = statusReserva;
    }
}
