package org.vaadin.customgrid;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.AbstractGridExtension;
import com.vaadin.ui.Grid.MultiSelectionModel;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Widgetset("org.vaadin.customgrid.CustomGridWidgetset")
@Theme("customgrid")
public class CustomGridUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = CustomGridUI.class)
	public static class Servlet extends VaadinServlet {
	}

	/**
	 * Server-side of the extension to change the editor event handling
	 */
	public static class EditorHandlingOverrideExtension extends AbstractGridExtension {
		private EditorHandlingOverrideExtension(Grid grid) {
			super(grid);
		}

		public static EditorHandlingOverrideExtension extend(Grid grid) {
			return new EditorHandlingOverrideExtension(grid);
		}
	}

	/**
	 * Multiple selection model without selection column.
	 */
	public static class MultiSelectionNoColumnModel extends MultiSelectionModel {
	}

	private Grid grid;
	final VerticalLayout layout = new VerticalLayout();

	@Override
	protected void init(VaadinRequest request) {
		VerticalLayout baseLayout = new VerticalLayout();
		baseLayout.setMargin(true);
		baseLayout.setSpacing(true);
		baseLayout.addComponent(new Label(
				"Customised Grid with special Editor event handling and multi selection model without selection column. Editor can be moved around with Ctrl/Cmd + Arrow keys."));
		layout.setSpacing(true);
		baseLayout.addComponent(layout);
		setContent(baseLayout);

		layout.addComponent(new Button("Replace Grid", e -> createNewGrid()));
		layout.addComponent(new Button("Set Editor Event Handler", e -> EditorHandlingOverrideExtension.extend(grid)));

		createNewGrid();
		EditorHandlingOverrideExtension.extend(grid);
	}

	private void createNewGrid() {
		Grid grid = new Grid();

		grid.setEditorEnabled(true);
		grid.setEditorBuffered(false);

		grid.addColumn("name");
		CheckBox editorCheckbox = new CheckBox();
		editorCheckbox.addStyleName("with-margin");
		grid.addColumn("status", Boolean.class).setHeaderCaption("Works?").setRenderer(new CheckboxRenderer())
				.setEditorField(editorCheckbox);
		grid.setSelectionModel(new MultiSelectionNoColumnModel());

		grid.addRow("Unbuffered editor", true);
		grid.addRow("Editor event handling", true);
		grid.addRow("MultiSelection without selection column", true);
		grid.addRow("Shift selection", false);
		grid.addRow("Column drag resize", false);

		if (this.grid != null) {
			layout.replaceComponent(this.grid, grid);
		} else {
			layout.addComponentAsFirst(grid);
		}
		this.grid = grid;
	}

}