import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TCPServidor {
	private static Carta vira;
	private static Baralho baralho = new Baralho();
	private static final Comparator<Carta> CARTAS_COMPARATOR = new Comparator<Carta>() {
		public int compare(Carta c1, Carta c2) {
			int manilhaValor = vira.getValor() + 1;
			manilhaValor = manilhaValor % 13;
			if (c1.getValor() == c2.getValor()) {
				if (c1.getValor() == manilhaValor)return c1.getNaipe() - c2.getNaipe();
				return 0;
			}
			if (c1.getValor() == manilhaValor)return 1;
			if (c2.getValor() == manilhaValor)return -1;
			return c1.getValor() - c2.getValor();
		}
	};

	public static void main(String[] args) throws IOException {
		int porta = Porta.NUM;
		ServerSocket servidor = new ServerSocket(porta);
		System.out.println("Porta " + porta + " aberta!");

		Jogador[] jogadores = new Jogador[4];

		conecta(servidor, jogadores);

		Placar placar = new Placar();

		do {
			List<List<Carta>> mesas = new ArrayList<>();
			testaBaralhoStatic();
			baralho = new Baralho();
			enviaCarta(jogadores, 3);

			for (int k = 0; k < 3; k++) {
				PlacarMao placarMao = new PlacarMao();
				System.out.println("vira: ");
				vira = baralho.tiraCarta();
				enviaCarta(jogadores, vira);
				List<Carta> mesa = new ArrayList<>();
				mesas.add(mesa);
				for (int j = k; j < jogadores.length; j++) {
					turnoRodada(jogadores, mesa, j);
				}
				for (int j = 0; j < k; j++) {
					turnoRodada(jogadores, mesa, j);
				}
				calculaPontos(placarMao, mesa);
				enviaPlacarMao(jogadores, placarMao);
				if (placarMao.continua == 0) {
					if(placarMao.time1 != placarMao.time2){
						if(placarMao.time1>placarMao.time2){
							placar.time1 += placar.valorMao;
						}
					}
					break;
				}
			}
			testaBaralhoStatic();
			enviaPlacar(jogadores, placar);
		} while (!placar.fimDeJogo);

		fecha(servidor, jogadores);
		servidor.close();
	}

	protected static void turnoRodada(Jogador[] jogadores, List<Carta> mesa,
			int j) throws IOException {
		Carta cartarecebida = recebeCarta(jogadores[j]);
		mesa.add(cartarecebida);
		for (int i = 0; i < jogadores.length; i++) {
			if (i != j) {
				if (cartarecebida.isPraCima()) {
					enviaCarta(jogadores[i], cartarecebida);
				} else {
					enviaCarta(jogadores[i], new Carta(Naipe.PRA_BAIXO,
							ValorCarta.PRA_BAIXO));
				}
			}
		}
		printaCartas(mesa, "mesa");
	}

	private static void calculaPontos(PlacarMao placarMao, List<Carta> mesa) {
		Collections.sort(mesa, CARTAS_COMPARATOR);
		System.out.println("PONTOS RODADA");
		boolean empate = true;
		int manilha = vira.getValor()+1;
		manilha = manilha%13;
		int ind = mesa.size()-1;
		System.out.println(manilha);
		for (Carta carta : mesa) {
			if(carta.getValor() == manilha){
				empate = false;
			}
		}
		if(empate){
			if(mesa.get(ind).getValor() == mesa.get(ind-1).getValor())return;
		}
		int idVencedor = mesa.get(mesa.size() - 1).getIdJogador();
		if (idVencedor == 0 || idVencedor == 2) {
			System.out.println("RODADA VENCIDA PELA EQUIPE JOGADOR 0 e 2");
			placarMao.time1++;
		} else {
			System.out.println("RODADA VENCIDA PELA EQUIPE JOGADOR 1 e 3");
			placarMao.time2++;
		}
	}

	private static Carta recebeCarta(Jogador jogador) throws IOException {
		int naipe = jogador.inFromClient.read();
		int valorCarta = jogador.inFromClient.read();
		int cima = jogador.inFromClient.read();
		Carta carta = new Carta(naipe, valorCarta);
		if (cima == 0) {
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

	private static void enviaCarta(Jogador[] jogadores, int vezes)
			throws IOException {
		for (int j = 0; j < vezes; j++) {
			enviaCarta(jogadores);
		}
	}

	private static void enviaCarta(Jogador[] jogadores) throws IOException {
		for (int i = 0; i < jogadores.length; i++) {
			enviaCarta(jogadores[i]);
		}
	}

	private static void enviaCarta(Jogador[] jogadores, Carta carta)
			throws IOException {
		for (int j = 0; j < jogadores.length; j++) {
			enviaCarta(jogadores[j], carta);
		}
	}

	private static void enviaCarta(Jogador jogador, Carta carta)
			throws IOException {
		jogador.outToClient.write(carta.getNaipe());
		jogador.outToClient.write(carta.getValor());
	}

	private static void enviaCarta(Jogador jogador) throws IOException {
		Carta c = baralho.tiraCarta();
		jogador.outToClient.write(c.getNaipe());
		jogador.outToClient.write(c.getValor());
	}

	private static void enviaPlacar(Jogador jogador, Placar placar)
			throws IOException {
		int pontuacao02 = placar.time1;
		int pontuacao13 = placar.time2;

		jogador.outToClient.write(pontuacao02);
		jogador.outToClient.write(pontuacao13);
	}

	private static void enviaPlacar(Jogador[] jogadores, Placar placar)
			throws IOException {
		for (int i = 0; i < jogadores.length; i++) {
			enviaPlacar(jogadores[i], placar);
		}
	}

	private static void enviaPlacarMao(Jogador jogador, PlacarMao placarMao)
			throws IOException {
		int pontuacao02 = placarMao.time1;
		int pontuacao13 = placarMao.time2;
		int continua = 1;
		if (pontuacao02 == 2 || pontuacao13 == 2) {
			continua = 0;
		}
		jogador.outToClient.write(pontuacao02);
		jogador.outToClient.write(pontuacao13);
		jogador.outToClient.write(continua);
	}

	private static void enviaPlacarMao(Jogador[] jogadores, PlacarMao placarMao)
			throws IOException {
		for (int i = 0; i < jogadores.length; i++) {
			enviaPlacarMao(jogadores[i], placarMao);
		}
	}

	private static void testaBaralhoStatic() {
		System.out.print("tamanho baralho:" + baralho.cartas.size());
		int naipe = 999;
		for (Carta carta : baralho.cartas) {
			if (naipe != carta.getNaipe()) {
				naipe = carta.getNaipe();
				System.out.println();
			}
			System.out.print(carta + ", ");
		}
	}

	private static void printaCartas(List<Carta> cartas, String string) {
		if (cartas.isEmpty())
			return;
		System.out.print(string + ": [");
		for (int i = 0; i < cartas.size() - 1; i++) {
			System.out.print(cartas.get(i) + ", ");
		}
		if (!cartas.isEmpty()) {
			System.out.print(cartas.get(cartas.size() - 1) + "]");
		}
		System.out.println();
	}

}
