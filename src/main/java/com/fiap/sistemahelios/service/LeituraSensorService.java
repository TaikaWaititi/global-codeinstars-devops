package com.fiap.sistemahelios.service;

import com.fiap.sistemahelios.dto.request.LeituraSensorRequestDTO;
import com.fiap.sistemahelios.dto.response.LeituraSensorResponseDTO;
import com.fiap.sistemahelios.exception.RecursoNaoEncontradoException;
import com.fiap.sistemahelios.model.LeituraSensor;
import com.fiap.sistemahelios.model.Sensor;
import com.fiap.sistemahelios.repository.LeituraSensorRepository;
import com.fiap.sistemahelios.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeituraSensorService {

    private final LeituraSensorRepository leituraSensorRepository;
    private final SensorRepository sensorRepository;

    @Autowired
    public LeituraSensorService(LeituraSensorRepository leituraSensorRepository, SensorRepository sensorRepository) {
        this.leituraSensorRepository = leituraSensorRepository;
        this.sensorRepository = sensorRepository;
    }

    @Transactional
    public LeituraSensorResponseDTO salvar (LeituraSensorRequestDTO requestDTO) {

        Sensor sensor = sensorRepository.findById(requestDTO.idSensor())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sensor não encontrado com ID: " + requestDTO.idSensor()));

        LeituraSensor leituraSensor = new LeituraSensor();
        leituraSensor.setSensor(sensor);
        leituraSensor.setValorLeitura(requestDTO.valorLeitura());
        leituraSensor.setDataHoraLeitura(requestDTO.dataHoraLeitura());
        leituraSensor.setStatusLeitura(requestDTO.statusLeitura());

        LeituraSensor leituraSensorSalvo = leituraSensorRepository.save(leituraSensor);

        return LeituraSensorResponseDTO.fromEntity(leituraSensorSalvo);
    }

    @Transactional(readOnly = true)
    public Page<LeituraSensorResponseDTO> listarTodos(Pageable pageable) {
        Page<LeituraSensor> leituraSensores = leituraSensorRepository.findAll(pageable);

        return leituraSensores.map(LeituraSensorResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public LeituraSensorResponseDTO buscarPorId(Long id) {
        LeituraSensor leituraSensor = leituraSensorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("LeituraSensor não encontrado com ID: " + id));
        return LeituraSensorResponseDTO.fromEntity(leituraSensor);
    }

    @Transactional(readOnly = true)
    public Page<LeituraSensorResponseDTO> buscarLeiturasPorSensor(Long idSensor, Pageable pageable) {
        sensorRepository.findById(idSensor)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sensor não encontrado com ID: " + idSensor));

        return leituraSensorRepository.findBySensor_Id(idSensor, pageable)
                .map(LeituraSensorResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<LeituraSensorResponseDTO> listarStatusSensores(Pageable pageable) {

        return leituraSensorRepository.findAll(pageable)
                .map(LeituraSensorResponseDTO::fromEntity);
    }

    @Transactional
    public LeituraSensorResponseDTO atualizar(Long id, LeituraSensorRequestDTO requestDTO) {
        LeituraSensor leituraSensorExistente = leituraSensorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("LeituraSensor não encontrado com ID: " + id));

        Sensor sensor = sensorRepository.findById(requestDTO.idSensor())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sensor não encontrado com ID: " + requestDTO.idSensor()));

        leituraSensorExistente.setSensor(sensor);
        leituraSensorExistente.setStatusLeitura(requestDTO.statusLeitura());
        leituraSensorExistente.setDataHoraLeitura(requestDTO.dataHoraLeitura());
        leituraSensorExistente.setStatusLeitura(requestDTO.statusLeitura());

        LeituraSensor leituraSensorAtualizado = leituraSensorRepository.save(leituraSensorExistente);

        return LeituraSensorResponseDTO.fromEntity(leituraSensorAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        if (!leituraSensorRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("LeituraSensor não encontrado com ID: " + id);
        }
        leituraSensorRepository.deleteById(id);
    }


}
