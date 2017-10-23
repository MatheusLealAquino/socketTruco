import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Jogador {
	private int id;
	private DataOutputStream outToClient;
	private BufferedReader inFromClient;
	private Socket socket;

	public Jogador(Socket socket) {
		this.socket = socket;
	}
	
	public void setOutToClient(DataOutputStream outToClient) {
		this.outToClient = outToClient;
	}
	
	public void setInFromClient(BufferedReader inFromClient) {
		this.inFromClient = inFromClient;
	}

	public int getId() {
		return id;
	}
	
	public BufferedReader getInFromClient() {
		return inFromClient;
	}

	public DataOutputStream getOutToClient() {
		return outToClient;
	}
	
	public Socket getSocket() {
		return socket;
	}

	public void setId(int id) {
		this.id = id;
	}
}
