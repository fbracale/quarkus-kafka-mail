# Quarkus Kafka Mail

Este projeto é uma aplicação Quarkus que integra Kafka e envio de emails. Ele consome mensagens de um tópico Kafka, processa os dados e envia emails usando a API reativa do Quarkus Mailer. Em caso de falhas, as mensagens são enviadas para uma Dead Letter Queue (DLQ) para análise posterior.

## Tecnologias Utilizadas

- **Quarkus**: Framework Java para aplicações nativas em nuvem.
- **Kafka**: Sistema de mensagens distribuído.
- **Quarkus Mailer**: API para envio de emails.
- **SmallRye Reactive Messaging**: Integração reativa com Kafka.
- **Jackson**: Biblioteca para manipulação de JSON.

## Funcionalidades

- Consumo de mensagens de um tópico Kafka.
- Desserialização de mensagens JSON em objetos Java.
- Envio de emails com base nos dados processados.
- Tratamento de erros e envio de mensagens para uma Dead Letter Queue (DLQ).

## Estrutura do Projeto

### Classes Principais

#### 1. **KafkaConsumer**
- Consome mensagens do tópico Kafka configurado.
- Desserializa o JSON recebido em um objeto `EmailStructure`.
- Envia emails usando o serviço `EmailServiceQuarkus`.
- Em caso de falhas, envia a mensagem para a DLQ com headers personalizados.

#### 2. **KafkaProducer**
- Produz mensagens JSON para o tópico Kafka configurado.

#### 3. **EmailServiceQuarkus**
- Serviço responsável por enviar emails de forma reativa.
- Valida os dados do email antes de enviá-lo.

#### 4. **EmailStructure**
- Classe que representa a estrutura do email.
- Contém campos como `from`, `to`, `subject`, `body`, `cc`, entre outros.

### Configurações

#### Arquivo `application.properties`

```properties
# Configurações do Kafka
kafka.bootstrap.servers=localhost:9092

# Configurações do canal de entrada (consumidor)
mp.messaging.incoming.topic-in.connector=smallrye-kafka
mp.messaging.incoming.topic-in.auto.offset.reset=earliest
mp.messaging.incoming.topic-in.topic=kafka-mail
mp.messaging.incoming.topic-in.value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
mp.messaging.incoming.topic-in.failure-strategy=dead-letter-queue
mp.messaging.incoming.topic-in.dead-letter-queue.topic=kafka-mail-dlq

# Configurações do canal de saída (produtor)
mp.messaging.outgoing.dlq.connector=smallrye-kafka
mp.messaging.outgoing.dlq.topic=kafka-mail-dlq
```

## Como Executar

### Pré-requisitos

- **Java 21 ou superior**
- **Apache Kafka** em execução
- **Maven**

### Passos

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/quarkus-kafka-mail.git
   cd quarkus-kafka-mail
   ```

2. Inicie o Kafka localmente.

3. Compile e execute o projeto:
   ```bash
   mvn clean compile quarkus:dev
   ```

4. Produza mensagens no tópico Kafka configurado (`kafka-mail`):
   ```bash
   kafka-console-producer --broker-list localhost:9092 --topic kafka-mail
   ```

5. Verifique os logs para confirmar o envio de emails ou mensagens enviadas para a DLQ.

## Exemplo de Mensagem Kafka

Envie uma mensagem JSON para o tópico Kafka:

```json
{
  "classe": "email",
  "objeto": {
    "from": "sender@example.com",
    "to": "recipient@example.com",
    "subject": "Test Email",
    "body": "This is a test email.",
    "cc": "cc@example.com"
  }
}
```

## Tratamento de Erros

- **Mensagens Inválidas**: Se o campo `classe` não for `"email"` ou se o campo `objeto` estiver ausente, a mensagem será enviada para a DLQ.
- **Falha no Envio de Email**: Se o envio do email falhar, a mensagem será enviada para a DLQ com um header indicando o motivo da falha.

## Estrutura da Dead Letter Queue (DLQ)

As mensagens enviadas para a DLQ incluem headers personalizados, como:

- `error-message`: Motivo da falha.
- `original-key`: Chave original da mensagem.

## Testes

### Testes Unitários

- Teste de consumo de mensagens no `KafkaConsumer`.
- Teste de envio de emails no `EmailServiceQuarkus`.

### Como Executar os Testes

```bash
mvn test
```

## Contribuição

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues ou enviar pull requests.

## Licença

Este projeto está licenciado sob a [MIT License](LICENSE).