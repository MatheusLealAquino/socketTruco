class Baralho(object):

	baralho = []
	naipes = ['espadas' , 'paus', 'ouro', 'copas']
	cartas = ['as', 'dois', 'tres', 'quatro', 'cinco', 'seis', 'sete', 'oito', 'nove', 'dez', 'valete', 'dama', 'rei']
	
	for naipe in range(4):
		for carta in range(13):
			baralho.append({cartas[carta]+' de '+ naipes[naipe] : naipe*13+carta})

	def __init__(self, arg):	
		self.arg = arg
		self.baralhoJogo = []
		for naipe in range(4):
			for carta in range(13):
				self.baralhoJogo.append(naipe*13+carta)
	

