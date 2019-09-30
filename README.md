# Edge Simulator

## システム概要

キャッシュ容量の制約を考慮した, レコードの配置戦略をテストするためのシミュレーター



## アーキテクチャ

Fig. 1のようなアーキテクチャを考える. C1 ~ C9 はMECサーバーを表しており, ユーザーはある範囲内で自由に動き回りながら, 各サーバーに対してHTTPリクエストをすることができる. 

<div align="center">
![arch](https://github.com/thanatoth/edge-simulator/blob/doc/doc/arch.png)
</div>

<div align="center">
Fig. 1 Our System Model of MEC Architecture
</div>


<br>
シミュレーター上では以下のように表現されている. 以下はサーバーが16個, クライアントが100人の場合で, 青丸がサーバー, 黒丸がクライアントを表している. 




<div align="center">
![arch_sim](https://github.com/thanatoth/edge-simulator/blob/doc/doc/arch_sim.png)
</div>


<br>
フィールドは区画で区切られており, 各クライアントは同一区画上のサーバーからレコードを取得することを試みる（HTTP GET リクエストをする）. 


<div align="center">
![near](https://github.com/thanatoth/edge-simulator/blob/doc/doc/near.png)
</div>


<br>
各サーバーは独立にキャッシュを持ち, ドキュメントを保持する. 





## アーキテクチャ詳細

### ディレクトリ構成

```bash
edge-server
├ CentralServer //クラウドサーバー（コンテンツサーバー)
├ ClientSide   
│  ├ Client     //クライアントを表現
├ Data   
│  ├ Document   //ドキュメントを表現
├ EdgeServer  
│  ├ EdgeServer //エッジサーバーを表現
├ Field 
│  ├ Point2D   //二次元座標系における点
│  ├ Area      //正方形区画
├ HTTP 
│  ├ HTTPResponseMetaData     //各リクエストに関するメタデータ（レスポンス時間, コスト）
├ MetaServer //デーモン系（実機を想定したときこれらをどうするか）
│  ├ DocumentIds //生成された全ドキュメントのIDを管理（実際のアプリケーションでは必要なし）
│  ├ ClientManager //全クライアントの状態を管理
│  ├ ServerManager //全サーバーの状態, またサーバーのグルーピングを管理
│  ├ FieldManager //フィールドを管理（範囲など, 実際のアプリケーションでは必要なし）
├ Strategy //戦略系(Next)
│  ├ Grouping //サーバー同士をどのようにグルーピングするかに関する戦略
│  ├ Post //POST(PUT)するサーバーの選択
│  ├ Relocation //データの再配置に関する戦略
│  ├ FieldManager //フィールドを管理（範囲など, 実際のアプリケーションでは必要なし）
└ Log   //ログ関係
```



## ドキュメント構成

全アプリケーションに共通するスキーマは現段階で, 次のように定めている. 

```
{
    "type": "object",
    "properties": {
        "_id": {
            "type": "string" //データが追加されたときに(POST時に)自動的に割り振る
        },
        "consistencyLevel": {
            "type": "integer",
            "minimum": 0
        },
        "priorityLevel": {
            "type": "integer", //キャッシュ優先度, 緊急性
        }
        "size" : {
        		"type" : "integer" //ドキュメントのサイズ. データが追加されたときに動的に生成する必要がある
        }
    },
}
```



## シミュレーションのシーケンス

### 準備

1. フィールドを用意する（現状は1km × 1km)
2. エッジサーバー (`id`, `location`, `samegroupServers` , `performanceLevel(性能)`,`capacity(容量)`, `remain(残り容量)`, `collection`をプロパティとして持つ)  のインスタンスをフィールド上に, 格子状に配置する
3. クライアント（`id`,` location`, `nearestServer(最も近くにあるサーバー. クライアントはこのサーバーに対してHTTPリクエストを行う)`をプロパティとしてもつ）のインスタンスをフィールド上に, ランダムに配置する. 
4. 他に, クラウドサーバー（`collection`をプロパティとして持つ）, クライアント管理用サーバー, サーバー管理用サーバーを配置する. 



### シミュレーション

**逐次**

1. HTTP POSTにより, ドキュメントを生成する. 
2. ユーザーは特定のドキュメントの取得（HTTP GET）を試みる. このとき, 最も近くのサーバーから取得できることを基準（0）として, どれだけのレスポンス時間, トラフィックコストがかかったかを記録しておく. 
3. 1 ~ 2を繰り返す



**並行**

逐次における1, 2を並行して行う. マルチスレッドで行うか, ランダムにユーザーとHTTPメソッドを決定し, HTTP リクエストをすることを繰り返す. 



**条件**（設定パラメータ）

- サーバーの数（16, 64, 256, 4^5, ・・・)
- クライアントの数

- 各エッジサーバーの容量（現状全サーバーを10000MBとして固定）

- 各ドキュメントのサイズ（現状全ドキュメントを1MBとして固定）

  

**課題とメトリクス**

1. ドキュメントのPOSTが頻繁に生じたとき, 任意のサーバーで容量オーバーが起こらないこと. 

→ 各エッジサーバーの, 各時点での残容量で評価する. あとで時間ごとにグラフ化など. 

2. ドキュメントのGETに対して, レスポンス時間, トラフィックコストが小さいこと

→ 全GETリクエストの平均レスポンス時間, 平均トラフィックコストで評価する. 評価式については, 『Proactive content caching by exploiting transfer learning for mobile edge computing』を参考にする

 

**各課題に対する解決策の方向性（今後解決策を作り上げていく）**

**1に対して**

- POSTするサーバーの選択を行う. 
- ドキュメントを優先度, リクエスト頻度に応じて, 再配置する. 例えば, 優先度の低いドキュメントはエッジサーバー上に配置せずコンテンツサーバーのみに配置, ないしは, エッジサーバーへの配置粒度を小さくする. 

ドキュメントと、それを受容しうるユーザーの位置を把握。それにともない











