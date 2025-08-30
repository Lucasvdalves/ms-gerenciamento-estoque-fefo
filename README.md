# Sistema de Gerenciamento de Estoque FEFO

### **Descrição do Projeto**

Este projeto implementa um sistema de gerenciamento de estoque que utiliza uma arquitetura de microserviços para garantir o controle rigoroso de produtos com base na regra **FEFO (First Expire, First Out)**. O sistema processa planilhas de entrada, gerencia o inventário e gera relatórios diários, garantindo que nenhum produto vença no estoque.

### **Arquitetura**

A solução é composta por uma arquitetura de microserviços que se comunicam de forma assíncrona. A orquestração é feita com Docker Compose, facilitando o gerenciamento de todos os componentes.

**Microserviços:**
* **`estoque-ingestao-dados`**: Serviço de entrada de dados. Recebe planilhas de Excel via endpoint REST e publica mensagens na fila do RabbitMQ.
* **`estoque-processamento-vendas`**: Consumidor de mensagens. Aplica a lógica FEFO para dar baixa no estoque, garantindo que os produtos mais próximos do vencimento sejam vendidos primeiro. Utiliza **Resilience4j** para resiliência.
* **`estoque-gerador-relatorios`**: Serviço de relatórios. Coleta dados de vendas diárias e envia um relatório por e-mail ao final do dia.

**Tecnologias:**
* **Backend**: Spring Boot
* **Mensageria**: RabbitMQ
* **Banco de Dados**: H2 (em memória, para demonstração)
* **Orquestração**: Docker e Docker Compose
* **Resiliência**: Resilience4j

### **Como Executar o Projeto**

Para rodar o sistema completo, siga os passos abaixo:

1.  **Pré-requisitos**:
    * Java JDK 21
    * Maven
    * Docker e Docker Compose

2.  **Compilar os Projetos**:
    * Navegue para cada uma das subpastas de microserviços.
    * Execute o comando de compilação em cada uma delas:
        ```bash
        mvn clean package
        ```

3.  **Iniciar os Containers**:
    * Retorne à pasta raiz (`ms-controle-fefo.raiz`).
    * Execute o comando `docker-compose` para construir as imagens e subir todos os serviços:
        ```bash
        docker-compose up --build
        ```
    * O sistema estará pronto para uso.

### **Endpoints de Teste**

* **Ingestão de Dados**: `POST` para `http://localhost:8080/api/estoque/upload`
* **RabbitMQ Management**: `http://localhost:15672` (Usuário: `guest`, Senha: `guest`)