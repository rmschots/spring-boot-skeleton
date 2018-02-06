package be.rmangels.skeleton.iface.user;

import be.rmangels.skeleton.infrastructure.ddd.ValueObject;

import java.io.Serializable;

public final class UserId extends ValueObject implements Serializable {

    public static UserId of(String uid) {
        return new UserId(uid);
    }

    private final String uid;

    private UserId() {
        uid = null;
    }

    private UserId(String uid) {
        this.uid = uid;
    }

    public String getValue() {
        return uid;
    }

}
