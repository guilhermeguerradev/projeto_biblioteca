# Sistema de Biblioteca

API REST para gestão de uma biblioteca acadêmica, com Java 21 e Spring Boot. O foco do projeto são as **regras de negócio** de empréstimos, renovações, reservas e multas, que se cruzam entre si.

## Regras de negócio

**Empréstimos**
- Prazo de devolução: **7 dias** para alunos e colaboradores, **14 dias** para professores
- Limite de **3 empréstimos ativos** por usuário
- Bloqueio de novo empréstimo para quem tem **multa pendente** ou **empréstimo atrasado**
- Não é possível pegar o mesmo livro duas vezes ao mesmo tempo

**Renovações**
- Até **3 renovações** por empréstimo, cada uma somando 7 dias
- Não renova empréstimo atrasado, nem livro que tenha **reserva ativa** de outra pessoa

**Reservas**
- Só é possível reservar um livro sem exemplares disponíveis na estante
- Quando o exemplar volta, ele fica guardado por **3 dias** para quem reservou; depois disso a reserva expira e o livro passa para o próximo da fila ou volta para a estante

**Multas**
- Geradas na devolução: **R$ 100 por dia de atraso** e **R$ 100** se o exemplar voltar danificado ou extraviado
- Pagamento pela rota `PATCH /multas/{id}/pagar`

**Tarefas automáticas** (`@Scheduled`, todo dia à meia-noite)
- Marca os empréstimos vencidos como atrasados
- Expira as reservas não retiradas no prazo

## Tecnologias

- Java 21, Spring Boot 4
- Spring Data JPA com **Specifications** para filtros dinâmicos
- Bean Validation
- **Flyway** (9 migrações versionadas) e banco H2 em memória
- Lombok, Maven

## Estrutura

```
controller/      endpoints REST
service/         regras de negócio e transações
repository/      acesso a dados
specification/   filtros dinâmicos de busca
dto/             requests e responses separados das entidades
entity/          entidades JPA e enums de status
exceptions/      exceções de negócio e handler global (@RestControllerAdvice)
```

## Principais endpoints

| Recurso | Rotas |
| --- | --- |
| Acervo | `/livros`, `/exemplares`, `/autores`, `/editoras`, `/categorias` |
| Pessoas | `/usuarios`, `/alunos`, `/professores`, `/colaboradores` |
| Empréstimos | `POST /emprestimos`, `PATCH /emprestimos/{id}/renovar`, `PATCH /emprestimos/{id}/finalizar` |
| Reservas | `POST /reservas`, `PATCH /reservas/{id}/cancelar` |
| Multas | `GET /multas`, `PATCH /multas/{id}/pagar` |

## Como rodar

Pré-requisitos: Java 21 e Maven.

```bash
git clone https://github.com/guilhermeguerradev/projeto_biblioteca.git
cd projeto_biblioteca
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`. O banco é H2 em memória, criado pelas migrações do Flyway a cada inicialização. O console do H2 fica em `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:bibliotecadb`, usuário `sa`, sem senha).

## Próximos passos

- [ ] Testes unitários das regras de empréstimo e reserva
- [ ] Documentação com Swagger/OpenAPI
- [ ] Autenticação com Spring Security
- [ ] PostgreSQL no lugar do H2

## Autor

**Guilherme Guerra de Paulo**, desenvolvedor back-end Java.
[LinkedIn](https://www.linkedin.com/in/guilhermeguerradepaulo) · [GitHub](https://github.com/guilhermeguerradev)
