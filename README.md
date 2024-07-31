# IoT-Home

IoT-HomeはRaspberry PiなどのIoT機器向けに開発されたアプリです。

詳しくは[おかゆグループサービス - Iot-Home](https://services.okayugroup.com/iothome)をご覧ください。

## 機能

画面上で**ノードをつなげるだけで** イベントを作成して、APIとしてWeb側からイベントを呼び出せます。

### ノード
 - ノードは画面上でドラッグ&ドロップで配置できます。
 - ノードは入力と出力を持ち、入力に接続されたノードが出力を発火すると、その出力を受け取ったノードがイベントを発火します。最後のノードのイベントの出力がAPIとしてWeb側に公開されます。

### イベント
 - ウェブリクエスト: HTTPリクエストを送信します。
 - ファイル実行: ファイルを実行します。実行形式ファイルと音声ファイルに対応しています。
 - コンソール: コンソールコマンドを実行します。Windows PowerShellとLinuxのbashに対応しています。

今後、随時新しいイベントを追加していく予定です。

## インストール

1. GitHubから[最新版](https://github.com/yossy4411/IoT-Home/releases/latest)のiot-home.zipをダウンロードしてください。
2. ダウンロードしたzipファイルを解凍し、任意の場所に配置します。
3. 解凍したディレクトリに移動し、iot-home.jarファイルをダブルクリックするか、以下のコマンドを使用して起動します。
   ```bash
   java -jar iot-home.jar
   ```
4. GUIが起動します。GUIが起動できない環境の場合は[こちら](https://services.okayugroup.com/docs/iot-home/console)のドキュメントをご覧ください。



