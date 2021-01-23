package dev.webfx.website.application.cards;

import javafx.scene.Node;

/**
 * @author Bruno Salmon
 */
public class WebFxCard extends Card {

    public WebFxCard() {
        super("WebFX?");
    }

    @Override
    Node createIllustrationNode() {
        useTitleSpaceForIllustrationWhenHidden = false;
        return new JavaFxToWebAnimationPane();
    }

    @Override
    String caption(int step) {
        switch (step) {
            case 1: return "A complete new way to develop web applications using JavaFX!";
            case 2: return "WebFX is a JavaFX \u2192 JS transpiler powered by GWT. You write your application in JavaFX and GWT will transpile it in pure JS (no plugin, no server).";
            case 3: return "WebFX patches the higher layer of JavaFX to make it GWT compatible, and replaces the lower layer with a JavaFX scene graph \u2192 browser DOM mapper.";
            case 4: return "You code and debug your application as usual in your Java IDE with the standard JavaFX runtime, and transpile it only when you want to test the web version.";
            case 5: return "WebFX is brand new and it may take a bit of time to cover all JavaFX features, but it has the potential to quickly become a successful open source project.";
            case 6: return "WebFX will be soon ready for beta testing. We hope you will love it.";
            default : return null;
        }
    }
}
