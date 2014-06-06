/*
 * Copyright 2014 Goodow.com
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

import com.goodow.realtime.json.JsonArray;
import com.google.gwt.core.client.js.JsInterface;
import com.google.gwt.core.client.js.JsProperty;

//@JsInterface
/**
 * Event fired when items are removed from a collaborative list.
 */
public interface ValuesRemovedEvent extends BaseModelEvent {
  @JsProperty
  /* The index of the first removed value. */
  int index();

  @JsProperty
  /* The values that were removed. */
  JsonArray values();
}
