# コンサータ服薬ログ

コンサータ服用後の主観的な変化、作業状況、副作用、睡眠などを日ごとに記録するための個人用ログアプリです。

現在、このリポジトリには以下の2系統があります。

- **PWA版**: ブラウザーで利用するオフラインWebアプリ
- **ネイティブAndroid版**: Kotlin + Jetpack Compose + Room で実装したAndroidアプリ

## PWA版

### 公開URL

https://wassanborn72.github.io/concerta-log/

Android Chromeで開き、「アプリをインストール」または「ホーム画面に追加」を選ぶことでPWAとして利用できます。

### 主な機能

- 日付ごとの服薬量・服薬時刻の記録
- 睡眠時間・睡眠の質の記録
- 着手のしやすさ、努力感、作業の進みやすさ、意欲、集中などの主観評価
- 不安、いらだち、食欲低下、身体症状、効果が切れる時の不調などの記録
- 作業実績・メモ・医師に伝えたいことの記録
- 7日間 / 30日間の推移グラフ
- 日付ごとの履歴表示・編集
- CSVエクスポート / インポート
- Service Workerによるオフライン利用

### データ保存

PWA版の記録はブラウザーの `localStorage` に保存されます。

外部サーバーへ記録を送信する処理はありませんが、ブラウザーデータの消去、PWAの削除、端末故障などで記録が失われる可能性があります。必要な記録はCSVとして定期的に書き出してください。

---

## ネイティブAndroid版

ネイティブ版は現在、`android-native` ブランチ内の `android-app/` にあります。

Draft PR:

https://github.com/WassanBorn72/concerta-log/pull/1

### 主な機能

- PWA版とほぼ同じ記録項目
- 日付を主キーとした保存・上書き編集
- 履歴表示
- 7日間 / 30日間の推移グラフ
- CSVエクスポート / インポート
- PWA版CSVの読み込み
- 完全オフライン動作

### データ保存

Android版では **Room (SQLite)** を使用し、記録をAndroidのアプリ専用領域に保存します。

- `INTERNET` 権限なし
- アプリから外部サーバーへ記録を送信する機能なし
- Androidの自動クラウドバックアップは無効
- Chromeのサイトデータ削除の影響を受けない
- 手動CSVバックアップに対応

アプリをアンインストールした場合や端末が故障した場合には記録が失われる可能性があるため、必要な記録はCSVでバックアップしてください。

### PWA版からの移行

1. PWA版の「履歴・CSV」からCSVを書き出します。
2. Android版の「履歴」から「CSVを読み込む」を選びます。
3. PWA版で書き出したCSVを選択します。

同じ日付の記録がすでにAndroid版に存在する場合、その日付の記録を読み込んだCSVの内容で更新します。

### 技術構成

- Kotlin
- Jetpack Compose / Material 3
- Room 2.8.5
- Compose BOM 2026.06.00
- Android Gradle Plugin 9.4.0
- compileSdk 36
- targetSdk 36
- minSdk 26
- JDK 17
- Gradle 9.6.0

### ビルド

GitHub Actionsの `Build native Android debug APK` ワークフローでdebug APKを自動ビルドできます。

直近のネイティブ版では、GitHub Actions上で **APKのビルド成功を確認済み**です。

Android Studioから実行する場合:

1. `android-native` ブランチを取得します。
2. Android Studioで `android-app/` を開きます。
3. JDK 17でGradle Syncします。
4. Android 8.0 (API 26) 以上の端末またはエミュレータで実行します。

## ブランチ構成

- `main`: PWA版
- `android-native`: ネイティブAndroid版の開発ブランチ

現在はPWA版を維持したまま、ネイティブ版を別ブランチで開発しています。

## プライバシーと注意事項

このアプリは個人の記録・振り返りを目的としています。

記録には服薬や体調に関する情報が含まれるため、CSVの保存先や共有先には注意してください。

このアプリは診断、治療判断、服薬量の決定を行うものではありません。服用方法や用量は処方に従ってください。
