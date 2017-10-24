import java.io.DataOutputStream;
import java.io.IOException;

public class MensagemCliente {
	private int idJogador; //id jogador que está enviando msg
	private int jogada;
	//0 joga carta pra cima, 1 joga carta pra baixo,
	//2 pede truco, 3 aceita, 4 retruca, 5 foge
	private int naipeCarta; //naipe da carta, caso essa esteja passando
	private int valorCarta; //valor da carta, caso essa esteja passando
	
	public MensagemCliente(int idJogador, int jogada, int naipeCarta, int valorCarta) {
		super();
		this.idJogador = idJogador;
		this.jogada = jogada;
		this.naipeCarta = naipeCarta;
		this.valorCarta = valorCarta;
	}
	
	//construtor para jogadas que não precisam preencher naipe e valor da carta
	public MensagemCliente(int idJogador, int jogada){
		this.idJogador = idJogador;
		this.jogada = jogada;
		this.naipeCarta = 0;
		this.valorCarta = 0;
	}
	
	public void envia(DataOutputStream outToServer) throws IOException{
		//System.out.println(this);
		outToServer.write(idJogador);
		outToServer.write(jogada);
		outToServer.write(naipeCarta);
		outToServer.write(valorCarta);
	}
	
	@Override
	public String toString() {
		return idJogador+" "+jogada+" "+naipeCarta+" "+valorCarta;
	}
	
	public int getJogada() {
		return jogada;
	}
	
	public int getIdJogador() {
		return idJogador;
	}
	
	public Carta getCarta(){
		Carta carta = new Carta(naipeCarta, valorCarta);
		carta.setIdJogador(idJogador);
		return carta;
	}
}
