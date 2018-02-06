package be.rmangels.skeleton.infrastructure.specification;

public class OrSpecification<T> implements Specification<T> {

    static <T> OrSpecification<T> or(Specification<T> left, Specification<T> right) {
        return new OrSpecification<>(left, right);
    }

    private final Specification<T> left;
    private final Specification<T> right;

    private OrSpecification(Specification<T> left, Specification<T> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean isSatisfiedBy(T t) {
        return left.isSatisfiedBy(t) || right.isSatisfiedBy(t);
    }
}
