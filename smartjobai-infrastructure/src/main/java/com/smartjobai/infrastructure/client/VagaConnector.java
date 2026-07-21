package com.smartjobai.infrastructure.client;

import com.smartjobai.core.entity.Vaga;

import java.util.List;

public interface VagaConnector {
    List<Vaga> buscarVagas(String termo, String localizacao);
    Vaga detalharVaga(String idExterno);
    void candidatar(Vaga vaga, String curriculoJson);
}
