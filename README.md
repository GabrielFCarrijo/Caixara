# Caixara Móveis

**Caixara Móveis**, um microserviço desenvolvido para gerenciar as operações de uma loja imobiliária. Este projeto foi construído com foco em escalabilidade e boas práticas, e inclui funcionalidades essenciais para o gerenciamento de pedidos, produtos, pagamentos a baixo tem mais especificaçõoes do projeto

## Melhorias
Fazendo o projeto faltou tempo para que eu conseguisse desenvolver alguns pontos:
   - Mensageria: na parte dos pagamentos tenho a ideia de implementar mensageria sendo assim o quando mudasse status do pagamento enviasse uma notificacao disparada por mensageria
   - Relatorios: questão dos relatorios penso em colocar um relatorio mais customizado para conseguir futamente fazer importacao e exportação
   - Inserção Massivo: inserir produtos de forma massiva as vezes ate com xlsx
   - Deploy em servidor: fiz a configuracao do job do deploy porem nao subi em nenhum servidor ia colocar no hamashi porem ele virou pago mais faltou apenas configurar e colocar as credencias para o deploy ir para algum servidor
   - API de pagamento: pensei em fazer integração do pagamento porem como era algo mais extenso e eu nao tinha tanto tempo fiz manualmente no projeto mais a ideia era consumir uma API

## Objetivo do Projeto

O **Caixara Móveis** tem como objetivo principal fornecer uma solução robusta e eficiente para gerenciar:

- **Produtos**: Cadastro, estoque e preços.
- **Pedidos**: Criação, atualização, listagem e exclusão de pedidos, com suporte a itens e cálculo automático do valor total.
- **Pagamentos**: Processamento de pagamentos com suporte a diferentes tipos de pagamento (ex.: cartão de crédito, boleto).
- **Relatórios**: Geração de relatórios com filtros como período, status e valor mínimo.

## Níveis de Acesso

Os níveis de acesso no Caixara Móveis foram definidos para garantir segurança e controle sobre as operações realizadas. Sendo assim os níveis e suas permissões sao:

    ADMIN
        Acesso total a todos os recursos.
        Pode gerenciar usuários, produtos, pedidos, pagamentos e relatórios.
        Endpoints disponíveis:
            /api/admin/**
            /api/operador/**
            /api/cliente/**

    OPERADOR
        Acesso a recursos operacionais, como gerenciamento de produtos e pedidos.
        Endpoints disponíveis:
            /api/operador/**

    CLIENTE
        Acesso limitado a informações relacionadas ao próprio cliente.
        Pode visualizar pedidos e informações associadas.
        Endpoints disponíveis:
            /api/cliente/**

### Configuração de Segurança

A autenticação e autorização são gerenciadas através do Spring Security. Os roles utilizados no sistema incluem prefixos como ROLE_ADMIN, ROLE_OPERADOR e ROLE_CLIENTE.
A criacao dos usuarios ja vem com a sua role definindo assim suas permissoes.

## Tecnologias Utilizadas

- **Java**: Linguagem principal do projeto.
- **Spring Boot**: Para construção do microserviço.
- **Hibernate**: Gerenciamento de persistência e mapeamento objeto-relacional (ORM).
- **JPA**: Para manipulação de entidades no banco de dados.
- **PostgreSQL**: Banco de dados utilizado no projeto.
- **Maven**: Gerenciamento de dependências.
- **JUnit**: Testes unitários para validação do sistema.
- **Mockito**: Mocking nas camadas de teste.
- **Docker**: Geração do container do PostgresSQL.

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
  
5. **Swagger**
   - http://localhost:8080/swagger-ui/index.html#/
     
## Como Executar o Projeto
   Lembrando que para conseguir executar o projeto basta abrir o Docker desktop por conta que quando voce executar o projeto ele ira subir um container de PostgresSQL e criar todos as tabelas e relacionamentos necessarios
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
     
6. Endipoints para acesso:
Para facilitar os testes segue a coleção do postman é so fazer o import e voces conseguirao ver com os body prontos:
```bash
https://drive.google.com/file/d/1QrLnkIVJIjsFSsayM34cd94A2CGnsKd_/view?usp=sharing
```
