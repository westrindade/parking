package com.fiap.parking.domain.repositories;

import com.fiap.parking.domain.model.Periodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PeriodoRepository extends JpaRepository<Periodo, UUID> {
}
