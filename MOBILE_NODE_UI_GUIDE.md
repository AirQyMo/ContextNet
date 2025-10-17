# 📱 Guia Completo - Interface Web Mobile Node

## 🎯 Visão Geral

Este projeto adiciona uma interface web moderna ao MobileNode que simula uma tela de celular e exibe alertas de qualidade do ar em tempo real através de WebSocket.

## 📋 Arquitetura

```
MobileNode (Java) ←→ WebSocket ←→ Interface Web (React + TypeScript)
     Backend               |              Frontend
  (Porta 8080)             |          (Porta 3000)
```

### Backend (MobileNode)
- Servidor WebSocket usando Spark Java
- Endpoint: `ws://localhost:8080/alerts`
- Transmite alertas em tempo real para clientes conectados

### Frontend (React + TypeScript)
- Interface que simula um iPhone
- Conexão WebSocket automática com reconexão
- Exibição visual de alertas com:
  - Sensor que detectou
  - Poluente identificado
  - Nível de risco (baixo, moderado, alto)
  - Doenças relacionadas

## 🚀 Como Executar

### 1. Compilar o MobileNode

```bash
cd mobile-node
mvn clean install
```

### 2. Executar o MobileNode

```bash
java -jar target/mobile-node.jar
```

O servidor WebSocket iniciará automaticamente na porta 8080.

### 3. Instalar Dependências da Interface

```bash
cd mobile-node-ui
npm install
```

### 4. Executar a Interface Web

```bash
npm run dev
```

A interface estará disponível em: `http://localhost:3000`

### 5. Abrir no Navegador

Acesse `http://localhost:3000` e você verá:
- Um celular simulado no centro
- Status de conexão no topo
- Painel de informações ao lado

## 📡 Testando Alertas

### Opção 1: Enviar Alerta Manualmente pelo MobileNode

No terminal do MobileNode, escolha a opção `(A)` para enviar um alerta de teste:

```
(G) Groupcast | (P) Message to PN | (A) Send Alert to PN | (Z) to finish)? A
```

### Opção 2: Através do Processing Node

1. Inicie o sistema completo (Kafka, Group Definer, Processing Node)
2. Envie alertas pelo ProcessingNode
3. O MobileNode receberá e transmitirá para a interface web

## 🎨 Recursos da Interface

### Design do Celular
- **Moldura realista** com notch (entalhe) do iPhone
- **Barra de status** com hora, sinal e bateria
- **Efeito 3D** ao passar o mouse
- **Totalmente responsivo**

### Cartões de Alerta
Cada alerta exibe:
- 📍 **ID do Sensor**: Identificação do sensor que detectou
- 🌫️ **Poluente**: Tipo de poluente (PM2.5, PM4, CO2, etc.)
- ⚠️ **Nível de Risco**: 
  - 🟢 **Baixo** (verde)
  - 🟡 **Moderado** (amarelo)
  - 🔴 **Alto** (vermelho)
- 🏥 **Doenças Relacionadas**: Lista de possíveis efeitos na saúde

### Painel de Informações
- Status de conexão em tempo real
- Níveis de risco explicados
- Estatísticas (alertas recebidos, sensores ativos)

## 🔧 Configuração

### Alterar Porta do WebSocket

**Backend (MobileNode.java):**
```java
port(8080); // Altere para a porta desejada
```

**Frontend (App.tsx):**
```typescript
const websocket = new WebSocket('ws://localhost:8080/alerts')
// Altere a URL conforme necessário
```

### Build para Produção

```bash
cd mobile-node-ui
npm run build
```

Os arquivos otimizados estarão em `mobile-node-ui/dist/`

## 🌐 Estrutura do Projeto Web

```
mobile-node-ui/
├── src/
│   ├── components/
│   │   ├── MobilePhone.tsx       # Componente do celular
│   │   ├── MobilePhone.css
│   │   ├── AlertCard.tsx         # Cartão de alerta individual
│   │   └── AlertCard.css
│   ├── App.tsx                   # Componente principal
│   ├── App.css
│   ├── types.ts                  # Tipos TypeScript
│   ├── main.tsx                  # Entry point
│   └── index.css                 # Estilos globais
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
└── README.md
```

## 📊 Formato dos Dados

### Mensagem WebSocket Recebida

```json
{
  "timestamp": 1728504516302,
  "topic": "GroupMessageTopic",
  "message": "[Pollutant{name='CO2', riskLevel='moderate', affectedDiseases=AffectedDiseases{disease=[dor de cabeça leve, dificuldade de concentração, fadiga]}}]"
}
```

### Formato do Alerta Completo

```json
{
  "analisys": {
    "alert_id": "alert_1706798417002",
    "timestamp": "2025-10-01T22:40:17.002-03:00",
    "sensores": [
      {
        "sensor_id": "IAQ_6227821",
        "poluentes": [
          {
            "poluente": "pm25",
            "risk_level": "moderate",
            "affected_diseases": {
              "disease": [
                "asma",
                "bronquite",
                "irritação respiratória"
              ]
            }
          }
        ]
      }
    ]
  }
}
```

## 🐛 Troubleshooting

### WebSocket não conecta
1. Verifique se o MobileNode está rodando
2. Confirme que a porta 8080 está disponível
3. Verifique o console do navegador para erros

### Alertas não aparecem
1. Confirme que o MobileNode está recebendo mensagens (veja os logs)
2. Verifique a aba Network do DevTools para mensagens WebSocket
3. Certifique-se de que o formato do JSON está correto

### Interface não carrega
1. Execute `npm install` novamente
2. Limpe o cache: `npm run build -- --force`
3. Verifique se a porta 3000 está disponível

## 🎓 Tecnologias Utilizadas

### Backend
- ☕ **Java 8**
- 🔥 **Spark Java** - Framework web leve
- 🔌 **Jetty WebSocket** - Servidor WebSocket
- 📦 **Jackson** - Processamento JSON

### Frontend
- ⚛️ **React 18** - Framework UI
- 📘 **TypeScript** - Type safety
- ⚡ **Vite** - Build tool rápido
- 🎨 **CSS3** - Animações e gradientes modernos
- 🔌 **WebSocket API** - Comunicação em tempo real

## 📝 Melhorias Futuras

- [ ] Histórico persistente de alertas
- [ ] Filtros por sensor ou tipo de poluente
- [ ] Notificações push no navegador
- [ ] Gráficos de tendência de qualidade do ar
- [ ] Mapa com localização dos sensores
- [ ] Modo escuro (dark mode)
- [ ] PWA (Progressive Web App)

## 📄 Licença

Este projeto faz parte do sistema ContextNet.



