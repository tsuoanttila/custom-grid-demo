package org.vaadin.customgrid;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.AbstractGridExtension;
import com.vaadin.ui.Grid.MultiSelectionModel;
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
    public static class EditorHandlingOverrideExtension extends
            AbstractGridExtension {
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

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        final Grid grid = new Grid();
        grid.setEditorEnabled(true);
        grid.setEditorBuffered(false);

        grid.addColumn("name");
        CheckBox editorCheckbox = new CheckBox();
        editorCheckbox.addStyleName("with-margin");
        grid.addColumn("status", Boolean.class).setHeaderCaption("Works?")
                .setRenderer(new CheckboxRenderer())
                .setEditorField(editorCheckbox);
        grid.setSelectionModel(new MultiSelectionNoColumnModel());
        EditorHandlingOverrideExtension.extend(grid);

        grid.addRow("Unbuffered editor", true);
        grid.addRow("Editor event handling", true);
        grid.addRow("MultiSelection without selection column", true);
        grid.addRow("Shift selection", false);
        grid.addRow("Column drag resize", false);

        layout.addComponent(grid);
    }

}