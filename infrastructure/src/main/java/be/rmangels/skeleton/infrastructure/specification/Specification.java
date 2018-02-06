package be.rmangels.skeleton.infrastructure.specification;

public interface Specification<T> {

    boolean isSatisfiedBy(T t);

    default Specification<T> and(Specification<T> other) {
        return AndSpecification.and(this, other);
    }

    default Specification<T> or(Specification<T> other) {
        return OrSpecification.or(this, other);
    }
}
