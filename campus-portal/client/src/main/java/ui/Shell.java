package ui;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import ui.pages.Navigation;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

/** The signed-in window: navigation, page header, connection banner and messages. */
public final class Shell {
    private static final int REFRESH_SECONDS = 4;

    private final Portal portal;
    private final PortalApp app;
    private final BorderPane root = new BorderPane();
    private final VBox navigation = new VBox();
    private final ToggleGroup navigationGroup = new ToggleGroup();
    private final StackPane content = new StackPane();
    private final Label title = new Label();
    private final Button backButton = new Button("Back");
    private final HBox banner = new HBox();
    private final Label toast = new Label();
    private final PauseTransition toastTimer = new PauseTransition(Duration.seconds(5));
    private final Deque<Page> history = new ArrayDeque<>();
    private final Timeline refresher = new Timeline(new KeyFrame(Duration.seconds(REFRESH_SECONDS), event -> autoRefresh()));
    private Page current;
    private Supplier<Page> homePage;

    Shell(Portal portal, PortalApp app) {
        this.portal = portal;
        this.app = app;
        build();
        refresher.setCycleCount(Timeline.INDEFINITE);
        refresher.play();
    }

    Parent view() {
        return root;
    }

    public Page currentPage() {
        return current;
    }

    private void build() {
        title.getStyleClass().add("page-title");
        backButton.setOnAction(event -> back());
        backButton.setVisible(false);
        backButton.setManaged(false);
        HBox header = new HBox(backButton, title);
        header.getStyleClass().add("page-header");
        buildBanner();
        toast.getStyleClass().add("toast");
        toast.setVisible(false);
        toast.setWrapText(true);
        toast.setMaxWidth(560);
        toast.setMouseTransparent(true);
        toastTimer.setOnFinished(event -> toast.setVisible(false));
        StackPane.setAlignment(toast, Pos.BOTTOM_CENTER);
        StackPane.setMargin(toast, new Insets(0, 0, 18, 0));
        VBox.setVgrow(content, Priority.ALWAYS);
        StackPane main = new StackPane(new VBox(banner, header, content), toast);
        root.setCenter(main);
        root.setLeft(buildSidebar());
        setOnline(portal.isOnline());
    }

    private void buildBanner() {
        Label label = new Label("You are offline. Saved pages are shown and changes are not possible.");
        Button reconnect = new Button("Reconnect");
        reconnect.setId("reconnect");
        reconnect.setOnAction(event -> portal.reconnect());
        banner.getChildren().addAll(label, reconnect);
        banner.getStyleClass().add("banner");
    }

    private Node buildSidebar() {
        Label brand = new Label("Campus Portal");
        brand.getStyleClass().add("brand-small");
        brand.setPadding(new Insets(0, 8, 2, 8));
        Label person = new Label(portal.fullName());
        person.getStyleClass().add("muted");
        person.setPadding(new Insets(0, 8, 4, 8));
        navigation.getChildren().addAll(brand, person);
        new Navigation(portal, this).build();
        navigation.getStyleClass().add("sidebar-list");
        ScrollPane scroll = new ScrollPane(navigation);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox footer = new VBox(1, sidebarButton("Switch theme", app::toggleTheme),
                sidebarButton("Sign out", this::signOut), sidebarButton("Exit", Platform::exit));
        footer.getStyleClass().add("sidebar-footer");
        BorderPane sidebar = new BorderPane(scroll);
        sidebar.setBottom(footer);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(205);
        sidebar.setMinWidth(205);
        return sidebar;
    }

    private static Button sidebarButton(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().setAll("nav-item");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(event -> action.run());
        return button;
    }

    private void signOut() {
        portal.logout();
    }

    void stop() {
        refresher.stop();
    }

    public void addGroup(String name) {
        Label label = new Label(name);
        label.getStyleClass().add("nav-group");
        navigation.getChildren().add(label);
    }

    public Node addItem(String name, Supplier<Page> page) {
        ToggleButton item = new ToggleButton(name);
        item.getStyleClass().setAll("nav-item");
        item.setMaxWidth(Double.MAX_VALUE);
        item.setToggleGroup(navigationGroup);
        item.setOnAction(event -> {
            item.setSelected(true);
            history.clear();
            show(page.get());
        });
        navigation.getChildren().add(item);
        if (homePage == null) {
            homePage = page;
            item.setSelected(true);
        }
        return item;
    }

    void showHome() {
        show(homePage.get());
    }

    void open(Page page) {
        history.push(current);
        show(page);
    }

    void back() {
        if (!history.isEmpty()) {
            show(history.pop());
        }
    }

    public void show(Page page) {
        toast.setVisible(false);
        current = page;
        page.attach(this);
        title.setText(page.title());
        boolean canGoBack = !history.isEmpty();
        backButton.setVisible(canGoBack);
        backButton.setManaged(canGoBack);
        content.getChildren().setAll(page.view());
        page.refresh();
    }

    public void refreshPage() {
        if (current != null) {
            current.refresh();
        }
    }

    private void autoRefresh() {
        if (portal.isOnline()) {
            refreshPage();
        }
    }

    void setOnline(boolean online) {
        banner.setVisible(!online);
        banner.setManaged(!online);
    }

    void showUnavailable() {
        Label label = new Label("This page is not available offline.");
        label.getStyleClass().add("muted");
        VBox box = new VBox(label);
        box.getStyleClass().add("page-body");
        content.getChildren().setAll(box);
    }

    void showMessage(String message) {
        showToast(message, false);
    }

    void showError(String message) {
        showToast(message, true);
    }

    public String failure() {
        return toast.isVisible() && toast.getStyleClass().contains("failure") ? toast.getText() : "";
    }

    boolean isOfflineBannerShown() {
        return banner.isVisible();
    }

    private void showToast(String message, boolean failure) {
        toast.setText(message);
        toast.getStyleClass().remove("failure");
        if (failure) {
            toast.getStyleClass().add("failure");
        }
        toast.setVisible(true);
        toastTimer.playFromStart();
    }
}
