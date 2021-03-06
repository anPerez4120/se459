package game;

import game.GUI.choice.Choice;
import game.GUI.gameWinner.GameWinner;
import game.GUI.welcome.ChooseTeam;
import game.GUI.welcome.Welcome;
import game.GUI.Board;
import game.GUI.SprintEnd.SprintEnd;
import game.GUI.boardOne.PlayerBoard;
import game.GUI.boardTwo.PlayerBoard2;

import java.util.ArrayList;

import game.backend.Card;
import game.backend.PointsKeeperSingleton;
import game.backend.Question;
import game.backend.Token;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class Main extends Application{
	
	Welcome welcome;
	Board board;
	ChooseTeam chooseTeam;
	Choice choice;
	PlayerBoard board1;
	PlayerBoard2 board2;	
	SprintEnd sprintEnd;
	GameWinner gameWinner;
	Stage window;
	PointsKeeperSingleton teams = PointsKeeperSingleton.getUniqueInstance();
	int sceneCounter = 0;
	boolean teamsAreFull;
	static ArrayList<Card> handDealt = new ArrayList<Card>();
	Point2D clickPoint;
	boolean firstTime = true;
	int sprintNumber = 1;
	
	public static void main(String args[]) {
		launch(args);
	}

	@Override
	public void start(Stage window) throws Exception {
		this.window = window;
		window.setTitle("Agile Card Game");
		
		/* Adds both teams */		
		teams.addTeam("team1");
		teams.addTeam("team2");
		
		welcome = new Welcome();
		board = new Board();
		chooseTeam = new ChooseTeam();
		choice = new Choice();
		board1 = new PlayerBoard();
		board2 = new PlayerBoard2();   
		gameWinner = new GameWinner();
		
		
		//Adds the event handlers so that i can change scenes on button clicks and when i released all the cards 
		window.addEventHandler(ActionEvent.ACTION, actionHandler);
		window.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseHandler);
				
		window.setScene(welcome.getScene());
		window.show();
	}
	
	EventHandler<ActionEvent> actionHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent event) {
			
			if(teams.checkIfTeam1Turn()) {
				System.out.println("team1's turn");
			} else System.out.println("team2's turn");			
			System.out.println("Sprint#: " + sprintNumber);
			
			//If we have passed sprint 4 for both teams then we can announce the winner
			if (sprintNumber > 4) {
				try {
					gameWinner.start(window);
				} catch (Exception e) {					
					e.printStackTrace();
				}
			}
									
			else if (welcome.getValidInput() && sceneCounter == 0 && teams.checkBothTeamsEmpty()) {
				try {
					chooseTeam.start(window);
					sceneCounter++;					
				} catch (Exception e) {					
					e.printStackTrace();
				}
			}
			
			//Checks to see that the teams are full before going on to the next scene
			else if (sceneCounter == 1 && chooseTeam.checkIfTeamsAreFull()) {

				try {
					choice.start(window);
					sceneCounter++;					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
			}
			
			//Set the topic and change to the next board
			else if (choice.getTopic() != null && sceneCounter == 2) {
				try {
					int numPlayerPerTeam = welcome.getNumPlayersPerTeam();
					String topic = choice.getTopic();
					board1.setNumOfPlayers(numPlayerPerTeam);
					board1.setTopic(topic);
					board1.start(window);					
					sceneCounter++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			else if(sceneCounter == 4 && board2.checkIfEndOfSprint()) {
				//TODO:
				sceneCounter = 1;			
				
				//Make a blank slate for these scenes
				choice = new Choice();
				board1 = new PlayerBoard();
				board2 = new PlayerBoard2();  
				
				//checks to see if it is Team2's turn, if so then we can we can change the sprintNumber
				if (!teams.checkIfTeam1Turn()) {
					//Will use this to keep track of what sprint we are on
					sprintNumber++;
				}
				//switch to the next teams turn
				teams.changeTurns();
								
			}
						
		}
		
	};
	
	EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent mouseEvent) {		
			String eventName = mouseEvent.getEventType().getName();
			
			switch(eventName) {
				case("MOUSE_RELEASED"):
					//If there are no cards left to be dealt to the players, and this isn't their first time then continue 
					if (board1.getHandDealt().isEmpty() && sceneCounter == 3) {
						try {
							board2.setListofPlayers(board1.getPlayers());
							board2.start(window);
							sceneCounter++;
							System.out.println("Board2: " + sceneCounter);
						} catch (Exception e) {					
							e.printStackTrace();
						}
					}
					break;
				default:
					break;
			}
		}
		
	};

}
