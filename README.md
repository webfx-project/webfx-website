# The WebFX website

It presents the technology through a series of animated cards. It's a WebFX application itself, so it's a web app written in Java & JavaFX and transpiled into JS by WebFX. Here is the [source code][webfx-website-code] of the JavaFX application class.

It is automatically deployed through the GitHub and Netlify CI/CD pipeline. On each push on the main branch, the [GitHub workflow][webfx-website-workflow] triggers the GWT compilation and push the generated web app to Netlify which updates the website.

The website is web responsive. The responsive web design is implemented using the JavaFX layout system, simply by extending the JavaFX Pane class and overriding the layoutChildren() method, at many level of the scene graph (root container, cards container, cards, and inside card containers).

If you find any bug, you can open an issue.

[webfx-website-code]: https://github.com/webfx-project/webfx-website/blob/main/webfx-website-application/src/main/java/dev/webfx/website/application/WebFXWebsiteApplication.java
[webfx-website-workflow]: https://github.com/webfx-project/webfx-website/blob/main/.github/workflows/builds.yml