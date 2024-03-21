package application;

import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;
import java.util.*;

import java.util.HashMap;
import java.util.Map;

public class WinnerScene {
    private Scene scene;
    private String gameCode;
    private VBox vbox = new VBox();
    
    private Runnable onBackHandle;
    private Runnable onHomeHandle;
    private Runnable onLeaderboardHandle;
    
    private String mainFilePath = "";
    
    private Map<String, Integer> playerScores = new HashMap<>();

    public WinnerScene(String gameCode) {
        this.gameCode = gameCode;
        this.updateInterface();

        StackPane root = new StackPane();
        
        ImageView imageView = new ImageView(new Image(getClass().getClassLoader().getResource("winner.png").toExternalForm()));
        imageView.setFitWidth(500);
        imageView.setFitHeight(300);
        
        this.vbox.getChildren().add(imageView);
        this.vbox.setAlignment(Pos.CENTER);
        this.vbox.setPadding(new Insets(20));

        root.getChildren().add(this.vbox);
        root.setStyle("-fx-background-color: green;");

        Scene scene1 = new Scene(root, 1100, 700);

        this.scene = scene1;
    }

    public Scene getScene() {
        return scene;
    }
    
    public void setOnBackHandle(Runnable handle) {
    	onBackHandle = handle;
    }
    
    public void setOnHomeHandle(Runnable handle) {
    	onHomeHandle = handle;
    }
    
    public String getMainFilePath() {
    	String flag = this.mainFilePath.replace("./data/", "");
    	System.out.println(flag);
    	return flag.replace(".txt", "");
    }
    
    private void updateInterface() {
        try (BufferedReader br = new BufferedReader(new FileReader("./data/" + this.gameCode + ".txt"))) {
        	String line;
        	
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("DECK:") && !line.startsWith("CURRENT:")) {
                    String[] parts = line.split(",");
                    String username = parts[0];
                    int score = parts.length == 1 ? 100 : 100 - (parts[1].length() * 10);
                    
                    this.playerScores.put(username, score);
                }
            }
            
            br.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if(this.gameCode.startsWith("tr-")) {
        	String lowestScorer = null;
            int lowestScore = Integer.MAX_VALUE;

            for (Map.Entry<String, Integer> entry : this.playerScores.entrySet()) {
                String username = entry.getKey();
                int score = entry.getValue();

                if (score < lowestScore) {
                    lowestScore = score;
                    lowestScorer = username;
                }
            }
            
            System.out.println(this.playerScores);
            
            List<String> lines = new ArrayList<>();
            String mainTournamentFilePath = "./data/";
            
            int indexOfHyphen = (this.gameCode + ".txt").indexOf('_');

            if (indexOfHyphen != -1) {
            	mainTournamentFilePath = mainTournamentFilePath + (this.gameCode + ".txt").substring(0, indexOfHyphen) + ".txt";
            	this.mainFilePath = mainTournamentFilePath;
            } else {
                System.out.println("No hyphen found in the input string.");
            }
            
            try (BufferedReader buffer = new BufferedReader(new FileReader(mainTournamentFilePath))) {
                String l;
                while ((l = buffer.readLine()) != null) {
                    lines.add(l);
                }
                
                buffer.close();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            
            String winnerUsername = "";
            Integer foundedIndex = 0;
            for(int i = 0; i < lines.size(); i++) {
            	String li = lines.get(i);
            	if(li.contains(",")) {
                	String[] usernames = li.split(",");
                	
                	if(usernames[0].equals(lowestScorer)) {
                		li = usernames[1];
                		winnerUsername = li;
                	} else if(usernames[1].equals(lowestScorer)) {
                		li = usernames[0];
                		winnerUsername = li;
                	}
                	foundedIndex = i;
                	lines.set(i, li);
            	}
            }
            
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(mainTournamentFilePath))) {
                for (String updatedLine : lines) {
                    bw.write(updatedLine);
                    System.out.println(updatedLine);
                    bw.newLine();
                }
                
                if(lines.size() == 1 && !lines.get(0).contains(",")) {
                	ImageView imageView = new ImageView(new Image(getClass().getClassLoader().getResource("home.png").toExternalForm()));
                    imageView.setFitWidth(120);
                    imageView.setFitHeight(40);
                    
                    imageView.setOnMouseClicked(event -> onHomeHandle.run());
                	
                	Label userLabel = new Label(lines.get(0) + " WON THE TOURNAMENT!");
                    userLabel.setTextFill(Color.WHITE);
                    userLabel.setAlignment(Pos.CENTER);
                    userLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
                	
                    this.vbox.getChildren().addAll(imageView, userLabel);
                    
                    this.vbox.setPrefWidth(1100);
                    this.vbox.setAlignment(Pos.CENTER);
                    
                    this.deleteTournamentFiles();
                } else {
                	ImageView imageView = new ImageView(new Image(getClass().getClassLoader().getResource("previous.png").toExternalForm()));
                    imageView.setFitWidth(120);
                    imageView.setFitHeight(40);
                    
                    imageView.setOnMouseClicked(event -> onBackHandle.run());
                	
                	Label userLabel = new Label(winnerUsername + " WON THE MATCH!");
                    userLabel.setTextFill(Color.WHITE);
                    userLabel.setAlignment(Pos.CENTER);
                    userLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
                    
                    this.vbox.getChildren().addAll(imageView, userLabel);
                }
                
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (BufferedReader br = new BufferedReader(new FileReader("./data/" + this.gameCode + ".txt"))) {
                String line;
                int rowIndex = 1;
                boolean emptyDeck = false;
                
                while ((line = br.readLine()) != null) {
                    if (!line.startsWith("DECK:") && !line.startsWith("CURRENT:")) {
                        String[] parts = line.split(",");
                        String username = parts[0];
                        
                        System.out.println(parts[0]);
                        if(parts.length > 1) {
                        	System.out.println(parts[1]);
                        }
                        
                        int score = 0;
                        
                        if(parts.length > 1) {
                        	score = 100 - (parts[1].toString().length() * 10);
                        } else {
                        	score = 100;
                        	emptyDeck = true;
                        	
                        	ImageView imageView = new ImageView(new Image(getClass().getClassLoader().getResource("home.png").toExternalForm()));
                            imageView.setFitWidth(120);
                            imageView.setFitHeight(40);
                            
                            imageView.setOnMouseClicked(event -> onHomeHandle.run());
                        	
                            Label userLabel = new Label(username + " WON THE MATCH!");
                            userLabel.setTextFill(Color.WHITE);
                            userLabel.setAlignment(Pos.CENTER);
                            userLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
                        	
                            this.vbox.getChildren().addAll(imageView, userLabel);
                            this.vbox.setAlignment(Pos.CENTER);
                            this.vbox.setPadding(new Insets(10));
                            
                            this.vbox.setPrefWidth(1100);
                        }
                        this.playerScores.put(username, score);

                        rowIndex++;
                    }
                }
                
                if(emptyDeck == false) {
                	String higherScorer = null;
                    int highestScore = Integer.MIN_VALUE;
                	
                    for (Map.Entry<String, Integer> entry : this.playerScores.entrySet()) {
                        String username = entry.getKey();
                        int score = entry.getValue();

                        if (score > highestScore) {
                        	highestScore = score;
                        	higherScorer = username;
                        }
                    }
                	
                	ImageView imageView = new ImageView(new Image(getClass().getClassLoader().getResource("home.png").toExternalForm()));
                    imageView.setFitWidth(120);
                    imageView.setFitHeight(40);
                    
                    imageView.setOnMouseClicked(event -> onHomeHandle.run());
                	
                    Label userLabel = new Label(higherScorer + " WON THE MATCH!");
                    userLabel.setTextFill(Color.WHITE);
                    userLabel.setAlignment(Pos.CENTER);
                    userLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
                	
                    this.vbox.getChildren().addAll(imageView, userLabel);
                    this.vbox.setPadding(new Insets(10));
                    
                    this.vbox.setAlignment(Pos.CENTER);
                    this.vbox.setPrefWidth(1100);
                }
                
                this.deleteSingleFile("./data/" + this.gameCode + ".txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        this.updateLeaderBoardData(playerScores);
    }
    
    public void updateLeaderBoardData(Map<String, Integer> player) {
        Map<String, Integer> dataMap = new HashMap<>();

        try (Scanner scanner = new Scanner(new File("./data/leaderboard.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    int value = Integer.parseInt(parts[1].trim());
                    dataMap.put(name, value);
                }
            }
            
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        Map<String, Integer> mergedMap = new HashMap<>(player);

        for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
            String username = entry.getKey();
            Integer score = entry.getValue();

            if (mergedMap.containsKey(username)) {
                int existingScore = mergedMap.get(username);
                mergedMap.put(username, existingScore + score);
            } else {
                mergedMap.put(username, score);
            }
        }
        
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(mergedMap.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./data/leaderboard.txt"))) {
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                String line = entry.getKey() + "," + entry.getValue();
                writer.write(line);
                writer.newLine();
            }
            
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteTournamentFiles() {
    	File directory = new File("./data/");
    	String fileToDelete = "";	
        int indexOfHyphen = (this.gameCode + ".txt").indexOf('_');

        if (indexOfHyphen != -1) {
        	fileToDelete = fileToDelete + (this.gameCode + ".txt").substring(0, indexOfHyphen);
        	
        	System.out.println("FILE TO DELETE: " + fileToDelete);
        	
        	if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles();

                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && file.getName().startsWith(fileToDelete)) {
                            if (file.delete()) {
                                System.out.println("Deleted file: " + file.getName());
                            } else {
                                System.err.println("Failed to delete file: " + file.getName());
                            }
                        }
                    }
                }
            } else {
                System.err.println("Directory does not exist or is not a directory: ./data/");
            }
        } else {
            System.out.println("No _ found in the input string.");
        }
    }
    
    public void deleteSingleFile(String filePath) {
        File fileToDelete = new File(filePath);

        if (fileToDelete.exists() && fileToDelete.isFile()) {
            if (fileToDelete.delete()) {
                System.out.println("Deleted file: " + filePath);
            } else {
                System.err.println("Failed to delete file: " + filePath);
            }
        } else {
            System.err.println("File does not exist or is not a file: " + filePath);
        }
    }
}