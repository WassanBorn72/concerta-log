# コンサータ服薬ログ Android版

PWA版 `concerta-log` を Kotlin + Jetpack Compose + Room でネイティブAndroidアプリ化したものです。

## 方針

- **端末内完結**: Room (SQLite) をアプリ専用領域に保存
- **INTERNET権限なし**: アプリから外部サーバーへ記録を送信する機能はありません
- **Android自動バックアップ無効**: 服薬・体調記録が意図せずクラウドへ複製されるのを避けます
- **手動バックアップ**: Storage Access Framework を使ってCSVを書き出し・読み込み
- **PWA互換**: 既存PWAが書き出したCSVをそのまま読み込めます

## 主な機能

- 日付、服薬量、服薬時刻、睡眠、主観的変化、副作用、作業実績の記録
- 日付を主キーとした保存・上書き編集
- 7日 / 30日の推移グラフ
- 履歴表示
- CSVエクスポート / インポート
- PWA版と共通の評価項目
- オフライン動作

## 技術構成

- Kotlin
- Jetpack Compose / Material 3
- Room 2.8.5
- Compose BOM 2026.09.00
- Android Gradle Plugin 9.4.0
- compileSdk 37 / targetSdk 36
- minSdk 26
- JDK 17

## Android Studioで開く

1. このリポジトリの `android-native` ブランチを取得します。
2. Android Studioで `android-app` フォルダをプロジェクトとして開きます。
3. Gradle 9.6.0 / JDK 17 を使用してSyncします。
4. Android 8.0 (API 26) 以上の端末またはエミュレータで実行します。

## PWAからの移行

1. PWA版の「履歴・CSV」からCSVを書き出します。
2. Android版の「履歴」→「CSVを読み込む」を選びます。
3. 書き出したCSVを選択します。

同じ日付の記録がすでにAndroid版にある場合、その日付の記録をCSV側の内容で更新します。

## データについて

RoomデータベースはAndroidのアプリ専用領域に置かれます。Chromeのサイトデータ削除の影響は受けません。一方、アプリをアンインストールすると原則としてDBも削除されるため、残したい記録はCSVでバックアップしてください。

このアプリは記録・振り返り用です。診断や服薬量の決定を行うものではありません。
