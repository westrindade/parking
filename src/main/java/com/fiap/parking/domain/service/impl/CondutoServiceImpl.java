package com.fiap.parking.domain.service.impl;

import com.fiap.parking.domain.model.Condutor;
import com.fiap.parking.domain.model.TipoPagamento;
import com.fiap.parking.domain.model.Veiculo;
import com.fiap.parking.domain.repositories.CondutorRepository;
import com.fiap.parking.domain.dto.CondutorDTO;
import com.fiap.parking.domain.service.CondutorService;
import com.fiap.parking.utils.Utils;
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
    public List<CondutorDTO> findAll() {

        var condutor = this.condutorRepository.findAll();

        return condutor.stream().map(this::toCondutorDTO).collect(Collectors.toList());

    }

    @Override
    public CondutorDTO findByCpf(String cpf) {
        var condutor =  this.condutorRepository.findById(cpf)
                .orElseThrow( () -> new IllegalArgumentException(Utils.getMessage("condutor.nao.encontrado")) );
        return this.toCondutorDTO(condutor);
    }

    @Override
    public CondutorDTO  save(CondutorDTO condutorDTO) {
        final Condutor condutor = toCondutor(condutorDTO);
//        condutor.setTesteVeiculo(condutor.getVeiculos());

        List<Veiculo> veiculos = new ArrayList<>();
        for (Veiculo veiculo : condutorDTO.veiculos()) {
            veiculo.setCondutor(condutor);
            veiculos.add(veiculo);
        }
        condutor.setVeiculos(veiculos);

        return toCondutorDTO(this.condutorRepository.save(condutor));
    }

    @Override
    public void savePayment(String cpf, String tipoPagamento) {

        String tipoPagamentoUpperCase = tipoPagamento.toUpperCase();
        if (Arrays.stream(TipoPagamento.values())
                .noneMatch(enumValue -> enumValue.name().equals(tipoPagamentoUpperCase))) {
            throw new IllegalArgumentException("Tipo de pagamento inválido: " + tipoPagamento);
        }
        var condutor =  this.condutorRepository.findById(cpf)
                .orElseThrow( () -> new IllegalArgumentException(Utils.getMessage("condutor.nao.encontrado")) );

        condutor.setTipoPagamentoPadrao(TipoPagamento.valueOf(tipoPagamento.toUpperCase()));
        this.condutorRepository.save(condutor);

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
                condutorDTO.cep(),
                condutorDTO.veiculos()
        );
    }
}
