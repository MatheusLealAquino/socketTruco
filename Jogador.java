import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Jogador {
	public DataOutputStream outToClient;
	public BufferedReader inFromClient;
	public List<Carta> mao = new ArrayList<Carta>();
	public Socket socket;

	public Jogador(Socket socket) {
		this.socket = socket;
	}
	
	public void setOutToClient(DataOutputStream outToClient) {
		this.outToClient = outToClient;
	}
	
	public void setInFromClient(BufferedReader inFromClient) {
		this.inFromClient = inFromClient;
	}

	public void recebeCarta(Carta c) {
		mao.add(c);
	}

	public Carta joga(int naipe, int valor) {
		for (Carta c : mao) {
			if (c.getNaipe() == naipe && c.getValor() == valor) {
				mao.remove(c);
				return c;
			}
		}
		return null;
	}

}
