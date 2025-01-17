package com.github.joonasvali.naturalmouse.support;

import com.github.joonasvali.naturalmouse.api.SpeedManager;
import com.github.joonasvali.naturalmouse.util.FlowTemplates;
import com.github.joonasvali.naturalmouse.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
public class DefaultSpeedManager implements SpeedManager {
  private static final double SMALL_DELTA = 10e-6;
  private final List<Flow> flows = new ArrayList<>();
  private long mouseMovementBaseTimeMs = 300;
  private long mouseMovementScaledTimeMs = 500;
  private long mouseMovementScaledDistance = 300;

  public DefaultSpeedManager(Collection<Flow> flows) {
    this.flows.addAll(flows);
  }

  public DefaultSpeedManager() {
    this(Arrays.asList(
        new Flow(FlowTemplates.constantSpeed()),
        new Flow(FlowTemplates.variatingFlow()),
        new Flow(FlowTemplates.interruptedFlow()),
        new Flow(FlowTemplates.interruptedFlow2()),
        new Flow(FlowTemplates.slowStartupFlow()),
        new Flow(FlowTemplates.slowStartup2Flow()),
        new Flow(FlowTemplates.adjustingFlow()),
        new Flow(FlowTemplates.jaggedFlow()),
        new Flow(FlowTemplates.stoppingFlow())
    ));
  }

  @Override
  public Pair<Flow, Long> getFlowWithTime(double distance) {
    double fittsTime = mouseMovementBaseTimeMs + mouseMovementScaledTimeMs * Math.log(1 + Math.E * (distance / (double) mouseMovementScaledDistance));
    double time = fittsTime + (long)(Math.random() * fittsTime);
    //double time = mouseMovementTimeMs + (long)(Math.random() * mouseMovementTimeMs);
    Flow flow = new Flow(flows.get((int) (Math.random() * flows.size())).getFlowCharacteristics());

    // Let's ignore waiting time, e.g 0's in flow, by increasing the total time
    // by the amount of 0's there are in the flow multiplied by the time each bucket represents.
    double timePerBucket = time / (double)flow.getFlowCharacteristics().length;
    for (double bucket : flow.getFlowCharacteristics()) {
      if (Math.abs(bucket - 0) < SMALL_DELTA) {
        time += timePerBucket;
      }
    }

    return new Pair<>(flow, (long)time);
  }

  public void setMouseMovementBaseTimeMs(long mouseMovementBaseTimeMs) {
    this.mouseMovementBaseTimeMs = mouseMovementBaseTimeMs;
  }
  public void setMouseMovementScaledTimeMs(long mouseMovementScaledTimeMs) {
    this.mouseMovementScaledTimeMs = mouseMovementScaledTimeMs;
  }
  public void setMouseMovementScaledDistance(long mouseMovementScaledDistance) {
    this.mouseMovementScaledDistance = mouseMovementScaledDistance;
  }
}
