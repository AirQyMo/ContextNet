import { ParsedAlert } from '../types'
import './AlertCard.css'

interface AlertCardProps {
  alert: ParsedAlert
}

function AlertCard({ alert }: AlertCardProps) {
  const getRiskColor = (riskLevel: string) => {
    switch (riskLevel.toLowerCase()) {
      case 'low':
        return 'low'
      case 'moderate':
        return 'moderate'
      case 'high':
        return 'high'
      default:
        return 'moderate'
    }
  }

  const formatTime = (timestamp: string) => {
    try {
      const date = new Date(timestamp)
      return date.toLocaleTimeString('pt-BR', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })
    } catch {
      return '--:--'
    }
  }

  // Group sensors by sensorId
  const groupedSensors = alert.sensors.reduce((acc, sensor) => {
    if (!acc[sensor.sensorId]) {
      acc[sensor.sensorId] = []
    }
    acc[sensor.sensorId].push(sensor)
    return acc
  }, {} as Record<string, typeof alert.sensors>)

  // Get the highest risk level for the sensor
  const getHighestRisk = (sensors: typeof alert.sensors) => {
    const riskOrder = { high: 3, moderate: 2, low: 1 }
    return sensors.reduce((highest, sensor) => {
      const currentRisk = riskOrder[sensor.riskLevel.toLowerCase() as keyof typeof riskOrder] || 1
      const highestRisk = riskOrder[highest.toLowerCase() as keyof typeof riskOrder] || 1
      return currentRisk > highestRisk ? sensor.riskLevel : highest
    }, 'low')
  }

  return (
    <div className="alert-card">
      {Object.entries(groupedSensors).map(([sensorId, sensors]) => (
        <div key={sensorId} className="alert-content">
          <div className="alert-header">
            <div className="sensor-info">
              <span className="sensor-icon">📍</span>
              <div>
                <div className="sensor-id">{sensorId}</div>
                <div className="alert-time">{formatTime(alert.timestamp)}</div>
              </div>
            </div>
            <div className={`risk-badge ${getRiskColor(getHighestRisk(sensors))}`}>
              {getHighestRisk(sensors)}
            </div>
          </div>

          {sensors.map((sensor, index) => (
            <div key={index} className="pollutant-section">
              <div className="pollutant-info">
                <div className="pollutant-header">
                  <span className="pollutant-icon">🌫️</span>
                  <strong>Poluente:</strong> {sensor.pollutant.toUpperCase()}
                  <span className={`pollutant-risk-badge ${getRiskColor(sensor.riskLevel)}`}>
                    {sensor.riskLevel}
                  </span>
                </div>
              </div>

              <div className="diseases-section">
                <div className="diseases-header">
                  <span className="diseases-icon">⚠️</span>
                  <strong>Possíveis efeitos na saúde:</strong>
                </div>
                <ul className="diseases-list">
                  {sensor.diseases.map((disease, idx) => (
                    <li key={idx}>{disease}</li>
                  ))}
                </ul>
              </div>
            </div>
          ))}
        </div>
      ))}
    </div>
  )
}

export default AlertCard

