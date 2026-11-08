package br.ufba.matc76.chamado.model;

public record ChamadoResumo (
    long abertos,
    long emAndamento,
    long resolvidos,
    long total
) {}
