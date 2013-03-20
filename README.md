tiny-http-server
================

このレポジトリは、私が所属する大学のクラブ([C.A.C.](http://ksu-cac.com/ "C.A.C. Web site"))のイベントとして開催された制作合宿(2013/03/19-2013/03/20)で私が作成した作品です。

合宿終了時のコードにドキュメントとサンプルページを追加したリビジョンは以下になります。

[tasuten/tiny-http-server at 9207fd5fd4d28a7e2442a4107be7928d7c5be0d3 · GitHub](https://github.com/tasuten/tiny-http-server/tree/9207fd5fd4d28a7e2442a4107be7928d7c5be0d3 "tasuten/tiny-http-server at 9207fd5fd4d28a7e2442a4107be7928d7c5be0d3 · GitHub")

HEADのコードは気まぐれで修正が加わったりしています。

内容としては、極々簡素なHTTPサーバをJavaで書いてみたものとなっています。

せっかくなのでgithubで公開しています。


起動方法
========
    $ make
    $ java HttpServer port # portはオプション。デフォルトは8080
終了はCTRL-Cで。簡易なマニュアルがmanual.txtに付いています。

謝辞
====
* Javaのソケットプログラミングの部分は[発展プログラミング演習II](https://twitter.com/KSUCSEAP2_2012 "発展プロ演II 2012 (KSUCSEAP2_2012)さんはTwitterを使っています")で教わったものを参考にさせて頂いています。
* 参考文献として以下のものを挙げさせて頂きます
    * [ハイパーテキスト転送プロトコル -- HTTP/1.1](http://www.studyinghttp.net/rfc_ja/rfc2616 "ハイパーテキスト転送プロトコル -- HTTP/1.1")
    * [Webを支える技術 ── HTTP，URI，HTML，そしてREST（WEB+DB PRESS plusシリーズ）](http://gihyo.jp/magazine/wdpress/plus/978-4-7741-4204-3 "Webを支える技術 ── HTTP，URI，HTML，そしてREST（WEB+DB PRESS plusシリーズ）｜gihyo.jp … 技術評論社") ISBN4774142042
    * [Java™ Platform, Standard Edition 6 API 仕様](http://docs.oracle.com/javase/jp/6/api/ "JavaTM Platform, Standard Edition 6 API 仕様")

ライセンス
==========
[NYSL](http://www.kmonos.net/nysl/ "NYSL")

免責事項
========
書いた当人のネットワークプログラミング及びJavaプログラミングの経験は少なく、概ね試行錯誤で書いた風な感じです。

よって、一般に普及しているWebサーバ程に完成されたものではございません。
ですので、このソフトウェアを用いたことに起因するいかなる損害の責任も負い兼ねます。
