/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.vaadin.customgrid.client;

import org.vaadin.customgrid.CheckboxRenderer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.client.connectors.ClickableRendererConnector;
import com.vaadin.client.renderers.ClickableRenderer;
import com.vaadin.client.ui.VCheckBox;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.shared.ui.Connect;

import elemental.json.JsonObject;

/**
 * Connector for the CheckboxRenderer class.
 */
@Connect(CheckboxRenderer.class)
public class CheckboxRendererConnector extends
        ClickableRendererConnector<Boolean> {

    public CheckboxRendererConnector() {
    }

    @Override
    public CheckboxRenderer getRenderer() {
        return (CheckboxRenderer) super.getRenderer();
    }

    @Override
    protected HandlerRegistration addClickHandler(
            ClickableRenderer.RendererClickHandler<JsonObject> handler) {
        return getRenderer().addClickHandler(handler);
    }

    /**
     * Client-side implementation of the clickable checkbox renderer
     */
    public static class CheckboxRenderer extends
            ClickableRenderer<Boolean, VCheckBox> {

        public CheckboxRenderer() {
            super();
        }

        @Override
        public VCheckBox createWidget() {
            VCheckBox b = GWT.create(VCheckBox.class);
            b.addClickHandler(this);
            b.setStylePrimaryName("v-checkbox");
            return b;
        }

        @Override
        public void render(RendererCellReference cell, Boolean value,
                VCheckBox checkBox) {
            checkBox.setValue(value, false);
        }

        @Override
        public void onClick(ClickEvent event) {
            // VCheckBox sends two clicks when clicked by the label. Don't send
            // duplicate events to server.
            if (Element.as(event.getNativeEvent().getEventTarget())
                    .getTagName().equalsIgnoreCase("input")) {
                super.onClick(event);
            }

            // Don't let grid handle the events.
            event.stopPropagation();
        }
    }
}
