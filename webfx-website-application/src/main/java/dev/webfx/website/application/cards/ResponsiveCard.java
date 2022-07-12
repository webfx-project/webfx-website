package dev.webfx.website.application.cards;

import dev.webfx.extras.webtext.HtmlText;
import dev.webfx.website.application.shared.WebSiteShared;
import dev.webfx.website.application.images.ImageLoader;
import dev.webfx.website.application.shared.LayoutPane;
import dev.webfx.website.application.shared.ScalePane;
import javafx.animation.KeyValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

/**
 * @author Bruno Salmon
 */
final class ResponsiveCard extends FlipCard {

    private Pane desktopTabletMobilePane;
    private SVGPath thumbUp;

    ResponsiveCard() {
        super("Responsive");
    }

    private static Pane createDeviceScreen(Node content, double screenDeviceWidth) {
        Rectangle contentClip = new Rectangle();
        content.setClip(contentClip);
        return new LayoutPane(content) {
            @Override
            protected void layoutChildren(double width, double height) {
                double borderWidthPercent = 0.05, scale = screenDeviceWidth < 0 ? 1 : width * (1 - 2 * borderWidthPercent) / screenDeviceWidth;
                content.setScaleX(scale);
                content.setScaleY(scale);
                double borderWidth = borderWidthPercent * width, borderHeight = borderWidth * (screenDeviceWidth > 0 && screenDeviceWidth < 400 ? 4 : 1);
                WebSiteShared.setRegionBackground(this, Color.gray(0.1), new CornerRadii(borderWidth));
                double cw = (width - 2 * borderWidth) / scale, ch = (height - 2 * borderHeight) / scale, cx = borderWidth - cw * (1 - scale) / 2, cy = borderHeight -ch * (1 - scale) / 2;
                layoutInArea(content, cx, cy, cw, ch);
                contentClip.setWidth(cw);
                contentClip.setHeight(ch);
            }
        };
    }

    private static Pane createShowCaseDeviceScreen(double screenDeviceWidth) {
        return createDeviceScreen(new ResponsiveShowCasePane(), screenDeviceWidth);
    }

    private static Pane createSplitBarShowCaseDeviceScreen() {
        return createDeviceScreen(ResponsiveShowCasePane.createSplitPanes(), -1);
    }

    @Override
    String caption(int step) {
        switch (step) {
            case 1: return "Your responsive web design made easy and powerful with the JavaFX layout system.";
            case 2: return "JavaFX can callback your code during the layout pass to ask you to position nodes. This is a fantastic feature! Unmatched by HTML & CSS!";
            case 3: return "As opposed to CSS rules, your layout code has access to your application context, giving you all the visibility you need to easily write your responsive design.";
            case 4: return "While the number of CSS rules and functions are limited, you are empowered with all the possibilities of a programmatic language in your layout code.";
            case 5: return "As opposed to CSS rules such as @media which works only at the top level, your layout code will work whatever its level in the scene graph.";
            case 6: return "We made this website responsive entirely based on this JavaFX feature, using it at many different levels of the scene graph. Easy and powerful.";
            default: return null;
        }
    }

    @Override
    void prepareCardTransition(int step, CardTransition cardTransition) {
        super.prepareCardTransition(step, cardTransition);
        switch (step) {
            case 1:
                if (desktopTabletMobilePane == null) {
                    Pane desktopScreen = createShowCaseDeviceScreen(1024), tabletScreen = createShowCaseDeviceScreen(768), mobileScreen = createShowCaseDeviceScreen(360);
                    Pane thumbUpPane = new ScalePane(thumbUp = WebSiteShared.createThumbUp());
                    thumbUp.setFill(Color.WHITE);
                    thumbUp.setEffect(WebSiteShared.dropShadow);
                    desktopTabletMobilePane = new LayoutPane(desktopScreen, tabletScreen, mobileScreen, thumbUpPane) {
                        @Override
                        protected void layoutChildren(double width, double height) {
                            height -= getTitleSpace();
                            double dx = 0, dy = 0.1 * height, dw = width, dh = 0.7 * height;
                            double th = 0.5 * height, tw = th * 0.75, tx = 0.65 * width - tw / 2, ty = 0.7 * height - th / 2;
                            double mh = 0.25 * height, mw = mh * 0.6 , mx = 0.25 * width - mw / 2, my = ty + th - mh;
                            double uw = width / 3, uh = height / 3, ux = width / 2 - uw / 2, uy = height / 2 - uh / 2;
                            layoutInArea(desktopScreen, dx, dy, dw, dh);
                            layoutInArea(tabletScreen,  tx, ty, tw, th);
                            layoutInArea(mobileScreen,  mx, my, mw, mh);
                            layoutInArea(thumbUpPane,   ux, uy, uw, uh);
                        }
                    };
                }
                cardTransition.addKeyValue(new KeyValue(thumbUp.opacityProperty(), 0));
                flipToNewContent(desktopTabletMobilePane);
                break;
            case 2:
            case 3:
                Pane screen = createShowCaseDeviceScreen(-1);
                HtmlText codeText = new HtmlText();
                if (step == 2)
                    codeText.setText("<pre style=\"background-color: #1d1d26; color: #c9c9d1; font-family: monospace; font-size: 9.8pt;\">\n" +
                            "<span style=\"color: #676773;\">// A typical JavaFX container with custom layout code:<br></span>Pane container = <span style=\"color: #e0957b;\">new </span>Pane(node1<span style=\"color: #e0957b; font-weight: bold;\">, </span>node2<span style=\"color: #e0957b; font-weight: bold;\">, </span>node3<span style=\"color: #e0957b; font-weight: bold;\">, </span>..., nodeN) {<br><span style=\"color: #85a658;\">&nbsp; &nbsp; @Override </span><span style=\"color: #e0957b;\">protected void </span><span style=\"color: #c7a65d;\">layoutChildren</span>() {<br><span style=\"color: #676773;\">&nbsp; &nbsp; &nbsp; &nbsp; // Your layout code here. It will be automatically<br></span><span style=\"color: #676773;\">&nbsp; &nbsp; &nbsp; &nbsp; // called by JavaFX at appropriate times!<br></span>&nbsp; &nbsp; }<br>}<span style=\"color: #e0957b; font-weight: bold;\">;<br></span>\n" +
                            "</pre>");
                else
                    codeText.setText("<pre style=\"background-color: #1d1d26; color: #c9c9d1; font-family: monospace; font-size: 9.8pt;\">\n" +
                            "<span style=\"color: #676773;\">// Example of a simple responsive design implementation:<br></span>Pane container = <span style=\"color: #e0957b;\">new </span>Pane(node1<span style=\"color: #e0957b; font-weight: bold;\">, </span>node2<span style=\"color: #e0957b; font-weight: bold;\">, </span>node3<span style=\"color: #e0957b; font-weight: bold;\">, </span>..., nodeN) {<br><span style=\"color: #85a658;\">&nbsp; &nbsp; @Override </span><span style=\"color: #e0957b;\">protected void </span><span style=\"color: #c7a65d;\">layoutChildren</span>() {<br><span style=\"color: #676773;\">&nbsp; &nbsp; &nbsp; &nbsp; // Getting the dimensions of the container<br></span><span style=\"color: #e0957b;\">&nbsp; &nbsp; &nbsp; &nbsp; double </span>width = getWidth()<span style=\"color: #e0957b; font-weight: bold;\">, </span>height = getHeight()<span style=\"color: #e0957b; font-weight: bold;\">;<br></span><span style=\"color: #e0957b;\"><span style=\"background-color: #1d1d26; color: #c9c9d1; font-family: monospace; font-size: 9.8pt;\"><span style=\"color: #676773;\">&nbsp; &nbsp; &nbsp; &nbsp; // Your responsive rules can look like this:<br></span></span>&nbsp; &nbsp; &nbsp; &nbsp; double </span>x1<span style=\"color: #e0957b; font-weight: bold;\">, </span>y1<span style=\"color: #e0957b; font-weight: bold;\">, </span>h1<span style=\"color: #e0957b; font-weight: bold;\">, </span>w1<span style=\"color: #e0957b; font-weight: bold;\">, </span>x2<span style=\"color: #e0957b; font-weight: bold;\">, </span>y2<span style=\"color: #e0957b; font-weight: bold;\">,</span> etc...<span style=\"color: #e0957b; font-weight: bold;\">;<br></span><span style=\"background-color: #1d1d26; color: #c9c9d1; font-family: monospace; font-size: 9.8pt;\"><span style=\"color: #e0957b;\">&nbsp; &nbsp; &nbsp; &nbsp; if </span>(width &gt; <span style=\"color: #4dacf0;\">1024</span>) {<br>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; x1 = ...<span style=\"color: #e0957b; font-weight: bold;\">; </span>y1 = ...<span style=\"color: #e0957b; font-weight: bold;\">;</span> etc...<span style=\"color: #e0957b; font-weight: bold;\"><br></span>&nbsp; &nbsp; &nbsp; &nbsp; } <span style=\"color: #e0957b;\">else if </span>(width &gt; <span style=\"color: #4dacf0;\">800</span>) {...} <span style=\"color: #e0957b;\">else </span>etc...</span>\n" +
                            "</div>\n" +
                            "<div style=\"background-color: #1d1d26; color: #c9c9d1; font-family: monospace; font-size: 9.8pt;\">\n" +
                            "<span style=\"color: #676773;\">&nbsp; &nbsp; &nbsp; &nbsp; // You have direct access to your nodes and<br></span><span style=\"color: #676773;\">&nbsp; &nbsp; &nbsp; &nbsp; // can finally position them all in one go:<br></span> &nbsp; &nbsp; &nbsp; &nbsp; layoutInArea(<span style=\"color: #93a6f5;\">node1</span><span style=\"color: #e0957b; font-weight: bold;\">, </span>x1<span style=\"color: #e0957b; font-weight: bold;\">, </span>y1<span style=\"color: #e0957b; font-weight: bold;\">, </span>w1<span style=\"color: #e0957b; font-weight: bold;\">, </span>h1<span style=\"color: #e0957b; font-weight: bold;\">, </span>...)<span style=\"color: #e0957b; font-weight: bold;\">;<br></span>&nbsp; &nbsp; &nbsp; &nbsp; layoutInArea(<span style=\"color: #93a6f5;\">node2</span><span style=\"color: #e0957b; font-weight: bold;\">, </span>x2<span style=\"color: #e0957b; font-weight: bold;\">, </span>y2<span style=\"color: #e0957b; font-weight: bold;\">, </span>w2<span style=\"color: #e0957b; font-weight: bold;\">, </span>h2<span style=\"color: #e0957b; font-weight: bold;\">, </span>...)<span style=\"color: #e0957b; font-weight: bold;\">;<br></span>&nbsp; &nbsp; &nbsp; &nbsp; layoutInArea(<span style=\"color: #93a6f5;\">node3</span><span style=\"color: #e0957b; font-weight: bold;\">, </span>x3<span style=\"color: #e0957b; font-weight: bold;\">, </span>y3<span style=\"color: #e0957b; font-weight: bold;\">, </span>w3<span style=\"color: #e0957b; font-weight: bold;\">, </span>h3<span style=\"color: #e0957b; font-weight: bold;\">, </span>...)<span style=\"color: #e0957b; font-weight: bold;\">;<br></span><span style=\"color: #676773;\">&nbsp; &nbsp; &nbsp; &nbsp; ...<br></span>&nbsp; &nbsp; &nbsp; &nbsp; layoutInArea(<span style=\"color: #93a6f5;\">nodeN</span><span style=\"color: #e0957b; font-weight: bold;\">, </span>xN<span style=\"color: #e0957b; font-weight: bold;\">, </span>yN<span style=\"color: #e0957b; font-weight: bold;\">, </span>wN<span style=\"color: #e0957b; font-weight: bold;\">, </span>hN<span style=\"color: #e0957b; font-weight: bold;\">, </span>...)<span style=\"color: #e0957b; font-weight: bold;\">;<br></span>&nbsp; &nbsp; }<br>}<span style=\"color: #e0957b; font-weight: bold;\">;<br></span>\n" +
                            "</pre>");
                flipToNewContent(new LayoutPane(screen, codeText) {
                    @Override
                    protected void layoutChildren(double width, double height) {
                        double codeHeight = codeText.prefHeight(width);
                        layoutInArea(screen, 0, 0, width, height - codeHeight - 0.03 * height);
                        layoutInArea(codeText, 0, height - codeHeight, width, codeHeight);
                    }
                });
                break;
            case 4:
                VBox vBox = new VBox(
                        WebSiteShared.setHtmlText(new HtmlText(), "See how the EnzoClocks demo calculates the optimal positions of the clocks using a circle packing algorithm."),
                        new ScalePane(ImageLoader.loadImage("EnzoClocks.png")),
                        WebSiteShared.setHtmlText(new HtmlText(), "Something impossible to achieve with CSS.")
                );
                vBox.setAlignment(Pos.CENTER);
                vBox.setSpacing(0.05 * flipPanel.getHeight());
                flipToNewContent(vBox);
                break;
            case 5:
                Text text = WebSiteShared.setUpText(new Text("Move the bars"), 40, true, false, true, true);
                text.setFill(Color.BLACK);
                text.setMouseTransparent(true);
                StackPane stackPane = new StackPane(
                        createSplitBarShowCaseDeviceScreen(),
                        text
                );
                stackPane.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                    cardTransition.addKeyValue(new KeyValue(text.opacityProperty(), 0));
                    cardTransition.run(true);
                });
                flipToNewContent(stackPane);
                break;
            case 6:
                cardTransition.addKeyValue(new KeyValue(thumbUp.opacityProperty(), 1));
                flipToNewContent(desktopTabletMobilePane);
                break;
        }
    }
}
