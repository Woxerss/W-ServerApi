package com.woxerss.wserverapi.events;

import java.sql.Timestamp;

// gson-2.9.0
import com.google.gson.JsonObject;

public class CustomEvent {
    String type;
    String value;
    Long timestamp;
    
    public CustomEvent(String type, String value) {
        Timestamp tstamp = new Timestamp(System.currentTimeMillis());
        this.type = type;
        this.value = value;
        this.timestamp = tstamp.getTime();
    }

    public JsonObject toJson() {
        JsonObject eventJson = new JsonObject();

        eventJson.addProperty("type", type);
        eventJson.addProperty("value", value);
        eventJson.addProperty("timestamp", timestamp / 1000);   // Делим на 1000 чтобы отрезать микросекунды

        return eventJson;
    }
}
