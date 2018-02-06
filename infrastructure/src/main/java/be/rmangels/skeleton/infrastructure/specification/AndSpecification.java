package be.rmangels.skeleton.infrastructure.specification;

public class AndSpecification<T> implements Specification<T> {

    static <T> AndSpecification<T> and(Specification<T> left, Specification<T> right) {
        return new AndSpecification<>(left, right);
    }

    private final Specification<T> left;
    private final Specification<T> right;

    private AndSpecification(Specification<T> left, Specification<T> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean isSatisfiedBy(T t) {
        return left.isSatisfiedBy(t) && right.isSatisfiedBy(t);
    }
}
