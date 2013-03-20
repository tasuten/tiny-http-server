import java.net.*;
import java.io.*;

class HttpServer{
    private ServerSocket server;
    private int port;

    /* コンストラクタ
     * 引数はポート番号 */
    public HttpServer(int p) {
        this.port = p;
    }

    private void listen(){
        Socket socket = null;
        HttpClientHandler handler = null;
        try{
            server = new ServerSocket(port);
            System.out.println("HTTP Server started with port " + port);
            while(true){
                /* 接続がある度にHttpClientHandlerをそれぞれ作成し、対応させる */
                socket = server.accept();

                handler = new HttpClientHandler(socket);
                handler.start();
            }

        } catch(IOException e){
            e.printStackTrace();
        }
    }

    /* 第一引数をポート番号とみなし、そのポート番号でサーバを起動
     * 無指定なら8080番 */
    public static void main(String[] args){
        int port;
        if (args.length == 0) {
            port = 8080;
        } else {
            port = Integer.parseInt(args[0]);
        }
        HttpServer server = new HttpServer(port);
        server.listen();
    }
}
