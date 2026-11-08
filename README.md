# API REST de Gerenciamento de Chamados

Trabalho da disciplina **Linguagens para Aplicação Comercial** (Java, Spring Boot e JSON).

A API gerencia chamados de suporte técnico. Os dados ficam em memória, em coleções Java, sem banco de dados.

## Integrantes

- Jaiana Santana
- Micael Macedo
- Rafael Rocha

## Tecnologias

- Java 25
- Spring Boot 4.1 (Spring Web)
- Jackson (conversão JSON, incluído no Spring Web)
- Maven (via Maven Wrapper `mvnw`)

## Como executar

```bash
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`. 


## Endpoints

| Método | Caminho | Descrição | Respostas |
|---|---|---|---|
| GET | `/chamados` | Lista todos os chamados | 200 |
| GET | `/chamados/{id}` | Consulta um chamado pelo ID | 200, 404, 400 (ID não numérico) |
| GET | `/chamados/setor?nome=financeiro` | Filtra por setor, sem diferenciar maiúsculas de minúsculas | 200 (lista, possivelmente vazia) |
| GET | `/chamados/prioridade?nivel=alta` | Filtra por prioridade, sem diferenciar maiúsculas de minúsculas | 200, 400 (prioridade inexistente) |
| GET | `/chamados/busca?texto=impressora` | Busca chamados cujo título contém o texto | 200 (lista, possivelmente vazia) |
| GET | `/chamados/criticos` | Lista chamados de prioridade ALTA ainda não resolvidos | 200 |
| GET | `/chamados/resumo` | Quantidade de chamados por status e total geral | 200 |
| POST | `/chamados` | Cadastra um novo chamado a partir de um JSON | 201 |

Exemplo de corpo para o `POST`:

```json
{
  "titulo": "Servidor de arquivos fora do ar",
  "solicitante": "Jaiana Santana",
  "setor": "TI",
  "prioridade": "ALTA",
  "status": "ABERTO"
}
```

O campo `status` é opcional. Se não for informado, o chamado é criado como `ABERTO`. O `id` não deve ser enviado, pois é gerado pela API.

## Testes

As evidências de 15 requisições (consultas, filtros, situações sem correspondência, entrada inválida, cadastro e verificação dos dados cadastrados) estão em [`docs/TESTES.md`](docs/TESTES.md), com a requisição, a resposta HTTP/JSON e a explicação de cada resultado.

Para reproduzi-las:

1. Instale a extensão **REST Client** (Huachao Mao) no VS Code.
2. Inicie a aplicação com `./mvnw spring-boot:run`.
3. Abra [`docs/requisicoes.http`](docs/requisicoes.http) e clique em **Send Request** acima de cada requisição, na ordem.

Os testes devem ser executados com a aplicação recém-iniciada, pois os IDs e as contagens dependem do estado inicial dos dados em memória.

## Decisões de projeto

- **Geração de ID com `AtomicInteger`:** um único gerador é usado tanto pelos 10 chamados iniciais quanto pelo `POST`. Ele só avança e nunca recalcula a partir do tamanho da lista, o que evita IDs repetidos. Por ser atômico, também evita que duas requisições simultâneas recebam o mesmo ID.
- **Status padrão `ABERTO`:** se o `POST` não informar o status, o chamado é criado como `ABERTO`. Um status nulo deixaria o `/resumo` inconsistente (a soma dos status não bateria com o total) e faria o chamado aparecer indevidamente em `/criticos`.
- **Lista vazia × 404:** filtros e buscas sem resultado devolvem `200` com `[]`, porque a consulta é válida e apenas não encontrou nada. Já a consulta por ID inexistente devolve `404`, porque o recurso específico pedido não existe.
- **Prioridade inválida → 400:** no filtro de prioridade, o texto é convertido para maiúsculas e transformado no enum com `Prioridade.valueOf`. Valores inexistentes geram `400 Bad Request` com mensagem explicativa, em vez de um erro 500.
- **`/criticos` usa "diferente de RESOLVIDO":** a condição é `prioridade == ALTA && status != RESOLVIDO`, o que inclui `ABERTO` e `EM_ANDAMENTO` e continuaria correta se um novo status não resolvido fosse criado.
- **Mensagens de erro visíveis:** `spring.web.error.include-message=always` está ativo no `application.properties`, para que as respostas 400 e 404 informem o motivo do erro. Por padrão o Spring oculta essas mensagens por segurança. No Spring Boot 4, essa propriedade substitui a antiga `server.error.include-message`.

## Análise Técnica

### 1. Como o Spring Boot recebe e processa requisições HTTP

O Spring Boot traz um servidor web embutido, o Tomcat. Ao iniciar a aplicação, ele abre a porta 8080 e fica aguardando conexões. Não é preciso instalar nem configurar um servidor à parte.

Quando chega uma requisição, por exemplo `GET /chamados/3`, o fluxo é este:

1. O Tomcat recebe a requisição HTTP e a entrega ao `DispatcherServlet`, o servlet central do Spring MVC. Todas as requisições passam por ele.
2. O `DispatcherServlet` consulta o *Handler Mapping*. Esse componente compara o método HTTP e o caminho da requisição com as anotações `@GetMapping`, `@PostMapping` e `@RequestMapping` dos controllers, e descobre qual método deve atender a chamada.
3. Os argumentos do método são montados a partir da requisição. `@PathVariable` pega valores do caminho, `@RequestParam` pega parâmetros da query string e `@RequestBody` converte o corpo da requisição.
4. O método do controller é executado e devolve um resultado.
5. O resultado é convertido em resposta HTTP, com corpo em JSON, código de status e cabeçalhos, e enviado de volta ao cliente.

Se um método lança `ResponseStatusException`, o Spring monta uma resposta de erro com o status indicado, como 404 ou 400.

### 2. Função do Controller

O Controller é a camada que expõe a API para o mundo externo. Na aplicação, `ChamadoController` é o ponto de entrada das requisições. Ele:

- define as rotas (`/chamados`, `/chamados/{id}`, `/chamados/setor` etc.) por meio das anotações de mapeamento;
- recebe os dados enviados pelo cliente (parâmetros, variáveis de caminho e corpo JSON);
- aplica a lógica de cada operação, como filtrar a lista, buscar por ID ou cadastrar um novo chamado;
- escolhe o código de status HTTP da resposta, por exemplo `201 Created` no cadastro e `404 Not Found` quando o chamado não existe.

A anotação `@RestController` marca a classe como controller de API REST. Ela combina `@Controller` com `@ResponseBody`, o que faz o retorno de cada método ser escrito diretamente no corpo da resposta, em vez de ser interpretado como o nome de uma página. A anotação `@RequestMapping("/chamados")` define o prefixo comum a todas as rotas da classe.

Neste trabalho não há camadas de Service e Repository, então o controller também guarda a lista de chamados e executa toda a lógica.

### 3. Conversão entre objetos Java e JSON

A conversão é feita automaticamente pelo **Jackson**, a biblioteca de JSON que o Spring Boot já inclui e configura. O Spring MVC usa `HttpMessageConverter` para fazer essa ponte, em duas direções:

- **Java para JSON (serialização):** quando um método devolve um `Chamado` ou uma `List<Chamado>`, o Jackson lê os atributos do objeto e gera o JSON correspondente. Os enums `Prioridade` e `Status` viram texto, como `"ALTA"` e `"EM_ANDAMENTO"`.
- **JSON para Java (desserialização):** no `POST`, a anotação `@RequestBody` faz o Jackson ler o JSON do corpo da requisição e preencher um objeto `ChamadoCreate`, associando cada chave do JSON ao campo de mesmo nome. Os textos `"MEDIA"` e `"ABERTO"` são convertidos nos enums correspondentes.

Exemplo de JSON de um chamado:

```json
{
  "id": 1,
  "titulo": "Impressora não imprime",
  "solicitante": "Rafael Rocha",
  "setor": "Financeiro",
  "prioridade": "ALTA",
  "status": "ABERTO"
}
```

Se o JSON enviado for inválido, ou se um valor não existir no enum (por exemplo `"prioridade": "URGENTE"`), o Spring responde com erro `400 Bad Request`.

Na desserialização, o Jackson exige o nome exato da constante do enum. Por isso, no `POST`, `"prioridade": "alta"` em minúsculas também gera `400`. Já o filtro `/chamados/prioridade?nivel=alta` aceita minúsculas, porque ali a conversão é feita pelo próprio código, que transforma o texto em maiúsculas antes de chamar `Prioridade.valueOf`.

O cabeçalho `Content-Type: application/json` das respostas indica ao cliente o formato do corpo. Quando o mesmo erro é acessado por um navegador, que envia `Accept: text/html`, o Spring devolve uma página HTML (a *Whitelabel Error Page*) em vez de JSON. Esse mecanismo é a negociação de conteúdo.

### 4. Por que os chamados cadastrados não permanecem após reiniciar a aplicação

Os chamados ficam numa coleção Java (`List<Chamado>`), que existe apenas na memória RAM, dentro da heap da JVM, enquanto a aplicação está rodando.

Ao cadastrar um chamado com `POST`, ele é adicionado a essa lista e continua disponível para consultas. Isso acontece porque o Spring cria uma única instância do `ChamadoController` (escopo *singleton*) quando a aplicação sobe e reutiliza essa mesma instância em todas as requisições. Assim, a lista e o gerador de ID são compartilhados entre elas. Nada é gravado em disco nem em banco de dados.

Quando a aplicação é encerrada ou reiniciada, o processo da JVM termina e a memória é liberada, então a lista deixa de existir. Na nova execução, a lista é recriada do zero a partir dos chamados fictícios definidos no código, e os cadastros feitos antes são perdidos. O gerador de ID também é recriado, então, depois de reiniciar, o primeiro chamado cadastrado volta a receber o id 11.

Para manter os dados entre execuções seria preciso usar persistência, como JPA com um banco de dados (PostgreSQL, por exemplo) ou gravação em arquivo. O enunciado dispensa isso de propósito.

## Limitações conhecidas

- **Acentos:** os filtros e a busca ignoram maiúsculas e minúsculas, mas não acentos. `?texto=funcionario` não encontra "funcionário".
- **Enums no `POST`:** os valores de `prioridade` e `status` precisam ser enviados em maiúsculas (por exemplo `"ALTA"`), como explicado na seção 3.
- **Validação de campos:** apenas o `status` recebe valor padrão. Os demais campos do `POST` não são validados, então um chamado pode ser criado sem título ou setor. Uma evolução seria usar Bean Validation (`@NotBlank`, `@NotNull` e `@Valid`).
- **Concorrência na lista:** o `AtomicInteger` garante IDs únicos, mas a `ArrayList` não é *thread-safe*. Com muitas requisições simultâneas, uma alternativa seria `CopyOnWriteArrayList` ou `Collections.synchronizedList`.
- **Encapsulamento:** os atributos de `Chamado` são públicos. Uma evolução seria torná-los `private` e expô-los por getters.
- **Persistência:** os dados existem apenas em memória, como explicado na seção 4.