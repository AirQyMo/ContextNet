# 🚀 Guia Rápido - Interface Web Mobile Node

## ⚡ Início Rápido (3 passos)

### 1️⃣ Executar o MobileNode (Backend)
```bash
cd mobile-node
java -jar target/mobile-node.jar
```

✅ Você verá:
```
WebSocket server started on port 8080
WebSocket endpoint: ws://localhost:8080/alerts
```

### 2️⃣ Instalar e Executar a Interface Web
Em outro terminal:
```bash
cd mobile-node-ui
npm install
npm run dev
```

✅ Acesse: `http://localhost:3000`

### 3️⃣ Testar um Alerta
No terminal do MobileNode, pressione:
- Digite `A` (Send Alert to PN)
- O alerta aparecerá instantaneamente na interface web! 📱

---

## 🎯 O que você verá

### Na Interface Web:
- 📱 **Celular simulado** (estilo iPhone)
- 🟢 **Status "Conectado"** (canto superior)
- 🔔 **Alertas em tempo real** com:
  - Sensor que detectou
  - Tipo de poluente
  - Nível de risco (cores: verde/amarelo/vermelho)
  - Doenças relacionadas

### Exemplo de Alerta:
```
┌─────────────────────────────┐
│ 📍 IAQ_6227821    🔴 HIGH   │
├─────────────────────────────┤
│ 🌫️ Poluente: PM4           │
│                             │
│ ⚠️ Possíveis efeitos:       │
│   • irritação respiratória  │
│   • inflamação sistêmica    │
└─────────────────────────────┘
```

---

## 🔧 Arquitetura Simplificada

```
┌──────────────┐     WebSocket      ┌──────────────┐
│  MobileNode  │ ←─────────────────→ │  Interface   │
│   (Java)     │   ws://localhost:   │  Web (React) │
│  Porta 8080  │        8080         │  Porta 3000  │
└──────────────┘                     └──────────────┘
```

---

## 📊 Testando com o Sistema Completo

### Opção Avançada: Com Processing Node

1. **Inicie Kafka e Zookeeper:**
   ```bash
   docker compose -f start-gw.yml up
   ```

2. **Inicie o Processing Node:**
   ```bash
   cd processing-node
   java -jar target/processing-node.jar
   ```

3. **Inicie o MobileNode (com WebSocket):**
   ```bash
   cd mobile-node
   java -jar target/mobile-node.jar
   ```

4. **Inicie a Interface Web:**
   ```bash
   cd mobile-node-ui
   npm run dev
   ```

5. **Envie um alerta do Processing Node**
   - Os alertas fluirão: Processing Node → MobileNode → Interface Web

---

## 🎨 Recursos Visuais

### Níveis de Risco:
- 🟢 **LOW** (Baixo) - Verde
- 🟡 **MODERATE** (Moderado) - Amarelo  
- 🔴 **HIGH** (Alto) - Vermelho

### Interface Responsiva:
- Desktop: Celular simulado + painel de informações
- Mobile: Tela completa otimizada

---

## ❓ Problemas Comuns

### WebSocket não conecta?
```bash
# Verifique se a porta 8080 está livre
netstat -ano | findstr :8080

# Reinicie o MobileNode
```

### Interface não carrega?
```bash
# Limpe e reinstale
cd mobile-node-ui
rm -rf node_modules package-lock.json
npm install
npm run dev
```

### Alertas não aparecem?
1. ✅ Verifique os logs do MobileNode
2. ✅ Abra o DevTools do navegador (F12)
3. ✅ Aba "Network" → WS → Veja as mensagens

---

## 📖 Documentação Completa

Para mais detalhes, veja: `MOBILE_NODE_UI_GUIDE.md`

---

## 🎉 Pronto!

Agora você tem uma interface web moderna para visualizar os alertas de qualidade do ar em tempo real! 

**Dica:** Deixe a interface aberta em um monitor secundário para monitoramento contínuo! 🖥️📱



