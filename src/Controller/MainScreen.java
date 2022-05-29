package Controller;

import Model.Inventory;
import Model.Part;
import Model.Product;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class MainScreen implements Initializable {

    Stage stage;
    Parent scene;

    public Button MainPartSearchButton;
    public TextField MainPartSearch;
    public TableView MainPartTable;
    public TableColumn pIDColumn;
    public TableColumn pNameColumn;
    public TableColumn pInvColumn;
    public TableColumn pCostColumn;
    public Button MainPartAdd;
    public Button MainPartModify;
    public Button MainPartDelete;
    public Button MainProductSearchButton;
    public TextField MainProductSearch;
    public TableView MainProductTable;
    public TableColumn prodIDColumn;
    public TableColumn prodNameColumn;
    public TableColumn prodInvColumn;
    public TableColumn prodPriceColumn;
    public Button MainProductAdd;
    public Button MainProductModify;
    public Button MainProductDelete;
    public Button MainExit;

    //This gets the index of the selected part so that we can populate the ID value and load the correct part
    protected static int mainPartTableIndex;
    //same as above just with products
    protected static int mainProdTableIndex;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Populate Parts table
        MainPartTable.setItems(Inventory.getAllParts());
        pIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        pNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        pInvColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        pCostColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Select First Part when initialized
        MainPartTable.getSelectionModel().selectFirst();

        //Populate Products table
        MainProductTable.setItems(Inventory.getAllProducts());
        prodIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        prodNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        prodInvColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        prodPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Select first product when initialized
        MainProductTable.getSelectionModel().selectFirst();
    }

    public void searchForPart(MouseEvent mouseEvent) throws IOException{
        stage = (Stage)((Button)mouseEvent.getSource()).getScene().getWindow();

        String search = MainPartSearch.getText();
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
            MainPartTable.setItems(found);
        }
    }

    public void addPart(MouseEvent mouseEvent) throws IOException {
        stage = (Stage)((Button) mouseEvent.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/View/AddPart.fxml"));
        root.setStyle("-fx-font-family: 'Times New Roman';");
        stage.setScene(new Scene(root));
        stage.setTitle("Add Part");
        stage.show();

    }

    public void modifyPart(MouseEvent mouseEvent) throws IOException{

            //The index starts
            mainPartTableIndex = Inventory.getAllParts().indexOf((MainPartTable.getSelectionModel().getSelectedItem()));

            System.out.println(mainPartTableIndex);
            //We want to pass the part index to the modify part controller so that it can load the part of the specified index

            stage = (Stage)((Button) mouseEvent.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/View/modifyPart.fxml"));
            scene.setStyle("-fx-font-family: 'Times New Roman';");
            stage.setScene(new Scene(scene));
        stage.setTitle("Modify Part");
            stage.show();
    }

    public void deletePart(MouseEvent mouseEvent) throws IOException {
        stage = (Stage)((Button) mouseEvent.getSource()).getScene().getWindow();

        Part part = (Part) MainPartTable.getSelectionModel().getSelectedItem();
        System.out.println(part);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This part will be deleted.");
        alert.getDialogPane().getScene().getRoot().setStyle("-fx-font-family:'Times New Roman';");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK){
            Inventory.deletePart(part);
        }

    }

    public void searchForProduct(MouseEvent mouseEvent) throws IOException{
        stage = (Stage)((Button)mouseEvent.getSource()).getScene().getWindow();

        String search = MainProductSearch.getText();
        ObservableList<Product> found = FXCollections.observableArrayList();
        found = Inventory.lookupProduct(search);

        if(found.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No product Found");
            alert.setContentText("No product found with name " +search);
            alert.getDialogPane().getScene().getRoot().setStyle("-fx-font-family:'Times New Roman';");
            alert.showAndWait();
        } else {
            MainProductTable.setItems(found);
        }
    }

    public void addProduct(MouseEvent mouseEvent) throws IOException {
        stage = (Stage)((Button) mouseEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/View/addProduct.fxml"));
        scene.setStyle("-fx-font-family: 'Times New Roman';");
        stage.setScene(new Scene(scene,1200, 600));
        stage.setTitle("Add Product");
        stage.show();
    }

    public void modifyProduct(MouseEvent mouseEvent) throws IOException{

        //mainProdTableIndex = Inventory.getAllProducts().indexOf((MainProductTable.getSelectionModel().getSelectedItem()));
        Product selected = (Product) MainProductTable.getSelectionModel().getSelectedItem();

        stage = (Stage)((Button)mouseEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/View/ModifyProduct.fxml"));
        scene.setStyle("-fx-font-family: 'Times New Roman';");
        stage.setScene(new Scene(scene,1200,600));
        stage.setTitle("Modify Product");
        stage.show();

    }

    public void deleteProduct(MouseEvent mouseEvent) throws IOException{
        stage = (Stage)((Button)mouseEvent.getSource()).getScene().getWindow();

        Product product = (Product) MainProductTable.getSelectionModel().getSelectedItem();
        for(int i = 0; i < Inventory.getAllParts().size(); i++){
            if(Inventory.deleteValidation(Inventory.getAllParts().get(i)).equals("")){
                Inventory.deleteProduct(product);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Deleted");
                alert.setHeaderText("Product Removed");
                alert.setContentText("Product Successfully Deleted");
                alert.getDialogPane().getScene().getRoot().setStyle("-fx-font-family:'Times New Roman';");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Cannot delete product");
                alert.setContentText("Make sure product contains no parts before deleting");
                alert.getDialogPane().getScene().getRoot().setStyle("-fx-font-family:'Times New Roman';");
                alert.showAndWait();
            }
        }

    }

    public void exitProgram(MouseEvent mouseEvent) throws IOException{
        stage = (Stage)((Button)mouseEvent.getSource()).getScene().getWindow();
        stage.close();
    }
}
