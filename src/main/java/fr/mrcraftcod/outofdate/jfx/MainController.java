package fr.mrcraftcod.outofdate.jfx;

import fr.mrcraftcod.outofdate.model.OwnedProduct;
import fr.mrcraftcod.outofdate.utils.HBDatabase;
import fr.mrcraftcod.outofdate.utils.OpenFoodFacts;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-04-20.
 *
 * @author Thomas Couchoud
 * @since 2019-04-20
 */
public class MainController implements AutoCloseable{
	private static final DateTimeFormatter df = DateTimeFormatter.ISO_DATE;
	private final ObservableList<OwnedProduct> ownedProducts;
	private final static String productHintSeparator = "~";
	private final HBDatabase db;
	
	public MainController(){
		this.db = new HBDatabase();
		this.ownedProducts = FXCollections.observableArrayList(p -> new Observable[]{
				p.isOpenProperty(),
				p.spoilDateProperty(),
				p.subCountProperty(),
				p.isConsumedProperty(),
				p.getProduct().nameProperty(),
				p.getProduct().imageProperty(),
				p.getProduct().nutriscoreProperty()
		});
		this.ownedProducts.addListener(new ListChangeListener<OwnedProduct>(){
			@Override
			public void onChanged(Change<? extends OwnedProduct> change){
				change.getList().forEach(db::updateOwnedProduct);
			}
		});
		this.ownedProducts.addAll(db.getOwnedProducts());
	}
	
	@Override
	public void close() throws Exception{
		this.db.close();
	}
	
	public void removeOwnedProduct(OwnedProduct ownedProduct){
		if(this.db.removeOwnedProduct(ownedProduct))
			this.ownedProducts.remove(ownedProduct);
	}
	
	public List<String> getProductsHints(){
		return this.db.getProducts().stream().map(p -> String.format("%s%s%s", p.getId(), this.getProductHintSeparator(), p.getName())).distinct().collect(Collectors.toList());
	}
	
	public String getProductHintSeparator(){
		return productHintSeparator;
	}
	
	public Optional<OwnedProduct> addNewOwnedProduct(final String id){
		return this.db.getProduct(id).or(() -> {
			final var product = OpenFoodFacts.getProduct(id);
			product.ifPresent(this.db::persistProduct);
			return product;
		}).map(product -> {
			final var ownedProduct = new OwnedProduct(product);
			if(this.db.persistOwnedProduct(ownedProduct)){
				this.ownedProducts.add(ownedProduct);
				return ownedProduct;
			}
			return null;
		});
	}
	
	public ObservableList<OwnedProduct> getOwnedProducts(){
		return this.ownedProducts;
	}
	
	public void refreshProductInfos(){
		this.db.getProducts().stream().map(p -> {
			final var productOptional = OpenFoodFacts.getProduct(p.getId());
			if(productOptional.isPresent()){
				final var newProduct = productOptional.get();
				p.setName(newProduct.getName());
				p.setImage(newProduct.getImage());
				p.setNutriscore(newProduct.getNutriscore());
				return p;
			}
			return null;
		}).filter(Objects::nonNull).forEach(this.db::updateProduct);
	}
}
