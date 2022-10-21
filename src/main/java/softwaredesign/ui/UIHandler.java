package softwaredesign.ui;

import com.sothawo.mapjfx.*;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import softwaredesign.activities.ActivityFactory;
import softwaredesign.data.Report;
import softwaredesign.data.User;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UIHandler extends Application {
    private static final int SCENE_WIDTH = 800;
    private static final int SCENE_HEIGHT = 600;

    private Stage mainStage;

    private List<Coordinate> coordinateList;
    private CoordinateLine coordinateLine;
    private Extent extent;
    private MapView mapView;

    private FileChooser fileChooser;

    private FileChooser.ExtensionFilter gpxExtensionFilter;
    private Alert missingFileAlert;
    private Alert largeFileAlert;
    private Button fileChooserButton;

    private Label activityLabel;
    private Alert missingActivityAlert;
    private ChoiceBox<String> activityChoiceBox;

    private Alert missingBiometricsAlert;

    private TextFormatter<Double> doubleTextFormatter;
    private Label weightLabel;
    private TextField weightTextField;

    private TextFormatter<Integer> integerTextFormatter;
    private Label ageLabel;
    private TextField ageTextField;

    private ToggleGroup sexToggleGroup;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private GridPane sexChooserGridPane;

    private TextArea reportTextArea;
    private boolean reportGenerated;
    private Button generateReportButton;

    private FileChooser.ExtensionFilter pdfExtensionFilter;
    private Alert missingReportAlert;
    private Button saveReportButton;

    private GridPane mainGridPane;
    private BorderPane mainBorderPane;

    private void initGui(Stage stage) {
        mainStage = stage;

        coordinateList = null;
        coordinateLine = null;
        extent = null;
        initMapView();

        fileChooser = new FileChooser();

        gpxExtensionFilter = new FileChooser.ExtensionFilter("GPX files (*.gpx)", "*.gpx");
        initMissingFileAlert();
        initLargeFileAlert();
        initFileChooserButton();

        activityLabel = new Label(" Activity");
        initMissingActivityAlert();
        initActivityChoiceBox();

        initMissingBiometricsAlert();

        initDoubleTextFormatter();
        weightLabel = new Label(" Weight in kg");
        initWeightTextField();

        initIntegerTextFormatter();
        ageLabel = new Label(" Age in years");
        initAgeTextField();

        sexToggleGroup = new ToggleGroup();
        initMaleRadioButton();
        initFemaleRadioButton();
        initSexChooserGridPane();

        initReportTextArea();
        reportGenerated = false;
        initGenerateReportButton();

        pdfExtensionFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
        initMissingReportAlert();
        initSaveReportButton();

        initMainGridPane();
        initMainBorderPane();
    }

    private void initMapView() {
        mapView = new MapView();
        mapView.initialize(Configuration.builder().showZoomControls(false).build());
        mapView.setMinHeight(SCENE_HEIGHT * 1.6);
        mapView.setMinWidth(SCENE_WIDTH * 1.25);
    }

    private void initMissingFileAlert() {
        missingFileAlert = new Alert(Alert.AlertType.ERROR);
        missingFileAlert.setHeaderText(null);
        missingFileAlert.setContentText("Please select a file to proceed.");
    }

    private void initLargeFileAlert() {
        largeFileAlert = new Alert(Alert.AlertType.ERROR);
        largeFileAlert.setHeaderText(null);
        largeFileAlert.setContentText("File size exceeds 1MB.");
    }

    private void initFileChooserButton() {
        fileChooserButton = new Button("Open file");

        fileChooserButton.setOnAction(event -> {
            try {
                fileChooser.getExtensionFilters().add(gpxExtensionFilter);
                File chosenFile = fileChooser.showOpenDialog(mainStage);

                if (chosenFile != null) {
                    if (chosenFile.length() / 1000000.0 > 1) {
                        largeFileAlert.showAndWait();
                    } else {
                        User.getInstance().setGpx(chosenFile);
                        updateMapView();
                    }
                }
            } catch (IOException e) {
                missingFileAlert.showAndWait();
            }

            fileChooser.getExtensionFilters().remove(gpxExtensionFilter);
        });
    }

    private void initMissingActivityAlert() {
        missingActivityAlert = new Alert(Alert.AlertType.ERROR);
        missingActivityAlert.setHeaderText(null);
        missingActivityAlert.setContentText("Please select an activity to proceed.");
    }

    private void initActivityChoiceBox() {
        ActivityFactory activityFactory = new ActivityFactory();

        activityChoiceBox = new ChoiceBox<>(
            FXCollections.observableArrayList(activityFactory.getActivityNames())
        );

        activityChoiceBox.setOnAction(event -> User.getInstance().setActivity(
            activityFactory.getActivity(activityChoiceBox.getValue()))
        );
    }

    private void initMissingBiometricsAlert() {
        missingBiometricsAlert = new Alert(Alert.AlertType.WARNING);
        missingBiometricsAlert.setHeaderText(null);
        missingBiometricsAlert.setContentText("Missing biometrics. The generated report will lack precision.");
    }

    private void initDoubleTextFormatter() {
        Pattern doublePattern = Pattern.compile("\\d*(\\.\\d{0,2})?");

        UnaryOperator<TextFormatter.Change> doubleFilter = c -> {
            if (doublePattern.matcher(c.getControlNewText()).matches()) {
                return c;
            } else {
                return null;
            }
        };

        doubleTextFormatter = new TextFormatter<>(doubleFilter);
    }

    private void initWeightTextField() {
        weightTextField = new TextField();
        weightTextField.setPromptText("Weight");
        weightTextField.setTextFormatter(doubleTextFormatter);
    }

    private void initIntegerTextFormatter() {
        Pattern integerPattern = Pattern.compile("\\d{0,3}");

        UnaryOperator<TextFormatter.Change> integerFilter = c -> {
            if (integerPattern.matcher(c.getControlNewText()).matches()) {
                return c;
            } else {
                return null;
            }
        };

        integerTextFormatter = new TextFormatter<>(integerFilter);
    }

    private void initAgeTextField() {
        ageTextField = new TextField();
        ageTextField.setPromptText("Age");
        ageTextField.setTextFormatter(integerTextFormatter);
    }

    private void initMaleRadioButton() {
        maleRadioButton = new RadioButton("Male");
        maleRadioButton.setToggleGroup(sexToggleGroup);
    }

    private void initFemaleRadioButton() {
        femaleRadioButton = new RadioButton("Female");
        femaleRadioButton.setToggleGroup(sexToggleGroup);
    }

    private void initSexChooserGridPane() {
        sexChooserGridPane = new GridPane();
        sexChooserGridPane.add(maleRadioButton, 0, 0);
        sexChooserGridPane.add(femaleRadioButton, 0, 1);
    }

    private void initReportTextArea() {
        reportTextArea = new TextArea();
        reportTextArea.setPrefRowCount(8);
        reportTextArea.setPrefColumnCount(15);
        reportTextArea.setWrapText(true);
        reportTextArea.setEditable(false);
    }

    private void initGenerateReportButton() {
        generateReportButton = new Button("Generate report");

        generateReportButton.setOnAction(event -> generateReport());
    }

    private void generateReport() {
        if (User.getInstance().getGpx() != null && User.getInstance().getActivity() != null) {
            User.getInstance().setWeight(
                weightTextField.getText().isEmpty()
                ? null
                : Double.parseDouble(weightTextField.getText())
            );

            User.getInstance().setAge(
                ageTextField.getText().isEmpty()
                ? null
                : Integer.parseInt(ageTextField.getText())
            );

            RadioButton selected = (RadioButton) sexToggleGroup.getSelectedToggle();
            User.getInstance().setSex(
                selected == null
                ? null
                : selected.getText()
            );

            if (weightTextField.getText().isEmpty()
                || ageTextField.getText().isEmpty()
                || selected == null) {
                missingBiometricsAlert.showAndWait();
            }

            reportTextArea.setText(new Report().toString());

            reportGenerated = true;
        } else if (User.getInstance().getGpx() == null) {
            missingFileAlert.showAndWait();
        } else if (User.getInstance().getActivity() == null) {
            missingActivityAlert.showAndWait();
        }
    }

    private void initMissingReportAlert() {
        missingReportAlert = new Alert(Alert.AlertType.ERROR);
        missingReportAlert.setHeaderText(null);
        missingReportAlert.setContentText("Please generate a report to proceed.");
    }

    private void initSaveReportButton() {
        saveReportButton = new Button("Save report as PDF");

        saveReportButton.setOnAction(event -> {
            if (reportGenerated) {
                try {
                    fileChooser.getExtensionFilters().add(pdfExtensionFilter);
                    saveReport();
                } catch (IOException | NullPointerException e) {
                    missingFileAlert.showAndWait();
                }

                fileChooser.getExtensionFilters().remove(pdfExtensionFilter);
            } else {
                missingReportAlert.showAndWait();
            }
        });
    }

    private void saveReport() throws IOException, NullPointerException {
        PDDocument pdDocument = new PDDocument();
        PDPage pdPage = new PDPage();
        pdDocument.addPage(pdPage);

        WritableImage writableImage = new WritableImage(500, 500);
        mainBorderPane.getCenter().snapshot(null, writableImage);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

        PDImageXObject pdImageXObject = LosslessFactory.createFromImage(pdDocument, bufferedImage);

        String[] report = new Report().toString().split("\n");

        PDPageContentStream pdPageContentStream = new PDPageContentStream(pdDocument, pdPage);

        pdPageContentStream.drawImage(pdImageXObject, 55, 250);

        pdPageContentStream.beginText();
        pdPageContentStream.setFont(PDType1Font.HELVETICA, 12);
        pdPageContentStream.newLineAtOffset(75, 200);
        pdPageContentStream.setLeading(15);

        for (String line : report) {
            pdPageContentStream.showText(line);
            pdPageContentStream.newLine();
        }

        pdPageContentStream.endText();
        pdPageContentStream.close();

        pdDocument.save(fileChooser.showSaveDialog(mainStage));
        pdDocument.close();
    }

    private void initMainGridPane() {
        mainGridPane = new GridPane();

        mainGridPane.add(fileChooserButton, 0, 0);
        mainGridPane.add(activityLabel, 0, 1);
        mainGridPane.add(activityChoiceBox, 0, 2);
        mainGridPane.add(weightLabel, 0, 3);
        mainGridPane.add(weightTextField, 0, 4);
        mainGridPane.add(ageLabel, 0, 5);
        mainGridPane.add(ageTextField, 0, 6);
        mainGridPane.add(sexChooserGridPane, 0, 7);
        mainGridPane.add(generateReportButton, 0, 8);
        mainGridPane.add(reportTextArea, 0, 9);
        mainGridPane.add(saveReportButton, 0, 10);

        mainGridPane.setVgap(5);
        mainGridPane.setPadding(new Insets(5));
    }

    private void initMainBorderPane() {
        mainBorderPane = new BorderPane();

        mainBorderPane.setLeft(mainGridPane);
        mainBorderPane.setCenter(mapView);
    }

    private void updateMapView() {
        coordinateList = User.getInstance()
            .getGpx()
            .tracks()
            .flatMap(Track::segments)
            .findFirst()
            .map(TrackSegment::points)
            .orElse(Stream.empty())
            .map(this::wayPointToCoordinate)
            .collect(Collectors.toList());

        coordinateLine = new CoordinateLine(coordinateList)
            .setVisible(true)
            .setColor(Color.RED);

        extent = Extent.forCoordinates(coordinateList);

        mapView.addCoordinateLine(coordinateLine);

        if (coordinateLine.getCoordinateStream().findFirst().isPresent()) {
            mapView.setCenter(
                coordinateLine
                    .getCoordinateStream()
                    .findFirst()
                    .get()
            );
        }

        mapView.setExtent(extent);

        reportGenerated = false;
    }

    public Coordinate wayPointToCoordinate(WayPoint wayPoint) {
        return new Coordinate(wayPoint.getLatitude().doubleValue(), wayPoint.getLongitude().doubleValue());
    }

    @Override
    public void start(Stage primaryStage) {
        initGui(primaryStage);

        Scene scene = new Scene(mainBorderPane, SCENE_WIDTH, SCENE_HEIGHT);

        primaryStage.setTitle("GPXManager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void gui(String[] args) {
        launch(args);
    }
}
