package es.ubu.lsi.client;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import es.ubu.lsi.common.ChatMessage;
import es.ubu.lsi.server.ChatServer;

/**
 * ChatClientStarter
 * 
 * @author Juan José Santos Cambra
 *
 */
public class ChatClientStarter extends UnicastRemoteObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Para registro de horas
	 */
	private static SimpleDateFormat sdf;

	/**
	 * Servidor por defecto
	 */
	private final String DEFAULTHOST = "localhost";

	/**
	 * Texto de logout
	 */
	private static final String LOGOUT_TEXT = "logout";

	/**
	 * Salida de programa con error
	 */
	private static final int EXIT_NOK = 1;

	/**
	 * Salida de programa sin error
	 */
	private static final int EXIT_OK = 0;

	/**
	 * Constructor de la clase
	 * 
	 * @param args[]: Argumentos. 1: Nickname. 1: Nickname, 2: Host.
	 * @throws RemoteException if remote communication has problems
	 */
	public ChatClientStarter(String args[]) throws RemoteException {
		sdf = new SimpleDateFormat("HH:mm:ss");
		if (args.length == 1) {
			startClient(args[0], DEFAULTHOST);
		} else if (args.length == 2) {
			startClient(args[0], args[1]);
		} else {
			System.err.println("Wrong parameters. Must receive 1: {username} or 1: {hostname} 2: {username}");
			System.exit(EXIT_NOK);
		}
	}

	/**
	 * Lanza el cliente y escucha el input de teclado
	 * 
	 * @param nickname: Nombre del usuario
	 * @param hostname: Host de destino
	 */
	private void startClient(String nickname, String hostname) {

		// Escaner para leer el teclado
		Scanner scn = new Scanner(System.in);

		// Entrada del usuario
		String input;
		try {

			// Instanciamos el cliente
			ChatClientImpl client = new ChatClientImpl(nickname);

			// Exportamos el objeto cliente
			UnicastRemoteObject.exportObject(client, 0);

			// Obtenemos el servidor
			Registry registry = LocateRegistry.getRegistry(hostname);
			ChatServer server = (ChatServer) registry.lookup("/Server");

			// Obtenemos el id de cliente retornado por el servidor
			int clientId = server.checkIn(client);

			System.out
					.println("Id " + clientId + " has been assigned by the server, nickname: " + client.getNickName());

			client.setId(clientId);

			// Ciclo principal
			while (true) {

				// Leemos la siguiente línea
				input = scn.nextLine();

				// Comprobamos si el usuario quiere hacer logout
				if (input.contains(LOGOUT_TEXT)) {
					server.logout(client);
					System.exit(EXIT_OK);
				}

				ChatMessage chatMessage = new ChatMessage(client.getId(), client.getNickName(), input);
				server.publish(chatMessage);
			}

		} catch (Exception e) {
			System.err.println(e);
			System.err.println("[" + sdf.format(new Date()) + "]" + " - Error creating client.");
			System.exit(EXIT_NOK);
		}

	}

}
