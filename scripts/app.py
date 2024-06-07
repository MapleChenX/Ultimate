import socket

# 创建 socket 对象
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# 连接到服务器
s.connect(('localhost', 9000))

data = "Hello, World!"

dataBytes = bytes(data, 'utf-8')
dataLen = len(dataBytes)

dataLenBytes = dataLen.to_bytes(4, byteorder='big')
dataAll = dataLenBytes + dataBytes

for i in range(100):
    s.sendall(dataAll)
while True:
    print(1)


# 多线程接受服务器的消息



