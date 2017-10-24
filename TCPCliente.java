import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TCPCliente {
	
	private static int id = 5;
	private static int idSeuTime;
	private static Scanner teclado = new Scanner(System.in);
	private static DataOutputStream outToServer;
	private static BufferedReader inFromServer;
	private static Carta vira;
	private static int idTruco = -1;
	private static int idTimeTruco = -1;

	// protocolo - mensagem servidor
	
	//id jogador ou id time que é requerido que se faça algo ou que está fazendo
	private static int idJogador;
	//0 vez de jogar, 1 jogaram uma carta, 2 trucaram, 3 aceitaram, 4 retrucaram,
	//5 correram, 6 vira, 7 recebe, 8 rodada encerrada, 9 mão encerrada,
	//10 jogar carta, 11 encerra o jogo
	private static int jogada;
	// pontuação do time 1 na rodada
	private static int time1PontosRodada;
	// pontuação do time 2 na rodada
	private static int time2PontosRodada;
	// valor da mão na rodada
	private static int valorMao;
	// 1 true 0 false
	private static int cartaPraBaixo;
	// naipe da carta, caso essa esteja passando
	private static int naipeCarta; 
	// valor da carta, caso essa esteja passando
	private static int valorCarta; 
	// pontuação total do time 1
	private static int time1PontosTotal; 
	// pontuação total do time 2
	private static int time2PontosTotal; 

	public static void main(String[] args) throws UnknownHostException, IOException {
		
		System.out.println("Entrar com ip para conexão");
		String ip = teclado.next();
		System.out.println("Entrar com porta para conexão");
		String porta = teclado.next();
		
		Socket clientSocket = new Socket(ip, Integer.valueOf(porta));
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		id = inFromServer.read();
		idSeuTime = id % 2;
		System.out.println("Seu id: " + (id + 1));
		System.out.println("Você é do time " + (idSeuTime + 1));
		
		for (int i = 0; i < 4-id; i++) {
			System.out.println(inFromServer.readLine());
		}
		
		List<Carta> mao = new ArrayList<>();
		List<Carta> mesa = new ArrayList<>();

		boolean fim = false;

		do {
			recebeMsg();

			int jogadaEscolhida;
			
			//trata jogada segundo protocolo
			switch (jogada) {
			case 0:
				// vez de jogar
				if (idJogador == id) {
					System.out.println("é a sua vez de jogar");
					printaCartas(mao, "mao");
					printaCartas(mesa, "mesa");
					do {
						System.out.println("Jogar carta pra cima(1), carta pra baixo(2), pedir truco(3)");
						jogadaEscolhida = teclado.nextInt();
					} while (jogadaEscolhida != 1 && jogadaEscolhida != 2 && jogadaEscolhida != 3);
					jogadaEscolhida--;
					int naipeCartaEscolhida = 0;
					int valorCartaEscolhida = 0;
					if (jogadaEscolhida != 2) {
						int indiceEscolhido = -1;
						do {
							if(indiceEscolhido != -1)
								System.out.println("Alerta: você só tem "+mao.size()+" cartas na mão");
							System.out.println("Escolha sua carta pelo índice 1/2/3");
							indiceEscolhido = teclado.nextInt();
						} while (indiceEscolhido > mao.size() && indiceEscolhido < 1);
						indiceEscolhido--;
						Carta cartaJogada = mao.remove(indiceEscolhido);
						naipeCartaEscolhida = cartaJogada.getNaipe();
						valorCartaEscolhida = cartaJogada.getValor();
					}
					//envia mensagem cliente
					(new MensagemCliente(id, jogadaEscolhida, naipeCartaEscolhida,
							valorCartaEscolhida)).envia(outToServer);
				} else
					System.out.println("é a vez do jogador " + (idJogador + 1) + " jogar");
				break;
			case 1:
				// jogaram uma carta
				if (cartaPraBaixo == 0) {
					Carta carta = new Carta(naipeCarta, valorCarta);
					carta.setIdJogador(idJogador);
					mesa.add(carta);
					System.out.println("Jogador " + (idJogador + 1) + " jogou a carta " + carta);
				} else
					System.out.println("Jogador " + (idJogador + 1) + " jogou a carta jogada pra baixo");
				break;
			case 2:
				// trucaram
				idTruco = idJogador;
				System.out.println("jogador " + (idJogador + 1) + " trucou!");
				idTimeTruco = idTruco % 2;
				if (idSeuTime != idTimeTruco) {
					do {
						System.out.println("Aceitar(1), Retrucar(2), Correr(3)");
						jogadaEscolhida = teclado.nextInt();
					} while (jogadaEscolhida != 1 && jogadaEscolhida != 2 && jogadaEscolhida != 3);
					jogadaEscolhida += 2;
					MensagemCliente mc = new MensagemCliente(id, jogadaEscolhida);
					System.out.println("aguarde seu parceiro responder");
					if (id > idSeuTime) {
						inFromServer.read(); //bloquear para não enviar antes do outro jogador da equipe
					}
					mc.envia(outToServer);
				}
				break;
			case 3:
				// aceitaram trucada
				System.out.println(
						"Truco aceito pelo time " + ((idJogador % 2) + 1) + ", mão vale " + valorMao + " pontos");
				break;
			case 4:
				// retrucaram
				idTruco = idJogador;
				System.out.println("Time " + ((idJogador % 2) + 1) + " retrucou, mão vale " + valorMao + " pontos");
				idTimeTruco = idTruco % 2;
				if (idSeuTime != idTimeTruco) {
					do {
						System.out.println("Aceitar(1), Correr(2)");
						jogadaEscolhida = teclado.nextInt();
					} while (jogadaEscolhida != 1 && jogadaEscolhida != 2);

					if (jogadaEscolhida == 1)
						jogadaEscolhida = 3;
					if (jogadaEscolhida == 2)
						jogadaEscolhida = 5;
					
					MensagemCliente mc = new MensagemCliente(id, jogadaEscolhida);
					System.out.println("aguarde seu parceiro responder");
					if (id > idSeuTime) {
						inFromServer.read(); //bloquear para não enviar antes do outro jogador da equipe
					}
					mc.envia(outToServer);
				}
				break;
			case 5:
				// correram da trucada
				System.out.println("jogadores do time " + ((idJogador % 2) + 1) + " correram do truco");
				break;
			case 6:
				// vira
				vira = new Carta(naipeCarta, valorCarta);
				System.out.println("vira da rodada é o " + vira);
				break;
			case 7:
				// recebe carta
				if (idJogador == id) {
					Carta carta  = new Carta(naipeCarta, valorCarta);
					mao.add(carta);
					System.out.println("você recebeu a carta "+carta);
				}
				break;
			case 8:
				// rodada encerrada
				mesa = new ArrayList<>();
				System.out.println("fim da rodada");
				System.out.println("Placar rodada");
				System.out.println("TIME 1 - " + time1PontosRodada + " X " + time2PontosRodada + " - TIME 2");
				break;
			case 9:
				// mao encerrada
				mao = new ArrayList<>();
				System.out.println("fim da mão");
				System.out.println("==========================");
				System.out.println("PLACAR TOTAL");
				System.out.println("TIME 1 - " + time1PontosTotal + " X " + time2PontosTotal + " - TIME 2");
				break;
			case 10:
				// vez de jogar carta após pedir truco
				if (idJogador == id) {
					System.out.println("falta jogar uma carta");
					printaCartas(mao, "mao");
					printaCartas(mesa, "mesa");
					do {
						System.out.println("Jogar carta pra cima(1), carta pra baixo(2)");
						jogadaEscolhida = teclado.nextInt();
					} while (jogadaEscolhida != 1 && jogadaEscolhida != 2);
					jogadaEscolhida--;
					int naipeCartaEscolhida = 0;
					int valorCartaEscolhida = 0;
					int indiceEscolhido = -1;
					do{
						if(indiceEscolhido != -1)
							System.out.println("Alerta: você só tem "+mao.size()+" cartas na mão");
						System.out.println("Escolha sua carta pelo índice 1/2/3");
						indiceEscolhido = teclado.nextInt();
					}while(indiceEscolhido > mao.size() && indiceEscolhido < 1);
					indiceEscolhido--;
					
					Carta cartaJogada = mao.remove(indiceEscolhido);
					naipeCartaEscolhida = cartaJogada.getNaipe();
					valorCartaEscolhida = cartaJogada.getValor();
					MensagemCliente mc = new MensagemCliente(id, jogadaEscolhida, naipeCartaEscolhida,
							valorCartaEscolhida);
					mc.envia(outToServer);
				} else
					System.out.println("jogador " + (idJogador + 1) + " ainda tem que jogar a carta");
				break;
			case 11:
				//servidor disse que jogo acabou
				fim = true;
				break;
			}

		} while (!fim);

		teclado.close();
		outToServer.close();
		inFromServer.close();
		clientSocket.close();
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

	// recebe msg do servidor segundo o protocolo de msg definindo em MensagemServidor.java
	private static void recebeMsg() throws IOException {
		idJogador = inFromServer.read();
		jogada = inFromServer.read();
		time1PontosRodada = inFromServer.read();
		time2PontosRodada = inFromServer.read();
		valorMao = inFromServer.read();
		cartaPraBaixo = inFromServer.read();
		naipeCarta = inFromServer.read();
		valorCarta = inFromServer.read();
		time1PontosTotal = inFromServer.read();
		time2PontosTotal = inFromServer.read();

		//print teste
		//MensagemServidor ms = new MensagemServidor(idJogador, jogada, time1PontosRodada, time2PontosRodada, valorMao,
		//		cartaPraBaixo, naipeCarta, valorCarta, time1PontosTotal, time2PontosTotal);
		// System.out.println(ms);
	}

}
