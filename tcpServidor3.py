from socket import *

#numero de porta na qual o servidor estara esperando conexoes
serverPort = 12000

#criar o socket. AF_INET e SOCK_STREAM indicam TCP
serverSocket = socket(AF_INET, SOCK_STREAM)

#associar o socket a porta escolhida. primeiro argumento vazio indica
#que desejamos aceitar conexoes em qualquer interface de rede desse host
serverSocket.bind(('', serverPort))

#habilitar socket para aceitar conexoes. o argumento 1 indica que ate
#uma conexao sera deixada em espera, caso receba multiplas conexoes
#simultanes
serverSocket.listen(1)

print("O servidor esta pronto para receber conexoes: ")

while (True):
    #aguardar nova conexao
    print("Aguardando conexao...")
    connectionSocket, addr = serverSocket.accept()
    print("Nova conexao recebida!")

    #recepcao de dados
    print("Aguardando dados...")
    sentence = connectionSocket.recv(1024)
    #processamento
    print("Dado recebido do cliente")
    capitalizedSentence = sentence.decode('utf-8').upper()
    #envio
    print("Realizando envio...")
    connectionSocket.send(capitalizedSentence.encode('utf-8'))
    #fechamento
    print("Fechando socket...")
    connectionSocket.close()
