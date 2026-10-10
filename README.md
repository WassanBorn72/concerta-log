# コンサータ服薬ログ

**ネイティブAndroidアプリを中心に開発している、個人用の服薬記録アプリです。**

コンサータ服用後の主観的な変化、作業状況、副作用、睡眠などを日ごとに記録し、履歴や推移を確認できます。

ブラウザ版は画面・記録項目・CSV互換性の確認用として残しており、日常的な利用はネイティブAndroid版を前提としています。

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

GitHub Actionsの **`Build Android Release`** ワークフローで、正式署名鍵を使用したrelease APKとAABを生成します。

ワークフローでは次を実行します。

1. JDK 17 / Gradle 9.6 / Android SDK 36をセットアップ
2. GitHub SecretsからBase64化された正式署名鍵を復元
3. `assembleRelease` を実行
4. `bundleRelease` を実行
5. APK署名を `apksigner` で検証
6. AAB署名を `jarsigner` で検証
7. APK / AABをWorkflow Artifactとして保存

### release成果物

- 直接インストール用 APK: `concerta-log.apk`
- Google Play公開用 AAB: `concerta-log.aab`
- GitHub Actions Artifact名: `concerta-log-release`

APKはAndroid端末へ直接インストールするための配布物です。

AABは将来Google Playへ公開する場合に使用するための成果物です。通常、AABそのものをAndroid端末へ直接インストールする用途では使用しません。

## 正式署名鍵

正式署名鍵:

`concerta-log-release.jks`

署名鍵はリポジトリにはコミットせず、GitHub ActionsではBase64化した内容をRepository Secretsから復元します。

現在の正式署名鍵のフィンガープリント:

```text
Alias: concerta-log

SHA-1:
DD:9C:B5:61:07:9B:1D:A6:21:AE:D9:E1:D1:4C:02:67:7F:3B:99:04

SHA-256:
C5:22:FA:6C:3C:79:11:1D:50:07:CC:AE:EF:17:2A:A1:
D8:54:36:A6:2F:75:D2:8F:AE:16:67:59:92:17:B3:37
```

この署名鍵は、既存インストールへの上書きアップデートに必要です。新しいバージョンでも同じ鍵を使用します。

`.jks` / `.keystore` は `android-app/.gitignore` で除外しています。

## GitHub Actions Secrets

リポジトリの

**Settings → Secrets and variables → Actions → New repository secret**

から次の4つを登録します。

| Secret | 内容 |
| --- | --- |
| `CONCERTA_LOG_KEYSTORE_BASE64` | `concerta-log-release.jks` をBase64化した全文 |
| `CONCERTA_LOG_KEYSTORE_PASSWORD` | Keystoreのパスワード |
| `CONCERTA_LOG_KEY_ALIAS` | 署名鍵のAlias |
| `CONCERTA_LOG_KEY_PASSWORD` | Keyのパスワード |

パスワードやBase64文字列そのものはREADMEやGitHubのソースコードへ記載しません。

## release手順

1. `android-app/app/build.gradle.kts` の `versionName` と `versionCode` を更新します。
2. `versionCode` は前回より必ず大きい値にします。
3. 変更を `main` に反映します。
4. GitHubの **Actions → Build Android Release** を開きます。
5. 自動実行されたrun、または **Run workflow** から手動実行したrunが成功することを確認します。
6. run下部の **Artifacts → concerta-log-release** をダウンロードします。
7. Artifact内の `concerta-log.apk` を直接インストール用として配布します。
8. `concerta-log.aab` はGoogle Play公開用として保管します。
9. 必要に応じてGitHub Releasesへ `concerta-log.apk` を添付します。

現在のWorkflowは `main` へのpushでも自動実行され、`workflow_dispatch` による手動実行にも対応しています。

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
