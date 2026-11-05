package br.ufba.matc76.chamado.controller;

import br.ufba.matc76.chamado.model.Chamado;
import br.ufba.matc76.chamado.model.ChamadoCreate;
import br.ufba.matc76.chamado.model.Prioridade;
import br.ufba.matc76.chamado.model.Status;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/chamados")
public class ChamadoController {
    private final List<Chamado> ChamadosList = new ArrayList<>(
            List.of(
                new Chamado(
                    1,
                    "Na minha maquina não funciona",
                    "Micael Macedo",
                    "TI",
                    Prioridade.BAIXA,
                    Status.ABERTO
                ),
                new Chamado(
                    2,
                    "Erro no sistema de ponto",
                    "Carla",
                    "RH",
                    Prioridade.BAIXA,
                    Status.RESOLVIDO
                ),
                new Chamado(
                    3,
                    "Erro no processo de cadastro",
                    "Rafael Rocha",
                    "TI",
                    Prioridade.BAIXA,
                    Status.EM_ANDAMENTO
                ),
                new Chamado(
                    4,
                    "Na minha maquina não funciona",
                    "Micael Macedo",
                    "TI",
                    Prioridade.BAIXA,
                    Status.ABERTO
                ),
                new Chamado(
                    5,
                    "Na minha maquina não funciona",
                    "Micael Macedo",
                    "TI",
                    Prioridade.BAIXA,
                    Status.ABERTO
                ),
                new Chamado(
                    6,
                    "Na minha maquina não funciona",
                    "Micael Macedo",
                    "TI",
                    Prioridade.BAIXA,
                    Status.ABERTO
                ),
                new Chamado(
                    7,
                    "Na minha maquina não funciona",
                    "Micael Macedo",
                    "TI",
                    Prioridade.BAIXA,
                    Status.ABERTO
                ),
                new Chamado(
                    8,
                    "Na minha maquina não funciona",
                    "Micael Macedo",
                    "TI",
                    Prioridade.BAIXA,
                    Status.ABERTO
                ),
                new Chamado(
                    9,
                    "Na minha maquina não funciona",
                    "Micael Macedo",
                    "TI",
                    Prioridade.BAIXA,
                    Status.ABERTO
                ),
                new Chamado(
                    10,
                    "Na minha maquina não funciona",
                    "Micael Macedo",
                    "TI",
                    Prioridade.BAIXA,
                    Status.ABERTO
                ),
                new Chamado(
                    1,
                    "Na minha maquina não funciona",
                    "Micael Macedo",
                    "TI",
                    Prioridade.BAIXA,
                    Status.ABERTO
                )
            )
    );

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Chamado cadastrar(@RequestBody ChamadoCreate body) {
        int id = ChamadosList.size() + 1;

        Chamado data = new Chamado(
                id,
                body.titulo(),
                body.solicitante(),
                body.setor(),
                body.prioridade(),
                body.status()
        );

        ChamadosList.add(data);


        return data;
    }

    @GetMapping("/{id}")
    public Chamado buscar(@PathVariable int id) {
        return ChamadosList.stream()
                .filter(c -> c.id == id)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("")
    public List<Chamado> listar() {return ChamadosList;}
}
