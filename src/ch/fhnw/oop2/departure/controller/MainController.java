package ch.fhnw.oop2.departure.controller;

import ch.fhnw.oop2.departure.Main;
import ch.fhnw.oop2.departure.model.Departure;
import ch.fhnw.oop2.departure.model.Timetable;
import ch.fhnw.oop2.departure.util.JavaFxUtils;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by ernst on 26.04.2016.
 */
public class MainController implements Initializable {
	private ResourceBundle bundle;
	private Timetable timetable;
	private Departure departureProxy = new Departure();

	@FXML
	private TableView<Departure> tvDepartureTable;
	@FXML
	private TableColumn<Departure, String> tcId;
	@FXML
	private TableColumn<Departure, String> tcDepartureTime;
	@FXML
	private TableColumn<Departure, String> tcDestination;
	@FXML
	private TableColumn<Departure, String> tcPlatform;
	@FXML
	private TextField txtDepartureTime;
	@FXML
	private TextField txtDestination;
	@FXML
	private TextField txtPlatform;
	@FXML
	private TextField txtTrainNumber;
	@FXML
	private TextArea txtStops;
	@FXML
	private Button toggleLanguage;
	@FXML
	private TextField txtFilter;

	@FXML
	public void toggleLanguage(ActionEvent e) {
		if (toggleLanguage.getText().equals("DE")) {
			main.loadMainView(new Locale("de", "DE"));
		} else {
			main.loadMainView(new Locale("en", "EN"));
		}
	}

	@FXML
	public void redo(ActionEvent actionEvent) {
		//TODO redo
		JavaFxUtils.createAlert("Info", "Not yet implemented.", "We haven't implemented <redo> yet.");
	}

	@FXML
	public void undo(ActionEvent actionEvent) {
		//TODO undo
		JavaFxUtils.createAlert("Info", "Not yet implemented.", "We haven't implemented <undo> yet.");
	}

	@FXML
	public void clear(ActionEvent actionEvent) {
		timetable.delete(tvDepartureTable.getSelectionModel().getSelectedItem());
	}

	@FXML
	public void add(ActionEvent actionEvent) {
		timetable.createDeparture();
		tvDepartureTable.getSelectionModel().selectLast();
		tvDepartureTable.scrollTo(tvDepartureTable.getItems().size() - 1);
	}

	@FXML
	public void save(ActionEvent actionEvent) {
		FileChooser fileChooser = new FileChooser();
		if (timetable.getFile() != null) {
			fileChooser.setInitialFileName(timetable.getFile().getPath());
		}
		fileChooser.setTitle("Save File");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Json", "*.json"));
		File f = fileChooser.showSaveDialog(main.getPrimaryStage());

		timetable.saveJSON(f);
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;

		toggleLanguage.setTooltip(new Tooltip(bundle.getString("tooltip")));

		txtDepartureTime.setDisable(true);
		txtTrainNumber.setDisable(true);
		txtDestination.setDisable(true);
		txtStops.setDisable(true);
		txtPlatform.setDisable(true);

		tcId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
		tcId.setCellFactory(TextFieldTableCell.forTableColumn());
		tcId.setOnEditCommit((t) ->
				t.getTableView().getItems().get(
						t.getTablePosition().getRow()
				).setId(t.getNewValue())
		);

		tcDepartureTime.setCellValueFactory(cellData -> cellData.getValue().departureTimeProperty());
		tcDepartureTime.setCellFactory(TextFieldTableCell.forTableColumn());
		tcDepartureTime.setOnEditCommit((t) ->
				t.getTableView().getItems().get(
						t.getTablePosition().getRow()
				).setDepartureTime(t.getNewValue())
		);

		tcDestination.setCellValueFactory(cellData -> cellData.getValue().destinationProperty());
		tcDestination.setCellFactory(TextFieldTableCell.forTableColumn());
		tcDestination.setOnEditCommit((t) ->
				t.getTableView().getItems().get(
						t.getTablePosition().getRow()
				).setDestination(t.getNewValue())
		);

		tcPlatform.setCellValueFactory(cellData -> cellData.getValue().platformProperty());
		tcPlatform.setCellFactory(TextFieldTableCell.forTableColumn());
		tcPlatform.setOnEditCommit((t) ->
				t.getTableView().getItems().get(
						t.getTablePosition().getRow()
				).setPlatform(t.getNewValue())
		);

		JavaFxUtils.addFilter_OnlyNumbers(txtPlatform);

		//Proxybinding
		departureProxy.trainNumberProperty().bindBidirectional(txtTrainNumber.textProperty());
		departureProxy.departureTimeProperty().bindBidirectional(txtDepartureTime.textProperty());
		departureProxy.destinationProperty().bindBidirectional(txtDestination.textProperty());
		departureProxy.viaProperty().bindBidirectional(txtStops.textProperty());
		departureProxy.platformProperty().bindBidirectional(txtPlatform.textProperty());

		//OnDemand Binding
		tvDepartureTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != null) {
				departureProxy.trainNumberProperty().unbindBidirectional(oldValue.trainNumberProperty());
				departureProxy.departureTimeProperty().unbindBidirectional(oldValue.departureTimeProperty());
				departureProxy.destinationProperty().unbindBidirectional(oldValue.destinationProperty());
				departureProxy.viaProperty().unbindBidirectional(oldValue.viaProperty());
				departureProxy.platformProperty().unbindBidirectional(oldValue.platformProperty());
			}
			if (newValue != null) {
				departureProxy.trainNumberProperty().bindBidirectional(newValue.trainNumberProperty());
				departureProxy.departureTimeProperty().bindBidirectional(newValue.departureTimeProperty());
				departureProxy.destinationProperty().bindBidirectional(newValue.destinationProperty());
				departureProxy.viaProperty().bindBidirectional(newValue.viaProperty());
				departureProxy.platformProperty().bindBidirectional(newValue.platformProperty());
				txtDepartureTime.setDisable(false);
				txtTrainNumber.setDisable(false);
				txtDestination.setDisable(false);
				txtStops.setDisable(false);
				txtPlatform.setDisable(false);
			} else {
				txtDepartureTime.setDisable(true);
				txtTrainNumber.setDisable(true);
				txtDestination.setDisable(true);
				txtStops.setDisable(true);
				txtPlatform.setDisable(true);
				timetable.setFile(null);

			}
		});
		Platform.runLater(() -> {
			if (timetable != null && timetable.getFile() == null && timetable.getDeparturesData().isEmpty()) {
				if (JavaFxUtils.createYesNoAlert("Load Data", "Do you want to load some Data?", "", "Yes", "No")) {
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Open Resource File");
					fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Json", "*.json"), new FileChooser.ExtensionFilter("Comma Separated Value", "*.csv"));

					File f = fileChooser.showOpenDialog(main.getPrimaryStage());
					if (f != null) {
						timetable.load(f);
					}
				}
			}
		});

	}

	public void setTimetable(Timetable timetable) {
		this.timetable = timetable;
		FilteredList<Departure> filteredData = new FilteredList<>(timetable.getDeparturesData(), p -> true);
		txtFilter.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(departure -> {
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				String lowerCaseFilter = newValue.toLowerCase();
				return departure.getDepartureTime().toLowerCase().contains(lowerCaseFilter) ||
						departure.getDestination().toLowerCase().contains(lowerCaseFilter) ||
						departure.getId().toLowerCase().contains(lowerCaseFilter) ||
						departure.getPlatform().toLowerCase().contains(lowerCaseFilter);

			});
		});
		tvDepartureTable.setItems(filteredData);
	}

	private Main main;

	public void setMain(Main main) {
		this.main = main;
	}
}
