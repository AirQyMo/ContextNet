import { useState, useEffect, useRef } from 'react'
import MobilePhone from './components/MobilePhone'
import { WebSocketMessage, ParsedAlert, Alert } from './types'
import './App.css'

function App() {
  const [alerts, setAlerts] = useState<ParsedAlert[]>([])
  const [connectionStatus, setConnectionStatus] = useState<'connecting' | 'connected' | 'disconnected'>('connecting')
  const [ws, setWs] = useState<WebSocket | null>(null)
  const isConnecting = useRef(false)

  useEffect(() => {
    // Previne múltiplas conexões
    if (isConnecting.current) {
      console.log('⚠️ Connection already in progress, skipping...')
      return
    }
    
    isConnecting.current = true
    connectWebSocket()
    
    // Cleanup function - fecha a conexão quando o componente é desmontado
    return () => {
      console.log('🔌 Cleaning up WebSocket connection...')
      isConnecting.current = false
      if (ws) {
        ws.close()
      }
    }
  }, []) // eslint-disable-line react-hooks/exhaustive-deps

  const connectWebSocket = () => {
    // Fecha conexão anterior se existir
    if (ws) {
      console.log('🔌 Closing existing WebSocket connection...')
      ws.close()
    }

    console.log('🔌 Creating new WebSocket connection...')
    const websocket = new WebSocket('ws://localhost:8080/alerts')

    websocket.onopen = () => {
      console.log('✅ WebSocket connected successfully')
      setConnectionStatus('connected')
      isConnecting.current = false
    }

    websocket.onmessage = (event) => {
      try {
        const data: WebSocketMessage = JSON.parse(event.data)
        console.log('📨 Received WebSocket message:', data)
        
        // Try to parse the message content
        try {
          const messageContent = JSON.parse(data.message)
          console.log('📋 Parsed message content:', messageContent)
          
          // Check if it's an alert message with full analysis
          if (messageContent.analisys) {
            console.log('✅ Alert with analysis detected')
            const alertData: Alert = messageContent
            const parsedAlert = parseAlert(alertData)
            setAlerts(prev => [parsedAlert, ...prev].slice(0, 10))
          } else {
            console.log('ℹ️ Message without analysis field:', messageContent)
          }
        } catch (parseError) {
          // If it's not JSON, it might be a simple text message from Java toString
          console.log('📝 Raw message (not JSON):', data.message)
          
          // Try to extract ALL pollutants from Java toString format
          // Example: "[Pollutant{name='CO2', riskLevel='moderate', affectedDiseases=AffectedDiseases{disease=[...]}}]"
          const pollutantRegex = /Pollutant\{name='([^']+)',\s*riskLevel='([^']+)',\s*affectedDiseases=AffectedDiseases\{disease=\[([^\]]+)\]\}\}/g;
          const matches = [...data.message.matchAll(pollutantRegex)];
          
          if (matches.length > 0) {
            console.log(`🔍 Extracted ${matches.length} pollutant(s) from toString`)
            
            const sensors = matches.map(match => {
              const [, pollutant, riskLevel, diseasesStr] = match;
              const diseases = diseasesStr.split(',').map(d => d.trim());
              
              return {
                sensorId: data.topic || 'Unknown',
                pollutant: pollutant,
                riskLevel: riskLevel,
                diseases: diseases
              };
            });
            
            // Create a simplified alert with all pollutants
            const simpleAlert: ParsedAlert = {
              id: `alert_${Date.now()}`,
              timestamp: new Date().toISOString(),
              sensors: sensors,
              receivedAt: new Date()
            }
            
            console.log('✅ Created simple alert with pollutants:', simpleAlert)
            setAlerts(prev => [simpleAlert, ...prev].slice(0, 10))
          } else {
            console.warn('⚠️ Could not parse message format:', data.message)
          }
        }
      } catch (error) {
        console.error('❌ Error processing WebSocket message:', error)
      }
    }

    websocket.onerror = (error) => {
      console.error('WebSocket error:', error)
      setConnectionStatus('disconnected')
    }

    websocket.onclose = (event) => {
      console.log('❌ WebSocket disconnected', event.code, event.reason)
      setConnectionStatus('disconnected')
      isConnecting.current = false
      
      // Só tenta reconectar se não foi um fechamento intencional (code 1000)
      if (event.code !== 1000) {
        console.log('⏳ Will attempt to reconnect in 3 seconds...')
        setTimeout(() => {
          console.log('🔄 Attempting to reconnect...')
          setConnectionStatus('connecting')
          connectWebSocket()
        }, 3000)
      } else {
        console.log('🔌 Connection closed intentionally, not reconnecting')
      }
    }

    setWs(websocket)
  }

  const parseAlert = (alert: Alert): ParsedAlert => {
    const analysis = alert.analisys
    const sensors = analysis.sensores.flatMap(sensor =>
      sensor.pollutants.map(pollutant => ({
        sensorId: sensor.sensorId,
        pollutant: pollutant.name,
        riskLevel: pollutant.riskLevel,
        diseases: pollutant.affectedDiseases.disease
      }))
    )

    return {
      id: analysis.alert_id,
      timestamp: analysis.timestamp,
      sensors,
      receivedAt: new Date()
    }
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1>🌡️ Monitor de Qualidade do Ar</h1>
        <div className={`connection-status ${connectionStatus}`}>
          <span className="status-dot"></span>
          <span className="status-text">
            {connectionStatus === 'connected' && 'Conectado'}
            {connectionStatus === 'connecting' && 'Conectando...'}
            {connectionStatus === 'disconnected' && 'Desconectado'}
          </span>
        </div>
      </header>
      
      <div className="app-content">
        <MobilePhone alerts={alerts} />
        
        <div className="info-panel">
          <div className="info-card">
            <h2>📱 Como funciona</h2>
            <p>
              Este aplicativo simula uma tela de celular que recebe alertas em tempo real 
              sobre a qualidade do ar de diferentes sensores.
            </p>
          </div>
          
          <div className="info-card">
            <h2>🎨 Níveis de Risco</h2>
            <div className="risk-levels">
              <div className="risk-item low">
                <span className="risk-badge">Baixo</span>
                <span>Condições normais</span>
              </div>
              <div className="risk-item moderate">
                <span className="risk-badge">Moderado</span>
                <span>Atenção necessária</span>
              </div>
              <div className="risk-item high">
                <span className="risk-badge">Alto</span>
                <span>Cuidado requerido</span>
              </div>
            </div>
          </div>

          <div className="info-card">
            <h2>📊 Estatísticas</h2>
            <div className="stats">
              <div className="stat-item">
                <div className="stat-value">{alerts.length}</div>
                <div className="stat-label">Alertas recebidos</div>
              </div>
              <div className="stat-item">
                <div className="stat-value">
                  {new Set(alerts.flatMap(a => a.sensors.map(s => s.sensorId))).size}
                </div>
                <div className="stat-label">Sensores ativos</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default App

