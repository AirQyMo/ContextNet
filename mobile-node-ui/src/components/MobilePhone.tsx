import { ParsedAlert } from '../types'
import AlertCard from './AlertCard'
import './MobilePhone.css'

interface MobilePhoneProps {
  alerts: ParsedAlert[]
}

function MobilePhone({ alerts }: MobilePhoneProps) {
  const currentTime = new Date().toLocaleTimeString('pt-BR', { 
    hour: '2-digit', 
    minute: '2-digit' 
  })

  return (
    <div className="mobile-phone">
      <div className="phone-frame">
        <div className="phone-notch">
          <div className="camera"></div>
          <div className="speaker"></div>
        </div>
        
        <div className="phone-screen">
          <div className="status-bar">
            <div className="time">{currentTime}</div>
            <div className="indicators">
              <span>📶</span>
              <span>📡</span>
              <span>🔋</span>
            </div>
          </div>

          <div className="screen-content">
            <div className="app-header-mobile">
              <h2>Alertas de Qualidade do Ar</h2>
              <p className="subtitle">
                {alerts.length === 0 
                  ? 'Aguardando alertas...' 
                  : `${alerts.length} ${alerts.length === 1 ? 'alerta recebido' : 'alertas recebidos'}`}
              </p>
            </div>

            <div className="alerts-container">
              {alerts.length === 0 ? (
                <div className="no-alerts">
                  <div className="no-alerts-icon">🔔</div>
                  <p>Nenhum alerta no momento</p>
                  <p className="no-alerts-sub">
                    Os alertas aparecerão aqui quando forem detectados problemas na qualidade do ar
                  </p>
                </div>
              ) : (
                alerts.map((alert) => (
                  <AlertCard key={alert.id} alert={alert} />
                ))
              )}
            </div>
          </div>
        </div>

        <div className="phone-button"></div>
      </div>
    </div>
  )
}

export default MobilePhone


