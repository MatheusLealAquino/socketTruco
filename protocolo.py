import sys

def recebeMensagem(socket):
	tamanhoBuffer = socket.recv(35)
	mensagem = socket.recv(int.from_bytes(tamanhoBuffer, byteorder='big'))
	return mensagem.decode('utf-8')

def enviaMensagem(msg,socket):
	tamanhoBuffer = sys.getsizeof(msg.encode('utf-8'))
	socket.send(tamanhoBuffer.to_bytes(2, byteorder='big'))
	socket.send(msg.encode('utf-8'))