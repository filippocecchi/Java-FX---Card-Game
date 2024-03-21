package application;

import javafx.scene.Scene;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AdminScene {
    private Runnable onLogoutHandle;
    private Runnable onCreateGameHandle;
    private Runnable onUserSceneHandle;
	
    private Scene scene;
    
    private boolean isTournament;
    private ObservableList<String> matchesList = FXCollections.observableArrayList();
	
	public AdminScene() {
		this.getMatchFromFile();
		
		BorderPane root = new BorderPane();

		Scene scene1 = new Scene(root, 1100, 700);
		
        ImageView logoutImage = new ImageView(new Image(getClass().getClassLoader().getResource("logout.png").toExternalForm()));
        logoutImage.setFitWidth(110);
        logoutImage.setFitHeight(50);
        
        logoutImage.setOnMouseClicked(event -> onLogoutHandle.run());
		
        ImageView createTournamentImage = new ImageView(new Image(getClass().getClassLoader().getResource("create_tournament.png").toExternalForm()));
        createTournamentImage.setFitWidth(290);
        createTournamentImage.setFitHeight(50);
        
        createTournamentImage.setOnMouseClicked(event -> createGame(true));
		
        ImageView createSingleMatchImage = new ImageView(new Image(getClass().getClassLoader().getResource("create_single_game.png").toExternalForm()));
        createSingleMatchImage.setFitWidth(290);
        createSingleMatchImage.setFitHeight(50);
        
        createSingleMatchImage.setOnMouseClicked(event -> createGame(false));
        
        ImageView userList = new ImageView(new Image(getClass().getClassLoader().getResource("edit_user.png").toExternalForm()));
        userList.setFitWidth(160);
        userList.setFitHeight(50);
        
        userList.setOnMouseClicked(event -> onUserSceneHandle.run());

		HBox topButtonsBox = new HBox(logoutImage, createTournamentImage, createSingleMatchImage, userList);
		topButtonsBox.setSpacing(10);
		topButtonsBox.setPadding(new Insets(10));
		topButtonsBox.setAlignment(Pos.TOP_LEFT);
		root.setTop(topButtonsBox);
		
        ImageView registeredMatchesLabel = new ImageView(new Image(getClass().getClassLoader().getResource("registered_match.png").toExternalForm()));
        registeredMatchesLabel.setFitWidth(350);
        registeredMatchesLabel.setFitHeight(50);
		
		ListView<String> matchesListView = new ListView<>();
		
		matchesListView.setItems(this.matchesList);
		matchesListView.setCellFactory(param -> createMatchCell());
		
		VBox centerBox = new VBox(registeredMatchesLabel, matchesListView);
		centerBox.setSpacing(10);
		centerBox.setPadding(new Insets(10));
		VBox.setVgrow(matchesListView, Priority.ALWAYS);
		matchesListView.setMaxWidth(Double.MAX_VALUE);
		root.setCenter(centerBox);
		root.setStyle("-fx-background-color: green;");

		this.scene = scene1;
	}
	
	public Scene getScene() {
		return this.scene;
	}
	
	public boolean getIsTournament() {
		return this.isTournament;
	}
	
    public void setOnLogoutHandle(Runnable handler) {
        onLogoutHandle = handler;
    }
    
    public void setOnCreateGameHandle(Runnable handler) {
    	onCreateGameHandle = handler;
    }
    
    public void setOnUserSceneHandle(Runnable handler) {
    	onUserSceneHandle = handler;
    }
    
    public void createGame(boolean isTournament) {
    	this.isTournament = isTournament;
    	
    	onCreateGameHandle.run();
    }
    
    public void getMatchFromFile() {
        String directoryPath = "./data/";
        
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directoryPath))) {
            for (Path file : directoryStream) {
                String fileName = file.getFileName().toString();
                if (!fileName.equals("leaderboard.txt") && !fileName.equals(".DS_Store")) {
                    //System.out.println("Processing file: " + fileName);
                	this.matchesList.add(fileName.substring(0, fileName.length() - 4));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private ListCell<String> createMatchCell() {
        return new ListCell<>() {
            private final Button deleteButton = new Button("DELETE");

            {
            	deleteButton.setOnAction(event -> {
            		String file = getItem();
            		
        	        File fileToDelete = new File("./data/" + file + ".txt");

        	        if (fileToDelete.exists()) {
        	            if (fileToDelete.delete()) {
        	                System.out.println("File deleted successfully.");
        	                
        	                matchesList.remove(file);
        	            } else {
        	                System.err.println("Failed to delete the file.");
        	            }
        	        } else {
        	            System.err.println("File does not exist.");
        	        }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        };
    }
}