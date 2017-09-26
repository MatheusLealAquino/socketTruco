from socket import *
import random

class Jogador:

    def __init__(self, serverSocket, k):
        self.mao = []
        self.connectionSocket, self.addr = serverSocket.accept()
        self.id = k
        self.connectionSocket.send(str(self.id).encode('utf-8'))

def recebeCarta(mao, k = 1):
    for i in range(k):
        ind = random.randint(0,len(baralho)-1)
        valor = baralho.pop(ind)
        mao.append(valor)
    return

def printarMao(mao):
    print(mao)
    return

def printarMaoJogadores(jogadores):
    for i in range(len(jogadores)):
        mao = "\nCartas do jogador "+str((i+1))+": "+str(jogadores[i].mao)
        jogadores[i].connectionSocket.send(mao.encode('utf-8'))

def iniciaMaoJogadores(jogadores):
    for i in range(len(jogadores)):
        recebeCarta(jogadores[i].mao, 3)


def daCarta(jogadores):
    for i in range(len(jogadores)):
        recebeCarta(jogadores[i].mao)

def fechandoSocket(jogadores):
    for i in range(len(jogadores)):
        jogadores[i].connectionSocket.close()

def receberDados(jogador):
    return jogador.connectionSocket.recv(1024)

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
serverSocket.listen(4)

print("O servidor esta pronto para receber conexoes: ")

baralho = []
naipes = ['espadas' , 'paus', 'ouro', 'copas']
cartas = ['as', 'dois', 'tres', 'quatro', 'cinco', 'seis', 'sete', 'oito', 'nove', 'dez', 'valete', 'dama', 'rei']
for naipe in range(4):
    for carta in range(13):
        baralho.append({cartas[carta]+' de '+ naipes[naipe] : naipe*13+carta})

mao1 = mao2 = mao3 = mao4 = []
valorMao1 = valorMao2 = valorMao3 = valorMao4 = 1 


while (True):
    #aguardar nova conexao
    print("Aguardando jogador1...")
    j1 = Jogador(serverSocket, 1)

    print("Aguardando jogador2...")
    j2 = Jogador(serverSocket, 2)

    print("Aguardando jogador3...")
    j3 = Jogador(serverSocket, 3)

    print("Aguardando jogador4...")
    j4 = Jogador(serverSocket, 4)

    jogadores = [j1, j2, j3, j4]
    print("Todos Jogadores estão prontos!")
    #iniciando a mao de todos jogadores
    iniciaMaoJogadores(jogadores)

    #printando mao dos jogadores para cada jogador
    printarMaoJogadores(jogadores)

    #recepcao de dados
    sentence = receberDados(j1); #connectionSocket1.recv(1024)
    #processamento
    print("Dado recebido do cliente")
    capitalizedSentence = sentence.decode('utf-8').upper()
    #envio
    print("Realizando envio...")
    j1.connectionSocket.send(capitalizedSentence.encode('utf-8'))
    j2.connectionSocket.send(capitalizedSentence.encode('utf-8'))
    j3.connectionSocket.send(capitalizedSentence.encode('utf-8'))
    j4.connectionSocket.send(capitalizedSentence.encode('utf-8'))
    
    #fechamento de todos sockets
    print("Fechando socket...")
    fechandoSocket(jogadores)
