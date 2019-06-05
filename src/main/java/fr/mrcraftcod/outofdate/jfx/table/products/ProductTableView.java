package fr.mrcraftcod.outofdate.jfx.table.products;

import fr.mrcraftcod.outofdate.jfx.EditProductView;
import fr.mrcraftcod.outofdate.jfx.MainController;
import fr.mrcraftcod.outofdate.jfx.table.cells.ImageProductTableCell;
import fr.mrcraftcod.outofdate.jfx.table.cells.ProductTableCell;
import fr.mrcraftcod.outofdate.jfx.utils.LangUtils;
import fr.mrcraftcod.outofdate.model.OwnedProduct;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import lombok.Getter;
import java.net.URL;
import java.util.function.Consumer;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-04-20.
 *
 * @author Thomas Couchoud
 * @since 2019-04-20
 */
public class ProductTableView extends TableView<OwnedProduct>{
	@Getter
	private final MainController controller;
	
	public ProductTableView(final Stage parentStage, final MainController controller){
		super();
		this.controller = controller;
		this.setSortPolicy(cell -> false);
		
		final var columnID = new TableColumn<OwnedProduct, String>(LangUtils.getString("product_table_column_id"));
		columnID.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getProduct().getId()));
		columnID.setCellFactory(cb -> new ProductTableCell<>(this.getOnProductEdit(parentStage)));
		
		final var columnName = new TableColumn<OwnedProduct, String>(LangUtils.getString("product_table_column_name"));
		columnName.setCellValueFactory(obj -> obj.getValue().getProduct().nameProperty());
		columnName.setCellFactory(cb -> new ProductTableCell<>(this.getOnProductEdit(parentStage)));
		
		final var columnPicture = new TableColumn<OwnedProduct, URL>(LangUtils.getString("product_table_column_picture"));
		columnPicture.setCellValueFactory(obj -> obj.getValue().getProduct().imageProperty());
		columnPicture.setCellFactory(cb -> new ImageProductTableCell(this.getOnProductEdit(parentStage)));
		
		final var columnDaysLeft = new TableColumn<OwnedProduct, Number>(LangUtils.getString("product_table_column_days_left"));
		columnDaysLeft.setCellValueFactory(obj -> obj.getValue().daysLeftProperty());
		columnDaysLeft.setCellFactory(cb -> new ProductTableCell<>(this.getOnProductEdit(parentStage)));
		
		final var columnOpen = new TableColumn<OwnedProduct, Boolean>(LangUtils.getString("product_table_column_open"));
		columnOpen.setCellValueFactory(obj -> obj.getValue().isOpenProperty());
		columnOpen.setCellFactory(cb -> new ProductTableCell<>(this.getOnProductEdit(parentStage)));
		
		final var columnSubCount = new TableColumn<OwnedProduct, Number>(LangUtils.getString("product_table_column_sub_count"));
		columnSubCount.setCellValueFactory(obj -> obj.getValue().subCountProperty());
		columnSubCount.setCellFactory(cb -> new ProductTableCell<>(this.getOnProductEdit(parentStage)));
		
		final var columnNutriscore = new TableColumn<OwnedProduct, String>(LangUtils.getString("product_table_column_nutriscore"));
		columnNutriscore.setCellValueFactory(obj -> obj.getValue().getProduct().nutriscoreProperty());
		columnNutriscore.setCellFactory(cb -> new ProductTableCell<>(this.getOnProductEdit(parentStage)));
		
		this.getColumns().addAll(columnID, columnName, columnPicture, columnDaysLeft, columnOpen, columnSubCount, columnNutriscore);
		
		this.setItems(createList());
	}
	
	protected Consumer<OwnedProduct> getOnProductEdit(final Stage parentStage){
		return product -> new EditProductView(parentStage, product);
	}
	
	protected ObservableList<OwnedProduct> createList(){
		final var filteredList = getController().getOwnedProducts().filtered(p -> !p.getIsConsumed());
		final var sortedList = filteredList.sorted();
		//TODO
		return sortedList;
	}
}
