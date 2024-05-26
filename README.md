# Iot-Home

Iot-Homeアプリを使えば、HTTP通信でコマンドや音声ファイルを実行することができます。

WebHookの逆みたいなもんですね。

## 使い方
[Release](https://github.com/yossy4411/Iot-Home/releases/latest)から最新版のIot-Home_jar.zipをダウンロードします。

ダウンロードしたzipを展開し、ディレクトリに移動して`java -jar Iot-Home.jar`を実行します。

GUIでWebツリーの編集が可能です。

## イベント

### コマンド実行イベント
 - コンソールコマンド 引数: [command]
   
   Linux/Macのコマンドを実行します。
 - Windows Powershell コマンド 引数: [command]
   
   PowerShellでコマンドを実行します。
 - Windows コマンド 引数: [command]
   
   Windowsコマンドプロンプトでコマンドを実行します。

### ファイル実行イベント
- ファイルを実行 [filePath, directory(optional)] 

  任意のファイルパスのファイルを実行します。
  任意で実行の基準となるディレクトリを指定できます。

- 音声を再生 [filePath]

  任意のファイルパスにある音声ファイルを実行します。
  mp3, wavに対応しています。
  
