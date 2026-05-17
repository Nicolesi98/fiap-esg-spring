# fiap-esg-spring

Projeto em Spring Boot para o backend de ESG.

## Tecnologias

- Java 17
- Spring Boot 3.2.4
- Maven
- Spring Data JPA
- Spring Security com Basic Auth
- Flyway
- Oracle Database no perfil padrao
- H2 em memoria no perfil `test`
- Swagger/OpenAPI

## Pre-requisitos

Para executar localmente sem Docker:

- Java 17 instalado
- Maven ou Maven Wrapper do projeto

Para executar com Docker:

- Docker instalado e em execucao

## Executando com Docker

O projeto possui um `Dockerfile` multi-stage otimizado. A primeira etapa compila a aplicacao com Maven em uma imagem Alpine e a segunda etapa executa somente o `.jar` em uma imagem JRE Musl leve.

O build usa cache do Maven via BuildKit, entao a primeira execucao ainda baixa as dependencias, mas as proximas builds tendem a ser bem mais rapidas. A etapa `dependency:go-offline` nao e usada para evitar downloads extras e camadas desnecessarias.

Para priorizar velocidade no Docker, os testes nao sao compilados nem executados durante a geracao da imagem. Use `./mvnw test` ou `.\mvnw.cmd test` para validar a aplicacao separadamente.

### 1. Gerar a imagem

Na raiz do projeto, execute:

```bash
docker build -t fiap-esg-spring .
```

Se o seu Docker estiver com BuildKit desativado, habilite antes de gerar a imagem.

No Linux/macOS:

```bash
DOCKER_BUILDKIT=1 docker build -t fiap-esg-spring .
```

No Windows PowerShell:

```powershell
$env:DOCKER_BUILDKIT=1; docker build -t fiap-esg-spring .
```

### 2. Subir a aplicacao localmente com H2

Use o perfil `test` para rodar com banco H2 em memoria, sem depender do banco Oracle externo:

```bash
docker run -d --rm --name fiap-esg-spring -p 8080:8080 -e SPRING_PROFILES_ACTIVE=test fiap-esg-spring
```

Para acompanhar os logs:

```bash
docker logs -f fiap-esg-spring
```

Para parar a aplicacao:

```bash
docker stop fiap-esg-spring
```

A aplicacao ficara disponivel em:

```text
http://localhost:8080
```

### 3. Acessar Swagger

Com a aplicacao em execucao, acesse:

```text
http://localhost:8080/swagger-ui/index.html
```

### 4. Autenticacao

Os endpoints da API usam Basic Auth. Por padrao:

```text
usuario: admin
senha: admin123
```

Para alterar as credenciais ao subir o container:

```bash
docker run --rm -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=test \
  -e APP_SECURITY_USERNAME=admin \
  -e APP_SECURITY_PASSWORD=admin123 \
  fiap-esg-spring
```

No PowerShell, o mesmo comando pode ser executado em uma linha:

```powershell
docker run --rm -p 8080:8080 -e SPRING_PROFILES_ACTIVE=test -e APP_SECURITY_USERNAME=admin -e APP_SECURITY_PASSWORD=admin123 fiap-esg-spring
```

### 5. Console H2

Quando o perfil `test` esta ativo, o console H2 fica disponivel em:

```text
http://localhost:8080/h2-console
```

Dados de conexao:

```text
JDBC URL: jdbc:h2:mem:fiapesg
User Name: sa
Password:
```

No perfil `test`, o console H2 esta configurado com `web-allow-others=true` para funcionar quando a aplicacao roda dentro do Docker.

## Executando localmente sem Docker

### 1. Rodar com H2 em memoria

No Linux/macOS:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=test
```

No Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=test
```

### 2. Rodar com Oracle

O perfil padrao usa o datasource Oracle configurado em `src/main/resources/application.yaml`.

No Linux/macOS:

```bash
./mvnw spring-boot:run
```

No Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

## Build e testes

Gerar o pacote da aplicacao:

```bash
./mvnw clean package
```

No Windows PowerShell:

```powershell
.\mvnw.cmd clean package
```

Executar os testes:

```bash
./mvnw test
```

No Windows PowerShell:

```powershell
.\mvnw.cmd test
```

## Endpoints uteis

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Actuator: `http://localhost:8080/actuator`
- H2 Console: `http://localhost:8080/h2-console`

## Documentacao complementar

A pasta `documentation` contem materiais de apoio do projeto:

- `Modelo MER.pdf`: modelagem ER do banco de dados.
- `FIAP ESG Energia.postman_collection.json`: collection pronta para importar no Postman e testar os endpoints da API.

## Observacoes

- Para desenvolvimento local rapido, prefira o perfil `test`, pois ele usa H2 em memoria.
- Para conectar em Oracle via Docker, suba o container sem `SPRING_PROFILES_ACTIVE=test` ou configure as variaveis/propriedades de datasource conforme o ambiente desejado.
- As migrations do Flyway ficam em `src/main/resources/db/migration`.
