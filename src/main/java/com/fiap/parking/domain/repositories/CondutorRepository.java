package com.fiap.parking.domain.repositories;

import com.fiap.parking.domain.model.Condutor;
import com.fiap.parking.domain.model.Parquimetro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CondutorRepository extends JpaRepository<Condutor,String> {
}
