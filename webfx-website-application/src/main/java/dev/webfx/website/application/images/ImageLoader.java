package dev.webfx.website.application.images;

import dev.webfx.kit.launcher.WebFxKitLauncher;
import dev.webfx.platform.shared.services.resource.ResourceService;
import javafx.scene.image.ImageView;

/**
 * @author Bruno Salmon
 */
public final class ImageLoader {

    public static ImageView loadImage(String resourcePath) {
        return loadImage(resourcePath, ImageLoader.class);
    }

    public static ImageView loadImage(String resourcePath, Class packageClass) {
        if (WebFxKitLauncher.supportsWebP())
            resourcePath = resourcePath.replace(".png", ".webp");
        return new ImageView(ResourceService.toUrl(resourcePath, packageClass));
    }

}
