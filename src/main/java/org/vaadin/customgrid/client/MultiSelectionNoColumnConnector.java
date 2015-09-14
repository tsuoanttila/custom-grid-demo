package org.vaadin.customgrid.client;

import org.vaadin.customgrid.CustomGridUI.MultiSelectionNoColumnModel;

import com.vaadin.client.connectors.MultiSelectionModelConnector;
import com.vaadin.client.renderers.ComplexRenderer;
import com.vaadin.client.widget.grid.selection.SelectionModel.Multi;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.ui.Connect;

import elemental.json.JsonObject;

/**
 * Connector for multiple selection model without selection column.
 */
@Connect(MultiSelectionNoColumnModel.class)
public class MultiSelectionNoColumnConnector extends
        MultiSelectionModelConnector {

    @Override
    protected Multi<JsonObject> createSelectionModel() {
        return new MultiSelectionNoColumnModel();
    }

    public class MultiSelectionNoColumnModel extends MultiSelectionModel {

        @Override
        protected ComplexRenderer<Boolean> createSelectionColumnRenderer(
                Grid<JsonObject> grid) {
            // No selection column renderer.
            return null;
        }

    }

}
