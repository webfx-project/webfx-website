package dev.webfx.website.application.demos;

import dev.webfx.website.application.images.ImageLoader;
import dev.webfx.extras.panes.ScaleMode;
import dev.webfx.extras.panes.ScalePane;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.*;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import static dev.webfx.website.application.shared.WebSiteShared.*;

/**
 * @author Bruno Salmon
 */
final class DemoThumbnail extends Pane {

    enum DemoCategory {
        CUSTOM_CONTROL ("Custom control", Color.BLUE),
        GAME ("Game", Color.DEEPPINK.darker()),
        ANIMATION ("Animation", Color.DEEPPINK.darker()),
        WEB_WORKER ("Web worker", Color.RED),
        WEBASSEMBLY ("WebAssembly", Color.RED),
        WEBGL("WebGL", Color.RED);

        final String name;
        final Color color;

        DemoCategory(String name, Color color) {
            this.name = name;
            this.color = color;
        }
    }

    private final String demoLink, videoUrl;
    private final ScalePane demoImageScalePane;
    private ScalePane demoVideoScalePane;
    private final Text demoNameText, demoCategoryText;
    private final Region categoryFullBackgroundRegion = new Region(), categoryFadingBackgroundRegion = new Region();
    private final SVGPath githubLogo = createGithubLogo();
    private final ScalePane githubLogoPane =  new ScalePane(githubLogo);

    public DemoThumbnail(String demoName, DemoCategory demoCategory, ScaleMode crop) {
        this(demoName, demoCategory, crop, demoName.replace(" ", "") + ".png");
    }

    public DemoThumbnail(String demoName, DemoCategory demoCategory, ScaleMode crop, String imageName) {
        this(demoName, demoCategory, crop, imageName, imageName.substring(0, imageName.indexOf('.')));
    }

    public DemoThumbnail(String demoName, DemoCategory demoCategory, ScaleMode crop, String imageName, String token) {
        this(demoName, demoCategory, crop, imageName, "https://" + token.toLowerCase() + ".webfx.dev", "https://github.com/webfx-demos/webfx-demo-" + token.toLowerCase(), "https://webfx-demos.github.io/webfx-demos-videos/" + token + ".mp4");
    }

    public DemoThumbnail(String demoName, DemoCategory demoCategory, ScaleMode scaleMode, String imageName, String demoLink, String repositoryLink, String videoUrl) {
        this(demoName, demoCategory, scaleMode, imageName, demoLink, repositoryLink, videoUrl, CARD_TRANSLUCENT_BACKGROUND);
    }

    public DemoThumbnail(String demoName, DemoCategory demoCategory, ScaleMode scaleMode, String imageName, String demoLink, String repositoryLink, String videoUrl, Paint background) {
        this.demoLink = demoLink;
        this.videoUrl = videoUrl;
        setRegionBackground(this, background);
        demoCategoryText = setUpText(new Text(demoCategory.name), 20, false, true, false, false);
        setRegionBackground(categoryFullBackgroundRegion, demoCategory.color);
        setRegionBackground(categoryFadingBackgroundRegion, new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.TRANSPARENT), new Stop(1, demoCategory.color)));
        demoImageScalePane = new ScalePane(scaleMode, ImageLoader.loadImage(imageName));
        demoNameText = setUpText(new Text(demoName), 40, true, true, false, true);
        demoNameText.setFill(Color.IVORY);
        setShapeHoverAnimationColor(githubLogo, LAST_GITHUB_GRADIENT_COLOR.darker());
        setOnMouseEntered(e -> githubLogo.setVisible(true));
        setOnMouseExited( e -> githubLogo.setVisible(false));
        githubLogo.setVisible(false);
        runOnMouseClick(githubLogo, () -> openUrl(repositoryLink));
        getChildren().setAll(demoImageScalePane, demoNameText, categoryFadingBackgroundRegion, categoryFullBackgroundRegion, demoCategoryText, githubLogoPane);
    }

    void startVideo() {
        if (videoUrl != null && demoVideoScalePane == null) {
            MediaPlayer mediaPlayer = new MediaPlayer(new Media(videoUrl));
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            demoVideoScalePane = new ScalePane(demoImageScalePane.getScaleMode(), new MediaView(mediaPlayer));
            // The demo video is initially displayed on behind the image (which is hiding it)
            getChildren().add(0, demoVideoScalePane);
            // Removing the demo image when the video starts playing
            mediaPlayer.setOnPlaying(() -> getChildren().remove(demoImageScalePane));
            mediaPlayer.setMute(true); // Videos have no sound, but we explicitly mute them, otherwise iPads will refuse to play more than 2 videos
            mediaPlayer.play();
        }
    }

    String getDemoLink() {
        return demoLink;
    }

    @Override
    protected void layoutChildren() {
        double width = getWidth(), height = getHeight();
        setFont(demoNameText, Math.max(16, width * 0.07), true, false);
        setFont(demoCategoryText, Math.max(16, width * 0.05), false, true);
        layoutInArea(demoImageScalePane, 0, 0, width, height, 0 , HPos.CENTER, VPos.CENTER);
        if (demoVideoScalePane != null)
            layoutInArea(demoVideoScalePane, 0, 0, width, height, 0 , HPos.CENTER, VPos.CENTER);
        layoutInArea(demoNameText, 0, 0, width, height, 0 , HPos.CENTER, VPos.BOTTOM);
        double githubSize = demoNameText.prefHeight(width);
        layoutInArea(githubLogoPane, width - githubSize - 5, height - githubSize - 5, githubSize, githubSize, 0 , HPos.LEFT, VPos.BOTTOM);
        double rightCategoryMargin = 5;
        layoutInArea(demoCategoryText, 0, 0, width - rightCategoryMargin, height, 0 , HPos.RIGHT, VPos.TOP);
        double categoryHeight = demoCategoryText.prefHeight(width - rightCategoryMargin);
        double fullCategoryWidth = demoCategoryText.prefWidth(height) - rightCategoryMargin;
        double fadeCategoryWidth = Math.min(width - fullCategoryWidth, 100);
        layoutInArea(categoryFadingBackgroundRegion, width - fullCategoryWidth - fadeCategoryWidth, 0, fadeCategoryWidth, categoryHeight, 0 , HPos.LEFT, VPos.TOP);
        layoutInArea(categoryFullBackgroundRegion, width - fullCategoryWidth, 0, fullCategoryWidth, categoryHeight, 0 , HPos.RIGHT, VPos.TOP);
    }

    private static void setFont(Text text, double size, boolean bold, boolean italic) {
        if (text.getFont().getSize() != size)
            text.setFont(Font.font("Arial", bold ? FontWeight.BOLD : FontWeight.NORMAL, italic ? FontPosture.ITALIC : FontPosture.REGULAR, size));
    }
}
