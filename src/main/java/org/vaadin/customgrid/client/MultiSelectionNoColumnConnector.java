package org.vaadin.customgrid.client;

import org.vaadin.customgrid.CustomGridUI.MultiSelectionNoColumnModel;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.connectors.MultiSelectionModelConnector;
import com.vaadin.client.renderers.ComplexRenderer;
import com.vaadin.client.widget.grid.EventCellReference;
import com.vaadin.client.widget.grid.selection.SelectionModel.Multi;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.ui.Connect;

import elemental.json.JsonObject;

/**
 * Connector for multiple selection model without selection column.
 */
@Connect(MultiSelectionNoColumnModel.class)
public class MultiSelectionNoColumnConnector extends MultiSelectionModelConnector {

	public class MultiSelectionNoColumnModel extends MultiSelectionModel {
		@Override
		protected ComplexRenderer<Boolean> createSelectionColumnRenderer(Grid<JsonObject> grid) {
			// No selection column renderer.
			return null;
		}
	}

	@Override
	protected Multi<JsonObject> createSelectionModel() {
		return new MultiSelectionNoColumnModel();
	}

	@Override
	protected void extend(ServerConnector target) {
		super.extend(target);

		// Listen to context menu events and do selection
		getGrid().addDomHandler(new ContextMenuHandler() {

			@Override
			public void onContextMenu(ContextMenuEvent event) {
				EventCellReference<JsonObject> cell = getGrid().getEventCell();

				JsonObject row = cell.getRow();
				if (cell.isBody() && row != null) {
					if (getGrid().isSelected(row)) {
						getGrid().deselect(row);
					} else {
						getGrid().select(row);
					}
					event.preventDefault();
				}
			}
		}, ContextMenuEvent.getType());
	}

}
