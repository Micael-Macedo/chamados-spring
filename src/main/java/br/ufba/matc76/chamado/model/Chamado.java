package br.ufba.matc76.chamado.model;

public class Chamado {
    public int id;
    public String titulo;
    public String solicitante;
    public String setor;
    public Prioridade prioridade;
    public Status status;

    public Chamado(int id, String titulo, String solicitante, String setor, Prioridade prioridade,Status status){
        this.id = id;
        this.titulo = titulo;
        this.solicitante = solicitante;
        this.setor = setor;
        this.prioridade = prioridade;
        this.status = status;
    }
}
