package be.rmangels.skeleton.infrastructure.ddd;

import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.OffsetDateTime;

@MappedSuperclass
public abstract class BaseEntity<ID extends Id> {

    private static final String COLUMN_LAST_MODIFIED_DATE = "LAST_MODIFIED_DATE";

    @LastModifiedDate
    @Column(name = COLUMN_LAST_MODIFIED_DATE)
    private OffsetDateTime lastModifiedDate;

    public abstract ID getId();

    public OffsetDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity<?> that = (BaseEntity<?>) o;
        return java.util.Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {

        return java.util.Objects.hash(getId());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + getId().getValue();
    }
}

