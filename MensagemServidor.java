import java.io.IOException;


public class MensagemServidor {
	
	//id jogador ou id time que é requerido que se faça algo ou que está fazendo
	int idJogador;
	//0 vez de jogar, 1 jogaram uma carta, 2 trucaram, 3 aceitaram, 4 retrucaram,
	//5 correram, 6 vira, 7 recebe, 8 rodada encerrada, 9 mão encerrada,
	//10 jogar carta, 11 encerra o jogo
	int jogada;
	int time1PontosRodada;
	int time2PontosRodada;
	int valorMao;
	//1 true 0 false
	int cartaPraBaixo;
	//naipe da carta, caso essa esteja passando
	int naipeCarta;
	//valor da carta, caso essa esteja passando
	int valorCarta;
	int time1PontosTotal;
	int time2PontosTotal;
	
	public MensagemServidor(int idJogador, int jogada,
			int time1PontosRodada, int time2PontosRodada, int valorMao, int cartaPraBaixo, int naipeCarta, int valorCarta,
			int time1PontosTotal, int time2PontosTotal) {
		this.idJogador = idJogador;
		this.jogada = jogada;
		this.time1PontosRodada = time1PontosRodada;
		this.time2PontosRodada = time2PontosRodada;
		this.cartaPraBaixo = cartaPraBaixo;
		this.valorMao = valorMao;
		this.naipeCarta = naipeCarta;
		this.valorCarta = valorCarta;
		this.time1PontosTotal = time1PontosTotal;
		this.time2PontosTotal = time2PontosTotal;
	}
	
	
	//construtor para mensagens que não precisam de parametros que já iniciamos com 0
	public MensagemServidor(int jogada, int time1PontosRodada, int time2PontosRodada, int valorMao,
			int time1PontosTotal, int time2PontosTotal) {
		this.idJogador = 0;
		this.cartaPraBaixo = 0;
		this.naipeCarta = 0;
		this.valorCarta = 0;
		this.jogada = jogada;
		this.time1PontosRodada = time1PontosRodada;
		this.time2PontosRodada = time2PontosRodada;
		this.valorMao = valorMao;
		this.time1PontosTotal = time1PontosTotal;
		this.time2PontosTotal = time2PontosTotal;
	}

	public void envia(Jogador[] jogadores) throws IOException{
		System.out.println(this);
		for (int i = 0; i < jogadores.length; i++) {
			jogadores[i].getOutToClient().write(idJogador);
			jogadores[i].getOutToClient().write(jogada);
			jogadores[i].getOutToClient().write(time1PontosRodada);
			jogadores[i].getOutToClient().write(time2PontosRodada);
			jogadores[i].getOutToClient().write(valorMao);
			jogadores[i].getOutToClient().write(cartaPraBaixo);
			jogadores[i].getOutToClient().write(naipeCarta);
			jogadores[i].getOutToClient().write(valorCarta);
			jogadores[i].getOutToClient().write(time1PontosTotal);
			jogadores[i].getOutToClient().write(time2PontosTotal);
		}
	}
	
	@Override
	public String toString() {
		return idJogador+" "+jogada+" "+time1PontosRodada+" "+time2PontosRodada+" "+valorMao+" "+cartaPraBaixo+" "+naipeCarta+" "+valorCarta+" "+time1PontosTotal+" "+time2PontosTotal;
	}
}