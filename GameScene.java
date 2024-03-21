package application;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameScene {
    private Scene scene;
    
    private Runnable onLogoutHandle;
    private Runnable onGameWin;
    
    private String gameCode;

    private ArrayList<String> playerArray = new ArrayList<>();
    private ArrayList<Integer> playerCardArray = new ArrayList<>();
    private String gameCard;
    
    private String currentPlayer = "Loading";
    private Integer currentPlayerIndex = 0;
    
    private ImageView gameImage;
    private HBox bottomImages;
    private BorderPane rootPane;
    private Label gameInfoLabel;
    private VBox topPane;
    private ImageView logoutImage;
    
    private boolean isGameWon = false;

    public GameScene(String gameCode) {
        this.gameCode = gameCode;
        this.rootPane = new BorderPane();
        this.isGameWon = false;
        
        this.gameCard = null;
        
        this.setUpGame();
        
        this.logoutImage = new ImageView(new Image(getClass().getClassLoader().getResource("logout.png").toExternalForm()));
        this.logoutImage.setFitWidth(90);
        this.logoutImage.setFitHeight(50);
        
        this.logoutImage.setOnMouseClicked(event -> onLogoutHandle.run());
        
        this.gameInfoLabel = new Label("Game Code: " + this.gameCode + " | Current Player: " + this.currentPlayer);
        this.gameInfoLabel.setTextFill(Color.WHITE);
        this.gameInfoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        BorderPane.setAlignment(this.gameInfoLabel, Pos.CENTER);
        BorderPane.setMargin(this.gameInfoLabel, new Insets(20, 0, 0, 0));

        this.topPane = new VBox(20);
        this.topPane.getChildren().addAll(this.logoutImage, this.gameInfoLabel);
        BorderPane.setAlignment(this.topPane, Pos.CENTER);
        this.topPane.setAlignment(Pos.CENTER);
        this.rootPane.setTop(this.topPane);

		this.gameImage = new ImageView(new Image(getClass().getClassLoader().getResource(this.gameCard + ".png").toExternalForm()));
		
        this.gameImage.setFitWidth(120);
        this.gameImage.setFitHeight(200);
        this.rootPane.setCenter(this.gameImage);
        
        this.bottomImages = new HBox(10);
        this.bottomImages.setAlignment(Pos.CENTER);
        
        for (Integer playerCard : playerCardArray) {
        	ImageView imageView = new ImageView();
        	if(this.currentPlayer.contains("BOT")) {
        		imageView = new ImageView(new Image(getClass().getClassLoader().getResource("back.png").toExternalForm()));
        	} else {
        		imageView = new ImageView(new Image(getClass().getClassLoader().getResource(playerCard + ".png").toExternalForm()));	
        	}
            imageView.setFitWidth(120);
            imageView.setFitHeight(200);
            
            imageView.setOnMouseClicked(event -> this.handleEvent(playerCard));
            
            this.bottomImages.getChildren().add(imageView);
        }

        BorderPane.setMargin(gameInfoLabel, new Insets(0, 0, 0, 20));
        this.rootPane.setBottom(this.bottomImages);
        this.rootPane.setStyle("-fx-background-color: green;");

        Scene scene1 = new Scene(this.rootPane, 1100, 700);

        this.scene = scene1;
    }

    public Scene getScene() {
        return scene;
    }
    
    public void setOnLogoutHandle(Runnable handler) {
        onLogoutHandle = handler;
    }
    
    public void setOnGameWinHandle(Runnable handler) {
    	onGameWin = handler;
    }

	public String getGameCode() {
		return gameCode;
	}

	public void setGameCode(String gameCode) {
		this.gameCode = gameCode;
	}
	
	private void getPlayerCard() {
		if(this.isGameWon == false) {
			String fileName = "./data/" + this.gameCode + ".txt";
			
			this.playerCardArray.clear();
			
			try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
				String line;
	            while ((line = br.readLine()) != null) {
	                String[] parts = line.split(",");
	                for(int i = 0; i < parts.length; i++) { System.out.println("DEBUG: " + parts[i]); }
	                if(parts[0].toString().equals(this.currentPlayer)) {
	                	if(parts.length > 1) {
	                    	String numberPart = parts[1].trim();
	                    	System.out.println(numberPart);
	                        for (char digitChar : numberPart.toCharArray()) {
	                            try {
	                                int digit = Integer.parseInt(String.valueOf(digitChar));

	                                this.playerCardArray.add(digit);
	                            } catch (NumberFormatException e) {
	                            	System.out.println(e);
	                            }
	                        }	
	                	}
	                }
	            }
	            
	            br.close();
	            System.out.println("PLAYER: " + this.currentPlayer + " - CARD: " + this.playerCardArray);
	            
	            this.bottomImages = new HBox(10);
	            this.bottomImages.setAlignment(Pos.CENTER);
	            this.bottomImages.getChildren().clear();
	            
	            if(this.currentPlayer.contains("BOT")) {
	                for (Integer playerCard : playerCardArray) {
	                	System.out.println("CARD:" + playerCard);
	                    ImageView imageView = new ImageView(new Image(getClass().getClassLoader().getResource("back.png").toExternalForm()));
	                    
	                    if(this.playerCardArray.size() > 8) {
		                    imageView.setFitWidth(120 / this.playerCardArray.size() + 80);
		                    imageView.setFitHeight(200 / this.playerCardArray.size() + 90);
	                    } else {
		                    imageView.setFitWidth(120);
		                    imageView.setFitHeight(200);
	                    }
	                    
	                    imageView.setOnMouseClicked(event -> this.handleEvent(playerCard));
	                    
	                    this.bottomImages.getChildren().add(imageView);
	                }
	            } else {
	                for (Integer playerCard : playerCardArray) {
	                	System.out.println("CARD:" + playerCard);
	                    ImageView imageView = new ImageView(new Image(getClass().getClassLoader().getResource(playerCard + ".png").toExternalForm()));

	                    if(this.playerCardArray.size() > 8) {
		                    imageView.setFitWidth(120 / this.playerCardArray.size() + 80);
		                    imageView.setFitHeight(200 / this.playerCardArray.size() + 90);
	                    } else {
		                    imageView.setFitWidth(120);
		                    imageView.setFitHeight(200);
	                    }
	                    
	                    imageView.setOnMouseClicked(event -> this.handleEvent(playerCard));
	                    
	                    System.out.println(playerCard);
	                    
	                    this.bottomImages.getChildren().add(imageView);
	                }
	            }
	            
	            this.rootPane.setBottom(this.bottomImages);
	            
	            if(this.currentPlayer.contains("BOT")) {
	                ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
	                
	                Runnable task = () -> {
	                    Platform.runLater(() -> {
	                        botLogic();
	                    });
	                };

	                int timeoutInSeconds = 2;
	                executorService.schedule(task, timeoutInSeconds, TimeUnit.SECONDS);
	                
	                executorService.shutdown();
	            	
	            }
			} catch(IOException e) {
				e.printStackTrace();
			}			
		}
	}
	
	private void handleEvent(Integer card) {
		Integer valid = 0;
		
		if(!this.gameCard.startsWith("back")) {
			valid = Integer.parseInt(this.gameCard) + 1;
		}
		if(this.gameCard.startsWith("7")) { valid = 1; }
		
		if(card.toString().startsWith("9")) { valid = card; }
		if(card.toString().startsWith("8")) { valid = card; }
		
		if(this.gameCard.startsWith("9")) { valid = card; }
		if(this.gameCard.startsWith("8")) { valid = card; }
		
		if(this.gameCard.equals("back") || card == valid) {
			this.gameCard = card.toString();

			this.gameImage.setImage(new Image(getClass().getClassLoader().getResource(this.gameCard + ".png").toExternalForm()));
			this.gameImage.setFitWidth(120);
			this.gameImage.setFitHeight(200);
            
			this.bottomImages.getChildren().clear();
			
			this.playerCardArray.removeIf(item -> item == card);
			
	        for (Integer playerCard : playerCardArray) {
	            ImageView newImageView = new ImageView(new Image(getClass().getClassLoader().getResource(playerCard + ".png").toExternalForm()));
	            newImageView.setFitWidth(120);
	            newImageView.setFitHeight(200);
	            
	            newImageView.setOnMouseClicked(event -> this.handleEvent(playerCard));
	            
	            this.bottomImages.getChildren().add(newImageView);
	        }
	        
			if(this.playerCardArray.size() == 0) {
				this.removeCardFromFile(card);
				onGameWin.run();
				this.isGameWon = true;
			} else {
				this.removeCardFromFile(card);
			}
		} else {
        	this.drawFromDeck();
		}
		
		this.updateCurrentCard();
		this.moveToNextPlayer();
	}
	
	private void botLogic() {
		if(this.isGameWon == false) {
			boolean found = false;
			
			if(this.gameCard.startsWith("back") || this.gameCard.startsWith("8") || this.gameCard.startsWith("9")) {
				Integer flag = this.playerCardArray.get(0);
				
				this.gameCard = flag.toString();
				
				System.out.println("NUOVA CARTA ESTRATTA: " + this.gameCard);
				
				this.gameImage = new ImageView();
				this.gameImage.setImage(new Image(getClass().getClassLoader().getResource(this.gameCard + ".png").toExternalForm()));
				
				this.gameImage.setFitWidth(120);
				this.gameImage.setFitHeight(200);
				
				this.rootPane.setCenter(this.gameImage);
				
				this.playerCardArray.removeIf(card -> card == Integer.parseInt(this.gameCard));
				this.removeCardFromFile(Integer.parseInt(this.gameCard));
			} else {
		        for (Integer playerCard : this.playerCardArray) {
		        	Integer valid = Integer.parseInt(this.gameCard) + 1;

		        	if(valid == playerCard && found == false) {
		        		found = true;
		        		
		    			this.gameCard = playerCard.toString();
		    			this.gameImage.setImage(new Image(getClass().getClassLoader().getResource(this.gameCard + ".png").toExternalForm()));
		    			
		    			this.gameImage.setFitWidth(120);
		    			this.gameImage.setFitHeight(200);
		    			
		    			this.rootPane.setCenter(this.gameImage);
		        	}
		        }
		        
		        if(found == true) {
		        	this.playerCardArray.removeIf(card -> card == Integer.parseInt(this.gameCard));
		        	this.removeCardFromFile(Integer.parseInt(this.gameCard));
		        } else {
		        	this.drawFromDeck();
		        }
			}
			
			this.updateCurrentCard();
			if(this.playerCardArray.size() == 0) { onGameWin.run(); }
			else {
				this.moveToNextPlayer();
			}			
		}
	}
	
	private void moveToNextPlayer() {
		if(this.gameCard.contains("9")) {
	        if(this.currentPlayerIndex < this.playerArray.size() - 2) {
	        	this.currentPlayerIndex = this.currentPlayerIndex + 2;
	        } else if(this.currentPlayerIndex == this.playerArray.size() - 2) {
	        	this.currentPlayerIndex = 0;
	        } else {
	        	this.currentPlayerIndex = 1;
	        }
	        
	        this.currentPlayer = this.playerArray.get(this.currentPlayerIndex);
		} else {
	        if(this.currentPlayerIndex < this.playerArray.size() - 1) {
	        	this.currentPlayerIndex = this.currentPlayerIndex + 1;
	        } else {
	        	this.currentPlayerIndex = 0;
	        }
	        
	        this.currentPlayer = this.playerArray.get(this.currentPlayerIndex);
		}
		
        this.gameInfoLabel = new Label();
        this.gameInfoLabel = new Label("Game Code: " + this.gameCode + " | Current Player: " + this.currentPlayer);
        this.gameInfoLabel.setTextFill(Color.WHITE);
        this.gameInfoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        BorderPane.setAlignment(this.gameInfoLabel, Pos.CENTER);
        BorderPane.setMargin(this.gameInfoLabel, new Insets(20, 0, 0, 0));
        
        BorderPane.setMargin(this.gameInfoLabel, new Insets(0, 0, 0, 20));
        this.rootPane.setBottom(this.bottomImages);
        
        this.topPane = new VBox(20);
        this.topPane.getChildren().addAll(this.logoutImage, this.gameInfoLabel);
        BorderPane.setAlignment(this.topPane, Pos.CENTER);
        this.topPane.setAlignment(Pos.CENTER);
        this.rootPane.setTop(this.topPane);
		
		this.getPlayerCard();
	}
	
	private void drawFromDeck() {
		String fileName = "./data/" + this.gameCode + ".txt";
		ArrayList<String> updatedLines = new ArrayList<>();
    	Integer deckNumber = 0;
    	
    	try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("DECK:")) {
                	if(line.length() > 5) {
                        String deckRow = line.substring(5);
                        deckNumber = Integer.parseInt(deckRow.substring(0, 1));
                        
                        String newDeck = deckRow.substring(1, deckRow.length());
                        updatedLines.add("DECK:" + newDeck);
                        
                        this.playerCardArray.add(deckNumber);
                        
                        ImageView newImageView = new ImageView();
                        if(this.currentPlayer.contains("BOT")) {
                            newImageView = new ImageView(new Image(getClass().getClassLoader().getResource("back.png").toExternalForm()));
                        } else {
                            newImageView = new ImageView(new Image(getClass().getClassLoader().getResource(deckNumber + ".png").toExternalForm()));
                        }
        	            newImageView.setFitWidth(120);
        	            newImageView.setFitHeight(200);	
                        
                        this.bottomImages.getChildren().add(newImageView);
                	} else {
                		onGameWin.run();
                		this.isGameWon = true;
                	}
                } else {
                	updatedLines.add(line);
                }
            }
            
            reader.close();
            
            for (int i = 0; i < updatedLines.size(); i++) {
            	String l = updatedLines.get(i);
            	if(l.startsWith(this.currentPlayer + ",")) {
            		l = l + deckNumber.toString();
            		updatedLines.set(i, l);
            	}
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    	
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private void setUpGame() {
    	String fileName = "./data/" + this.gameCode + ".txt";
    	
		try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.trim().startsWith("DECK:")) {
                    int commaIndex = line.indexOf(',');
                    if (commaIndex != -1) {
                        String extractedData = line.substring(0, commaIndex).trim();
                        this.playerArray.add(extractedData);
                        
                        System.out.println("extracted: " + extractedData);
                    }
                }
                if(line.trim().startsWith("CURRENT:")) {
                	int doubleDotIndex = line.indexOf(':');
                	String flag = line.toString().substring(doubleDotIndex+1, line.length()).trim();
                	
                	this.gameCard = flag.toString().toLowerCase();
                }
            }
            
            this.currentPlayer = this.playerArray.get(0);
            this.currentPlayerIndex = 0;
            

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        this.getPlayerCard();
	}
	
	private void updateCurrentCard() {
		String fileName = "./data/" + this.gameCode + ".txt";
	    ArrayList<String> lines = new ArrayList<>();
	    
	    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            if (line.startsWith("CURRENT:")) {
	            	line = "CURRENT:" + this.gameCard.toString();
	            	
	            	lines.add(line);
	            } else {
	                lines.add(line);
	            }
	        }
	        
	        reader.close();
	    } catch (IOException e) {
	        System.out.println("File not found or deleted");
	    }
	    
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
	        for (String line : lines) {
	            writer.write(line);
	            writer.newLine();
	        }
	        
	        writer.close(); 
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	private void removeCardFromFile(Integer cardToRemove) {
	    String fileName = "./data/" + this.gameCode + ".txt";
	    ArrayList<String> lines = new ArrayList<>();
	    
	    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            if (line.startsWith(this.currentPlayer + ",")) {

	                String[] parts = line.split(",");
	                StringBuilder updatedLine = new StringBuilder(parts[0] + ",");

                    for(int y = 0; y < parts[1].length(); y++) {
                    	if(!(parts[1].charAt(y) == cardToRemove.toString().charAt(0))) {
                    		updatedLine.append(parts[1].charAt(y));
                    	}
                    }
	                lines.add(updatedLine.toString());
	            } else {
	                lines.add(line);
	            }
	        }
	        
	        reader.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
	        for (String line : lines) {
	            writer.write(line);
	            writer.newLine();
	        }
	        
	        writer.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}