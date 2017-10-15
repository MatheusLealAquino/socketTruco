
import java.util.ArrayList;
import java.util.List;


public class Baralho {
	public List<Carta> cartas = new ArrayList<Carta>();
	
	public Baralho(){
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 13; j++) {
				cartas.add(new Carta(i, j));
			}
		}
	}
	
	public Carta tiraCarta(){
		int index = (int)Math.floor(Math.random()*cartas.size());
		Carta c = cartas.remove(index);
		System.out.println(c);
		return c;
	}
	
	public Carta converte(int naipe, int cartaValor){
		return cartas.get(naipe*13+cartaValor);
	}
}
