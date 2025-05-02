package mobile.screen.tool;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MobileTool extends Application {
    private Label digitalClockLabel;
    private TextField calcDisplay;
    private String expression = "";
    private Calendar calendar = new GregorianCalendar();
    private GridPane calendarPane;
    private Label monthLabel;
    private TextArea notepadArea;
    private String savedNotes = "";
    private static final Color DARKBROWN = Color.rgb(139, 69, 19);
    private static final double CLOCK_WIDTH = 200;
    private static final double CLOCK_HEIGHT = 200;
    private static final double CLOCK_CENTER_X = CLOCK_WIDTH / 2;
    private static final double CLOCK_CENTER_Y = CLOCK_HEIGHT / 2;
    private static final double CLOCK_RADIUS = CLOCK_WIDTH * 0.4;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4e8c1; -fx-border-color: #8b4513; -fx-border-width: 10;");
        digitalClockLabel = new Label();
        digitalClockLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 24));
        digitalClockLabel.setTextFill(Color.DARKGREEN);
        digitalClockLabel.setPadding(new Insets(10));
        updateDigitalClock();
        root.setTop(digitalClockLabel);
        BorderPane.setAlignment(digitalClockLabel, Pos.CENTER);
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: #e6d7a3; -fx-border-color: #8b4513;");
        Tab calendarTab = new Tab("Calendar");
        calendarTab.setClosable(false);
        calendarPane = createCalendar();
        calendarTab.setContent(calendarPane);
        Tab clockTab = new Tab("Clock");
        clockTab.setClosable(false);
        StackPane analogClock = createAnalogClock();
        clockTab.setContent(analogClock);
        Tab calcTab = new Tab("Calculator");
        calcTab.setClosable(false);
        GridPane calculator = createCalculator();
        calcTab.setContent(calculator);
        Tab notepadTab = new Tab("Notepad");
        notepadTab.setClosable(false);
        VBox notepad = createNotepad(primaryStage);
        notepadTab.setContent(notepad);
        Tab galleryTab = new Tab("Gallery");
        galleryTab.setClosable(false);
        VBox gallery = createGallery(primaryStage);
        galleryTab.setContent(gallery);
        tabPane.getTabs().addAll(calendarTab, clockTab, calcTab, notepadTab, galleryTab);
        root.setCenter(tabPane);
        Timeline digitalClockTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateDigitalClock()));
        digitalClockTimer.setCycleCount(Animation.INDEFINITE);
        digitalClockTimer.play();
        Scene scene = new Scene(root, 400, 600);
        primaryStage.setTitle("Mobile Tool");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    private void updateDigitalClock() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        digitalClockLabel.setText(now.format(formatter));
    }
    private StackPane createAnalogClock() {
        StackPane clockPane = new StackPane();
        clockPane.setStyle("-fx-background-color: #fff8dc; -fx-border-color: #8b4513; -fx-border-width: 2;");
        Canvas canvas = new Canvas(CLOCK_WIDTH, CLOCK_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Timeline clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> drawClock(gc)));
        clockTimeline.setCycleCount(Animation.INDEFINITE);
        clockTimeline.play();
        clockPane.getChildren().add(canvas);
        StackPane.setAlignment(canvas, Pos.CENTER);
        return clockPane;
    }
    private void drawClock(GraphicsContext gc) {
        Calendar cal = GregorianCalendar.getInstance();
        double hours = cal.get(Calendar.HOUR) % 12 + cal.get(Calendar.MINUTE) / 60.0;
        double minutes = cal.get(Calendar.MINUTE) + cal.get(Calendar.SECOND) / 60.0;
        double seconds = cal.get(Calendar.SECOND);
        gc.clearRect(0, 0, CLOCK_WIDTH, CLOCK_HEIGHT);
        gc.setFill(Color.IVORY);
        gc.fillOval(CLOCK_CENTER_X - CLOCK_RADIUS, CLOCK_CENTER_Y - CLOCK_RADIUS, CLOCK_RADIUS * 2, CLOCK_RADIUS * 2);
        gc.setStroke(DARKBROWN);
        gc.setLineWidth(2);
        gc.strokeOval(CLOCK_CENTER_X - CLOCK_RADIUS, CLOCK_CENTER_Y - CLOCK_RADIUS, CLOCK_RADIUS * 2, CLOCK_RADIUS * 2);
        gc.setStroke(DARKBROWN);
        gc.setLineWidth(1);
        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians(i * 30);
            double x1 = CLOCK_CENTER_X + CLOCK_RADIUS * Math.cos(angle);
            double y1 = CLOCK_CENTER_Y + CLOCK_RADIUS * Math.sin(angle);
            double x2 = CLOCK_CENTER_X + (CLOCK_RADIUS - 10) * Math.cos(angle);
            double y2 = CLOCK_CENTER_Y + (CLOCK_RADIUS - 10) * Math.sin(angle);
            gc.strokeLine(x1, y1, x2, y2);
        }
        gc.setStroke(Color.rgb(0, 153, 255));
        gc.setLineWidth(4);
        drawHand(gc, hours * 30, CLOCK_RADIUS * 0.5);
        gc.setStroke(Color.rgb(0, 255, 0));
        gc.setLineWidth(3);
        drawHand(gc, minutes * 6, CLOCK_RADIUS * 0.7);
        gc.setStroke(Color.rgb(255, 0, 0));
        gc.setLineWidth(2);
        drawHand(gc, seconds * 6, CLOCK_RADIUS * 0.8);
    }
    private void drawHand(GraphicsContext gc, double angle, double length) {
        double x = CLOCK_CENTER_X + length * Math.cos(Math.toRadians(angle - 90));
        double y = CLOCK_CENTER_Y + length * Math.sin(Math.toRadians(angle - 90));
        gc.strokeLine(CLOCK_CENTER_X, CLOCK_CENTER_Y, x, y);
    }
    private GridPane createCalendar() {
        calendarPane = new GridPane();
        calendarPane.setAlignment(Pos.CENTER);
        calendarPane.setPadding(new Insets(10));
        calendarPane.setHgap(5);
        calendarPane.setVgap(5);
        calendarPane.setStyle("-fx-background-color: #fff8dc;");
        HBox navBar = new HBox(10);
        navBar.setAlignment(Pos.CENTER);
        Button prevYear = new Button("<<");
        prevYear.setStyle("-fx-background-color: #d2b48c; -fx-text-fill: #8b4513; -fx-border-color: #8b4513;");
        prevYear.setOnAction(e -> {
            calendar.add(Calendar.YEAR, -1);
            updateCalendar();
        });
        Button prevMonth = new Button("<");
        prevMonth.setStyle("-fx-background-color: #d2b48c; -fx-text-fill: #8b4513; -fx-border-color: #8b4513;");
        prevMonth.setOnAction(e -> {
            calendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });
        monthLabel = new Label();
        monthLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
        monthLabel.setTextFill(Color.DARKGREEN);
        Button nextMonth = new Button(">");
        nextMonth.setStyle("-fx-background-color: #d2b48c; -fx-text-fill: #8b4513; -fx-border-color: #8b4513;");
        nextMonth.setOnAction(e -> {
            calendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });
        Button nextYear = new Button(">>");
        nextYear.setStyle("-fx-background-color: #d2b48c; -fx-text-fill: #8b4513; -fx-border-color: #8b4513;");
        nextYear.setOnAction(e -> {
            calendar.add(Calendar.YEAR, 1);
            updateCalendar();
        });
        navBar.getChildren().addAll(prevYear, prevMonth, monthLabel, nextMonth, nextYear);
        calendarPane.add(navBar, 0, 0, 7, 1);
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
            calendarPane.add(dayLabel, i, 1);
        }
        updateCalendar();
        return calendarPane;
    }
    private void updateCalendar() {
        calendarPane.getChildren().removeIf(node -> GridPane.getRowIndex(node) >= 2);
        String monthYear = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, java.util.Locale.US) + " " + calendar.get(Calendar.YEAR);
        monthLabel.setText(monthYear);
        Calendar tempCal = (Calendar) calendar.clone();
        tempCal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int day = 1;
        for (int row = 0; row < 6 && day <= daysInMonth; row++) {
            for (int col = 0; col < 7; col++) {
                if (row == 0 && col < firstDayOfWeek) {
                    continue;
                }
                if (day <= daysInMonth) {
                    Label dayLabel = new Label(String.valueOf(day));
                    dayLabel.setFont(Font.font("Courier New", 12));
                    dayLabel.setPadding(new Insets(5));
                    calendarPane.add(dayLabel, col, row + 2);
                    day++;
                }
            }
        }
    }
    private GridPane createCalculator() {
        GridPane calcPane = new GridPane();
        calcPane.setAlignment(Pos.CENTER);
        calcPane.setPadding(new Insets(10));
        calcPane.setHgap(5);
        calcPane.setVgap(5);
        calcPane.setStyle("-fx-background-color: #fff8dc;");
        calcDisplay = new TextField("");
        calcDisplay.setEditable(false);
        calcDisplay.setFont(Font.font("Courier New", 20));
        calcDisplay.setPrefWidth(200);
        calcDisplay.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #8b4513;");
        calcPane.add(calcDisplay, 0, 0, 4, 1);
        String[][] buttonLabels = {
                {"7", "8", "9", "/"},
                {"4", "5", "6", "*"},
                {"1", "2", "3", "-"},
                {"0", "C", "=", "+"}
        };
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                Button btn = new Button(buttonLabels[row][col]);
                btn.setFont(Font.font("Courier New", 14));
                btn.setPrefSize(50, 50);
                btn.setStyle("-fx-background-color: #d2b48c; -fx-text-fill: #8b4513; -fx-border-color: #8b4513;");
                String label = buttonLabels[row][col];
                btn.setOnAction(e -> handleCalculatorInput(label));
                calcPane.add(btn, col, row + 1);
            }
        }
        return calcPane;
    }
    private void handleCalculatorInput(String input) {
        switch (input) {
            case "C":
                expression = "";
                calcDisplay.setText("");
                break;
            case "=":
                if (!expression.isEmpty()) {
                    try {
                        double result = evaluateExpression(expression);
                        calcDisplay.setText(String.valueOf(result));
                        expression = String.valueOf(result);
                    } catch (Exception e) {
                        calcDisplay.setText("Error");
                        expression = "";
                    }
                }
                break;
            case "+": case "-": case "*": case "/":
                expression += " " + input + " ";
                calcDisplay.setText(expression);
                break;
            default:
                expression += input;
                calcDisplay.setText(expression);
                break;
        }
    }
    private double evaluateExpression(String expression) {
        String[] tokens = expression.trim().split("\\s+");
        if (tokens.length < 3 || tokens.length % 2 == 0) {
            throw new IllegalArgumentException("Invalid expression");
        }
        double result = Double.parseDouble(tokens[0]);
        for (int i = 1; i < tokens.length; i += 2) {
            String operator = tokens[i];
            double num2 = Double.parseDouble(tokens[i + 1]);
            result = calculate(result, num2, operator);
        }
        return result;
    }
    private double calculate(double num1, double num2, String operator) {
        switch (operator) {
            case "+": return num1 + num2;
            case "-": return num1 - num2;
            case "*": return num1 * num2;
            case "/":
                if (num2 == 0) throw new ArithmeticException("Division by zero");
                return num1 / num2;
            default: throw new IllegalArgumentException("Invalid operator");
        }
    }
    private VBox createNotepad(Stage primaryStage) {
        VBox notepadPane = new VBox(10);
        notepadPane.setAlignment(Pos.CENTER);
        notepadPane.setPadding(new Insets(10));
        notepadPane.setStyle("-fx-background-color: #fff8dc;");
        notepadArea = new TextArea();
        notepadArea.setFont(Font.font("Courier New", 14));
        notepadArea.setPrefSize(300, 400);
        notepadArea.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #8b4513; -fx-control-inner-background: #fff8dc;");
        notepadArea.setText(savedNotes);
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button saveButton = new Button("Save");
        saveButton.setFont(Font.font("Courier New", 14));
        saveButton.setStyle("-fx-background-color: #d2b48c; -fx-text-fill: #8b4513; -fx-border-color: #8b4513;");
        saveButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Notes");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(notepadArea.getText());
                    savedNotes = notepadArea.getText();
                } catch (IOException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error saving file: " + ex.getMessage());
                    alert.showAndWait();
                }
            }
        });
        Button clearButton = new Button("Clear");
        clearButton.setFont(Font.font("Courier New", 14));
        clearButton.setStyle("-fx-background-color: #d2b48c; -fx-text-fill: #8b4513; -fx-border-color: #8b4513;");
        clearButton.setOnAction(e -> notepadArea.clear());
        buttonBox.getChildren().addAll(saveButton, clearButton);
        notepadPane.getChildren().addAll(notepadArea, buttonBox);
        return notepadPane;
    }
    private VBox createGallery(Stage primaryStage) {
        VBox galleryPane = new VBox(10);
        galleryPane.setAlignment(Pos.CENTER);
        galleryPane.setPadding(new Insets(10));
        galleryPane.setStyle("-fx-background-color: #fff8dc;");
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(300);
        imageView.setFitHeight(400);
        Button selectButton = new Button("Open");
        selectButton.setFont(Font.font("Courier New", 14));
        selectButton.setStyle("-fx-background-color: #d2b48c; -fx-text-fill: #8b4513; -fx-border-color: #8b4513;");
        selectButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    Image image = new Image(file.toURI().toString());
                    imageView.setImage(image);
                } catch (Exception ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading image: " + ex.getMessage());
                    alert.showAndWait();
                }
            }
        });
        galleryPane.getChildren().addAll(imageView, selectButton);
        return galleryPane;
    }
    public static void main(String[] args) {
        launch(args);
    }
}