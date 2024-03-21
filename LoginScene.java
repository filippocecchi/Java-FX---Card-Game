package application;

import javafx.scene.Scene;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoginScene {
    private Scene scene;
    
    private Runnable onLoginSuccess;
    private Runnable onGameStart;
    private Runnable onTournamentLobby;
    private Runnable onInstruction;
    private Runnable onLeaderBoard;
    
    private String gameCode;
    
    private Label errorLabel;
    private Label fileNotExistLabel;
    
    private GridPane gridPane = new GridPane();
    
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    public LoginScene(String fileCode) {
        StackPane root = new StackPane();
        
        Image image = new Image(getClass().getClassLoader().getResource("logo.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        
        imageView.setFitWidth(600);
        imageView.setFitHeight(300);

        this.gridPane.setAlignment(Pos.CENTER);
        this.gridPane.setHgap(10);
        this.gridPane.setVgap(10);
        this.gridPane.setPadding(new Insets(20));
        
        this.gridPane.setMinWidth(600);
        this.gridPane.setPrefWidth(600);
        this.gridPane.setMaxWidth(600);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        
        Image loginImageFlag = new Image(getClass().getClassLoader().getResource("login.png").toExternalForm());
        ImageView loginImage = new ImageView(loginImageFlag);
        loginImage.setFitWidth(100);
        loginImage.setFitHeight(50);
        
        loginImage.setOnMouseClicked(event -> handleLoginButtonClick(usernameField.getText(), passwordField.getText()));
        
        HBox hbox = new HBox(20);
        hbox.setAlignment(Pos.CENTER);
        hbox.setPrefWidth(600);
        
        hbox.getChildren().addAll(usernameField, passwordField);
        
        errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        
        fileNotExistLabel = new Label();
        fileNotExistLabel.setTextFill(Color.RED);
        
        TextField gameCodeField = new TextField(fileCode);
        gameCodeField.setPrefWidth(600);
        
        Image btnImg = new Image(getClass().getClassLoader().getResource("play.png").toExternalForm());
        ImageView buttonImage = new ImageView(btnImg);
        buttonImage.setFitWidth(100);
        buttonImage.setFitHeight(50);
        
        buttonImage.setOnMouseClicked(event -> checkExistringMatch(gameCodeField.getText()));
        
        Image ldrBI = new Image(getClass().getClassLoader().getResource("leaderboard.png").toExternalForm());
        ImageView leaderBoardImage = new ImageView(ldrBI);
        leaderBoardImage.setFitWidth(50);
        leaderBoardImage.setFitHeight(50);
        
        leaderBoardImage.setOnMouseClicked(event -> onLeaderBoard.run());
        
        ImageView instructionImage = new ImageView(new Image(getClass().getClassLoader().getResource("info.png").toExternalForm()));
        instructionImage.setFitWidth(50);
        instructionImage.setFitHeight(50); 
        
        instructionImage.setOnMouseClicked(event -> onInstruction.run());
        
        HBox imageBox = new HBox(10);
        imageBox.setAlignment(Pos.CENTER_RIGHT);
        imageBox.setPrefWidth(600);
        
        imageBox.getChildren().addAll(leaderBoardImage, instructionImage);
        
        Label hint = new Label();
        hint.setText("ENTER GAME CODE");
        hint.setFont(new Font(15));
        hint.setTextFill(Color.WHITE);
        
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(imageBox, imageView, hint, gameCodeField, buttonImage, hbox, loginImage);
        
        this.gridPane.add(vbox, 0, 0);
        this.gridPane.add(fileNotExistLabel, 0, 1);
        this.gridPane.add(this.errorLabel, 0, 2);
        
        /*
        Text codeLabel = new Text("Game code: " + fileCode);
        
        if(fileCode != null) {
        	gridPane.add(codeLabel, 0, 4);
        }
        */

        root.getChildren().add(this.gridPane);
        root.setStyle("-fx-background-color: green;");

        Scene scene1 = new Scene(root, 1100, 700);

        this.scene = scene1;
    }

    public void setOnLoginSuccess(Runnable handler) {
        onLoginSuccess = handler;
    }
    
    public void setOnGameStart(Runnable handler) {
        onGameStart = handler;
    }
    
    public void setOnTournamentLobby(Runnable handler) {
        onTournamentLobby = handler;
    }
    
    public void setOnInstruction(Runnable handler) {
    	onInstruction = handler;
    }
    
    public void setOnLeaderBoard(Runnable handler) {
    	onLeaderBoard = handler;
    }

    public Scene getScene() {
        return scene;
    }
    
    public String getGameCode() {
    	return this.gameCode;
    }

    private void handleLoginButtonClick(String username, String password) {
        if (onLoginSuccess != null) {
        	if(username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
                onLoginSuccess.run();
        	} else {
        		System.out.println("non sei un admin");
        		
                this.errorLabel.setText("Invalid username or password!");
                this.errorLabel.setFont(new Font(18));
        	}
        }
    }
    
    private void checkExistringMatch(String fileName) {
    	File file = new File("./data/" + fileName + ".txt");
    	
    	if(file.exists()) {
    		this.gameCode = fileName;
    		
    		if(fileName.startsWith("tr-")) {
    			onTournamentLobby.run();
    		} else {
        		onGameStart.run();
    		}
    	} else {
    		System.out.println("il file" + fileName + " non esiste");
    		
            this.fileNotExistLabel.setText("Game code does not exist");
            this.fileNotExistLabel.setFont(new Font(18));
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
    	this.fileNotExistLabel.setText("");
    	this.errorLabel.setText("");
    }
}