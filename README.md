# API REST de Gerenciamento de Chamados

Trabalho da disciplina **Linguagens para Aplicação Comercial** (Java, Spring Boot e JSON).

A API gerencia chamados de suporte técnico. Os dados ficam em memória, em coleções Java, sem banco de dados.

## Como executar

```bash
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

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
  "solicitante": "Maria",
  "setor": "Financeiro",
  "prioridade": "ALTA",
  "status": "ABERTO"
}
```

Se o JSON enviado for inválido, ou se um valor não existir no enum (por exemplo `"prioridade": "URGENTE"`), o Spring responde com erro `400 Bad Request`.

### 4. Por que os chamados cadastrados não permanecem após reiniciar a aplicação

Os chamados ficam numa coleção Java (`List<Chamado>`), que existe apenas na memória RAM, dentro da heap da JVM, enquanto a aplicação está rodando.

Ao cadastrar um chamado com `POST`, ele é adicionado a essa lista e continua disponível para consultas. Nada é gravado em disco nem em banco de dados.

Quando a aplicação é encerrada ou reiniciada, o processo da JVM termina e a memória é liberada, então a lista deixa de existir. Na nova execução, a lista é recriada do zero a partir dos chamados fictícios definidos no código, e os cadastros feitos antes são perdidos.

Para manter os dados entre execuções seria preciso usar persistência, como JPA com um banco de dados (PostgreSQL, por exemplo) ou gravação em arquivo. O enunciado dispensa isso de propósito.
