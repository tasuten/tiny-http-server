# 結構適当です
all:
	make HttpServerUtil.class HttpClientHandler.class HttpServer.class \
		IllegalResultException.class
%.class: %.java
	javac $*.java -J-Dfile.encoding=UTF-8
clean:
	rm *.class

