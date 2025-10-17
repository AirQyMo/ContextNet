export interface Pollutant {
  name: string;
  riskLevel: string;
  affectedDiseases: {
    disease: string[];
  };
}

export interface SensorAlert {
  sensorId: string;
  pollutants: Pollutant[];
}

export interface AlertAnalysis {
  alert_id: string;
  timestamp: string;
  sensores: SensorAlert[];
}

export interface Alert {
  analisys: AlertAnalysis;
}

export interface WebSocketMessage {
  timestamp: number;
  topic: string;
  message: string;
}

export interface ParsedAlert {
  id: string;
  timestamp: string;
  sensors: Array<{
    sensorId: string;
    pollutant: string;
    riskLevel: string;
    diseases: string[];
  }>;
  receivedAt: Date;
}



