# Testes e demonstração
 
Evidências das requisições realizadas na API de Gerenciamento de Chamados. Todas as requisições estão em [`requisicoes.http`](requisicoes.http) e podem ser reproduzidas com a extensão REST Client do VS Code.
 
> **Importante:** os testes devem ser executados **na ordem**, com a aplicação recém-iniciada. Os dados ficam em memória, então os IDs e as contagens dependem do estado inicial (10 chamados) e dos cadastros feitos durante a sequência.
 
## Visão geral
 
| # | Teste | Categoria | Requisição | Status |
|---|---|---|---|---|
| 1 | Listar todos os chamados | Consulta | `GET /chamados` | 200 OK |
| 2 | Consultar chamado por ID | Consulta | `GET /chamados/7` | 200 OK |
| 3 | Consultar chamado inexistente | Situação sem correspondência | `GET /chamados/99` | 404 Not Found |
| 4 | Filtrar por setor | Filtro | `GET /chamados/setor?nome=FINANCEIRO` | 200 OK |
| 5 | Filtrar por setor inexistente | Situação sem correspondência | `GET /chamados/setor?nome=marketing` | 200 OK |
| 6 | Filtrar por prioridade | Filtro | `GET /chamados/prioridade?nivel=alta` | 200 OK |
| 7 | Filtrar por prioridade inválida | Entrada inválida | `GET /chamados/prioridade?nivel=urgente` | 400 Bad Request |
| 8 | Buscar pelo título | Filtro | `GET /chamados/busca?texto=impressora` | 200 OK |
| 9 | Buscar texto sem correspondência | Situação sem correspondência | `GET /chamados/busca?texto=teclado` | 200 OK |
| 10 | Listar chamados críticos | Regra combinada | `GET /chamados/criticos` | 200 OK |
| 11 | Resumo por status | Resumo | `GET /chamados/resumo` | 200 OK |
| 12 | Cadastrar novo chamado (sem informar o status) | Cadastro | `POST /chamados` | 201 Created |
| 13 | Verificar o chamado cadastrado | Verificação | `GET /chamados/11` | 200 OK |
| 14 | Verificar o resumo após o cadastro | Verificação | `GET /chamados/resumo` | 200 OK |
| 15 | Verificar os críticos após o cadastro | Verificação | `GET /chamados/criticos` | 200 OK |
 
## Cobertura dos requisitos
 
| Exigência do enunciado | Testes |
|---|---|
| Consultas | 1, 2 |
| Filtros (setor, prioridade, título) | 4, 6, 8 |
| Situações sem correspondência | 3, 5, 9 |
| Regra combinada (`/criticos`) | 10 |
| Resumo por status (`/resumo`) | 11 |
| Cadastro | 12 |
| Verificação dos dados cadastrados | 13, 14, 15 |
| Busca sem diferenciar maiúsculas/minúsculas | 4, 8 |
| Entrada inválida | 7 |
 
## Dados iniciais
 
| ID | Título | Setor | Prioridade | Status |
|---|---|---|---|---|
| 1 | Impressora não imprime | Financeiro | ALTA | ABERTO |
| 2 | Erro no sistema de ponto | RH | MEDIA | RESOLVIDO |
| 3 | Erro no processo de cadastro | TI | BAIXA | ABERTO |
| 4 | Não funciona no meu computador | Financeiro | ALTA | EM_ANDAMENTO |
| 5 | IMPRESSORA não imprime | RH | MEDIA | RESOLVIDO |
| 6 | impressora não imprime | TI | BAIXA | ABERTO |
| 7 | Senha bloqueada no sistema | Financeiro | ALTA | RESOLVIDO |
| 8 | Sem acesso à rede Wi-Fi | RH | MEDIA | EM_ANDAMENTO |
| 9 | Criar conta de e-mail para novo funcionário | TI | BAIXA | ABERTO |
| 10 | Rede fora do ar no setor | Gerencia | ALTA | EM_ANDAMENTO |
 
---
 
## Teste 1 — Listar todos os chamados
 
**Categoria:** Consulta
 
**Requisição**
 
```http
GET http://localhost:8080/chamados
```
 
**Resposta**
 
```
HTTP/1.1 200 
Content-Type: application/json
```
 
```json
[
  {
    "id": 1,
    "titulo": "Impressora não imprime",
    "solicitante": "Rafael Rocha",
    "setor": "Financeiro",
    "prioridade": "ALTA",
    "status": "ABERTO"
  },
  {
    "id": 2,
    "titulo": "Erro no sistema de ponto",
    "solicitante": "Micael Macedo",
    "setor": "RH",
    "prioridade": "MEDIA",
    "status": "RESOLVIDO"
  },
  {
    "id": 3,
    "titulo": "Erro no processo de cadastro",
    "solicitante": "Jaiana Santana",
    "setor": "TI",
    "prioridade": "BAIXA",
    "status": "ABERTO"
  },
  {
    "id": 4,
    "titulo": "Não funciona no meu computador",
    "solicitante": "Joselito Junior",
    "setor": "Financeiro",
    "prioridade": "ALTA",
    "status": "EM_ANDAMENTO"
  },
  {
    "id": 5,
    "titulo": "IMPRESSORA não imprime",
    "solicitante": "Rafael Rocha",
    "setor": "RH",
    "prioridade": "MEDIA",
    "status": "RESOLVIDO"
  },
  {
    "id": 6,
    "titulo": "impressora não imprime",
    "solicitante": "Micael Macedo",
    "setor": "TI",
    "prioridade": "BAIXA",
    "status": "ABERTO"
  },
  {
    "id": 7,
    "titulo": "Senha bloqueada no sistema",
    "solicitante": "Jaiana Santana",
    "setor": "Financeiro",
    "prioridade": "ALTA",
    "status": "RESOLVIDO"
  },
  {
    "id": 8,
    "titulo": "Sem acesso à rede Wi-Fi",
    "solicitante": "Joselito Junior",
    "setor": "RH",
    "prioridade": "MEDIA",
    "status": "EM_ANDAMENTO"
  },
  {
    "id": 9,
    "titulo": "Criar conta de e-mail para novo funcionário",
    "solicitante": "Rafael Rocha",
    "setor": "TI",
    "prioridade": "BAIXA",
    "status": "ABERTO"
  },
  {
    "id": 10,
    "titulo": "Rede fora do ar no setor",
    "solicitante": "Micael Macedo",
    "setor": "Gerencia",
    "prioridade": "ALTA",
    "status": "EM_ANDAMENTO"
  }
]
```
 
**Explicação:** A API retorna um array JSON com os 10 chamados fictícios carregados no construtor do `ChamadoController`, com IDs de 1 a 10 gerados pelo `AtomicInteger`. O método `listar()` devolve a `List<Chamado>` e o Jackson a converte em array; os enums `Prioridade` e `Status` aparecem como texto (`"ALTA"`, `"ABERTO"`).
 
---
 
## Teste 2 — Consultar chamado por ID
 
**Categoria:** Consulta
 
**Requisição**
 
```http
GET http://localhost:8080/chamados/7
```
 
**Resposta**
 
```
HTTP/1.1 200 
Content-Type: application/json
```
 
```json
{
  "id": 7,
  "titulo": "Senha bloqueada no sistema",
  "solicitante": "Jaiana Santana",
  "setor": "Financeiro",
  "prioridade": "ALTA",
  "status": "RESOLVIDO"
}
```
 
**Explicação:** O `{id}` da URL é recebido via `@PathVariable`. O stream filtra o chamado com id 7 e o `findFirst()` o encontra. Como a busca é por um recurso específico, a resposta é um único objeto JSON, e não um array.
 
---
 
## Teste 3 — Consultar chamado inexistente
 
**Categoria:** Situação sem correspondência
 
**Requisição**
 
```http
GET http://localhost:8080/chamados/99
```
 
**Resposta**
 
```
HTTP/1.1 404 
Content-Type: application/json
```
 
```json
{
  "timestamp": "2026-11-08T22:41:53.511Z",
  "status": 404,
  "error": "Not Found",
  "message": "404 NOT_FOUND",
  "path": "/chamados/99"
}
```
 
**Explicação:** Não existe chamado com id 99. O `findFirst()` devolve um `Optional` vazio e o `orElseThrow` lança uma `ResponseStatusException` com `HttpStatus.NOT_FOUND`, que o Spring converte em 404. Por ser a busca de um recurso específico, a ausência resulta em 404, e não em lista vazia (compare com os testes 5 e 9).
 
---
 
## Teste 4 — Filtrar por setor
 
**Categoria:** Filtro
 
**Requisição**
 
```http
GET http://localhost:8080/chamados/setor?nome=FINANCEIRO
```
 
**Resposta**
 
```
HTTP/1.1 200 
Content-Type: application/json
```
 
```json
[
  {
    "id": 1,
    "titulo": "Impressora não imprime",
    "solicitante": "Rafael Rocha",
    "setor": "Financeiro",
    "prioridade": "ALTA",
    "status": "ABERTO"
  },
  {
    "id": 4,
    "titulo": "Não funciona no meu computador",
    "solicitante": "Joselito Junior",
    "setor": "Financeiro",
    "prioridade": "ALTA",
    "status": "EM_ANDAMENTO"
  },
  {
    "id": 7,
    "titulo": "Senha bloqueada no sistema",
    "solicitante": "Jaiana Santana",
    "setor": "Financeiro",
    "prioridade": "ALTA",
    "status": "RESOLVIDO"
  }
]
```
 
**Explicação:** O parâmetro `nome` é recebido via `@RequestParam` e comparado com `equalsIgnoreCase`. Mesmo informado em maiúsculas, o filtro encontra os três chamados cadastrados como "Financeiro", o que comprova que a comparação não diferencia maiúsculas de minúsculas.
 
---
 
## Teste 5 — Filtrar por setor inexistente
 
**Categoria:** Situação sem correspondência
 
**Requisição**
 
```http
GET http://localhost:8080/chamados/setor?nome=marketing
```
 
**Resposta**
 
```
HTTP/1.1 200 
Content-Type: application/json
```
 
```json
[]
```
 
**Explicação:** Nenhum chamado pertence ao setor Marketing. A consulta é válida, mas o filtro não encontra resultados, então a API retorna 200 com um array vazio. Diferente do teste 3, aqui se consulta uma coleção filtrada, e não um recurso específico.
 
---
 
## Teste 6 — Filtrar por prioridade
 
**Categoria:** Filtro
 
**Requisição**
 
```http
GET http://localhost:8080/chamados/prioridade?nivel=alta
```
 
**Resposta**
 
```
HTTP/1.1 200 
Content-Type: application/json
```
 
```json
[
  {
    "id": 1,
    "titulo": "Impressora não imprime",
    "solicitante": "Rafael Rocha",
    "setor": "Financeiro",
    "prioridade": "ALTA",
    "status": "ABERTO"
  },
  {
    "id": 4,
    "titulo": "Não funciona no meu computador",
    "solicitante": "Joselito Junior",
    "setor": "Financeiro",
    "prioridade": "ALTA",
    "status": "EM_ANDAMENTO"
  },
  {
    "id": 7,
    "titulo": "Senha bloqueada no sistema",
    "solicitante": "Jaiana Santana",
    "setor": "Financeiro",
    "prioridade": "ALTA",
    "status": "RESOLVIDO"
  },
  {
    "id": 10,
    "titulo": "Rede fora do ar no setor",
    "solicitante": "Micael Macedo",
    "setor": "Gerencia",
    "prioridade": "ALTA",
    "status": "EM_ANDAMENTO"
  }
]
```
 
**Explicação:** O valor `alta` é convertido para maiúsculas e transformado no enum com `Prioridade.valueOf`. São retornados os quatro chamados de prioridade ALTA, inclusive o 7, que está RESOLVIDO (compare com o teste 10).
 
---
 
## Teste 7 — Filtrar por prioridade inválida
 
**Categoria:** Entrada inválida
 
**Requisição**
 
```http
GET http://localhost:8080/chamados/prioridade?nivel=urgente
```
 
**Resposta**
 
```
HTTP/1.1 400 
Content-Type: application/json
```
 
```json
{
  "timestamp": "2026-11-08T22:45:54.732Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Prioridade inválida: urgente",
  "path": "/chamados/prioridade"
}
```
 
**Explicação:** `URGENTE` não existe no enum `Prioridade`, então o `valueOf` lança `IllegalArgumentException`. O bloco `catch` a converte em `ResponseStatusException` com `HttpStatus.BAD_REQUEST`. O erro é do cliente (valor não permitido), por isso 400, e não 404 nem 500. A mensagem aparece porque `spring.web.error.include-message=always` está ativo no `application.properties`.
 
---
 
## Teste 8 — Buscar pelo título
 
**Categoria:** Filtro
 
**Requisição**
 
```http
GET http://localhost:8080/chamados/busca?texto=impressora
```
 
**Resposta**
 
```
HTTP/1.1 200 
Content-Type: application/json
```
 
```json
[
  {
    "id": 1,
    "titulo": "Impressora não imprime",
    "solicitante": "Rafael Rocha",
    "setor": "Financeiro",
    "prioridade": "ALTA",
    "status": "ABERTO"
  },
  {
    "id": 5,
    "titulo": "IMPRESSORA não imprime",
    "solicitante": "Rafael Rocha",
    "setor": "RH",
    "prioridade": "MEDIA",
    "status": "RESOLVIDO"
  },
  {
    "id": 6,
    "titulo": "impressora não imprime",
    "solicitante": "Micael Macedo",
    "setor": "TI",
    "prioridade": "BAIXA",
    "status": "ABERTO"
  }
]
```
 
**Explicação:** O texto buscado e cada título são convertidos para minúsculas antes do `contains`. Por isso a busca encontra "Impressora", "IMPRESSORA" e "impressora", comprovando que a busca textual não diferencia maiúsculas de minúsculas. A busca verifica se o título contém o termo, e não se é igual a ele.
 
---
 
## Teste 9 — Buscar texto sem correspondência
 
**Categoria:** Situação sem correspondência
 
**Requisição**
 
```http
GET http://localhost:8080/chamados/busca?texto=teclado
```
 
**Resposta**
 
```
HTTP/1.1 200 
Content-Type: application/json
```
 
```json
[]
```
 
**Explicação:** Nenhum título contém "teclado". A busca é válida, mas sem resultados, então a API retorna 200 com array vazio.
 
---
 
## Teste 10 — Listar chamados críticos
 
**Categoria:** Regra combinada
 
**Requisição**
 
```http
GET http://localhost:8080/chamados/criticos
```
 
**Resposta**
 
```
HTTP/1.1 200 
Content-Type: application/json
```
 
```json
[
  {
    "id": 1,
    "titulo": "Impressora não imprime",
    "solicitante": "Rafael Rocha",
    "setor": "Financeiro",
    "prioridade": "ALTA",
    "status": "ABERTO"
  },
  {
    "id": 4,
    "titulo": "Não funciona no meu computador",
    "solicitante": "Joselito Junior",
    "setor": "Financeiro",
    "prioridade": "ALTA",
    "status": "EM_ANDAMENTO"
  },
  {
    "id": 10,
    "titulo": "Rede fora do ar no setor",
    "solicitante": "Micael Macedo",
    "setor": "Gerencia",
    "prioridade": "ALTA",
    "status": "EM_ANDAMENTO"
  }
]
```
 
**Explicação:** O filtro combina as duas condições com `&&`: prioridade igual a ALTA e status diferente de RESOLVIDO. Dos quatro chamados ALTA do teste 6, o 7 não aparece porque está RESOLVIDO. Usar "diferente de RESOLVIDO" inclui tanto ABERTO quanto EM_ANDAMENTO (o 4 e o 10).
 
---
 
## Teste 11 — Resumo por status
 
**Categoria:** Resumo
 
**Requisição**
 
```http
GET http://localhost:8080/chamados/resumo
```
 
**Resposta**
 
```
HTTP/1.1 200 
Content-Type: application/json
```
 
```json
{
  "abertos": 4,
  "emAndamento": 3,
  "resolvidos": 3,
  "totalGeral": 10
}
```
 
**Explicação:** Cada quantidade é calculada no momento da requisição, contando os chamados de cada status com `filter().count()`; o total vem de `size()` da lista. A soma dos três status (4 + 3 + 3) bate com o total geral (10).
 
---
 
## Teste 12 — Cadastrar novo chamado (sem informar o status)
 
**Categoria:** Cadastro
 
**Requisição**
 
```http
POST http://localhost:8080/chamados
Content-Type: application/json
 
{
  "titulo": "Servidor de arquivos fora do ar",
  "solicitante": "Jaiana Santana",
  "setor": "TI",
  "prioridade": "ALTA"
}
```
 
**Resposta**
 
```
HTTP/1.1 201 
Content-Type: application/json
```
 
```json
{
  "id": 11,
  "titulo": "Servidor de arquivos fora do ar",
  "solicitante": "Jaiana Santana",
  "setor": "TI",
  "prioridade": "ALTA",
  "status": "ABERTO"
}
```
 
**Explicação:** O corpo JSON é convertido pelo Jackson no record `ChamadoCreate` (`@RequestBody`), que não possui o campo `id`. A API gera o identificador com `incrementAndGet()` (id 11, já que os IDs de 1 a 10 foram usados no construtor) e responde 201 por causa de `@ResponseStatus(HttpStatus.CREATED)`. Como o status não foi informado, o chamado recebe ABERTO como valor padrão.
 
---
 
## Teste 13 — Verificar o chamado cadastrado
 
**Categoria:** Verificação
 
**Requisição**
 
```http
GET http://localhost:8080/chamados/11
```
 
**Resposta**
 
```
HTTP/1.1 200 
Content-Type: application/json
```
 
```json
{
  "id": 11,
  "titulo": "Servidor de arquivos fora do ar",
  "solicitante": "Jaiana Santana",
  "setor": "TI",
  "prioridade": "ALTA",
  "status": "ABERTO"
}
```
 
**Explicação:** O chamado criado no teste 12 pode ser consultado pelo ID gerado, o que confirma que ele foi armazenado na lista em memória.
 
---
 
## Teste 14 — Verificar o resumo após o cadastro
 
**Categoria:** Verificação
 
**Requisição**
 
```http
GET http://localhost:8080/chamados/resumo
```
 
**Resposta**
 
```
HTTP/1.1 200 
Content-Type: application/json
```
 
```json
{
  "abertos": 5,
  "emAndamento": 3,
  "resolvidos": 3,
  "totalGeral": 11
}
```
 
**Explicação:** Comparado ao teste 11, `abertos` e `totalGeral` aumentaram em 1. Isso comprova que o resumo é calculado a cada requisição, a partir dos dados atuais, e não armazenado.
 
---
 
## Teste 15 — Verificar os críticos após o cadastro
 
**Categoria:** Verificação
 
**Requisição**
 
```http
GET http://localhost:8080/chamados/criticos
```
 
**Resposta**
 
```
HTTP/1.1 200 
Content-Type: application/json
```
 
```json
[
  {
    "id": 1,
    "titulo": "Impressora não imprime",
    "solicitante": "Rafael Rocha",
    "setor": "Financeiro",
    "prioridade": "ALTA",
    "status": "ABERTO"
  },
  {
    "id": 4,
    "titulo": "Não funciona no meu computador",
    "solicitante": "Joselito Junior",
    "setor": "Financeiro",
    "prioridade": "ALTA",
    "status": "EM_ANDAMENTO"
  },
  {
    "id": 10,
    "titulo": "Rede fora do ar no setor",
    "solicitante": "Micael Macedo",
    "setor": "Gerencia",
    "prioridade": "ALTA",
    "status": "EM_ANDAMENTO"
  },
  {
    "id": 11,
    "titulo": "Servidor de arquivos fora do ar",
    "solicitante": "Jaiana Santana",
    "setor": "TI",
    "prioridade": "ALTA",
    "status": "ABERTO"
  }
]
```
 
**Explicação:** O chamado 11 (ALTA + ABERTO) passou a aparecer entre os críticos, confirmando que o novo cadastro é considerado por todos os endpoints.