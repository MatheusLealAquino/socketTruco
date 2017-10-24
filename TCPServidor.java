import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class TCPServidor {
	private static Baralho baralho;
	
	private static int valorMao; //quanto vale a mão
	private static Carta vira; //vira da rodada
	private static List<Carta> mesa; //lista de cartas da mesa

	private static int lastTruco; //guarda ultimo time que pediu truco
	private static boolean trucoNegado; //guarda se algum time correu do truco
	
	//arrays para saber pontos na rodada e pontos totais
	private static int timePontosTotal[] = new int[2];
	private static int timePontosRodada[] = new int[2];

	//arrays para ter histórico das rodadas em uma mão
	private static boolean empates[] = new boolean[3];
	private static int vencedoresRodada[] = new int[3];

	public static void main(String[] args) throws IOException, InterruptedException {

		Scanner scan = new Scanner(System.in);
		System.out.println("Escolha porta para abrir o servidor");
		int porta = scan.nextInt();
		ServerSocket servidor = new ServerSocket(porta);
		System.out.println("Porta " + porta + " aberta!");

		Jogador[] jogadores = new Jogador[4];

		conecta(servidor, jogadores);
		
		timePontosTotal[0] = 0;
		timePontosTotal[1] = 0;
		int numMao = 1;
		do {
			System.out.println("mão nº" + numMao);
			//inicia todos paramentros para nova mão
			empates = new boolean[3];
			empates[0] = empates[1] = empates[2] = false;
			vencedoresRodada = new int[3];
			vencedoresRodada[0] = vencedoresRodada[1] = vencedoresRodada[2] = 2;
			timePontosRodada[0] = 0;
			timePontosRodada[1] = 0;
			valorMao = 1;
			lastTruco = -1;
			trucoNegado = false;
			baralho = new Baralho();

			for (int i = 0; i < jogadores.length; i++) {
				for (int j = 0; j < 3; j++) {
					Carta carta = baralho.tiraCarta();
					
					//envia mao de jogadores
					(new MensagemServidor(jogadores[i].getId(), 7, timePontosRodada[0],
							timePontosRodada[1], valorMao, 0, carta.getNaipe(), carta.getValor(), timePontosTotal[0],
							timePontosTotal[1])).envia(jogadores);
				}
			}

			for (int k = 0; k < 3; k++) {
				System.out.println("rodada nº" + (k+1));
				mesa = new ArrayList<>();
				vira = baralho.tiraCarta();
				//mensagem de vira
				(new MensagemServidor(4, 6, timePontosRodada[0], timePontosRodada[1], valorMao, 0, vira.getNaipe(),
						vira.getValor(), timePontosTotal[0], timePontosTotal[1])).envia(jogadores);

				for (int j = k; j < jogadores.length; j++) {
					if (trucoNegado)
						break;
					rodada(jogadores, j);
				}
				for (int j = 0; j < k; j++) {
					if (trucoNegado)
						break;
					rodada(jogadores, j);
				}
				if (trucoNegado)
					break;
				updateRodada(k);
				//mensagem de fim de rodada
				(new MensagemServidor(8, timePontosRodada[0], timePontosRodada[1], valorMao, timePontosTotal[0],
						timePontosTotal[1])).envia(jogadores);
				//caso em que a primeira rodada deu empate e a segunda foi vencida
				if (k == 1 && empates[0] == true && empates[1] == false) {
					break;
				}
				//caso que mesmo time venceu duas jogadas seguidas
				if (vencedoresRodada[0] == vencedoresRodada[1] && vencedoresRodada[0] != 2) {
					break;
				}
			}
			//se correram do truco o caso já foi tratado na função rodada
			if (!trucoNegado) {
				//só entra se não houver empate triplo
				if (!(empates[0] == empates[1] && empates[1] == empates[2] && empates[0] == true)) {
					if (timePontosRodada[0] != timePontosRodada[1]) {
						//caso em que algum time tem mais pontos que o outro
						if (timePontosRodada[0] > timePontosRodada[1])
							timePontosTotal[0] += valorMao;
						else
							timePontosTotal[1] += valorMao;
					} else {
						//caso em que não foram três empates o primeiro time sempre ganha
						if (timePontosRodada[0] == 1) {
							timePontosRodada[vencedoresRodada[0]] += valorMao;
						}
						
					}
				}
			}
			//mensagem de fim de mão
			(new MensagemServidor(9, timePontosRodada[0], timePontosRodada[1], valorMao, timePontosTotal[0],
					timePontosTotal[1])).envia(jogadores);
			numMao++;
		} while (timePontosTotal[0] < 12 && timePontosTotal[1] < 12);
		
		(new MensagemServidor(11, timePontosRodada[0], timePontosRodada[1], valorMao, timePontosTotal[0],
				timePontosTotal[1])).envia(jogadores);
		fecha(servidor, jogadores);
		servidor.close();
		scan.close();
	}

	//função que auxiliada ao sort de cartas trata os casos possíveis para ver qual carta venceu a rodada
	//se houve empate e faz update nos arrays que carregam o histórico de empates e times vencedores
	private static void updateRodada(int n) {
		if (mesa == null) {
			System.out.println("mesa vazia");
			empates[n] = true;
			return;
		}
		Collections.sort(mesa, CARTAS_COMPARATOR);
		Carta ultima = mesa.get(mesa.size() - 1);
		if (mesa.size() == 1) {
			System.out.println("mesa de só uma pessoa");
			timePontosRodada[ultima.getIdJogador() % 2]++;
			vencedoresRodada[n] = ultima.getIdJogador() % 2;
			return;
		}
		Carta penultima = mesa.get(mesa.size() - 2);
		if (ultima.getIdJogador() % 2 == penultima.getIdJogador() % 2) {
			if (mesa.size() > 2) {
				int compare = CARTAS_COMPARATOR.compare(ultima, mesa.get(mesa.size() - 2));
				if (compare != 0) {
					timePontosRodada[ultima.getIdJogador() % 2]++;
					vencedoresRodada[n] = ultima.getIdJogador() % 2;
					return;
				}
			} else {
				timePontosRodada[ultima.getIdJogador() % 2]++;
				vencedoresRodada[n] = ultima.getIdJogador() % 2;
				return;
			}
		} else {
			if (CARTAS_COMPARATOR.compare(ultima, penultima) != 0) {
				timePontosRodada[ultima.getIdJogador() % 2]++;
				vencedoresRodada[n] = ultima.getIdJogador() % 2;
				return;
			}
		}
		empates[n] = true;
	}

	//função usada para tratar a vez de cada jogador na rodada
	private static void rodada(Jogador[] jogadores, int j) throws IOException {
		(new MensagemServidor(j, 0, timePontosRodada[0], timePontosRodada[1], valorMao, 0, 0, 0, timePontosTotal[0],
				timePontosTotal[1])).envia(jogadores);
		MensagemCliente msgCliente = recebeMsg(jogadores[j]);
		Carta carta;
		switch (msgCliente.getJogada()) {
		case 0:
			// carta pra cima
			carta = msgCliente.getCarta();
			carta.setIdJogador(j);
			mesa.add(carta);
			(new MensagemServidor(j, 1, timePontosRodada[0], timePontosRodada[1], valorMao, 0, carta.getNaipe(),
					carta.getValor(), timePontosTotal[0], timePontosTotal[1])).envia(jogadores);
			break;
		case 1:
			// carta pra baixo
			carta = msgCliente.getCarta();
			carta.setIdJogador(j);
			(new MensagemServidor(j, 1, timePontosRodada[0], timePontosRodada[1], valorMao, 1, carta.getNaipe(),
					carta.getValor(), timePontosTotal[0], timePontosTotal[1])).envia(jogadores);
			break;
		case 2:
			// pedido de truco
			if (valorMao == 12 || lastTruco % 2 == j % 2) {
				//caso pedido de truco for inválido chamada recursiva com requisição de jogada novamente
				rodada(jogadores, j);
			} else {
				lastTruco = j;
				trataPedidoTruco(jogadores, j);
			}
			break;
		}

	}

	//função para depois de um pedido de truco ser usada para requisitar carta do jogador
	private static void rodadaPosTruco(Jogador[] jogadores, int j) throws IOException {
		(new MensagemServidor(j, 10, timePontosRodada[0], timePontosRodada[1], valorMao, 0, 0, 0, timePontosTotal[0],
				timePontosTotal[1])).envia(jogadores);
		MensagemCliente msgCliente = recebeMsg(jogadores[j]);
		Carta carta;
		switch (msgCliente.getJogada()) {
		case 0:
			// carta pra cima
			carta = msgCliente.getCarta();
			carta.setIdJogador(j);
			mesa.add(carta);
			(new MensagemServidor(j, 1, timePontosRodada[0], timePontosRodada[1], valorMao, 0, carta.getNaipe(),
					carta.getValor(), timePontosTotal[0], timePontosTotal[1])).envia(jogadores);
			break;
		case 1:
			// carta pra baixo
			carta = msgCliente.getCarta();
			carta.setIdJogador(j);
			(new MensagemServidor(j, 1, timePontosRodada[0], timePontosRodada[1], valorMao, 1, carta.getNaipe(),
					carta.getValor(), timePontosTotal[0], timePontosTotal[1])).envia(jogadores);
			break;
		}
	}

	//trata os pedidos de truco
	protected static void trataPedidoTruco(Jogador[] jogadores, int j) throws IOException {
		(new MensagemServidor(j, 2, timePontosRodada[0], timePontosRodada[1], valorMao, 0, 0, 0, timePontosTotal[0],
				timePontosTotal[1])).envia(jogadores);
		int jogada1 = recebeMsg(jogadores[(j + 1) % 2]).getJogada();
		//usada para fazer a resposta do segundo jogador da dupla vir depois
		jogadores[(j + 1) % 2 + 2].getOutToClient().write(1);
		int jogada2 = recebeMsg(jogadores[(j + 1) % 2 + 2]).getJogada();
		if (jogada1 == 5 || jogada2 == 5) {
			// correram
			(new MensagemServidor((j + 1) % 2, 5, timePontosRodada[0], timePontosRodada[1], valorMao, 0, 0, 0,
					timePontosTotal[0], timePontosTotal[1])).envia(jogadores);
			trucoNegado = true;
			timePontosTotal[lastTruco % 2] += valorMao;
		} else if (jogada1 == 3 || jogada2 == 3) {
			// aceitaram
			incrementaValorMao();
			(new MensagemServidor((j + 1) % 2, 3, timePontosRodada[0], timePontosRodada[1], valorMao, 0, 0, 0,
					timePontosTotal[0], timePontosTotal[1])).envia(jogadores);
			rodadaPosTruco(jogadores, j);

		} else if (jogada1 == 4 && jogada2 == 4) {
			// retrucaram
			incrementaValorMao();
			(new MensagemServidor((j + 1) % 2, 4, timePontosRodada[0], timePontosRodada[1], valorMao, 0, 0, 0,
					timePontosTotal[0], timePontosTotal[1])).envia(jogadores);
			if (valorMao != 12) {
				jogada1 = recebeMsg(jogadores[j % 2]).getJogada();
				jogadores[j % 2 + 2].getOutToClient().write(1);
				jogada2 = recebeMsg(jogadores[j % 2 + 2]).getJogada();

				if (jogada1 == 3 && jogada2 == 3) {
					// aceitaram
					incrementaValorMao();
					(new MensagemServidor(j % 2, 3, timePontosRodada[0], timePontosRodada[1], valorMao, 0, 0, 0,
							timePontosTotal[0], timePontosTotal[1])).envia(jogadores);
					rodadaPosTruco(jogadores, j);
				} else {
					// fugiram
					(new MensagemServidor(j % 2, 5, timePontosRodada[0], timePontosRodada[1], valorMao, 0, 0, 0,
							timePontosTotal[0], timePontosTotal[1])).envia(jogadores);
					trucoNegado = true;
					timePontosTotal[(lastTruco+1) % 2] += valorMao;
				}
			} else {
				// nao pode retrucar então faz chamada recursiva para enviar a msg de requisição de jogada no cliente
				trataPedidoTruco(jogadores, j);
			}
		}
	}

	//função para aumentar valor da mao
	private static void incrementaValorMao() {
		switch (valorMao) {
		case 1:
			valorMao = 3;
			break;
		case 3:
			valorMao = 6;
			break;
		case 6:
			valorMao = 9;
			break;
		case 9:
			valorMao = 12;
			break;
		}
	}

	// recebe msg do cliente segundo o protocolo de msg definindo em MensagemCliente.java
	private static MensagemCliente recebeMsg(Jogador jogador) throws IOException {
		int idJogador = jogador.getInFromClient().read();
		int jogada = jogador.getInFromClient().read();
		int naipeCarta = jogador.getInFromClient().read();
		int valorCarta = jogador.getInFromClient().read();

		MensagemCliente mc = new MensagemCliente(idJogador, jogada, naipeCarta, valorCarta);
		System.out.println(mc);
		return mc;
	}

	//funções que criam uma instância de jogador e o conectam
	private static void conecta(ServerSocket servidor, Jogador[] jogadores) throws IOException {
		for (int i = 0; i < jogadores.length; i++) {
			//for para enviar quantos jogares faltam
			for (int j = 0; j < i; j++) {
				jogadores[j].getOutToClient().writeBytes("faltam " + (4-i) + " jogadores\n");
			}
			jogadores[i] = new Jogador(servidor.accept());
			jogadores[i].setId(i);
			conecta(servidor, jogadores[i]);
		}
		for (int j = 0; j < 4; j++) {
			jogadores[j].getOutToClient().writeBytes("todos jogadores entraram\n");
		}
	}
	
	private static void conecta(ServerSocket servidor, Jogador jogador) throws IOException {
		jogador.setOutToClient(new DataOutputStream(jogador.getSocket().getOutputStream()));
		jogador.setInFromClient(new BufferedReader(new InputStreamReader(jogador.getSocket().getInputStream())));
		System.out.println("Nova conexao com o cliente " + jogador.getId() + " "
				+ jogador.getSocket().getInetAddress().getHostAddress());
		jogador.getOutToClient().write(jogador.getId());
	}

	private static void fecha(ServerSocket servidor, Jogador[] jogadores) throws IOException {
		for (int i = 0; i < jogadores.length; i++) {
			jogadores[i].getOutToClient().close();
			jogadores[i].getSocket().close();
		}
	}

	//comparator de cartas, para arrumá-las segundo o vira da rodada
	private static final Comparator<Carta> CARTAS_COMPARATOR = new Comparator<Carta>() {
		public int compare(Carta c1, Carta c2) {
			int valor = vira.getValor();
			int valorManilha = (valor + 1) % 13;
			if (c1.getValor() == valorManilha && c2.getValor() == valorManilha) {
				return c1.getNaipe() - c2.getNaipe();
			} else if (c1.getValor() == valorManilha) {
				return 1;
			} else if (c2.getValor() == valorManilha) {
				return -1;
			} else
				return c1.getValor() - c2.getValor();
		}
	};
}
