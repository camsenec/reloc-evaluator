package EdgeServer;

import Data.Document;
import Field.Point2D;
import Model.EdgeServerModel;
import Retrofit.EdgeServerAPI;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static Constants.Constants.BASE_URL;

/*
    サーバー上で動くサーバプログラムを想定
    サーバープログラムに組み込まなければならないものは,
    残り容量（もしくは差分）を取得することと,
    Managementサーバーに送信すること
    これは, ディベロッパーに任せる

    残り容量は基本的にどのプログラミング言語でも可能であろうし,
    Managementサーバーに送信することも容易

    'サーバーレス'とかであれば, そのプラットフォームの提供者が組み込むことを想定
    なんにしろ, サーバーマシンの保有者が利用することを想定
 */

public class MecHost {
    private int applicationId;
    private int serverId; //認証情報
    private int remain;

    public void updateRemain(int applicationId, int documentId){ }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId){ this.serverId = serverId; }
    public ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Document>> getCollection() {
        return collection;
    }

    public int getRemain() { return remain;}

    @Override
    public String toString() {
        return String.format("serverId : %d\t\tlocation : (%6.2f, %6.2f)", serverId);
    }
}
