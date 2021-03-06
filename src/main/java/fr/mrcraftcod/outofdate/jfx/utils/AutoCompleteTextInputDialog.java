/*
 * Copyright (c) 2014, 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package fr.mrcraftcod.outofdate.jfx.utils;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import java.util.Collection;

public class AutoCompleteTextInputDialog extends Dialog<String>{
	private final GridPane grid;
	private final Label label;
	private final AutocompletionTextField textField;
	
	public AutoCompleteTextInputDialog(final Collection<String> suggestions){
		final var dialogPane = getDialogPane();
		
		// -- textfield
		this.textField = new AutocompletionTextField(suggestions);
		this.textField.setMaxWidth(Double.MAX_VALUE);
		GridPane.setHgrow(textField, Priority.ALWAYS);
		GridPane.setFillWidth(textField, true);
		
		label = createContentLabel(dialogPane.getContentText());
		label.setPrefWidth(Region.USE_COMPUTED_SIZE);
		label.textProperty().bind(dialogPane.contentTextProperty());
		
		this.grid = new GridPane();
		this.grid.setHgap(10);
		this.grid.setMaxWidth(Double.MAX_VALUE);
		this.grid.setAlignment(Pos.CENTER_LEFT);
		
		dialogPane.contentTextProperty().addListener(o -> updateGrid());
		
		setTitle("");
		dialogPane.setHeaderText("");
		dialogPane.getStyleClass().add("text-input-dialog");
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		updateGrid();
		
		setResultConverter((dialogButton) -> {
			final var data = dialogButton == null ? null : dialogButton.getButtonData();
			return data == ButtonData.OK_DONE ? textField.getText() : null;
		});
	}
	
	private Label createContentLabel(final String text){
		final var label = new Label(text);
		label.setMaxWidth(Double.MAX_VALUE);
		label.setMaxHeight(Double.MAX_VALUE);
		label.getStyleClass().add("content");
		label.setWrapText(true);
		label.setPrefWidth(360);
		return label;
	}
	
	private void updateGrid(){
		grid.getChildren().clear();
		
		grid.add(label, 0, 0);
		grid.add(textField, 1, 0);
		getDialogPane().setContent(grid);
		
		Platform.runLater(textField::requestFocus);
	}
}