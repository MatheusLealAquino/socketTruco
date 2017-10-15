import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class TCPCliente {
	public static void main(String[] args) throws UnknownHostException,
			IOException {
		int porta = Porta.NUM;
		Socket clientSocket = new Socket("127.0.0.1", porta);
		Baralho baralho = new Baralho();
		List<Carta> mao = new ArrayList<Carta>();
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(
				System.in));
		DataOutputStream outToServer = new DataOutputStream(
				clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(
				clientSocket.getInputStream()));

		int id = inFromServer.read();
		System.out.println("id: "+id);
		
		//recebe mão
		recebeCarta(mao, inFromServer, 3);
		printaCartas(mao, "mao");
	
		//recebe vira
		Carta vira = recebeCarta(inFromServer);
		System.out.println("vira: "+vira);
		
		List<Carta> mesa = new ArrayList<>();
		//rodada 1
		for (int i = 0; i < 4; i++) {
			if(id != i){
				mesa.add(recebeCarta(inFromServer));
				printaCartas(mesa, "mesa");
			}else{
				System.out.println("Escolha carta pelo índice");
				for (int j = 0; j < mao.size(); j++) {
					System.out.print("carta"+(j+1)+" ");
				}
				System.out.println();
				printaCartas(mao, "mao");
				int ind = inFromClient.read()-1;
				System.out.println("Para cima? (s/n)");
				char cima = inFromClient.readLine().charAt(0);
				enviaCarta(mao, ind, cima, outToServer);
				printaCartas(mesa, "mesa");
			}
		}
		printaCartas(mao, "mao");

		inFromClient.close();
		outToServer.close();
		inFromServer.close();
		clientSocket.close();
	}
	
	private static void enviaCarta(List<Carta> mao, int ind, char cima, DataOutputStream outToServer) throws IOException {
		outToServer.write(mao.get(ind).getNaipe());
		outToServer.write(mao.get(ind).getValor());
		if(cima == 'y'){
			outToServer.write(1);
		}else{
			outToServer.write(0);
		}
		
	}

	private static Carta recebeCarta(BufferedReader inFromServer) throws IOException{
		int naipe = inFromServer.read();
		int valorCarta = inFromServer.read();
		Carta carta = new Carta(naipe, valorCarta);
		return carta;
	}

	private static void recebeCarta(List<Carta> mao,
			BufferedReader inFromServer, int i) throws NumberFormatException, IOException {
		for (int j = 0; j < 3; j++) {
			recebeCarta(mao, inFromServer);
		}
		
	}

	private static void recebeCarta(List<Carta> mao, BufferedReader inFromServer) throws NumberFormatException, IOException {
		if(inFromServer == null){
			System.out.println("in from server is null");
		}
		int naipe = inFromServer.read();
		int valorCarta = inFromServer.read();
		mao.add(new Carta(naipe, valorCarta));
	}

	private static void printaCartas(List<Carta> mao, String string) {
		System.out.print(string+": [");
		for(int i = 0; i < mao.size()-1; i++){
			System.out.print(mao.get(i)+", ");
		}
		if(!mao.isEmpty()){
			System.out.print(mao.get(mao.size()-1)+"]");
		}
		System.out.println();
	}
}
