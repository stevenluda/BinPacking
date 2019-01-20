package Constraints;

public class TotalWeightConstraint extends Constraint {
    int maxWeight;

    public TotalWeightConstraint(){}

    public TotalWeightConstraint(int maxWeight){
        this.maxWeight = maxWeight;
    }

    public boolean isFeasible(int currentWeight){
        return currentWeight <= maxWeight ? true : false;
    }

    @Override
    public boolean isFeasible() {
        return false;
    }
}
