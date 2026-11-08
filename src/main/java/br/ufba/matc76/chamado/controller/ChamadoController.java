package br.ufba.matc76.chamado.controller;

import br.ufba.matc76.chamado.model.Chamado;
import br.ufba.matc76.chamado.model.ChamadoCreate;
import br.ufba.matc76.chamado.model.ChamadoResumo;
import br.ufba.matc76.chamado.model.Prioridade;
import br.ufba.matc76.chamado.model.Status;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/chamados")
public class ChamadoController {
    private final List<Chamado> chamadosList = new ArrayList<>();
    private final AtomicInteger geradorId = new AtomicInteger(0);

    public ChamadoController() {
        chamadosList.add(new Chamado(
            geradorId.incrementAndGet(),
            "Impressora não imprime",
            "Rafael Rocha",
            "Financeiro",
            Prioridade.ALTA,
            Status.ABERTO)
            );
        chamadosList.add(new Chamado(
            geradorId.incrementAndGet(),
            "Erro no sistema de ponto",
            "Micael Macedo",
            "RH",
            Prioridade.MEDIA,
            Status.RESOLVIDO)
            );
        chamadosList.add(new Chamado(
            geradorId.incrementAndGet(),
            "Erro no processo de cadastro",
            "Jaiana Santana",
            "TI",
            Prioridade.BAIXA,
            Status.ABERTO)
            );
        chamadosList.add(new Chamado(
            geradorId.incrementAndGet(),
            "Não funciona no meu computador",
            "Joselito Junior",
            "Financeiro",
            Prioridade.ALTA,
            Status.EM_ANDAMENTO)
            );
        chamadosList.add(new Chamado(
            geradorId.incrementAndGet(),
            "IMPRESSORA não imprime",
            "Rafael Rocha",
            "RH",
            Prioridade.MEDIA,
            Status.RESOLVIDO)
            );
        chamadosList.add(new Chamado(
            geradorId.incrementAndGet(),
            "impressora não imprime",
            "Micael Macedo",
            "TI",
            Prioridade.BAIXA,
            Status.ABERTO)
            );
        chamadosList.add(new Chamado(
            geradorId.incrementAndGet(),
            "Senha bloqueada no sistema",
            "Jaiana Santana",
            "Financeiro",
            Prioridade.ALTA,
            Status.RESOLVIDO)
            );
        chamadosList.add(new Chamado(
            geradorId.incrementAndGet(),
            "Sem acesso à rede Wi-Fi",
            "Joselito Junior",
            "RH",
            Prioridade.MEDIA,
            Status.EM_ANDAMENTO)
            );
        chamadosList.add(new Chamado(
            geradorId.incrementAndGet(),
            "Criar conta de e-mail para novo funcionário",
            "Rafael Rocha",
            "TI",
            Prioridade.BAIXA,
            Status.ABERTO)
            );
        chamadosList.add(new Chamado(
            geradorId.incrementAndGet(),
            "Rede fora do ar no setor",
            "Micael Macedo",
            "Gerencia",
            Prioridade.ALTA,
            Status.EM_ANDAMENTO)
            );                               
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Chamado cadastrar(@RequestBody ChamadoCreate body) {

        Chamado chamado = new Chamado(
                geradorId.incrementAndGet(),
                body.titulo(),
                body.solicitante(),
                body.setor(),
                body.prioridade(),
                body.status()
        );

        chamadosList.add(chamado);

        return chamado;
    }

    @GetMapping("/{id}")
    public Chamado buscar(@PathVariable int id) {
        return chamadosList.stream()
                .filter(c -> c.id == id)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public List<Chamado> listar() {return chamadosList;}

    @GetMapping("/setor")
    public List<Chamado> filtrarPorSetor(@RequestParam String nome) {
        return chamadosList.stream()
                .filter(c -> nome.equalsIgnoreCase(c.setor))
                .toList();
    }

    @GetMapping("/prioridade")
    public List<Chamado> filtrarPorPrioridade(@RequestParam String nivel) {
        Prioridade prioridade;
        try {
            prioridade = Prioridade.valueOf(nivel.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prioridade inválida: " + nivel);
        }

        return chamadosList.stream()
                .filter(c -> c.prioridade == prioridade)
                .toList();
    }

    @GetMapping("/criticos")
    public List<Chamado> listarCriticos() {
        return chamadosList.stream()
                .filter(c -> c.prioridade == Prioridade.ALTA && c.status != Status.RESOLVIDO)
                .toList();
    }

    @GetMapping("/resumo")
    public ChamadoResumo resumo() {
        return new ChamadoResumo(
                chamadosList.stream().filter(c -> c.status == Status.ABERTO).count(),
                chamadosList.stream().filter(c -> c.status == Status.EM_ANDAMENTO).count(),
                chamadosList.stream().filter(c -> c.status == Status.RESOLVIDO).count(),
                chamadosList.size()
        );
    }

    @GetMapping("/busca")
    public List<Chamado> buscarPorTitulo(@RequestParam String texto) {
        String termo = texto.toLowerCase();
        return chamadosList.stream()
                .filter(c -> c.titulo != null && c.titulo.toLowerCase().contains(termo))
                .toList();
    }
}
