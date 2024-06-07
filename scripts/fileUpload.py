import socket

# 创建 socket 对象
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# 连接到服务器
s.connect(('localhost', 9000))

cmd = 2
cmdB = cmd.to_bytes(4, byteorder='big')
filename = "Hello, World!"
filenameBytes = bytes(filename, 'utf-8')
filenameLen = len(filenameBytes)
filenameLenBytes = filenameLen.to_bytes(4, byteorder='big')
filenameAll = cmdB + filenameLenBytes + filenameBytes

# 读取文件
with open('C:\\Users\\25056\Desktop\\aaa\\java-learning\\spring-boot\\The Ultimate\\note.md', 'rb') as file:
    # 读取文件内容
    file_content = file.read()
i = len(file_content)
fileLen = i.to_bytes(4, byteorder='big')
all = filenameAll + fileLen + file_content

s.sendall(all)



