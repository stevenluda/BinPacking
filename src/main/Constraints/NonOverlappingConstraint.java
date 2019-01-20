package main.Constraints;

public class NonOverlappingConstraint extends Constraint {

    @Override
    public boolean isFeasible() {
        return false;
    }
}
