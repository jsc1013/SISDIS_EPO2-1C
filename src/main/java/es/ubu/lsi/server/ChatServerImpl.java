package es.ubu.lsi.server;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
	// private List<ChatClient> clients;
	private Map<String, ChatClient> clients;

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

		if (!clients.containsKey(msg.getNickname())) {
			return;
		}

		if (msg.getMessage().startsWith("drop")) {

			ChatMessage serverMessage;
			String userName = msg.getMessage().replace("drop", "");
			userName = userName.trim();

			if (clients.containsKey(userName)) {
				clients.remove(userName);
				serverMessage = new ChatMessage(msg.getId(), ("[" + sdf.format(new Date()) + "] Server: "
						+ " El usuario " + userName + " ha sido desconectado"));
			} else {
				serverMessage = new ChatMessage(msg.getId(),
						("[" + sdf.format(new Date()) + "] Server: " + " El usuario " + userName + " no existe"));
			}

			clients.get(msg.getNickname()).receive((serverMessage));
		} else {

			msg.setMessage("[" + sdf.format(new Date()) + "] " + msg.getNickname() + ": " + msg.getMessage());

			for (Map.Entry<String, ChatClient> client : clients.entrySet()) {
				client.getValue().receive(msg);
			}
		}

	}

	public void shutdown(ChatClient client) throws RemoteException {
		// TODO Auto-generated method stub
	}

}
