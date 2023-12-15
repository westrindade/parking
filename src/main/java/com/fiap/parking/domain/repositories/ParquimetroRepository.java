package com.fiap.parking.domain.repositories;

import com.fiap.parking.domain.model.Parquimetro;
import com.fiap.parking.domain.model.StatusParquimetro;
import com.fiap.parking.domain.model.TipoParquimetro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParquimetroRepository extends JpaRepository<Parquimetro, UUID> {
    @Query("SELECT v FROM Parquimetro v WHERE v.status = :status")
    List<Parquimetro> findByStatus(@Param("status") StatusParquimetro status);

    @Query("SELECT v FROM Parquimetro v WHERE v.status = :status and v.tipoParquimetro = :tipoParquimetro")
    Optional<Parquimetro> findByStatusAndTipoParquimetro(@Param("status") StatusParquimetro status,
                                                        @Param("tipoParquimetro") TipoParquimetro tipoParquimetro);
}
