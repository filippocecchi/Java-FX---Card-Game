package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javafx.scene.paint.Color;

public class TournamentLobby {
    private Scene scene;
    private String gameCode;
    private String fullChildPath;
    
    private Runnable onLogoutHandle;
    private Runnable onStartGameHandle;

    public TournamentLobby(String gameCode) {
        this.gameCode = gameCode;

        VBox root = new VBox();
        root.setSpacing(10);
        root.setAlignment(Pos.CENTER);
        
        ImageView logoutButton = new ImageView(new Image(getClass().getClassLoader().getResource("logout.png").toExternalForm()));
        logoutButton.setFitWidth(90);
        logoutButton.setFitHeight(50);
        
        logoutButton.setOnMouseClicked(event -> onLogoutHandle.run());

        Label titleLabel = new Label("Tournament: " + this.gameCode);
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        ObservableList<String> matchList = readMatchesFromFile("./data/" + this.gameCode + ".txt");
        
        ListView<String> listView = new ListView<>(matchList);

        listView.setCellFactory(param -> createMatchCell());

        root.getChildren().addAll(logoutButton, titleLabel, listView);
        
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: green;");

        VBox.setVgrow(listView, Priority.ALWAYS);
        listView.setMaxWidth(Double.MAX_VALUE);

        this.scene = new Scene(root, 1100, 700);
    }

    public Scene getScene() {
        return this.scene;
    }
    
    public String getFullChildPath() {
    	return this.fullChildPath;
    }
    
    public void setOnLogoutHandle(Runnable handler) {
        onLogoutHandle = handler;
    }
    
    public void setOnStartGameHandle(Runnable handler) {
    	onStartGameHandle = handler;
    }
    
    private ObservableList<String> readMatchesFromFile(String filePath) {
        ObservableList<String> matchList = FXCollections.observableArrayList();
        ObservableList<String> notMatchedUser = FXCollections.observableArrayList();
        
        ObservableList<String> newFileLine = FXCollections.observableArrayList();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] players = line.split(",");
                if (players.length == 2) {
                    String match = players[0] + " vs " + players[1];
                    matchList.add(match);
                    newFileLine.add(line);
                } else {
                	notMatchedUser.add(players[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        String flag = "";
        
        if(notMatchedUser.size() > 0 && notMatchedUser.size() % 2 == 0) {
            for (int i = 0; i < notMatchedUser.size(); i++) {
                if (i % 2 == 0) {
                    flag = notMatchedUser.get(i);
                } else {
                	flag = flag + "," + notMatchedUser.get(i);
                    newFileLine.add(flag);
                    
                    String[] splitStrings = flag.split(",");
                    
                    matchList.add(splitStrings[0] + " vs " + splitStrings[1]);
                    flag = "";
                }
            }
        } else {
            for (int i = 0; i < notMatchedUser.size(); i++) {
            	newFileLine.add(notMatchedUser.get(i));
            }
        }
        
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
	        for (String line : newFileLine) {
	            writer.write(line);
	            writer.newLine();
	        }
	        
	        writer.close();  
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

        return matchList;
    }

    private ListCell<String> createMatchCell() {
        return new ListCell<>() {
            private final Button playButton = new Button("Play");

            {
                playButton.setOnAction(event -> {
                    String match = getItem();
                    String[] parts = match.split(" vs ");
                    
                    ArrayList<String> usernames = new ArrayList<>();
                    
                    for(int i = 0; i < parts.length; i++) {
                    	usernames.add(parts[i]);
                    }
                    
                    startChildGame(usernames);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(playButton);
                }
            }
        };
    }
    
    private void startChildGame(ArrayList<String> users) {  
        ArrayList<Integer> deck = new ArrayList<>();
        this.fullChildPath = this.gameCode + "_" + this.generateRandomFileName();
    	String fullPath = "./data/" + this.fullChildPath + ".txt";
    	
        for (int number = 1; number <= 9; number++) {
            for (int count = 0; count < 4; count++) {
                deck.add(number);
            }
        }
        
        Collections.shuffle(deck);
        
        Random random = new Random();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath, false))) {
            for (String username : users) {
                writer.write(username + ",");
                for (int i = 0; i < 5; i++) {
                    int randomIndex = random.nextInt(deck.size());
                    int card = deck.get(randomIndex);
                    writer.write(String.valueOf(card));
                    deck.remove(randomIndex);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("Created deck: " + deck);
        
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath, true));
            
            String fileDeck = "DECK:";
            String currentCard = "CURRENT:back";
            
            for (Integer card : deck) {
                fileDeck += card;
            }
            
            writer.write(fileDeck);
            writer.newLine();
            writer.write(currentCard);
            writer.close();
            
            onStartGameHandle.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String generateRandomFileName() {
        Random random = new Random();
        String characters = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder fileName = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            int randomIndex = random.nextInt(characters.length());
            fileName.append(characters.charAt(randomIndex));
        }
        return fileName.toString();
    }
}