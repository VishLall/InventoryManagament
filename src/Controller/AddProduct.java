package Controller;

import Model.Inventory;
import Model.Part;
import Model.Product;
import UserExceptions.InventoryOutOfRangeException;
import UserExceptions.MinOverMaxException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddProduct implements Initializable {

    public TextField AddProductMin;
    public TextField AddProductMax;
    public TextField AddProductID;
    public TextField AddProductName;
    public TextField AddProductInv;
    public TextField AddProductPrice;
    public Button AddProductSearchButton;
    public TextField AddProductSearch;
    public TableView AddProductAddTable;
    public TableColumn addUnitPrice;
    public TableColumn addInvLevel;
    public TableColumn addPartName;
    public TableColumn addPartID;
    public Button AddProductAdd;
    public TableView AddProductDeleteTable;
    public TableColumn deletePricePerUnit;
    public TableColumn deleteInvLevel;
    public TableColumn deletePartName;
    public TableColumn deletePartID;
    public Button AddProductDelete;
    public Button AddProductSave;
    public Button AddProductCancel;

    Stage stage;
    Parent scene;

    //Keeps track of parts that the user wants to add in to the inventory
    private ObservableList<Part> partsToAdd = FXCollections.observableArrayList();

    private int autoID;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Populate Add Parts table
        AddProductAddTable.setItems(Inventory.getAllParts());
        addPartID.setCellValueFactory(new PropertyValueFactory<>("id"));
        addPartName.setCellValueFactory(new PropertyValueFactory<>("name"));
        addInvLevel.setCellValueFactory(new PropertyValueFactory<>("stock"));
        addUnitPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        AddProductAddTable.getSelectionModel().selectFirst();

        //Populate Delete Products table
        AddProductDeleteTable.setItems(partsToAdd);
        deletePartID.setCellValueFactory(new PropertyValueFactory<>("id"));
        deletePartName.setCellValueFactory(new PropertyValueFactory<>("name"));
        deleteInvLevel.setCellValueFactory(new PropertyValueFactory<>("stock"));
        deletePricePerUnit.setCellValueFactory(new PropertyValueFactory<>("price"));


        autoID = Inventory.assignProductID();
        AddProductID.setText(Integer.toString(autoID));
        
    }

    public void lookupPart(MouseEvent mouseEvent) {

        String search = AddProductSearch.getText();
        ObservableList<Part> found = FXCollections.observableArrayList();
        found = Inventory.lookupPart(search);

        if(found.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No part Found");
            alert.setContentText("No part found with name " +search);
            alert.getDialogPane().getScene().getRoot().setStyle("-fx-font-family:'Times New Roman';");
            alert.showAndWait();
        } else {
            AddProductAddTable.setItems(found);
        }

    }

    public void addPart(MouseEvent mouseEvent) throws IOException {
        partsToAdd.add(Inventory.lookupPart((Inventory.getAllParts().indexOf((AddProductAddTable.getSelectionModel().getSelectedItem())) + 1)));
    }

    public void removePart(MouseEvent mouseEvent) {
        Part part = (Part) AddProductDeleteTable.getSelectionModel().getSelectedItem();

        partsToAdd.remove(part);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Deleted");
        alert.setHeaderText("Part Removed");
        alert.setContentText("Part Successfully Deleted");
        alert.getDialogPane().getScene().getRoot().setStyle("-fx-font-family:'Times New Roman';");
        alert.showAndWait();
    }

    public void saveProduct(MouseEvent mouseEvent) throws IOException{
        System.out.println(autoID);
        //Fulfilling the validation requirement in Set 1, make sure min is not greater than max
        //Also making sure that strings aren't being entered where an int is required
        try {
            int pID = Integer.parseInt(AddProductID.getText());
            int pMin = Integer.parseInt(AddProductMin.getText());
            int pMax = Integer.parseInt(AddProductMax.getText());
            String pName = new String(AddProductName.getText());
            int pInv = Integer.parseInt(AddProductInv.getText());
            double pPrice = Double.parseDouble(AddProductPrice.getText());

            if( pMin > pMax){
                throw new MinOverMaxException();
            }

            if (pInv < pMin || pInv > pMax) {
                throw new InventoryOutOfRangeException();
            }

            else {
                Product product = new Product(autoID, pName, pPrice, pInv, pMin, pMax);
                Inventory.addProduct(product);
            }
            Parent root = FXMLLoader.load(getClass().getResource("/View/MainScreen.fxml"));
            stage = (Stage) ((Button) mouseEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 850, 400);
            stage.setTitle("Main Screen");
            root.setStyle("-fx-font-family: 'Times New Roman';");
            stage.setScene(scene);
            stage.show();

        } catch (NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Add product Failed");
            alert.setContentText("Product cannot be added, empty or invalid values in one or more fields");
            alert.getDialogPane().getScene().getRoot().setStyle("-fx-font-family:'Times New Roman';");
            alert.showAndWait();
        }catch (MinOverMaxException e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Add Part Failed");
            alert.setContentText("Min cannot be more than Max");
            alert.getDialogPane().getScene().getRoot().setStyle("-fx-font-family:'Times New Roman';");
            alert.showAndWait();
        }catch ( InventoryOutOfRangeException e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Add Part Failed");
            alert.setContentText("Inventory must be between Min and Max");
            alert.getDialogPane().getScene().getRoot().setStyle("-fx-font-family:'Times New Roman';");
            alert.showAndWait();
        }
    }

    public void cancel(MouseEvent mouseEvent) throws IOException{
        stage = (Stage)((Button)mouseEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/View/MainScreen.fxml"));
        scene.setStyle("-fx-font-family: 'Times New Roman';");
        stage.setScene(new Scene(scene, 850, 400));
        stage.show();
    }
}
