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

import com.goodow.realtime.channel.server.impl.VertxPlatform;
import com.goodow.realtime.core.Handler;
import com.goodow.realtime.json.Json;
import com.goodow.realtime.json.JsonArray;
import com.goodow.realtime.json.JsonObject;
import com.goodow.realtime.store.impl.MemoryStore;

import org.junit.Test;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

public class CollaborativeMapTest extends TestVerticle {
  Model mod;
  CollaborativeMap map;

  @Override
  public void start() {
    initialize();
    VertxPlatform.register(vertx);

    Store store = new MemoryStore();
    store.load("docId", new Handler<Document>() {
      @Override
      public void handle(Document doc) {
        mod = doc.getModel();
        map = mod.createMap(null);

        startTests();
      }
    }, null, null);
  }

  @Test
  public void testClear() {
    map.set("k1", "v1");
    map.set("k2", "v2");
    map.clear();
    VertxAssert.assertEquals(0, map.size());

    VertxAssert.testComplete();
  }

  @Test
  public void testEventHandler() {
    final JsonArray events = Json.createArray();
    map.onValueChanged(new Handler<ValueChangedEvent>() {
      @Override
      public void handle(ValueChangedEvent event) {
        VertxAssert.assertEquals(EventType.VALUE_CHANGED, event.type());
        VertxAssert.assertTrue(event.isLocal());
        events.push(event);
      }
    });
    map.onObjectChanged(new Handler<ObjectChangedEvent>() {
      @Override
      public void handle(ObjectChangedEvent event) {
        CollaborativeStringTest.assertArraySame(events, event.events());
        VertxAssert.assertEquals("a", events.<ValueChangedEvent>get(0).property());
        VertxAssert.assertEquals(null, events.<ValueChangedEvent>get(0).oldValue());
        VertxAssert.assertEquals(3.0, events.<ValueChangedEvent>get(0).newValue());
        VertxAssert.assertEquals("a", events.<ValueChangedEvent>get(1).property());
        VertxAssert.assertEquals(3.0, events.<ValueChangedEvent>get(1).oldValue());
        VertxAssert.assertEquals(Json.createObject().set("a", true).set("b", 1.0), events
            .<ValueChangedEvent>get(1).newValue());
        VertxAssert.assertEquals("a", events.<ValueChangedEvent>get(2).property());
        VertxAssert.assertEquals(Json.createObject().set("a", true).set("b", 1.0), events
            .<ValueChangedEvent>get(2).oldValue());
        VertxAssert.assertEquals(null, events.<ValueChangedEvent>get(2).newValue());

        VertxAssert.assertEquals("x", events.<ValueChangedEvent>get(5).property());
        VertxAssert.assertEquals(null, events.<ValueChangedEvent>get(5).oldValue());
        VertxAssert.assertEquals(true, events.<ValueChangedEvent>get(5).newValue());

        VertxAssert.testComplete();
      }
    });
    map.set("a", 3);
    map.set("a", Json.createObject().set("a", true).set("b", 1.0));
    map.remove("a");

    CollaborativeMap map2 = mod.createMap(null);
    CollaborativeList list = mod.createList(null);
    list.push(map2);
    map.set("c", list);
    list.onValuesAdded(new Handler<ValuesAddedEvent>() {
      @Override
      public void handle(ValuesAddedEvent event) {
        events.push(event);
      }
    });
    map2.onValueChanged(new Handler<ValueChangedEvent>() {
      @Override
      public void handle(ValueChangedEvent event) {
        events.push(event);
      }
    });
    map2.set("x", true);
  }

  @Test
  public void testIllegalArgumentException() {
    try {
      map.set(null, "");
      VertxAssert.fail();
    } catch (IllegalArgumentException e) {
    }
    VertxAssert.testComplete();
  }

  @Test
  public void testInitialize() {
    VertxAssert.assertEquals(0, map.size());
    VertxAssert.assertTrue(map.isEmpty());

    JsonObject v4 = Json.createObject();
    v4.set("subKey", "subValue");
    JsonObject initialValue = Json.createObject();
    initialValue.set("k1", "v1").set("k2", 2).set("k3", true).set("k4", v4);
    map = mod.createMap(initialValue);
    VertxAssert.assertEquals(4, map.size());
    VertxAssert.assertEquals("v1", map.get("k1"));
    VertxAssert.assertEquals(2d, (Double) map.get("k2"), 0d);
    VertxAssert.assertEquals(true, map.get("k3"));
    VertxAssert.assertTrue(v4.toJsonString().equals(map.<JsonObject> get("k4").toJsonString()));
    VertxAssert.assertFalse(map.isEmpty());

    VertxAssert.testComplete();
  }

  @Test
  public void testItems() {
    map.set("k1", "v1");
    map.set("k2", "v2");
    JsonArray items = map.items();
    VertxAssert.assertEquals("k1", items.getArray(0).getString(0));
    VertxAssert.assertEquals("v1", items.getArray(0).getString(1));
    VertxAssert.assertEquals("k2", items.getArray(1).getString(0));
    VertxAssert.assertEquals("v2", items.getArray(1).getString(1));

    VertxAssert.testComplete();
  }

  @Test
  public void testRemove() {
    map.set("k1", "v1");
    VertxAssert.assertEquals("v1", map.remove("k1"));
    VertxAssert.assertNull(map.remove("k2"));
    VertxAssert.assertEquals(0, map.size());

    VertxAssert.testComplete();
  }

  @Test
  public void testSet() {
    JsonArray v4 = Json.createArray();
    v4.push("abc");
    VertxAssert.assertNull(map.set("k1", "v1"));
    VertxAssert.assertNull(map.set("k2", 4));
    VertxAssert.assertNull(map.set("k3", false));
    VertxAssert.assertNull(map.set("k4", v4));
    VertxAssert.assertNull(map.set("k5", null));
    VertxAssert.assertNull(map.set("k6", null));

    VertxAssert.assertEquals(4, map.size());
    VertxAssert.assertEquals("v1", map.get("k1"));
    VertxAssert.assertEquals(4d, map.<Double> get("k2"), 0d);
    VertxAssert.assertEquals(false, map.get("k3"));
    VertxAssert.assertTrue(v4.toJsonString().equals(map.<JsonArray> get("k4").toJsonString()));

    VertxAssert.assertEquals("v1", map.set("k1", ""));
    VertxAssert.assertEquals(4.0, map.set("k2", null));
    VertxAssert.assertEquals(false, map.set("k3", null));
    VertxAssert.assertEquals(2, map.size());
    VertxAssert.assertEquals("", map.get("k1"));
    VertxAssert.assertFalse(map.has("k2"));
    VertxAssert.assertFalse(map.has("k3"));

    VertxAssert.testComplete();
  }

  @Test
  public void testValues() {
    map.set("k1", "v1");
    map.set("k2", "v2");
    JsonArray values = map.values();
    VertxAssert.assertEquals("v1", values.get(0));
    VertxAssert.assertEquals("v2", values.get(1));

    VertxAssert.testComplete();
  }
}
