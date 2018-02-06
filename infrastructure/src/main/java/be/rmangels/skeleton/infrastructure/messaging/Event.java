package be.rmangels.skeleton.infrastructure.messaging;

import be.rmangels.skeleton.infrastructure.ddd.Id;
import be.rmangels.skeleton.infrastructure.ddd.ValueObject;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public abstract class Event<ID extends Id> extends ValueObject {

    @Valid
    @NotNull
    protected ID aggregateId;

    public ID getAggregateId() {
        return aggregateId;
    }
}
