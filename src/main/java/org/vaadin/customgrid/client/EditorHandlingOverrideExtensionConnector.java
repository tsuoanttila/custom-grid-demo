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
public class EditorHandlingOverrideExtensionConnector
        extends AbstractExtensionConnector {

    private Grid<JsonObject> grid;

    @Override
    protected void extend(ServerConnector target) {
        grid = getParent().getWidget();
        grid.getEditor()
                .setEventHandler(new DefaultEditorEventHandler<JsonObject>() {

                    @Override
                    protected boolean handleOpenEvent(
                            EditorDomEvent<JsonObject> event) {
                        Event e = event.getDomEvent();
                        if (e.getType().equals(BrowserEvents.CLICK)) {
                            editRow(event, event.getCell().getRowIndex(),
                                    event.getCell().getColumnIndexDOM());
                            return true;
                        } else if (e.getType().equals(BrowserEvents.DBLCLICK)) {
                            // On double click don't do anything.
                            return false;
                        }

                        // Default opening handling for all the other cases.
                        return super.handleOpenEvent(event);
                    }

                    @Override
                    protected boolean handleMoveEvent(
                            EditorDomEvent<JsonObject> event) {
                        Event e = event.getDomEvent();
                        if (e.getType().equals(BrowserEvents.KEYDOWN)) {
                            int row = event.getRowIndex();
                            int col = event.getCell().getColumnIndexDOM();

                            boolean change = false;
                            // Move with ctrl/meta + arrow keys
                            if (e.getKeyCode() == KeyCodes.KEY_DOWN
                                    && (e.getCtrlKey() || e.getMetaKey())) {
                                ++row;
                                change = true;
                            } else if (e.getKeyCode() == KeyCodes.KEY_UP
                                    && (e.getCtrlKey() || e.getMetaKey())) {
                                --row;
                                change = true;
                            } else if (e.getKeyCode() == KeyCodes.KEY_RIGHT
                                    && (e.getCtrlKey() || e.getMetaKey())) {
                                ++col;
                                change = true;
                            } else if (e.getKeyCode() == KeyCodes.KEY_LEFT
                                    && (e.getCtrlKey() || e.getMetaKey())) {
                                --col;
                                change = true;
                            } else if (e.getKeyCode() == KeyCodes.KEY_TAB) {
                                // Manage Tab to handle focus and scrolling
                                // gracfully.
                                boolean isLastColumn = event
                                        .getFocusedColumnIndex() == event
                                                .getGrid().getVisibleColumns()
                                                .size() - 1;
                                boolean isFirstColumn = event
                                        .getFocusedColumnIndex() > 0;
                                if ((e.getShiftKey() && isFirstColumn)
                                        || (!e.getShiftKey() && isLastColumn)) {
                                    return super.handleMoveEvent(event);
                                }
                            }

                            if (change) {
                                editRow(event, row, col);
                                e.preventDefault();
                            }
                            return change;

                        } else if (e.getType().equals(BrowserEvents.KEYPRESS)
                                || e.getType().equals(BrowserEvents.KEYUP)) {
                            // Don't handle any other key events.
                            return false;
                        }

                        // Mouse move events are handled as the default does it.
                        return super.handleMoveEvent(event);
                    }

                });
    }

    @Override
    public void onUnregister() {
        grid.getEditor()
                .setEventHandler(new DefaultEditorEventHandler<JsonObject>());

        super.onUnregister();
    }

    @Override
    public GridConnector getParent() {
        return (GridConnector) super.getParent();
    }

}
