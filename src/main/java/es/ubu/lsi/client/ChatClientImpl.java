package es.ubu.lsi.client;

import java.rmi.RemoteException;

import es.ubu.lsi.common.ChatMessage;

/**
 * ChatClientImpl.
 * 
 * @author Juan José Santos Cambra
 *
 */
public class ChatClientImpl implements ChatClient {

	/**
	 * Nickname del usuario
	 */
	private String nickname;

	/**
	 * ID del usuario que retornará el servidor
	 */
	private int id;

	protected ChatClientImpl(String nickname) throws RemoteException {
		super();
		this.nickname = nickname;
	}

	public int getId() throws RemoteException {
		return id;
	}

	public void setId(int id) throws RemoteException {
		this.id = id;
	}

	public void receive(ChatMessage msg) throws RemoteException {
		System.out.println(msg.getMessage());
	}

	public String getNickName() throws RemoteException {
		return nickname;
	}

}
