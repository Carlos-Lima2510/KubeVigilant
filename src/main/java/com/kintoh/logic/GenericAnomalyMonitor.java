package com.kintoh.logic;

import com.kintoh.domain.Event;
import com.kintoh.domain.Monitor;
import com.kintoh.domain.Resource;
import com.kintoh.domain.Anomaly;

import java.util.Optional;

public class GenericAnomalyMonitor implements Monitor<Resource> {

    public Optional<Event> check(Resource resource) {
        
        Optional<Anomaly> technicalAnomaly = resource.getCriticalAnomaly();

        return technicalAnomaly.map(anomalyData -> 
            new Event(anomalyData.severity(), anomalyData, resource)
        );
    }
}