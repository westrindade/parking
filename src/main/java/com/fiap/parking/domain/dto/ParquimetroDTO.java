package com.fiap.parking.domain.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiap.parking.domain.model.StatusParquimetro;

import com.fiap.parking.domain.model.TipoParquimetro;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ParquimetroDTO {
    
        @Getter @Setter
        protected UUID id;
        @Getter @Setter
        protected @NotNull String veiculo;
        @Getter @Setter
        protected @NotNull String condutor;
        @Getter @Setter
        protected String longitude;
        @Getter @Setter
        protected String latitude;
        @Getter @Setter
        protected BigDecimal valorHora;
        @Getter @Setter
        protected BigDecimal valorTotal;
        @Getter @Setter
        protected StatusParquimetro status;
        @Getter @Setter
        protected TipoParquimetro tipoParquimetro;

        protected List<PeriodoDTO> periodos;

        public ParquimetroDTO(UUID id, @NotNull String veiculo, @NotNull String condutor, String longitude,
                        String latitude, BigDecimal valorHora, BigDecimal valorTotal, StatusParquimetro status,
                        List<PeriodoDTO> periodos, TipoParquimetro tipoParquimetro) {
                this.id = id;
                this.veiculo = veiculo;
                this.condutor = condutor;
                this.longitude = longitude;
                this.latitude = latitude;
                this.valorHora = valorHora;
                this.valorTotal = valorTotal;
                this.status = status;
                this.tipoParquimetro = tipoParquimetro;
                this.periodos = periodos;
        }

        
}
