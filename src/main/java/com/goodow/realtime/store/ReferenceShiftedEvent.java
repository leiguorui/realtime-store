/*
 * Copyright 2012 Goodow.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.goodow.realtime.store;

import com.goodow.realtime.json.JsonObject;
import com.goodow.realtime.store.util.ModelFactory;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;

/**
 * Event fired when an index reference shifts
 */
@ExportPackage(ModelFactory.PACKAGE_PREFIX_REALTIME)
@Export(all = true)
public class ReferenceShiftedEvent extends BaseModelEvent {
  /**
   * The new index.
   */
  public final int newIndex;
  /**
   * The previous index.
   */
  public final int oldIndex;

  /**
   * @param serialized The serialized event object.
   */
  public ReferenceShiftedEvent(JsonObject serialized) {
    super(serialized.set("type", EventType.REFERENCE_SHIFTED.name()).set("bubbles", false));
    this.oldIndex = (int) serialized.getNumber("oldIndex");
    this.newIndex = (int) serialized.getNumber("newIndex");
  }

  public int getNewIndex() {
    return newIndex;
  }

  public int getOldIndex() {
    return oldIndex;
  }
}