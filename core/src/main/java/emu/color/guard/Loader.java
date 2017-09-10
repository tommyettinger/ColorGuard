package emu.color.guard;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public class Loader {
    public ObjectMap<String, Texture> textures;

    public Loader() {
        textures = new ObjectMap<String, Texture>(32);
    }

    public void loadTexture(final String url) {
        if (textures.containsKey(url))
            return;
        final Image img = new Image(url);
        img.setVisible(false);
        RootPanel.get().add(img);
        img.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                ImageElement imgElement = ImageElement.as(img.getElement());
                RootPanel.get().remove(img);
                Pixmap pixmap = new Pixmap(imgElement);
                textures.put(url, new Texture(pixmap));
                pixmap.dispose();
            }
        });
    }
}
