package br.ufba.matc76.chamado.model;

public record ChamadoCreate (
    String titulo,
    String solicitante,
    String setor,
    Prioridade prioridade,
    Status status
) {}
