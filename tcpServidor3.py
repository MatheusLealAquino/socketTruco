from socket import *
import random
from protocolo import *
from baralho import *


class Baralho(object):

    baralho = []
    naipes = ['espadas' , 'paus', 'ouro', 'copas']
    cartas = ['as', 'dois', 'tres', 'quatro', 'cinco', 'seis', 'sete', 'oito', 'nove', 'dez', 'valete', 'dama', 'rei']
    
    for naipe in range(4):
        for carta in range(13):
            baralho.append({cartas[carta]+' de '+ naipes[naipe] : naipe*13+carta})

    def __init__(self):    
        self.baralhoJogo = []
        for naipe in range(4):
            for carta in range(13):
                self.baralhoJogo.append(naipe*13+carta)

baralho = Baralho()
baralhoJogo = baralho.baralhoJogo

class Jogador:

    def __init__(self, serverSocket, k):
        self.mao = []
        self.connectionSocket, self.addr = serverSocket.accept()
        self.id = k
        self.valorMao = 1

def recebeCarta(mao, k = 1):
    for i in range(k):
        ind = random.randint(0,len(baralhoJogo)-1)
        valor = baralhoJogo.pop(ind)
        mao.append(valor)
    return

def printarMao(mao):
    print(mao)
    return

def enviaMaoJogadores(jogadores):
    for i in range(len(jogadores)):
        mao = "\nCartas do jogador "+str((i+1))+": "+str(jogadores[i].mao)
        enviaMensagem(mao,jogadores[i].connectionSocket)

def iniciaMaoJogadores(jogadores):
    for i in range(len(jogadores)):
        recebeCarta(jogadores[i].mao, 3)
        
def daCarta(jogadores):
    for i in range(len(jogadores)):
        recebeCarta(jogadores[i].mao)

def enviaId(jogadores):
    for i in range(len(jogadores)):
        enviaMensagem(jogadores[i].id,jogadores[i].connectionSocket)

def fechandoSocket(jogadores):
    for i in range(len(jogadores)):
        jogadores[i].connectionSocket.close()

def receberDados(jogadores):
    dados = []
    for i in range(len(jogadores)):
        dados.append(recebeMensagem(jogador[i].connectionSocket))
    return dados

#numero de porta na qual o servidor estara esperando conexoes
serverPort = 12001

#criar o socket. AF_INET e SOCK_STREAM indicam TCP
serverSocket = socket(AF_INET, SOCK_STREAM)

#associar o socket a porta escolhida. primeiro argumento vazio indica
#que desejamos aceitar conexoes em qualquer interface de rede desse host
serverSocket.bind(('', serverPort))

#habilitar socket para aceitar conexoes. o argumento 1 indica que ate
#uma conexao sera deixada em espera, caso receba multiplas conexoes
#simultanes
serverSocket.listen(2)


print("O servidor esta pronto para receber conexoes: ")

#aguardar nova conexao
print("Aguardando jogador1...")
j1 = Jogador(serverSocket,1)

print("Aguardando jogador2...")
j2 = Jogador(serverSocket,2)

jogadores = [j1, j2]
print("Todos Jogadores estão prontos!")

#envio
print("Realizando envio...")
enviaMaoJogadores(jogadores)

#recebe
print("aguardando carta")

#fechamento de todos sockets
print("Fechando socket...")
fechandoSocket(jogadores)