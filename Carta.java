import java.io.Serializable;


public class Carta{
	private int idJogador;
	private int naipe;
	private int valor;
	private boolean cima = true;
	
	public Carta(int naipe, int valor){
		this.naipe = naipe;
		this.valor = valor;
	}
	
	public void setIdJogador(int idJogador) {
		this.idJogador = idJogador;
	}
	
	public int getIdJogador() {
		return idJogador;
	}
	
	public void praBaixo(){
		this.cima = false;
	}
	

	public void praCima() {
		this.cima = true;
	}
	
	public boolean isPraCima() {
		return cima;
	}

	public int getNaipe() {
		switch (this.naipe) {
		case Naipe.OURO:
			return Naipe.OURO;
		case Naipe.PAUS:
			return Naipe.PAUS;
		case Naipe.COPAS:
			return Naipe.COPAS;
		case Naipe.ESPADAS:
			return Naipe.ESPADAS;
		case Naipe.PRA_BAIXO:
			return Naipe.PRA_BAIXO;
		default:
			return -1;
		}
	}

	public int getValor() {
		switch (this.valor) {
		case ValorCarta.AS:
			return ValorCarta.AS;
		case ValorCarta.DOIS:
			return ValorCarta.DOIS;
		case ValorCarta.TRES:
			return ValorCarta.TRES;
		case ValorCarta.QUATRO:
			return ValorCarta.QUATRO;
		case ValorCarta.CINCO:
			return ValorCarta.CINCO;
		case ValorCarta.SEIS:
			return ValorCarta.SEIS;
		case ValorCarta.SETE:
			return ValorCarta.SETE;
		case ValorCarta.OITO:
			return ValorCarta.OITO;
		case ValorCarta.NOVE:
			return ValorCarta.NOVE;
		case ValorCarta.DEZ:
			return ValorCarta.DEZ;
		case ValorCarta.VALETE:
			return ValorCarta.VALETE;
		case ValorCarta.DAMA:
			return ValorCarta.DAMA;
		case ValorCarta.REI:
			return ValorCarta.REI;
		case ValorCarta.PRA_BAIXO:
			return ValorCarta.PRA_BAIXO;
		default:
			return -1;
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		switch (this.valor) {
		case ValorCarta.AS:
			sb.append("�s");
			break;
		case ValorCarta.DOIS:
			sb.append("dois");
			break;
		case ValorCarta.TRES:
			sb.append("tr�s");
			break;
		case ValorCarta.QUATRO:
			sb.append("quatro");
			break;
		case ValorCarta.CINCO:
			sb.append("cinco");
			break;
		case ValorCarta.SEIS:
			sb.append("seis");
			break;
		case ValorCarta.SETE:
			sb.append("sete");
			break;
		case ValorCarta.OITO:
			sb.append("oito");
			break;
		case ValorCarta.NOVE:
			sb.append("nove");
			break;
		case ValorCarta.DEZ:
			sb.append("dez");
			break;
		case ValorCarta.VALETE:
			sb.append("valete");
			break;
		case ValorCarta.DAMA:
			sb.append("dama");
			break;
		case ValorCarta.REI:
			sb.append("rei");
			break;
		case ValorCarta.PRA_BAIXO:
			sb.append("carta");
			break;
		default:
			sb.append("nada");
			break;
		}
		switch (this.naipe) {
		case Naipe.OURO:
			sb.append(" de ouro");
			break;
		case Naipe.PAUS:
			sb.append(" de paus");
			break;
		case Naipe.COPAS:
			sb.append(" de copas");
			break;
		case Naipe.ESPADAS:
			sb.append(" de espadas");
			break;
		case Naipe.PRA_BAIXO:
			sb.append(" pra baixo");
			break;
		default:
			sb.append(" de nada");
			break;
		}
		
		if(!isPraCima()){
			sb.append("(virado pra baixo)");
		}
		
		return sb.toString();
	}


}
