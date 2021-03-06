package fr.mrcraftcod.outofdate.jfx;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import fr.mrcraftcod.outofdate.Main;
import fr.mrcraftcod.outofdate.jfx.table.consumed.ConsumedProductsTab;
import fr.mrcraftcod.outofdate.jfx.table.products.ProductsTab;
import fr.mrcraftcod.outofdate.jfx.utils.LangUtils;
import fr.mrcraftcod.outofdate.utils.CLIParameters;
import fr.mrcraftcod.utils.javafx.ApplicationBase;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-04-20.
 *
 * @author Thomas Couchoud
 * @since 2019-04-20
 */
public class MainApplication extends ApplicationBase{
	private static final Logger log = LoggerFactory.getLogger(MainApplication.class);
	private static CLIParameters cliParameters;
	private MainController controller;
	private TabPane tabPane;
	private ProductsTab productsTab;
	private ConsumedProductsTab consumedProductsTab;
	
	public static void main(String[] args){
		log.info("Starting OutOfFood version {}", Main.getMavenVersion());
		cliParameters = new CLIParameters();
		try{
			JCommander.newBuilder().addObject(cliParameters).build().parse(args);
		}
		catch(final ParameterException e){
			log.error("Failed to parse arguments: {}", e.getMessage());
			return;
		}
		Application.launch(args);
	}
	
	@Override
	public void preInit() throws Exception{
		super.preInit();
		this.controller = new MainController(cliParameters);
	}
	
	@Override
	public Image getIcon(){
		return new Image(this.getClass().getResourceAsStream("/jfx/icon.png"));
	}
	
	@Override
	public String getFrameTitle(){
		return "OutOfFood";
	}
	
	private MenuBar constructMenuBar(){
		final var menuBar = new MenuBar();
		final var os = System.getProperty("os.name");
		if(os != null && os.startsWith("Mac")){
			menuBar.useSystemMenuBarProperty().set(true);
		}
		
		final var menuProductAdd = new MenuItem(LangUtils.getString("menu_bar_product_add"));
		menuProductAdd.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		menuProductAdd.setOnAction(evt -> {
			if(Objects.nonNull(this.productsTab)){
				new Thread(() -> this.productsTab.addProduct()).start();
			}
		});
		
		final var menuProductRefresh = new MenuItem(LangUtils.getString("menu_bar_product_refresh"));
		menuProductRefresh.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
		menuProductRefresh.setOnAction(evt -> this.controller.refreshProductInfos());
		
		final var menuProduct = new Menu(LangUtils.getString("menu_bar_product"));
		menuProduct.getItems().addAll(menuProductAdd, menuProductRefresh);
		
		menuBar.getMenus().addAll(menuProduct);
		return menuBar;
	}
	
	@Override
	public Consumer<Stage> getOnStageDisplayed() throws Exception{
		return null;
	}
	
	@Override
	public Parent createContent(final Stage stage){
		final var borderPane = new BorderPane();
		borderPane.getStylesheets().add("/jfx/style.css");
		
		this.productsTab = new ProductsTab(stage, this.controller);
		this.consumedProductsTab = new ConsumedProductsTab(stage, this.controller);
		
		this.tabPane = new TabPane();
		this.tabPane.getTabs().addAll(this.productsTab, this.consumedProductsTab);
		
		borderPane.setTop(constructMenuBar());
		borderPane.setCenter(this.tabPane);
		return borderPane;
	}
	
	@Override
	public Consumer<Stage> getStageHandler(){
		return stage -> stage.setOnCloseRequest(cl -> {
			try{
				this.controller.close();
			}
			catch(Exception e){
				log.error("Failed to close controller", e);
			}
		});
	}
}
