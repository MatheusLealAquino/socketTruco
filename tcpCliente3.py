from socket import *

serverName = 'localhost'
serverPort = 12001

#criacao do socket
clienteSocket = socket(AF_INET, SOCK_STREAM)
#conexao com o servidor
clienteSocket.connect((serverName,serverPort))

#recebeid
id = clienteSocket.recv(1024)

if(id.decode('utf-8') == '1'):
	sentence = input("Input lowercase sentence: ")
	#envio de bytes
	clienteSocket.send(sentence.encode('utf-8'))

#recepcao
mao = clienteSocket.recv(1024)
modifiedSentence = mao.decode('utf-8')
print("From Server: ", modifiedSentence)

retorno = clienteSocket.recv(1024)
modifiedSentence = retorno.decode('utf-8')
print("From Server: ", modifiedSentence)

#fechamento
clienteSocket.close()
