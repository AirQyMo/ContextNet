# ContextNet - Processing Node

Projeto que usa ContextNet para um sistema de monitoramento do ar.

## Como executar

### Pré-requisitos
- Docker instalado
- Java 17 ou superior
- Maven (para compilar)

### 1. Subir a infraestrutura (Gateway, Kafka e Zookeeper)

```bash
docker compose -f docker-start-gw.yml up -d
```

### 2. Compilar e subir o Processing Node

```bash
source compile-all.sh
```

```bash
docker compose -f contextnet-stationary.yml up
```

## 🔧 Portas utilizadas

- **Gateway**: `6200` (UDP)
- **Kafka externo**: `6010`
- **Zookeeper**: `6000`
