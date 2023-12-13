package com.fiap.parking.domain.repositories;

import com.fiap.parking.domain.model.Estacionamento;
import com.fiap.parking.domain.model.StatusEstacionamento;
import com.fiap.parking.domain.model.TipoTempo;
import com.fiap.parking.domain.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EstacionamentoRepository extends JpaRepository<Estacionamento, UUID> {
    @Query("SELECT v FROM Estacionamento v WHERE v.status = :status")
    List<Estacionamento> findByStatus(@Param("status") StatusEstacionamento status);

    @Query("SELECT v FROM Estacionamento v WHERE v.status = :status and v.tipoTempo = :tipoTempo")
    Optional<Estacionamento> findByStatusAndTipoTempo(@Param("status") StatusEstacionamento status,
                                                      @Param("tipoTempo") TipoTempo tipoTempo);
}
