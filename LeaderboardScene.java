package application;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class LeaderboardScene {
    private Scene scene;
    
    private Runnable onLogoutHandle;
    
	public LeaderboardScene() {
		List<Map<String, String>> playerData = readPlayerDataFromFile();

        VBox playerListVBox = new VBox(20);

        ImageView home = new ImageView(new Image(getClass().getClassLoader().getResource("home.png").toExternalForm()));
        home.setFitWidth(110);
        home.setFitHeight(50);
        home.setOnMouseClicked(event -> onLogoutHandle.run());

        ImageView headerImage = new ImageView(new Image(getClass().getClassLoader().getResource("leaderboard_title.png").toExternalForm()));
        headerImage.setFitWidth(600);
        headerImage.setFitHeight(80);
        headerImage.setPreserveRatio(true);

        for (int i = 0; i < playerData.size(); i++) {
            Map<String, String> player = playerData.get(i);

            HBox playerHBox = new HBox(10);
            playerHBox.setPrefWidth(1100);
            playerHBox.setSpacing(20);
            playerHBox.setAlignment(Pos.CENTER);

            Label playerLabel = new Label(player.get("username") + " - " + player.get("score"));
            playerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");

            playerHBox.getChildren().addAll(playerLabel);
            playerListVBox.getChildren().add(playerHBox);
            
            BackgroundFill backgroundFill = new BackgroundFill(Color.GREEN, null, null);
            Background background = new Background(backgroundFill);
            playerListVBox.setBackground(background);
        }
        
        ScrollPane scrollPane = new ScrollPane(playerListVBox);
        BackgroundFill backgroundFill = new BackgroundFill(Color.GREEN, null, null);
        Background background = new Background(backgroundFill);
        scrollPane.setBackground(background);

        VBox vbox = new VBox(20, home, headerImage, scrollPane);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setPrefWidth(1100);
        vbox.setStyle("-fx-background-color: green;");

        Scene scene1 = new Scene(vbox, 1100, 700);
        scene1.setFill(Color.GREEN);
        
        this.scene = scene1;
	}
	
    public Scene getScene() {
        return scene;
    }
    
    public void setOnLogoutHandle(Runnable handle) {
    	onLogoutHandle = handle;
    }
    
    private List<Map<String, String>> readPlayerDataFromFile() {
        List<Map<String, String>> playerData = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("./data/leaderboard.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
 
                Map<String, String> playerInfo = new HashMap<>();
                playerInfo.put("username", parts[0].trim());
                playerInfo.put("score", parts[1].trim());

                playerData.add(playerInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return playerData;
    }
}
