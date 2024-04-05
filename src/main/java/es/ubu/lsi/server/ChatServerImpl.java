package es.ubu.lsi.server;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import es.ubu.lsi.client.ChatClient;
import es.ubu.lsi.common.ChatMessage;

/**
 * ChatServerImpl
 * 
 * @author Juan José Santos Cambra
 *
 */
public class ChatServerImpl implements ChatServer {

	/**
	 * Listado de clientes
	 */
	private Map<String, ChatClient> clients;

	/**
	 * Listado de clientes baneados
	 */
	private HashSet<String> bannedClients;

	/**
	 * Texto para baneo
	 */
	private final String BAN_TEXT = "ban";

	/**
	 * Texto para desbaneo
	 */
	private final String UNBAN_TEXT = "unban";

	/**
	 * Para registro de horas
	 */
	private static SimpleDateFormat sdf;

	/**
	 * Siguiente ID a dar al próximo usuario que se conecte
	 */
	private int nextClientID;

	/**
	 * Constructor de la clase
	 */
	public ChatServerImpl() {
		super();
		sdf = new SimpleDateFormat("HH:mm:ss");
		nextClientID = 0;
		clients = new HashMap<String, ChatClient>();
		bannedClients = new HashSet<String>();
	}

	/**
	 * Realiza el checkin del usuario
	 * 
	 * @return int: ID para el usuario
	 * @throws RemoteException if remote communication has problems
	 */
	public int checkIn(ChatClient client) throws RemoteException {
		clients.put(client.getNickName(), client);
		nextClientID++;
		return nextClientID;
	}

	/**
	 * Realiza el logout del usuario
	 * 
	 * @throws RemoteException if remote communication has problems
	 */
	public void logout(ChatClient client) throws RemoteException {
		if (clients.containsKey(client.getNickName())) {
			clients.remove(client.getNickName());
		}
	}

	/**
	 * Gestiona la publicación del mensaje del usuario dependiendo del contenido
	 * 
	 * @throws RemoteException if remote communication has problems
	 */
	public void publish(ChatMessage msg) throws RemoteException {

		if (bannedClients.contains(msg.getNickname())) {
			return;
		}

		if (msg.getMessage().startsWith(BAN_TEXT)) {
			ChatMessage serverMessage;
			String userName = msg.getMessage().replace(BAN_TEXT, "");
			userName = userName.trim();

			if (!bannedClients.contains(userName)) {
				bannedClients.add(userName);
			}

			serverMessage = new ChatMessage(msg.getId(), ("[" + sdf.format(new Date()) + "] Server: " + " El usuario "
					+ msg.getNickname() + " ha baneado a usuario " + userName));

			for (Map.Entry<String, ChatClient> client : clients.entrySet()) {
				if (!bannedClients.contains(client.getValue().getNickName())) {
					client.getValue().receive(serverMessage);
				}
			}

		} else if (msg.getMessage().startsWith(UNBAN_TEXT)) {

			ChatMessage serverMessage;
			String userName = msg.getMessage().replace(UNBAN_TEXT, "");
			userName = userName.trim();

			if (bannedClients.contains(userName)) {
				bannedClients.remove(userName);
				serverMessage = new ChatMessage(msg.getId(), ("[" + sdf.format(new Date()) + "] Server: "
						+ " El usuario " + msg.getNickname() + " ha desbaneado a usuario " + userName));

				for (Map.Entry<String, ChatClient> client : clients.entrySet()) {
					if (!bannedClients.contains(client.getValue().getNickName())) {
						client.getValue().receive(serverMessage);
					}
				}
			}
		} else {

			msg.setMessage("[" + sdf.format(new Date()) + "] " + msg.getNickname() + ": " + msg.getMessage());
			for (Map.Entry<String, ChatClient> client : clients.entrySet()) {
				if (!bannedClients.contains(client.getValue().getNickName())) {
					client.getValue().receive(msg);
				}
			}
		}

	}

	public void shutdown(ChatClient client) throws RemoteException {
		// TODO Auto-generated method stub
	}

}
