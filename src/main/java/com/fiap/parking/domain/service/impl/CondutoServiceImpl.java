package com.fiap.parking.domain.service.impl;

import com.fiap.parking.domain.model.Condutor;
import com.fiap.parking.domain.model.TipoPagamento;
import com.fiap.parking.domain.model.Veiculo;
import com.fiap.parking.domain.repositories.CondutorRepository;
import com.fiap.parking.domain.dto.CondutorDTO;
import com.fiap.parking.domain.service.CondutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CondutoServiceImpl implements CondutorService {

    @Autowired
    private CondutorRepository condutorRepository;

    @Override
    public ResponseEntity<?> findAll() {
        try {
            var condutor = this.condutorRepository.findAll();

            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(
                    condutor.stream().map(this::toCondutorDTO).collect(Collectors.toList())
            );
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> findByCpf(String cpf) {
        try {
            var condutor =  this.condutorRepository.findById(cpf)
                    .orElseThrow( () -> new IllegalArgumentException("Condutor não encontrado") );
            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(this.toCondutorDTO(condutor));
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> save(CondutorDTO condutorDTO) {
        Condutor condutor = toCondutor(condutorDTO);

        List<Veiculo> veiculos = new ArrayList<>();
        for (Veiculo veiculo : condutorDTO.veiculos()) {
            veiculo.setCondutor(condutor);
            veiculos.add(veiculo);
        }
        condutor.setVeiculos(veiculos);

        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(toCondutorDTO(this.condutorRepository.save(condutor)));
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (JpaSystemException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Atributo chave primaria não informado");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> savePayment(String cpf, String tipoPagamento) {

        try {

            String tipoPagamentoUpperCase = tipoPagamento.toUpperCase();
            if (Arrays.stream(TipoPagamento.values())
                    .noneMatch(enumValue -> enumValue.name().equals(tipoPagamentoUpperCase))) {
                throw new IllegalArgumentException("Tipo de pagamento inválido: " + tipoPagamento);
            }
            var condutor =  this.condutorRepository.findById(cpf)
                    .orElseThrow( () -> new IllegalArgumentException("Condutor não encontrado") );
            //Condutor condutor = toCondutor(this.findByCpf(cpf) );
            condutor.setTipoPagamentoPadrao(TipoPagamento.valueOf(tipoPagamento.toUpperCase()));

            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(this.condutorRepository.save(condutor));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    private CondutorDTO toCondutorDTO(Condutor condutor) {
        return new CondutorDTO(
                condutor.getCpf(),
                condutor.getNome(),
                condutor.getCelular(),
                condutor.getDataNascimento(),
                condutor.getTipoLogradouro(),
                condutor.getLogradouro(),
                condutor.getNroLogradouro(),
                condutor.getBairro(),
                condutor.getCidade(),
                condutor.getUf(),
                condutor.getCep(),
                condutor.getTipoPagamentoPadrao(),
                condutor.getVeiculos()
        );
    }

    private Condutor toCondutor(CondutorDTO condutorDTO) {
        return new Condutor(
                condutorDTO.cpf(),
                condutorDTO.nome(),
                condutorDTO.celular(),
                condutorDTO.dataNascimento(),
                condutorDTO.tipoLogradouro(),
                condutorDTO.logradouro(),
                condutorDTO.nroLogradouro(),
                condutorDTO.bairro(),
                condutorDTO.cidade(),
                condutorDTO.uf(),
                condutorDTO.cep()
        );
    }
}
