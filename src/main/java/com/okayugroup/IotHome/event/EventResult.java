package com.okayugroup.IotHome.event;

public record EventResult(Event.EventType type, int endCode, Object message){

}