package elegit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The starting point for this JavaFX application.
 */
public class Main extends Application {
    private Path logPath;

    public static SessionController sessionController;

    @Override
    public void start(Stage primaryStage) throws Exception{

        // -----------------------Logging Initialization Start---------------------------
        // Create a temp directory for the files to be placed in
        this.logPath = Files.createTempDirectory("elegitLogs");
        this.logPath.toFile().deleteOnExit();
        System.out.println(logPath.toString());
        System.setProperty("logFolder", logPath.toString());

        final Logger logger = LogManager.getLogger();
        // -----------------------Logging Initialization End-----------------------------

        logger.info("Starting up.");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/elegit/fxml/MainView.fxml"));
        fxmlLoader.load();
        Pane root = fxmlLoader.getRoot();
        sessionController = fxmlLoader.getController();

        primaryStage.setTitle("Elegit");

        // sets the icon
        Image img = new Image(getClass().getResourceAsStream("/elegit/elegit_icon.png"));
        primaryStage.getIcons().add(img);
        // handles mac os dock icon
        if (SystemUtils.IS_OS_MAC) {
            java.awt.image.BufferedImage dock_img = ImageIO.read(
                    getClass().getResourceAsStream(
                    "/elegit/elegit_icon.png"
                )
            );
            com.apple.eawt.Application.getApplication()
                    .setDockIconImage(dock_img);
        }

        primaryStage.setOnCloseRequest(event -> {
                // On close, upload the logs and delete the log.
                logger.info("Closed");
                sessionController.submitLog(this.logPath.toString());});

        BusyWindow.setParentWindow(primaryStage);

        Scene scene = new Scene(root, 1200, 650); // width, height

        // create the menu bar here
        MenuBar menuBar = MenuPopulator.getInstance().populate();
        // for now we'll only display menu on mac os
        // because it blocks repo dropdown menu on other platforms
        if (SystemUtils.IS_OS_MAC) {
            ((Pane) scene.getRoot()).getChildren().addAll(menuBar);
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
