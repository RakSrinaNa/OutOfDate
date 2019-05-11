/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 23/01/2018.
 *
 * @since 2018-01-23
 */
open module fr.mrcraftcod.outofdate {
	requires org.json;
	requires org.apache.commons.lang3;
	
	requires org.slf4j;
	requires ch.qos.logback.classic;
	
	requires fr.mrcraftcod.utils.http;
	requires fr.mrcraftcod.utils.javafx;
	
	exports fr.mrcraftcod.outofdate.jfx to javafx.graphics;
	exports fr.mrcraftcod.outofdate.model;
}