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

	public static void main(String[] args) throws UnknownHostException, IOException {
		int porta = Porta.NUM;
		Socket clientSocket = new Socket("127.0.0.1", porta);
		Baralho baralho = new Baralho();
		Scanner teclado = new Scanner(System.in);
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		id = inFromServer.read();
		System.out.println("id: " + id);

		Placar placar = new Placar();

		do {
			List<Carta> mao = new ArrayList<>();
			recebeCarta(mao, inFromServer, 3);
			for (int k = 0; k < 3; k++) {
				printaCartas(mao, "mao");
				Carta vira = recebeCarta(inFromServer);
				System.out.println("vira: " + vira);
				List<Carta> mesa = new ArrayList<>();
				for (int i = k; i < 4; i++) {
					turnoRodada(teclado, outToServer, inFromServer, mao, mesa, i);
				}
				for (int i = 0; i < k; i++) {
					turnoRodada(teclado, outToServer, inFromServer, mao, mesa, i);
				}
			}
			recebeFim(inFromServer, placar);
		} while (!placar.fimDeJogo);

		if (placar.vencedor == true) {
			System.out.println("Você venceu :D");
		} else {
			System.out.println("Você perdeu =(");
		}

		teclado.close();
		outToServer.close();
		inFromServer.close();
		clientSocket.close();
	}

	protected static void turnoRodada(Scanner teclado, DataOutputStream outToServer, BufferedReader inFromServer,
			List<Carta> mao, List<Carta> mesa, int i) throws IOException {
		if (id == i) {
			System.out.println("Escolha carta pelo índice");
			printaCartas(mao, "mao");
			for (int j = 0; j < mao.size(); j++) {
				System.out.print("carta" + (j + 1) + " ");
			}
			System.out.println();
			int ind = teclado.nextInt();
			System.out.println("Para cima? (s/n)");
			char cima = teclado.next().charAt(0);
			Carta cartaenviada = enviaCarta(mao, ind - 1, cima, outToServer);
			mesa.add(cartaenviada);
			printaCartas(mao, "mao");
		} else {
			mesa.add(recebeCarta(inFromServer));
		}
		printaCartas(mesa, "mesa");
	}

	private static Carta enviaCarta(List<Carta> mao, int ind, char cima, DataOutputStream outToServer)
			throws IOException {
		outToServer.write(mao.get(ind).getNaipe());
		outToServer.write(mao.get(ind).getValor());
		Carta carta = mao.remove(ind);
		if (cima == 's') {
			outToServer.write(1);
			carta.praCima();
		} else {
			carta.praBaixo();
			outToServer.write(0);
		}
		return carta;
	}

	private static Carta recebeCarta(BufferedReader inFromServer) throws IOException {
		int naipe = inFromServer.read();
		int valorCarta = inFromServer.read();
		Carta carta = new Carta(naipe, valorCarta);
		return carta;
	}

	private static void recebeCarta(List<Carta> mao, BufferedReader inFromServer, int i)
			throws NumberFormatException, IOException {
		for (int j = 0; j < 3; j++) {
			recebeCarta(mao, inFromServer);
		}

	}

	private static void recebeCarta(List<Carta> mao, BufferedReader inFromServer)
			throws NumberFormatException, IOException {
		int naipe = inFromServer.read();
		int valorCarta = inFromServer.read();
		Carta carta = new Carta(naipe, valorCarta);
		carta.setIdJogador(id);
		mao.add(carta);
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

	private static void recebeFim(BufferedReader inFromServer, Placar placar) throws IOException {
		int pontuacao02 = inFromServer.read();
		int pontuacao13 = inFromServer.read();
		if (pontuacao02 >= 12 || pontuacao13 >= 12) {
			placar.fimDeJogo = true;
		}
		if (id == 0 || id == 2) {
			placar.pontosTime = pontuacao02;
			placar.pontosAdversario = pontuacao13;
		} else if (id == 1 || id == 3) {
			placar.pontosTime = pontuacao13;
			placar.pontosAdversario = pontuacao02;
		}
		if (placar.pontosTime > placar.pontosAdversario)
			placar.vencedor = true;
	}
}
