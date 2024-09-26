# Vote Session Api

A VoteSessionAPI é uma solução robusta para gerenciar sessões de votação em assembleias de cooperativas, onde cada
associado possui um voto e as decisões são tomadas democraticamente. Desenvolvida em Java com Spring Boot, essa API REST
oferece uma interface clara e eficiente para dispositivos móveis, permitindo que os usuários interajam facilmente com o
sistema.


> ## Tecnologias e Arquitetura
Esta API foi desenvolvida em Java, utilizando o framework Spring Boot. A escolha do Spring Boot se deve à facilidade que
ele oferece em termos de documentação, maturidade, estabilidade, comunidade ativa, flexibilidade e facilidade de testes.

A API também adota uma arquitetura em camadas (layered architecture), que facilita a escrita de testes de unidade e de
integração, permitindo uma escalabilidade e manutenção mais simples do sistema. Para a persistência de dados, foi
utilizado
o banco de dados relacional PostgreSQL e o JPA. A escolha de um banco de dados relacional (SQL) foi motivada pelo nível
de relacionamentos observados nas entidades, tornando o PostgreSQL uma escolha ideal para esse mapeamento. Como não
foram
identificadas queries complexas, o uso do JPA se mostrou vantajoso devido às funções pré-existentes, evitando a
necessidade
de escrever cada query do zero.

Para gerenciar o versionamento do banco de dados, optou-se pelo uso de migrações, especificamente o Flyway. O Flyway
facilita
o versionamento, permitindo a automação de migrações, rollback, revisão e auditoria de registros, tudo com simplicidade.

Para garantir um bom desempenho, foi adotado o uso de cache com Redis, permitindo que dados de busca já armazenados no
Redis sejam acessados facilmente, sem a necessidade de interagir com o banco de dados toda vez que for feita uma busca
por informações estáticas.

Durante o desenvolvimento, foi observada a indisponibilidade do serviço de validação de CPF fornecido na descrição do
desafio, disponível pelo link: https://user-info.herokuapp.com/users/{cpf}.

Dessa forma, para realizar a validação utilizando um serviço externo, foi adotada a Inverter Texto API, que pode ser
acessada pelo link: https://api.invertexto.com/api-validador-cpf-cnpj. Esta API realiza a validação do CPF.

> ## Documentação da API e Qualidade do códico

Para fornecer uma documentação rica dos endpoints, optou-se pelo uso do Swagger, que pode ser acessado através do
link: http://localhost:8080/swagger-ui/index.html#/. Lembrando que essa documentação só pode ser acessada quando a
aplicação estiver rodando, e o serviço pode variar dependendo da sua hospedagem.

Para garantir uma qualidade de código excelente, foi adotado o uso do Sonar, onde podemos visualizar as métricas e a
cobertura de testes, acessível através do seguinte link: [link].

> ## Rodando a API

Para evitar a incompatibilidade de versões, o sistema pode ser executado usando o Docker.

### Pré-requisitos

- docker

### Passos com Docker

- Clonar o Repositorio

 ```
    git clone https://github.com/gervasioartur/votesession-api.git 
 ```

- Navegar até o diretório

 ```
    cd votesession-api
 ```

- Rodar o Docker

 ```
    docker-compose up -d
 ```

> ## Outras tecnologias e API's externas
- Webclient
- jakarta Validation
- Model Mapper
- Lombok
- Junit 5
- Jacoco