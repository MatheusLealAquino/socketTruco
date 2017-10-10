from socket import *

from protocolo import *

serverName = 'localhost'
serverPort = 12001 

#criacao do socket
clienteSocket = socket(AF_INET, SOCK_STREAM)
#conexao com o servidor
clienteSocket.connect((serverName,serverPort))

#recepcao
mao = recebeMensagem(clienteSocket)
print(mao)

#fechamento
clienteSocket.close()