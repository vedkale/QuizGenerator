/**
 * Filename: Quiz.java
 * Project: Final Project 
 * Authors: Ved Kale, Miriam Lebowitz, Niharika
 * Tomar, and Elizaveta Stepanova
 * Final Project GUI
 */
package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import javafx.application.Application;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * Runs the Quiz page GUI
 *
 */
public class Quiz extends Application {

	private int numQuestions;
	private boolean nextButtonClicked; // pauses program until "Next Question" is pressed.
	private int i;
	// private ArrayList<Question> askedQuestions;
	List<Question> questions;
	List<Answer> answers;
	String[] chosenAnswers;
	LinkedHashMap<Question, Answer> askedQuestions;
	ToggleGroup answersGroup;
	Answer currentAnswer;

	/**
	 * Runs the quiz page
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		i = 1; // counter
		// layout setup
		BorderPane root = new BorderPane();
		HBox hboxTopMenu = new HBox();
		HBox hboxBottomMenu = new HBox();
		hboxTopMenu.setSpacing(10);
		hboxBottomMenu.setSpacing(10);

		// Buttons needed for the page
		Button homeButton = new Button("Home");
		Button nextQuestionButton = new Button("Next Question");
		Button submitButton = new Button("Submit");

		// layout setup
		hboxTopMenu.getChildren().add(homeButton);
		hboxBottomMenu.getChildren().add(nextQuestionButton);
		hboxBottomMenu.getChildren().add(submitButton);

		// alert setup
		Alert alert = new Alert(AlertType.NONE);

		// data initialization
		questions = new ArrayList<>();
		answers = new ArrayList<>();
		chosenAnswers = new String[numQuestions];
		// loading questions and answers to quiz page
		for (int a = 0; a < Main.topicsToQuestion.size(); a++) {
			questions.addAll(Main.topics.get(Main.topicsToQuestion.get(a)).getQuestions().keySet());
			for (int b = 0; b < questions.size(); b++) {
				if (Main.topics.get(Main.topicsToQuestion.get(a)).getQuestions().get(questions.get(b)) != null)
					answers.add(Main.topics.get(Main.topicsToQuestion.get(a)).getQuestions().get(questions.get(b)));
			}
		}
		askedQuestions = new LinkedHashMap<Question, Answer>();
		// Add questions to a Vertical Box
		nextButtonClicked = true;
		while (i <= numQuestions && nextButtonClicked) {
			nextButtonClicked = false;
			VBox questionsAndAnswers = new VBox();

			// generating questions randomly
			Random random = new Random();
			int randomNumber = random.nextInt(questions.size());
			Question randomKey = questions.get(randomNumber);
			askedQuestions.put(randomKey, answers.get(randomNumber));
			currentAnswer = answers.get(randomNumber);

			// question formatting
			Label question = new Label("Question " + i + ": " + randomKey.getQuestion());
			question.setWrapText(true);
			question.setTextAlignment(TextAlignment.JUSTIFY);
			question.setMaxWidth(700);
			question.setTextFill(Color.WHITE);

			// image setup for question
			if (!(randomKey.image.equals("none"))) {
				ImageView image = new ImageView(new Image(randomKey.image));
				questionsAndAnswers.setPadding(new Insets(10, 10, 10, 10));
				image.setFitWidth(200);
				image.setPreserveRatio(true);
				questionsAndAnswers.getChildren().add(image);
			}

			// setting up answers for the quiz
			answersGroup = new ToggleGroup();
			questionsAndAnswers.getChildren().add(question);
			// Adds answer options
			for (int j = 0; j < answers.get(randomNumber).getAnswers().size(); j++) {
				RadioButton r = new RadioButton(answers.get(randomNumber).getAnswers().get(j));
				r.setToggleGroup(answersGroup);
				r.setTextFill(Color.WHITE);
				questionsAndAnswers.getChildren().add(r);
				root.setCenter(questionsAndAnswers);
			}
			// layout setup
			BorderPane bottom = new BorderPane();
			// displays questions in the quiz
			Label numQ = new Label("Number of Questions in Quiz: " + numQuestions);
			numQ.setTextFill(Color.WHITE);
			bottom.setBottom(numQ);
			root.setRight(bottom);

			// removes next button if only 1 question in quiz
			if (numQuestions == 1) {
				hboxBottomMenu.getChildren().remove(nextQuestionButton);
			}
			i++; // counter incremented

			// event handler for getting next question
			nextQuestionButton.setOnAction(new EventHandler<ActionEvent>() {
				/**
				 * This method loads a next questions when next button was clicked.
				 */
				@Override
				public void handle(ActionEvent arg0) {
					RadioButton selectedRadioButton = (RadioButton) answersGroup.getSelectedToggle();
					if (selectedRadioButton == null) {
						// adding alerts
						alert.setAlertType(AlertType.ERROR);
						alert.setContentText("Choose an answer");
						alert.show();
						// getting selected answer
						selectedRadioButton = (RadioButton) answersGroup.getSelectedToggle();
					}

					// if no answer selected
					if (selectedRadioButton != null) {
						chosenAnswers[i - 2] = "";
					}
					// getting selected answer by user
					chosenAnswers[i - 2] = selectedRadioButton.getText();

					// alert for correct or incorrect answers
					Alert correctness = new Alert(AlertType.INFORMATION);
					String message = "INCORRECT";
					if (currentAnswer.checkAnswer(selectedRadioButton.getText())) {
						message = "CORRECT!";
					}
					correctness.setContentText(message);
					correctness.setHeaderText("Result");
					correctness.showAndWait();

					nextButtonClicked = false;

					// if user inputs question number less than number of questions
					// in the database
					if (i <= numQuestions) {
						questionsAndAnswers.getChildren().clear();
						// random question generation
						Random random = new Random();
						int randomNumber = random.nextInt(questions.size());
						Question randomKey = questions.get(randomNumber);
						while (askedQuestions.keySet().contains(randomKey)) {
							randomNumber = random.nextInt(questions.size());
							randomKey = questions.get(randomNumber);
						}
						askedQuestions.put(randomKey, answers.get(randomNumber));
						currentAnswer = answers.get(randomNumber);

						// adding labels
						Label question = new Label("Question " + i + ": " + randomKey.getQuestion());
						answersGroup = new ToggleGroup();
						question.setTextFill(Color.WHITE);
						questionsAndAnswers.getChildren().add(question);
						// Adds answer options
						for (int j = 0; j < answers.get(randomNumber).getAnswers().size(); j++) {
							RadioButton r = new RadioButton(answers.get(randomNumber).getAnswers().get(j));
							r.setToggleGroup(answersGroup);
							r.setTextFill(Color.WHITE);
							questionsAndAnswers.getChildren().add(r);
							root.setCenter(questionsAndAnswers);
						}
						if (i == numQuestions) {
							hboxBottomMenu.getChildren().remove(nextQuestionButton);
						}
						i++; // incrementing counter
					}
				}
			});
		}

		// event handler to take user back to home page
		homeButton.setOnAction(new EventHandler<ActionEvent>() {
			/**
			 * This method creates a new scene with a pop up to go back to main page.
			 */
			public void handle(ActionEvent event) {
				Main main = new Main();
				Stage newStage = new Stage();
				try {
					main.start(newStage);
					primaryStage.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// event handler that submits the quiz to get results
		submitButton.setOnAction(new EventHandler<ActionEvent>() {
			/**
			 * This method creates a new screen with quiz results.
			 */
			public void handle(ActionEvent event) {
				RadioButton selectedRadioButton = (RadioButton) answersGroup.getSelectedToggle();
				if (selectedRadioButton == null) {
					alert.setAlertType(AlertType.ERROR);
					alert.setContentText("Choose an answer");
					alert.showAndWait();
					selectedRadioButton = (RadioButton) answersGroup.getSelectedToggle();
				}

				if (selectedRadioButton != null) {
					chosenAnswers[i - 2] = "";
				}
				chosenAnswers[i - 2] = selectedRadioButton.getText();

				Alert correctness = new Alert(AlertType.INFORMATION);
				String message = "INCORRECT";
				if (currentAnswer.checkAnswer(selectedRadioButton.getText())) {
					message = "CORRECT!";
				}
				correctness.setContentText(message);
				correctness.setHeaderText("Result");
				correctness.showAndWait();

				QuizResults quizResults = new QuizResults(chosenAnswers, askedQuestions);
				Stage newStage = new Stage();
				try {
					quizResults.start(newStage);
					primaryStage.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// layout setup
		root.setTop(hboxTopMenu);
		root.setBottom(hboxBottomMenu);
		Scene scene = new Scene(root, 1200, 600);
		// adding CSS
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		// Set the title
		primaryStage.setTitle("Quiz Generator");
		primaryStage.show();
	}

	/**
	 * Sets numQuestions field
	 * 
	 * @param numQs
	 */
	public void setNumQuestions(int numQs) {
		numQuestions = numQs;
	}
}
