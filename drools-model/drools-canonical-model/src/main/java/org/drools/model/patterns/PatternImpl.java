package org.drools.model.patterns;

import org.drools.model.*;
import org.drools.model.constraints.AbstractConstraint;
import org.drools.model.functions.Function1;
import org.drools.model.impl.DataSourceDefinitionImpl;

import java.util.*;

public class PatternImpl<T> extends AbstractSinglePattern implements Pattern<T> {

    private final Variable<T> variable;
    private Variable[] inputVariables;
    private final DataSourceDefinition dataSourceDefinition;
    private Constraint constraint;
    private Map<Variable, Function1<T, ?>> bindings;

    public PatternImpl(Variable<T> variable) {
        this(variable, SingleConstraint.EMPTY, DataSourceDefinitionImpl.DEFAULT);
    }

    public PatternImpl(Variable<T> variable, Constraint constraint, DataSourceDefinition dataSourceDefinition) {
        this.variable = variable;
        this.constraint = constraint;
        this.dataSourceDefinition = dataSourceDefinition;
    }

    @Override
    public DataSourceDefinition getDataSourceDefinition() {
        return dataSourceDefinition;
    }

    @Override
    public Variable<T> getPatternVariable() {
        return variable;
    }

    @Override
    public Variable<?>[] getBoundVariables() {
        return new Variable[] { variable };
    }

    @Override
    public Variable[] getInputVariables() {
        if (inputVariables == null) {
            this.inputVariables = collectInputVariables();
        }
        return inputVariables;
    }

    @Override
    public Constraint getConstraint() {
        return constraint;
    }

    public void addConstraint( Constraint constraint ) {
        this.constraint = ( (AbstractConstraint) this.constraint ).and( constraint );
    }

    public void addBinding(Variable boundVar, Function1<T, ?> func) {
        if (bindings == null) {
            bindings = new HashMap<>();
        }
        bindings.put(boundVar, func);
    }

    public Map<Variable, Function1<T, ?>> getBindings() {
        return bindings != null ? bindings : Collections.emptyMap();
    }

    private Variable[] collectInputVariables() {
        Set<Variable> varSet = new HashSet<Variable>();
        collectInputVariables(constraint, varSet);
        return varSet.toArray(new Variable[varSet.size()]);
    }

    private void collectInputVariables(Constraint constraint, Set<Variable> varSet) {
        if (constraint instanceof SingleConstraint) {
            for (Variable var : ((SingleConstraint)constraint).getVariables()) {
                varSet.add(var);
            }
        } else {
            for (Constraint child : constraint.getChildren()) {
                collectInputVariables(child, varSet);
            }
        }
    }
}