package be.rmangels.skeleton.infrastructure.messaging;

import be.rmangels.skeleton.infrastructure.ddd.Id;
import be.rmangels.skeleton.infrastructure.ddd.ValueObject;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public abstract class Command<ID extends Id> extends ValueObject {

    @Valid
    @NotNull
    protected ID aggregateId;
    @NotNull
    protected Integer version;

    public ID getAggregateId() {
        return aggregateId;
    }

    public int getVersion() {
        return version;
    }
}
