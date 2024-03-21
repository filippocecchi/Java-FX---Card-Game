package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.converter.IntegerStringConverter;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Pair;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UserScene {
	private Scene scene;
	private Label messageLabel;
	
	private Runnable onGoBackAdminHandler;
	
	public UserScene() {
		VBox vbox = new VBox(20);

        TableView<Pair<String, Integer>> tableView = new TableView<>();
        tableView.setEditable(true);
        
        TableColumn<Pair<String, Integer>, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(cellData -> {
            Pair<String, Integer> user = cellData.getValue();
            return new SimpleStringProperty(user.getKey());
        });
        usernameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        usernameColumn.setOnEditCommit(event -> {
            Pair<String, Integer> user = event.getRowValue();
            user = new Pair<>(event.getNewValue(), user.getValue());
            tableView.getItems().set(event.getTablePosition().getRow(), user);
        });
        
        usernameColumn.setPrefWidth(700);
        usernameColumn.setStyle("-fx-background-color: green; -fx-text-fill: white;");

        TableColumn<Pair<String, Integer>, Integer> scoreColumn = new TableColumn<>("Score");
        scoreColumn.setCellValueFactory(cellData -> {
            Pair<String, Integer> user = cellData.getValue();
            return new SimpleIntegerProperty(user.getValue()).asObject();
        });
        scoreColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        scoreColumn.setOnEditCommit(event -> {
            Pair<String, Integer> user = event.getRowValue();
            user = new Pair<>(user.getKey(), event.getNewValue());
            tableView.getItems().set(event.getTablePosition().getRow(), user);
        });
        
        scoreColumn.setPrefWidth(400);
        scoreColumn.setStyle("-fx-background-color: green; -fx-text-fill: white;");

        tableView.getColumns().addAll(usernameColumn, scoreColumn);

        ObservableList<Pair<String, Integer>> userList = loadUserDataFromFile("./data/leaderboard.txt");
        tableView.setItems(userList);

        ImageView headerImage = new ImageView(new Image(getClass().getClassLoader().getResource("manage_user.png").toExternalForm()));
        headerImage.setFitWidth(600);
        headerImage.setFitHeight(80);
        headerImage.setPreserveRatio(true);
        
        ImageView home = new ImageView(new Image(getClass().getClassLoader().getResource("previous.png").toExternalForm()));
        home.setFitWidth(110);
        home.setFitHeight(50);
        home.setOnMouseClicked(event -> onGoBackAdminHandler.run());
        
        ImageView saveChanges = new ImageView(new Image(getClass().getClassLoader().getResource("save_changes.png").toExternalForm()));
        saveChanges.setFitWidth(180);
        saveChanges.setFitHeight(50);
        saveChanges.setOnMouseClicked(e -> saveUserDataToFile("./data/leaderboard.txt", tableView.getItems()));
        
        this.messageLabel = new Label();
        
        vbox.getChildren().addAll(home, headerImage, tableView, saveChanges, this.messageLabel);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: green;");

        this.scene = new Scene(vbox, 1100, 700);
	}
	
	public Scene getScene() {
		return this.scene;
	}
	
	public void setOnGoBackAdminHandler(Runnable handler) {
		onGoBackAdminHandler = handler;
	}
	
    private ObservableList<Pair<String, Integer>> loadUserDataFromFile(String fileName) {
        List<Pair<String, Integer>> userList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String username = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    userList.add(new Pair<>(username, score));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList(userList);
    }

    private void saveUserDataToFile(String fileName, ObservableList<Pair<String, Integer>> userList) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (Pair<String, Integer> user : userList) {
                bw.write(user.getKey() + "," + user.getValue());
                bw.newLine();
            }
            
            this.messageLabel.setText("Data updated correctly!");
            this.messageLabel.setTextFill(Color.WHITE);
            this.messageLabel.setFont(new Font(18));
            
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            
            this.messageLabel.setText("Error occurred during data update!");
            this.messageLabel.setTextFill(Color.RED);
            this.messageLabel.setFont(new Font(18));
        }
        
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
    
    private void setToEmptyLabel() {
    	this.messageLabel.setText("");
    }
}
