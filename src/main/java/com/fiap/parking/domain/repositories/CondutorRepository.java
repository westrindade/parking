package com.fiap.parking.domain.repositories;

import com.fiap.parking.domain.model.Condutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CondutorRepository extends JpaRepository<Condutor,String> {
}
