package be.rmangels.skeleton.infrastructure.messaging;

public interface CommandHandler {

    boolean canHandle(Command<?> command);

    void handle(Command<?> command);
}
