# Caixara Móveis

Bem-vindo ao **Caixara Móveis**, um microserviço desenvolvido para gerenciar as operações de uma loja imobiliária. Este projeto foi construído com foco em escalabilidade e boas práticas, e inclui funcionalidades essenciais para o gerenciamento de pedidos, produtos, pagamentos e muito mais.

## Objetivo do Projeto

O **Caixara Móveis** tem como objetivo principal fornecer uma solução robusta e eficiente para gerenciar:

- **Produtos**: Cadastro, estoque e preços.
- **Pedidos**: Criação, atualização, listagem e exclusão de pedidos, com suporte a itens e cálculo automático do valor total.
- **Pagamentos**: Processamento de pagamentos com suporte a diferentes tipos de pagamento (ex.: cartão de crédito, boleto).
- **Relatórios**: Geração de relatórios com filtros como período, status e valor mínimo.

## Tecnologias Utilizadas

- **Java**: Linguagem principal do projeto.
- **Spring Boot**: Para construção do microserviço.
- **Hibernate**: Gerenciamento de persistência e mapeamento objeto-relacional (ORM).
- **JPA**: Para manipulação de entidades no banco de dados.
- **PostgreSQL**: Banco de dados utilizado no projeto.
- **Maven**: Gerenciamento de dependências.
- **JUnit**: Testes unitários para validação do sistema.
- **Mockito**: Mocking nas camadas de teste.

## Funcionalidades Implementadas

1. **Gerenciamento de Produtos**
   - Cadastro, busca e atualização de produtos.
   - Controle de estoque com validação de quantidade disponível.

2. **Gerenciamento de Pedidos**
   - Criação de pedidos com itens, preços unitários e cálculo automático do total.
   - Atualização de pedidos com validação de estoque.
   - Exclusão de pedidos.

3. **Processamento de Pagamentos**
   - Integração com serviços de pagamento.
   - Suporte a parcelamento e validação de valor.

4. **Relatórios**
   - Geração de relatórios filtrados por período, status e valor mínimo.

## Pontos de Melhoria

- **Autenticação e Autorização**: Implementar autenticação JWT para segurança nas operações.
- **Validações Avançadas**: Adicionar mais regras de negócios, como controle de devoluções.
- **Melhorias na Interface de API**: Expandir a documentação com Swagger.
- **Escalabilidade**: Configuração de balanceamento de carga e integração com sistemas de filas (ex.: RabbitMQ).

## Como Executar o Projeto

1. Clone o repositório:
   ```bash
   git clone https://github.com/GabrielFCarrijo/Caixara.git
   ```
2. Entre no diretório do projeto:
   ```bash
   cd caixara-moveis
   ```
3. Configure o banco de dados PostgreSQL:
   - Crie um banco de dados chamado `caixara_db`.
   - Atualize as configurações no arquivo `application.properties`.

4. Execute o projeto com Maven:
   ```bash
   mvn spring-boot:run
   ```

5. Acesse a API:
   - URL base: `http://localhost:8080`

## Testes

Para executar os testes unitários:
```bash
mvn test
