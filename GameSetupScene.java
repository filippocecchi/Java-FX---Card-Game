package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GameSetupScene {
    private Scene scene;
    private Runnable onLogoutHandle;
    
    private boolean isTournament;
    
    private boolean isFirstInviaClick = true;
    
    private String currentFileName = null;
    private String title = "";
    private Integer playerForRow = 0;
    
    private Integer players = 0;
    private Label errorLabel;
	
	public GameSetupScene(boolean isTournament) {
		this.isTournament = isTournament;
		this.interfaceSetup();
        
        ImageView titleLabel = new ImageView(new Image(getClass().getClassLoader().getResource(this.title).toExternalForm()));
        titleLabel.setFitWidth(290);
        titleLabel.setFitHeight(40);
        
        ImageView logoutButton = new ImageView(new Image(getClass().getClassLoader().getResource("logout.png").toExternalForm()));
        logoutButton.setFitWidth(90);
        logoutButton.setFitHeight(50);
        
        logoutButton.setOnMouseClicked(event -> onLogoutHandle.run());
        
        ImageView playerInputLabel = new ImageView(new Image(getClass().getClassLoader().getResource("enter_player_name.png").toExternalForm()));
        playerInputLabel.setFitWidth(280);
        playerInputLabel.setFitHeight(30);        
        
        TextField playerInputField = new TextField();
        
        ListView<String> playerListView = new ListView<>();
        
        ImageView addButton = new ImageView(new Image(getClass().getClassLoader().getResource("add_player.png").toExternalForm()));
        addButton.setFitWidth(160);
        addButton.setFitHeight(50);
        
        addButton.setOnMouseClicked(e -> addPlayer(playerInputField, playerListView));
        
        ImageView doneButton = new ImageView(new Image(getClass().getClassLoader().getResource("create_game.png").toExternalForm()));
        doneButton.setFitWidth(160);
        doneButton.setFitHeight(50); 
        
        doneButton.setOnMouseClicked(event -> this.generateDeckAndUserCard());
        
        errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        VBox layout = new VBox(20, titleLabel, logoutButton, playerInputLabel, playerInputField, addButton, playerListView, doneButton, this.errorLabel);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: green;");

        this.scene = new Scene(layout, 1100, 700);
	}
	
	public Scene getScene() {
		return this.scene;
	}
	
	public String getFileName() {
		if(this.currentFileName != null) {
			return this.currentFileName.substring(0, this.currentFileName.length() - 4);	
		} else {
			return null;
		}
	}
	
    public void setOnLogoutHandle(Runnable handler) {
        onLogoutHandle = handler;
    }
    
    public void interfaceSetup() {
    	if(this.isTournament == false) {
    		title = "create_single_game_white.png";
    	} else {
    		title = "create_tournament_white.png";
    	}
    }
	
    private void addPlayer(TextField playerInputField, ListView<String> playerListView) {
        String playerName = playerInputField.getText();
        if (!playerName.isEmpty()) {
            playerListView.getItems().add(playerName);
            playerInputField.clear();
        }
        
        if (isFirstInviaClick) {
        	if(this.isTournament == false) {
                this.currentFileName = generateRandomFileName();
                isFirstInviaClick = false;	
        	} else {
        		this.currentFileName = "tr-" + generateRandomFileName();
        		isFirstInviaClick = false;
        	}
        }

        String directoryPath = "./data/";
        String fullPath = "";

        fullPath = directoryPath + this.currentFileName;
        
        System.out.println("FILE PATH: " + fullPath);
        File file = new File(fullPath);
        
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath, true));
            
        	if(this.isTournament == false) {
            	boolean isEmpty = file.length() == 0;

                if (!isEmpty) {
                    writer.newLine();
                }
                
                writer.write(playerName + ",");	
        	} else {
        		if(this.playerForRow % 2 == 0 && this.playerForRow != 0) {
        			writer.newLine();
        			writer.write(playerName + ",");
        		} else {
        			if(this.playerForRow == 0) { writer.write(playerName + ","); }
        			else { writer.write(playerName); }
        		}
        		
        		this.playerForRow = this.playerForRow + 1;
        	}
        	
        	this.players ++;
            writer.close();
            
            System.out.println("File create or edited: " + fullPath);
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
        return fileName.toString() + ".txt";
    }
    
    private void generateDeckAndUserCard() {
    	if(this.players > 1) {
        	if(this.isTournament == false) {
                ArrayList<Integer> deck = new ArrayList<>();
            	String fullPath = "./data/" + this.currentFileName;
                
                for (int number = 1; number <= 9; number++) {
                    for (int count = 0; count < 4; count++) {
                        deck.add(number);
                    }
                }
                
                Collections.shuffle(deck);
                
                ArrayList<String> usernames = new ArrayList<>();

                try (BufferedReader br = new BufferedReader(new FileReader(fullPath))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (!line.startsWith("DECK:")) {
                            usernames.add(line.trim());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                Random random = new Random();

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath, false))) {
                    for (String username : usernames) {
                        writer.write(username);
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
                    
                    onLogoutHandle.run();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        	} else {
        		if(this.playerForRow % 2 == 0 && this.players >= 4) {
            		onLogoutHandle.run();	
        		} else {
                    this.errorLabel.setText("You have to add another player!");
                    this.errorLabel.setFont(new Font(18));
        			System.out.println("You have to add another player!");
        			
        	        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        	        
        	        Runnable task = () -> {
        	            Platform.runLater(() -> {
        	                setToEmptyLabel();
        	            });
        	        };

        	        int timeoutInSeconds = 2;
        	        executorService.schedule(task, timeoutInSeconds, TimeUnit.SECONDS);
        	        
        	        executorService.shutdown();
        		}
        	}	
    	} else {
            this.errorLabel.setText("You have to add more that one player!");
            this.errorLabel.setFont(new Font(18));
            
            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
            
            Runnable task = () -> {
                Platform.runLater(() -> {
                    setToEmptyLabel();
                });
            };

            int timeoutInSeconds = 2;
            executorService.schedule(task, timeoutInSeconds, TimeUnit.SECONDS);
            
            executorService.shutdown();
    	}
    }
    
    private void setToEmptyLabel() {
    	this.errorLabel.setText("");
    }
}
