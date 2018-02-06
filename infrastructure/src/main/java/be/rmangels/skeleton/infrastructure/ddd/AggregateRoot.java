package be.rmangels.skeleton.infrastructure.ddd;

import be.rmangels.skeleton.infrastructure.messaging.Command;
import be.rmangels.skeleton.infrastructure.messaging.CommandHandler;
import be.rmangels.skeleton.infrastructure.messaging.Event;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@MappedSuperclass
public abstract class AggregateRoot<ID extends Id> extends BaseEntity<ID> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AggregateRoot.class);

    @Version
    @NotNull
    @Column(name= "VERSION")
    private int versie;
    @JsonIgnore
    @Transient
    private final List<CommandHandler> commandHandlers = newArrayList();
    @JsonIgnore
    @Transient
    private final List<Event<ID>> uncommittedEvents = newArrayList();

    protected AggregateRoot() {
        this.registerCommandHandlers();
    }

    public int getVersie() {
        return versie;
    }

    private void registerCommandHandlers() {
        this.commandHandlers.addAll(this.getCommandHandlers());
    }

    protected List<CommandHandler> getCommandHandlers() {
        return newArrayList();
    }

    public void execute(Command<?> command) {
        LOGGER.debug(String.format("Received command %s", command));
        checkVersion(command);
        commandHandlers.stream()
            .filter(commandHandler -> commandHandler.canHandle(command))
            .forEach(commandHandler -> commandHandler.handle(command));
    }

    private void checkVersion(Command<?> command) {
        if (this.versie != command.getVersion()) {
            throw new OptimisticLockException(String.format("Command with version %s tried to adapt aggregateroot %s with version %s. Command was %s.", command.getVersion(), this.getClass().getSimpleName(), getVersie(), command));
        }
    }

    public List<Event<ID>> getUncommittedEvents() {
        return uncommittedEvents;
    }

    void clearUncommittedEvents() {
        uncommittedEvents.clear();
    }

    protected void sendEvent(Event<ID> event) {
        this.uncommittedEvents.add(event);
    }
}
