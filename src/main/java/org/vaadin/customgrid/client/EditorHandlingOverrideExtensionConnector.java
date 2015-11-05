package org.vaadin.customgrid.client;

import org.vaadin.customgrid.CustomGridUI.EditorHandlingOverrideExtension;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.connectors.GridConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.widget.grid.DefaultEditorEventHandler;
import com.vaadin.client.widgets.Grid;
import com.vaadin.client.widgets.Grid.EditorDomEvent;
import com.vaadin.shared.ui.Connect;

import elemental.json.JsonObject;

/**
 * Custom editor event handling logic
 */
@Connect(EditorHandlingOverrideExtension.class)
public class EditorHandlingOverrideExtensionConnector extends AbstractExtensionConnector {

	private final class CustomEventHandler extends DefaultEditorEventHandler<JsonObject> {

		@Override
		protected boolean isOpenEvent(EditorDomEvent<JsonObject> event) {
			String eventType = event.getDomEvent().getType();
			if (eventType.equals(BrowserEvents.DBLCLICK)) {
				// Don't open on double click
				return false;
			}
			return super.isOpenEvent(event)
					|| (eventType.equals(BrowserEvents.CLICK) && noModifiers(event.getDomEvent()));
		}

		private boolean noModifiers(Event e) {
			return !e.getAltKey() && !e.getCtrlKey() && !e.getMetaKey() && !e.getShiftKey();
		}

		@Override
		protected boolean handleMoveEvent(EditorDomEvent<JsonObject> event) {
			// Override most of handleMoveEvent, since the changes are big.
			Event e = event.getDomEvent();
			int row = event.getRowIndex();
			int col = event.getCell().getColumnIndexDOM();

			if (e.getType().equals(BrowserEvents.CLICK)) {
				if (!hasModifiers(e)) {
					editRow(event, row, col);
				}
				return true;
			}

			if (e.getType().equals(BrowserEvents.KEYDOWN)) {

				boolean change = false;
				// Move with ctrl/meta + arrow keys
				if (e.getKeyCode() == KeyCodes.KEY_DOWN && (e.getCtrlKey() || e.getMetaKey())) {
					++row;
					change = true;
				} else if (e.getKeyCode() == KeyCodes.KEY_UP && (e.getCtrlKey() || e.getMetaKey())) {
					--row;
					change = true;
				} else if (e.getKeyCode() == KeyCodes.KEY_RIGHT && (e.getCtrlKey() || e.getMetaKey())) {
					++col;
					change = true;
				} else if (e.getKeyCode() == KeyCodes.KEY_LEFT && (e.getCtrlKey() || e.getMetaKey())) {
					--col;
					change = true;
				} else if (e.getKeyCode() == KeyCodes.KEY_TAB) {
					// Manage Tab to handle focus and scrolling gracefully.
					boolean isLastColumn = event.getFocusedColumnIndex() == event.getGrid().getVisibleColumns().size()
							- 1;
					boolean isFirstColumn = event.getFocusedColumnIndex() > 0;
					if ((e.getShiftKey() && isFirstColumn) || (!e.getShiftKey() && isLastColumn)) {
						return super.handleMoveEvent(event);
					}
				}

				if (change) {
					editRow(event, row, col);
					e.preventDefault();
				}
				return change;

			} else if (e.getType().equals(BrowserEvents.KEYPRESS) || e.getType().equals(BrowserEvents.KEYUP)) {
				// Don't handle any other key events.
				return false;
			}

			// Touch move events are handled as the default does it.
			return super.handleMoveEvent(event);
		}

		private boolean hasModifiers(Event e) {
			return e.getAltKey() || e.getCtrlKey() || e.getMetaKey() || e.getShiftKey();
		}
	}

	private Grid<JsonObject> grid;

	@Override
	protected void extend(ServerConnector target) {
		grid = getParent().getWidget();
		grid.getEditor().setEventHandler(new CustomEventHandler());
	}

	@Override
	public void onUnregister() {
		grid.getEditor().setEventHandler(new DefaultEditorEventHandler<JsonObject>());

		super.onUnregister();
	}

	@Override
	public GridConnector getParent() {
		return (GridConnector) super.getParent();
	}

}
