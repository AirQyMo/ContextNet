package SmartClassroom;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket handler for broadcasting alert messages to web clients
 */
@WebSocket
public class AlertWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(AlertWebSocketHandler.class);
    private static final ConcurrentHashMap<Session, Boolean> sessions = new ConcurrentHashMap<>();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        sessions.put(session, true);
        logger.info("WebSocket client connected: " + session.getRemoteAddress());
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        sessions.remove(session);
        logger.info("WebSocket client disconnected: " + session.getRemoteAddress());
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        logger.error("WebSocket error: " + error.getMessage());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        logger.info("Received message from client: " + message);
    }

    /**
     * Broadcast a message to all connected WebSocket clients
     * @param message The message to broadcast
     */
    public static void broadcast(String message) {
        sessions.keySet().forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.getRemote().sendString(message);
                }
            } catch (IOException e) {
                logger.error("Error broadcasting message to client: " + e.getMessage());
            }
        });
    }

    /**
     * Get the number of connected clients
     * @return Number of active connections
     */
    public static int getConnectionCount() {
        return sessions.size();
    }
}


