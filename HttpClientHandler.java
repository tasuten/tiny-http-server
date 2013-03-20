import java.io.*;
import java.net.*;

class HttpClientHandler extends Thread{
    /* フィールド扱いのためこれらの変数はnullで初期化される */
    private Socket socket;
    private BufferedReader inString; /* クライアントからの文字列受け取りに用いる */
    private BufferedWriter outString; /* クライアントへの文字列送信に用いる */
    private BufferedOutputStream outBinary; /* クライアントへのバイナリ送信に用いる */
    private BufferedInputStream fileIn; /* ファイルからの読み込みに用いる */

    /* 実装済みメソッド */
    private static final String[] IMPLEMENTED_METHOD = {
        "GET", "HEAD", "OPTIONS"
    };

    /* コンストラクタ */
    public HttpClientHandler(Socket socket){
        this.socket = socket;
    }

    public void run(){
        try{
            // 各種ストリームを開く
            open();

            /* クライアントからのリクエストの1行目の
             * リクエストラインを受け取る */
            String line = receive();
            /* クライアントから送信されず接続が切れた場合nullが帰ってくるので */
            if (line == null) {
                throw new NullPointerException("connection closed");
            }
            String[] requestLine = line.split(" ");
            /* リクエストラインが妥当では無い場合 */
            if (requestLine.length != 3) {
                errorResponse("400 Bad Request", "Illegal request line");
                throw new IllegalResultException("400 Bad Request");
            }

            String method = requestLine[0];
            String uri = requestLine[1];
            String httpVer = requestLine[2];

            /* 実装済みで無いメソッドで来た場合 */
            if (!(HttpServerUtil.isMember(IMPLEMENTED_METHOD, method))) {
                errorResponse("501 Not Implemented", "Method " + method + " is Not Implemented.");
                throw new IllegalResultException("501 Not Implemented");
            }

            // 簡易なディレクトリトラバーサル対策
            if (uri.contains("..")){
                errorResponse("400 Bad Request", "Illegal request line");
                throw new IllegalResultException("400 Bad Request");
            }

            // URIのパスは、このサーバが起動しているディレクトリからと見做す
            File targetFile = new File("./" + uri);

            /* OPTIONSメソッドはサポートしているメソッドを返す
             * 本来ならば、各リソースそれぞれに対して可能なメソッドを返すべきだが、
             * ここではGETとHEADとOPTIONSしか実装してないのでどんなリソース相手でも同じ結果を返すようにしている */
            if (method.equals("OPTIONS")) {
                send("HTTP/1.1 200 OK");
                System.out.println("200 OK");
                send("Allow: " + IMPLEMENTED_METHOD[0] + ", "+ IMPLEMENTED_METHOD[1]);
            } else { /* OPTIONS以外のメソッド */
                // ディレクトリならばそのディレクトリ直下のindex.htmlを参照する */
                if(targetFile.isDirectory()) {
                    targetFile = new File("./" + uri + "/index.html");
                }
                /* 目的のリソースが存在しない場合 */
                if (!targetFile.exists()) {
                    errorResponse("404 Not Found", "Not Found");
                    throw new IllegalResultException("404 Not Found");
                }
                /* 目的のリソースを読み込めない場合 */
                if (!targetFile.canRead()) {
                    errorResponse("403 Forbidden", "You can't access to this file.");
                    throw new IllegalResultException("403 Forbidden");
                }

                /* ここまで来たということは正常なリクエスト */
                send("HTTP/1.1 200 OK");
                System.out.println("200 OK");
                String mimeType = HttpServerUtil.getMIMEType(targetFile);
                /* MIME Typeがtext/***な場合、RFC2616で既定のエンコーディングが
                 * ISO-8859-1とされてしまっているので
                 * charsetで敢えてutf-8を指定している
                 * 出来れば各ファイルのエンコーディングを分析してそれに応じた値にしたほうがいいんだろうけど…
                 */
                if (mimeType.contains("text/")) {
                    send("Content-Type: " + mimeType + "; charset=utf-8"); // 決め打ち…
                } else {
                    send("Content-Type: " + mimeType);
                }
                send("Date: " + HttpServerUtil.rfc1123CurrentDate());
                send("Content-Length: " + targetFile.length());
                send("Server: Tiny HTTP Server/0.1");

                /* HEADメソッドの場合ヘッダのみ返す
                 * GETメソッドの場合ボディも返す
                 */
                if (method.equals("GET")) {

                    send(""); // ヘッダとボディの間には空行

                    /* テキストファイルだろうとバイナリファイルだろうと
                    * バイナリとして送信する */
                    fileIn = new BufferedInputStream(new FileInputStream(targetFile));
                    int readByte;
                    while ((readByte = fileIn.read()) != -1) {
                        outBinary.write(readByte);
                        outBinary.flush();
                    }
                }
            }

        } catch(IOException e){
            System.err.println(e);
        } catch(IllegalResultException e){
            System.err.println(e);
        } catch(NullPointerException e){
            System.err.println(e);
        }finally{
            close();
        }
    }

    /* 必要になる各種ストリームを開く */
    private void open() throws IOException{
        InputStream socketIn = socket.getInputStream();
        OutputStream socketOut = socket.getOutputStream();
        inString = new BufferedReader(new InputStreamReader(socketIn));
        outString = new BufferedWriter(new OutputStreamWriter(socketOut));
        outBinary = new BufferedOutputStream(socketOut);
    }

    /* 1行受け取りそれを文字列で返す */
    private String receive() throws IOException{
        String line = inString.readLine();
        return line;
    }

    /* messageを改行付きで送り出す */
    private void send(String message) throws IOException{
        outString.write(message);
        outString.write("\r\n");
        outString.flush();
    }

    /* 各種ストリームを閉じる */
    private void close(){
        if(inString != null){
            try{
                inString.close();
            } catch(IOException e){ }
        }
        if(outString != null){
            try{
                outString.close();
            } catch(IOException e){ }
        }

        if (outBinary != null) {
            try {
                outBinary.close();
            } catch (IOException e) { }
        }
        if(socket != null){
            try{
                socket.close();
            } catch(IOException e){ }
        }

        if (fileIn != null) {
            try{
                fileIn.close();
            } catch(IOException e) {}
        }
    }

    /* 引数例:statusCode..."404 Not Found" bodyMessage..."This file does not exist."
     * statusCodeはステータスコード、bodyMessageはHTMLで表示させるメッセージ */
    private void errorResponse(String statusCode, String bodyMessage) throws IOException {
        send("HTTP/1.1 " + statusCode);
        send("Content-Type: text/html; charset=utf-8");
        send("");
        send("<html><head><title>" + statusCode + "</title></head>");
        send("<body><h1>" + statusCode + "</h1>");
        send("<p>" + bodyMessage + "</p>");
        send("</html>");
    }


}
