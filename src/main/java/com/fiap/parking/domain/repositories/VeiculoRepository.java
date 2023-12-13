package com.fiap.parking.domain.repositories;

import com.fiap.parking.domain.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo,String> {
    @Query("SELECT v FROM Veiculo v WHERE v.condutor.cpf = :cpf")
    List<Veiculo> findByCondutorCpf(@Param("cpf") String cpf);

}
