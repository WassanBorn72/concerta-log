# コンサータ服薬ログ

**ネイティブAndroidアプリを中心に開発している、個人用の服薬記録アプリです。**

コンサータ服用後の主観的な変化、作業状況、副作用、睡眠などを日ごとに記録し、履歴や推移を確認できます。

ブラウザ版は確認用として残しており、日常的な利用はネイティブAndroid版を前提としています。

## Android版をダウンロード

最新版は **GitHub Releases** からダウンロードできます。

### [最新版の `concerta-log.apk` をダウンロード](https://github.com/WassanBorn72/concerta-log/releases/latest/download/concerta-log.apk)

Android端末へ直接インストールする場合は `concerta-log.apk` を使用してください。

リリース履歴やGoogle Play用のAABは、[Releasesページ](https://github.com/WassanBorn72/concerta-log/releases) から確認できます。

## Android版

Android版のソースは `android-app/` にあります。

### 主な機能

- 日付ごとの服薬量・服薬時刻の記録
- 睡眠時間・睡眠の質の記録
- 着手のしやすさ、努力感、作業の進みやすさ、意欲、集中などの主観評価
- 不安、いらだち、食欲低下、身体症状、効果が切れる時の不調などの記録
- 作業実績・メモ・医師に伝えたいことの記録
- 日付ごとの履歴表示・編集
- 7日間 / 30日間の推移グラフ
- CSVエクスポート / インポート
- ブラウザ版CSVの読み込み
- 完全オフライン動作

### データ保存

Android版では **Room (SQLite)** を使用し、記録をAndroidのアプリ専用領域に保存します。

- `INTERNET` 権限なし
- 外部サーバーへの記録送信なし
- Androidの自動クラウドバックアップは無効
- Chromeやブラウザーのサイトデータ削除の影響を受けない
- CSVによる手動バックアップ・復元に対応

アプリをアンインストールした場合や端末が故障した場合には記録が失われる可能性があります。残したい記録はCSVでバックアップしてください。

## 現在のアプリ情報

| 項目 | 値 |
| --- | --- |
| App ID | `com.wassanborn72.concertalog` |
| 表示名 | `コンサータ服薬ログ` |
| versionName | `1.0.1` |
| versionCode | `2` |
| minSdk | `26` |
| targetSdk | `36` |
| compileSdk | `36` |

## 技術構成

- Kotlin
- Jetpack Compose / Material 3
- Room 2.8.5
- Compose BOM 2026.06.00
- Activity Compose 1.12.4
- Lifecycle 2.10.0
- Android Gradle Plugin 9.4.0
- Gradle 9.6.0
- JDK 17

## Android release

GitHub Actions の **`Build Android Release`** で、署名済みのrelease APKとAABを生成します。

成果物:

- `concerta-log.apk`：Android端末へ直接インストールするためのAPK
- `concerta-log.aab`：Google Play公開用のAndroid App Bundle

releaseビルドには、GitHub Actions Secretsに登録した署名情報を使用します。

## Android Studioで実行する場合

1. リポジトリを取得します。
2. Android Studioで `android-app/` を開きます。
3. JDK 17でGradle Syncします。
4. Android 8.0 (API 26) 以上の端末またはエミュレータで実行します。

## ブラウザ確認版

確認用ブラウザ版のソースは `web-preview/` にあります。

公開URL:

https://wassanborn72.github.io/concerta-log/

GitHub Pagesでは `web-preview/` の内容だけを公開します。

ブラウザ版はネイティブ版の画面・記録項目・CSV互換性を手軽に確認するための補助版です。記録はブラウザーの `localStorage` に保存されるため、日常的な記録にはAndroid版を使用してください。

## ブラウザ版からAndroid版への移行

1. ブラウザ版の「履歴・CSV」からCSVを書き出します。
2. Android版の「履歴」から「CSVを読み込む」を選びます。
3. 書き出したCSVを指定します。

同じ日付の記録がAndroid版に存在する場合、その日付の記録をCSV側の内容で更新します。

## リポジトリ構成

```text
.
├─ android-app/                 # メインのネイティブAndroidアプリ
├─ web-preview/                 # 確認用ブラウザ版
├─ .github/
│  └─ workflows/
│     ├─ android-release.yml    # 署名済みAPK / AABビルド
│     └─ deploy.yml             # ブラウザ確認版のGitHub Pages公開
└─ README.md
```

## プライバシーと注意事項

このアプリは個人の記録・振り返りを目的としています。

記録には服薬や体調に関する情報が含まれるため、CSVの保存先や共有先には注意してください。

このアプリは診断、治療判断、服薬量の決定を行うものではありません。服用方法や用量は処方に従ってください。
