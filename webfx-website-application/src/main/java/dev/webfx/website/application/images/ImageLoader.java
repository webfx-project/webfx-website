package dev.webfx.website.application.images;

import dev.webfx.kit.launcher.WebFxKitLauncher;
import dev.webfx.platform.shared.services.resource.ResourceService;
import javafx.scene.image.ImageView;

/**
 * @author Bruno Salmon
 */
public final class ImageLoader {

    public static ImageView loadImage(String resourcePath) {
        return new ImageView(toImageUrl(resourcePath));
    }

    public static String toImageUrl(String resourcePath) {
        if (WebFxKitLauncher.supportsWebP())
            resourcePath = resourcePath.replace(".png", ".webp");
        return ResourceService.toUrl(resourcePath, ImageLoader.class);
    }

}
