import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class TCPServidor {
	private static Baralho baralho;

	public static void main(String[] args) throws IOException {
		int porta = Porta.NUM;
		ServerSocket servidor = new ServerSocket(porta);
		System.out.println("Porta " + porta + " aberta!");

		baralho = new Baralho();
		Jogador[] jogadores = new Jogador[4];

		testaBaralhoStatic();

		conecta(servidor, jogadores);

		int pt1 = 0;
		int pt2 = 0;
		
		int ptRodada = 1;
		
		//envia mão inicial
		enviaCarta(jogadores, 3);
		
		//mostra vira
		Carta vira = baralho.tiraCarta();
		System.out.println("vira: " + vira);
		enviaCarta(jogadores, vira);
		
		List<Carta> mesa = new ArrayList<>();
		//primeira rodada
		recebeCarta(jogadores, mesa);

		testaBaralhoStatic();
		
		baralho = new Baralho();

		servidor.close();
	}

	private static void recebeCarta(Jogador[] jogadores, List<Carta> mesa) throws IOException {
		for (int i = 0; i < jogadores.length; i++) {
			mesa.add(recebeCarta(jogadores[i]));
			for (int j = 0; j < jogadores.length; j++) {
				if(j != 1){
					jogadores[j].recebeCarta(mesa.get(mesa.size()-1));
				}
			}
		}
	}
	private static Carta recebeCarta(Jogador jogador) throws IOException {
			int naipe = jogador.inFromClient.read();
			int valorCarta = jogador.inFromClient.read();
			int cima = jogador.inFromClient.read();
			Carta carta = new Carta(naipe, valorCarta);
			if(cima == 0){
				carta.praBaixo();
			}
			return carta;
	}

	private static void fecha(ServerSocket servidor, Jogador[] jogadores)
			throws IOException {
		for (int i = 0; i < jogadores.length; i++) {
			jogadores[i].outToClient.close();
			jogadores[i].socket.close();
		}
	}

	private static void conecta(ServerSocket servidor, Jogador[] jogadores)
			throws IOException {
		for (int i = 0; i < jogadores.length; i++) {
			jogadores[i] = new Jogador(servidor.accept());
			conecta(servidor, jogadores[i], i);
		}
	}

	private static void conecta(ServerSocket servidor, Jogador jogador, int i)
			throws IOException {
		System.out.println("Aguardando conexão do cliente " + i + "...");
		jogador.setOutToClient(new DataOutputStream(jogador.socket
				.getOutputStream()));
		jogador.setInFromClient(new BufferedReader(new InputStreamReader(
				jogador.socket.getInputStream())));
		System.out.println("Nova conexao com o cliente " + i + " "
				+ jogador.socket.getInetAddress().getHostAddress());
		jogador.outToClient.write(i);
	}

	private static void enviaCarta(Jogador[] jogadores, int vezes) throws IOException {
		for (int j = 0; j < vezes; j++) {
			enviaCarta(jogadores);
		}
	}


	private static void enviaCarta(Jogador[] jogadores) throws IOException {
		for (int i = 0; i < jogadores.length; i++) {
			enviaCarta(jogadores[i]);
		}
	}
	
	private static void enviaCarta(Jogador[] jogadores, Carta carta) throws IOException {
		for (int j = 0; j < jogadores.length; j++) {
			enviaCarta(jogadores[j], carta);
		}
	}

	private static void enviaCarta(Jogador jogador, Carta carta) throws IOException {
		if(jogador.outToClient == null){
			System.out.println("out to client is null");
		}
		jogador.outToClient.write(carta.getNaipe());
		jogador.outToClient.write(carta.getValor());
	}

	private static void enviaCarta(Jogador jogador) throws IOException {
		Carta c = baralho.tiraCarta();
		if(jogador.outToClient == null){
			System.out.println("out to client is null");
		}
		jogador.outToClient.write(c.getNaipe());
		jogador.outToClient.write(c.getValor());
	}

	private static void testaBaralhoStatic() {
		System.out.print("tamanho baralho:" + baralho.cartas.size());
		int naipe = 999;
		for (Carta carta : baralho.cartas) {
			if(naipe!=carta.getNaipe()){
				naipe = carta.getNaipe();
				System.out.println();
			}
			System.out.print(carta+", ");
		}
	}
}
