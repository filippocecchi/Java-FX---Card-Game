package application;
	
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        createLoginView(null);

        primaryStage.setTitle("SPACCA");
        primaryStage.show();
    }

    private void createLoginView(String fileCode) {
        LoginScene login = new LoginScene(fileCode);
        login.setOnLoginSuccess(() -> switchToHomeView());
        login.setOnGameStart(() -> switchToGameView(login.getGameCode()));
        login.setOnTournamentLobby(() -> switchToTournamentLobbyView(login.getGameCode()));
        login.setOnInstruction(() -> switchToInstructionView());
        login.setOnLeaderBoard(() -> switchToLeaderBoardView());
        
        primaryStage.setTitle("LOGIN - SPACCA");
        primaryStage.setScene(login.getScene());
    }

    private void createHomeView() {
    	AdminScene adminScene = new AdminScene();
    	adminScene.setOnLogoutHandle(() -> switchToLoginView(null));
    	adminScene.setOnCreateGameHandle(() -> switchToGameSetupView(adminScene.getIsTournament()));
    	adminScene.setOnUserSceneHandle(() -> switchToUserSceneView());
    	
    	primaryStage.setTitle("ADMIN - SPACCA");
    	primaryStage.setScene(adminScene.getScene());
    	primaryStage.setResizable(false);
    }
    
    private void createGameView(String gameCode) {
    	GameScene game = new GameScene(gameCode);
    	game.setOnLogoutHandle(() -> switchToLoginView(null));
    	game.setOnGameWinHandle(() -> switchToWinView(gameCode));
    	
    	primaryStage.setTitle("GAME - SPACCA");
    	primaryStage.setScene(game.getScene());
    	primaryStage.setResizable(false);
    }
    
    private void createWinView(String gameCode) {
    	WinnerScene winner = new WinnerScene(gameCode);
    	winner.setOnBackHandle(() -> switchToTournamentLobbyView(winner.getMainFilePath()));
    	winner.setOnHomeHandle(() -> switchToLoginView(null));
    	
    	primaryStage.setTitle("GAME WINNER - SPACCA");
    	primaryStage.setScene(winner.getScene());
    	primaryStage.setResizable(false);
    }
    
    private void createGameSetupView(boolean isTournament) {
    	GameSetupScene gameSetup = new GameSetupScene(isTournament);
    	gameSetup.setOnLogoutHandle(() -> switchToLoginView(gameSetup.getFileName()));
    	
    	String title = "";
    	if(isTournament == false) { title = "SIMPLE MATCH SETUP - SPACCA"; }
    	else { title = "TOURNAMENT SETUP - SPACCA"; }
    	
    	primaryStage.setTitle(title);
    	primaryStage.setScene(gameSetup.getScene());
    	primaryStage.setResizable(false);
    }
    
    private void createTournamentLobbyView(String gameCode) {
    	TournamentLobby trLobby = new TournamentLobby(gameCode);
    	trLobby.setOnLogoutHandle(() -> switchToLoginView(null));
    	trLobby.setOnStartGameHandle(() -> switchToGameView(trLobby.getFullChildPath()));
    	
    	primaryStage.setTitle("TOURNAMENT " + gameCode + " - SPACCA");
    	primaryStage.setScene(trLobby.getScene());
    	primaryStage.setResizable(false);
    }
    
    private void createLeaderboardView() {
    	LeaderboardScene lead = new LeaderboardScene();
    	lead.setOnLogoutHandle(() -> switchToLoginView(null));
    	
    	primaryStage.setTitle("LEADERBOARD - SPACCA");
    	primaryStage.setScene(lead.getScene());
    	primaryStage.setResizable(false);
    }
    
    private void createInstructionView() {
    	InstructionScene instruction = new InstructionScene();
    	instruction.setOnGoBackHomeHandler(() -> switchToLoginView(null));
    	
    	primaryStage.setTitle("INSTRUCTIONS - SPACCA");
    	primaryStage.setScene(instruction.getScene());
    	primaryStage.setResizable(false);
    }
    
    private void createUserSceneView() {
    	UserScene user = new UserScene();
    	user.setOnGoBackAdminHandler(() -> switchToHomeView());
    	
    	primaryStage.setTitle("MANAGE USER - SPACCA");
    	primaryStage.setScene(user.getScene());
    	primaryStage.setResizable(false);
    }

    private void switchToHomeView() {
        createHomeView();
    }

    private void switchToLoginView(String fileCode) {
        createLoginView(fileCode);
    }
    
    private void switchToGameView(String gameCode) {
    	createGameView(gameCode);
    }
    
    private void switchToWinView(String gameCode) {
    	createWinView(gameCode);
    }
    
    private void switchToGameSetupView(boolean isTournament) {
    	createGameSetupView(isTournament);
    }
    
    private void switchToTournamentLobbyView(String gameCode) {
    	createTournamentLobbyView(gameCode);
    }
    
    private void switchToLeaderBoardView() {
    	createLeaderboardView();
    }
    
    private void switchToInstructionView() {
    	createInstructionView();
    }
    
    private void switchToUserSceneView() {
    	createUserSceneView();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
