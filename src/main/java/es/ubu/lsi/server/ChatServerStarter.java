package es.ubu.lsi.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * ChatServerStarter
 * 
 * @author Juan José Santos Cambra
 *
 */
public class ChatServerStarter {

	/**
	 * Constructor de la clase
	 */
	public ChatServerStarter() {
		registerServer();
	}

	/**
	 * Registra el servidor en RMI
	 */
	public void registerServer() {
		try {
			ChatServer server = new ChatServerImpl();

			ChatServer stub = (ChatServer) UnicastRemoteObject.exportObject(server, 0);

			Registry register = LocateRegistry.getRegistry();
			register.rebind("/Server", stub);

			System.out.println("Server registered");
		} catch (Exception e) {
			System.err.println("Excepción de servidor: " + e.toString());
		}
	}

}
