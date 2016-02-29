# Gram

「[本格Androidハンズオンセミナー](http://eure.connpass.com/event/26809/)」の資料です。

## 完成品

今回のハンズオンでは、自分がInstagramに投稿した画像一覧をグリッドで表示して、タップすると拡大表示されるアプリを実装することを目標とします。

![Sample](https://raw.githubusercontent.com/yuyakaido/Gram/master/sample.gif)

## 概要

- InstagramのAPI
- API通信
  - ライブラリの準備
  - サーバーモデル
  - APIクライアントの作成
  - クライアントモデル
- 画像のグリッド表示
- 画像の拡大表示

## InstagramのAPI

自分が投稿した画像一覧を取得するために以下のAPIを使用します。

- https://api.instagram.com/v1/users/self/media/recent/?access_token=ACCESS-TOKEN

InstagramのAPIを叩くためにはACCESS-TOKENを取得する必要がありますが、このACCESS-TOKENの取得処理はすでに実装してあるため、皆さんが実装する必要はありません。実際にAPIを叩いて、画像一覧を取得する処理を実装していくことになります。

APIレスポンスは大まかに以下のような構造になっています。

```json
{
  "data": [
    {
      "images": {
        "low_resolution": {
          "url": "https://..."
        },
        "thumbnail": {
          "url": "https://..."
        },
        "standard_resolution": {
          "url": "https://..."
        }
      }
    },
    {
      "images": {
        ...
      }
    },
    {
      "images": {
        ...
      }
    }
  ]
}
```

JSONのルート要素として`data`配列があり、その配列に自分の投稿が入っています。また、それぞれの投稿が`images`として3種類の画像URL（低解像度、サムネイル、標準解像度）を持つ構造になっています。

## API通信

### ライブラリの準備

API通信の実装では、[Volley](https://bintray.com/android/android-utils/com.android.volley.volley/view)が使われることが多かったですが、最近は[Retrofit](https://github.com/square/retrofit)が主流となっています。そこで今回は後者の`Retrofit`で実装していきます。

`Retrofit`をアプリ内で使用するために、`app/build.gradle`の`dependencies`に以下を追記してください。また、`Retrofit`をより便利に使用するために[RxJava](https://github.com/ReactiveX/RxJava)と[RxAndroid](https://github.com/ReactiveX/RxAndroid)も使用します。

```groovy
dependencies {

    ...

    // Rx
    compile 'io.reactivex:rxandroid:1.1.0'
    compile 'io.reactivex:rxjava:1.1.0'

    // HTTP
    compile 'com.squareup.retrofit2:retrofit:2.0.0-beta4'
    compile 'com.squareup.retrofit2:converter-gson:2.0.0-beta4'
    compile 'com.squareup.retrofit2:adapter-rxjava:2.0.0-beta4'
    
    // Image
    compile 'com.github.bumptech.glide:glide:3.7.0'
}
```

これで`Retrofit`を使用する準備が整ったので、API通信を実装していきます。

### サーバーモデル

APIレスポンスをクライアントで表現するために、サーバーモデルを定義します。今回使用するAPIのレスポンスでは`data`が配列になっていて、その配列に自分が投稿が入っているという構造になっています。

まずは1件の投稿を表すクラスを以下のように作成します。

```java
public class InstagramMediaResponse {

    @SerializedName("id")
    public String id;

    @SerializedName("images")
    public Map<String, InstagramImageResponse> images;

    public static class InstagramImageResponse {
        @SerializedName("url")
        public String url;
    }

}
```

また、APIのレスポンス全体を表すクラスも以下のように作成します。

```java
public class RecentMediaResponse {

    @SerializedName("data")
    public List<InstagramMediaResponse> data;

}
```

### APIクライアントの作成

APIレスポンスをクライアントで表現することが可能になったので、`Retrofit`を使ってAPI通信を実装していきます。

まずは以下のような`InstagramService`を作成します。

```java
public interface InstagramService {
    @GET("users/self/media/recent")
    Observable<RecentMediaResponse> getRecentMedia(@Query("access_token") String accessToken);
}
```

次に実際に通信を行うAPIクライアントを作成します。

`Retrofit`では、`Retrofit.Builder`に対して先ほど作成した`InstagramService`を渡すだけで簡単にAPIクライアントを作成することが出来ます。

```java
InstagramService service = new Retrofit.Builder()
                .baseUrl("https://api.instagram.com/v1/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(InstagramService.class);
```

`addCallAdapterFactory(RxJavaCallAdapterFactory.create())`は必須ではありませんが、`Retrofit`をより便利に使用するために使われることが多くなっています。また、`addConverterFactory(GsonConverterFactory.create())`はJSONをJavaにクラスへ自動的にマッピングするための必須設定です。

ここまでで実際にInstagramのAPIを叩く準備が整いました。

### クライアントモデルの作成

先ほど作成したサーバーモデルはあくまでAPIのレスポンスに合わせた形式であり、クライアントでは扱いにくいため、以下のようなクライアントモデルを作成します。

```java
public class InstagramMedia implements Serializable {

    public String id;

    public String thumbnailUrl;

    public String lowResolutionUrl;

    public String standardResolutionUrl;

}
```

サーバーモデルからクライアントモデルに変換するために、以下のようなクラスも同時に作成します。

```java
public class InstagramMediaConverter {

    private InstagramMediaConverter() {}

    public static List<InstagramMedia> convert(RecentMediaResponse recentMediaResponse) {
        List<InstagramMedia> instagramMedias = new ArrayList<>();
        for (InstagramMediaResponse response : recentMediaResponse.data) {
            InstagramMedia instagramMedia = new InstagramMedia();
            instagramMedia.id = response.id;
            Map<String, InstagramMediaResponse.InstagramImageResponse> images = response.images;
            instagramMedia.thumbnailUrl = images.get("thumbnail").url;
            instagramMedia.lowResolutionUrl = images.get("low_resolution").url;
            instagramMedia.standardResolutionUrl = images.get("standard_resolution").url;
            instagramMedias.add(instagramMedia);
        }
        return instagramMedias;
    }

}
```

先ほどのAPIクライアント生成処理と、サーバーモデルからクライアントモデルの変換処理を、`InstagramNetwork`クラスとしてまとめます。

```java
public class InstagramNetwork {

    public static Observable<List<InstagramMedia>> getRecentMedia(String accessToken) {
        InstagramService service = new Retrofit.Builder()
                .baseUrl("https://api.instagram.com/v1/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(InstagramService.class);
        return service.getRecentMedia(accessToken)
                .map(new Func1<RecentMediaResponse, List<InstagramMedia>>() {
                    @Override
                    public List<InstagramMedia> call(RecentMediaResponse recentMediaResponse) {
                        return InstagramMediaConverter.convert(recentMediaResponse);
                    }
                });
    }

}
```

## 画像のグリッド表示

APIからのデータ取得処理が実装できたので、次は画面に表示する部分を実装していきます。

現状の`activity_main`には認証のためのボタンのみが設置されていますが、画像を一覧表示するための`GridView`を追加で設置します。

```xml
<?xml version="1.0" encoding="utf-8"?>

<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <Button
        android:id="@+id/activity_main_authentication_button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        android:text="Authenticate"/>

    <GridView
        android:id="@+id/activity_main_grid_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:visibility="gone"
        android:padding="4dp"
        android:verticalSpacing="4dp"
        android:horizontalSpacing="4dp"
        android:numColumns="3">
    </GridView>

</RelativeLayout>
```

`GridView`に設定するレイアウトを`item_instagram_grid_view`として作成します。

```xml
<?xml version="1.0" encoding="utf-8"?>

<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.yuyakaido.android.gram.ui.view.SquareImageView
        android:id="@+id/item_instagram_media_grid_image"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

</RelativeLayout>
```

`GridView`に設定するための`Adapter`も作成します。

```java
public class InstagramMediaGridAdapter extends ArrayAdapter<InstagramMedia> {

    private LayoutInflater layoutInflater;
    private List<InstagramMedia> instagramMedias;

    public InstagramMediaGridAdapter(Context context, List<InstagramMedia> instagramMedias) {
        super(context, 0, instagramMedias);
        this.layoutInflater = LayoutInflater.from(context);
        this.instagramMedias = instagramMedias;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_instagram_media_grid, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        InstagramMedia instagramMedia = getItem(position);

        Glide.with(getContext())
                .load(instagramMedia.thumbnailUrl)
                .into(holder.image);

        return convertView;
    }

    @Override
    public InstagramMedia getItem(int position) {
        return instagramMedias.get(position);
    }

    @Override
    public int getCount() {
        return instagramMedias.size();
    }

    public void setInstagramMedias(List<InstagramMedia> instagramMedias) {
        this.instagramMedias = instagramMedias;
    }

    public static class ViewHolder {
        public ImageView image;

        public ViewHolder(View view) {
            this.image = (ImageView) view.findViewById(R.id.item_instagram_media_grid_image);
        }
    }

}
```

`MainActivity`の`onCreate()`にて`GridView`と`Adapter`の初期化を行います。

```java
adapter = new InstagramMediaGridAdapter(this, new ArrayList<InstagramMedia>());
gridView = (GridView) findViewById(R.id.activity_main_grid_view);
gridView.setAdapter(adapter);
```

`MainActivity`の`onInstagramAuthenticationCompleted()`にて、先ほど作成した`InstagramNetwork`を使って画像一覧を取得して、結果を`GridView`に表示します。

```java
    private void fetchInstagramMedias(String accessToken) {
        InstagramNetwork.getRecentMedia(accessToken)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<InstagramMedia>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<InstagramMedia> instagramMedias) {
                        authenticateButton.setVisibility(View.GONE);
                        gridView.setVisibility(View.VISIBLE);
                        adapter.setInstagramMedias(instagramMedias);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
```

`subscribeOn(Schedulers.newThread())`はAPI通信をバックグラウンドで行うための設定で、`observeOn(AndroidSchedulers.mainThread())`はメインスレッドに結果をコールバックするための設定です。

さいごに`AndroidManifest`にインターネット通信を許可するための設定を追加してください。

```
<uses-permission android:name="android.permission.INTERNET"/>
```

ここまででアプリを起動すると、認証ボタンが表示されて、認証を行うと自分が投稿した画像一覧が表示されるはずです。

## 画像の拡大表示

APIから画像一覧を取得して、グリッドに表示するところまでは出来たので、画像をタップした際に拡大表示してみます。

まずは画面のレイアウトを`activity_preview_media.xml`として作成します。

```xml
<?xml version="1.0" encoding="utf-8"?>

<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.yuyakaido.android.gram.ui.view.SquareImageView
        android:id="@+id/activity_preview_media_image"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_centerVertical="true"/>

</RelativeLayout>
```

次に`PreviewMediaActivity`クラスを以下のように作成します。

```java
public class PreviewMediaActivity extends AppCompatActivity {

    private static final String ARGS_INSTAGRAM_MEDIA = "ARGS_INSTAGRAM_MEDIA";

    public static Intent createIntent(Context context, InstagramMedia instagramMedia) {
        Intent intent = new Intent(context, PreviewMediaActivity.class);
        intent.putExtra(ARGS_INSTAGRAM_MEDIA, instagramMedia);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_media);

        Intent intent = getIntent();
        InstagramMedia instagramMedia = (InstagramMedia) intent.getSerializableExtra(ARGS_INSTAGRAM_MEDIA);

        ImageView imageView = (ImageView) findViewById(R.id.activity_preview_media_image);
        Glide.with(this)
                .load(instagramMedia.standardResolutionUrl)
                .into(imageView);
    }

}
```

`PreviewMediaActivity`を`AndroidManifest.xml`に追加します。

```xml
<application
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:theme="@style/Theme.AppCompat.Light"
    android:allowBackup="false">
    
    ...

    <activity android:name=".ui.activity.PreviewMediaActivity"/>

</application>
```

`GridView`の要素をタップした際に、`PreviewMediaActivity`に遷移する処理を追加します。

```java
@Override
public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    InstagramMedia instagramMedia = adapter.getItem(position);
    startActivity(PreviewMediaActivity.createIntent(this, instagramMedia));
}
```

ここまででアプリを起動すると、画像タップで別画面にて拡大表示されるようになっていると思います。
