from socket import *

serverName = 'localhost'
serverPort = 12000

#criacao do socket
clienteSocket = socket(AF_INET, SOCK_STREAM)
#conexao com o servidor
clienteSocket.connect((serverName,serverPort))

sentence = input("Input lowercase sentence: ")
#envio de bytes
clienteSocket.send(sentence.encode('utf-8'))

#recepcao
retorno = clienteSocket.recv(1024)
modifiedSentence = retorno.decode('utf-8')
print("From Server: ", modifiedSentence)

#fechamento
clienteSocket.close()
