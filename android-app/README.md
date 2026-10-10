# コンサータ服薬ログ Android版

`android-native` ブランチで開発しているネイティブAndroid版です。

PWA版の機能と記録項目を引き継ぎつつ、データ保存をブラウザーの `localStorage` から **Room (SQLite)** に変更しています。

## 方針

- **端末内完結**
- **INTERNET権限なし**
- **外部サーバーへの記録送信なし**
- **Android自動クラウドバックアップ無効**
- **CSVによる手動バックアップ・復元**
- **PWA版CSVとの互換性を維持**

## 主な機能

- 日付、服薬量、服薬時刻
- 睡眠時間・睡眠の質
- 着手のしやすさ
- 着手に必要な努力感
- 作業の進みやすさ
- 意欲
- 頭・気持ちの落ち着き
- ぼんやり感
- 集中の保ちやすさ
- 不安・緊張
- いらだち
- 食欲低下
- 身体の副作用
- 効果が切れる時の不調
- 作業実績・メモ・医師に伝えたいこと
- 履歴表示・編集
- 7日間 / 30日間の推移グラフ
- CSVエクスポート / インポート

## データ保存

記録はRoomデータベースとしてAndroidのアプリ専用領域に保存されます。

Chromeのサイトデータ削除の影響は受けません。

一方で、アプリのアンインストールや端末故障では記録が失われる可能性があるため、必要な記録はCSVで書き出してください。

Androidの自動クラウドバックアップは無効化しています。

## PWA版からの移行

1. PWA版でCSVを書き出します。
2. Android版の「履歴」画面を開きます。
3. 「CSVを読み込む」を選びます。
4. PWA版で書き出したCSVを指定します。

PWA版とAndroid版は同じ主要列名を使っているため、既存記録を移行できます。

同じ日付の記録が存在する場合は、その日付のデータを読み込んだ内容で更新します。

## 技術構成

- Kotlin
- Jetpack Compose
- Material 3
- Room 2.8.5
- Compose BOM 2026.06.00
- Activity Compose 1.12.4
- Lifecycle 2.10.0
- Android Gradle Plugin 9.4.0
- Gradle 9.6.0
- JDK 17
- compileSdk 36
- targetSdk 36
- minSdk 26

## Android Studioで開く

1. リポジトリの `android-native` ブランチを取得します。
2. Android Studioで `android-app/` を開きます。
3. JDK 17を使用してGradle Syncします。
4. Android 8.0 (API 26) 以上の端末またはエミュレータで実行します。

## GitHub Actions

`.github/workflows/android-debug.yml` でdebug APKをビルドします。

ワークフロー:

`Build native Android debug APK`

現在のコードではGitHub Actions上で **debug APKのビルド成功を確認済み**です。

生成されたAPKはWorkflow Artifactの `concerta-log-debug` として取得できます。

## applicationId

`com.wassanborn72.concertalog`

## 注意事項

このアプリは記録・振り返り用です。

診断、治療判断、服薬量の決定を行うものではありません。服用方法や用量は処方に従ってください。
