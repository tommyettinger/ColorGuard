package color.guard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by Tommy Ettinger on 9/9/2017.
 */
public class Loader {
    public ObjectMap<String, Texture> textures;

    public Loader() {
        textures = new ObjectMap<String, Texture>(32);
    }

    public void loadTexture(final String url) {
        if(textures.containsKey(url))
            return;
        Gdx.net.sendHttpRequest(new Net.HttpRequest(), new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                byte[] bytes = httpResponse.getResult();
                Pixmap pixmap = new Pixmap(bytes, 0, bytes.length);
                textures.put(url, new Texture(pixmap));
                pixmap.dispose();
            }

            @Override
            public void failed(Throwable t) {

            }

            @Override
            public void cancelled() {

            }
        });;
    }
}