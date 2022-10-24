// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.website.application {

    // Direct dependencies modules
    requires java.base;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.media;
    requires webfx.extras.scalepane;
    requires webfx.extras.webtext;
    requires webfx.kit.launcher;
    requires webfx.lib.enzo;
    requires webfx.platform.resource;
    requires webfx.platform.uischeduler;
    requires webfx.platform.windowlocation;

    // Exported packages
    exports dev.webfx.website.application;
    exports dev.webfx.website.application.cards;
    exports dev.webfx.website.application.demos;
    exports dev.webfx.website.application.images;
    exports dev.webfx.website.application.shared;

    // Resources packages
    opens dev.webfx.website.application.images;

    // Provided services
    provides javafx.application.Application with dev.webfx.website.application.WebFXWebsiteApplication;

}