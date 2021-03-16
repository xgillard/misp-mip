package com.github.xgillard.mispmip;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

public class Model {
    private final Graph    graph;

    private final GRBEnv   env;
    public  final GRBModel model;

    private final GRBVar[] vars;

    public Model(final Graph g) throws GRBException {
        this.graph = g;
        this.env   = new GRBEnv("misp.log");
        this.model = new GRBModel(this.env);
        this.vars  = new GRBVar[g.nb_vars];

        model.set(GRB.IntAttr.ModelSense, -1); // maximization

        for (int i = 0; i < g.nb_vars; i++) {
            this.vars[i] = this.model.addVar(0,1,g.weights[i], GRB.BINARY, "x"+i);
        }
        for (int i = 0; i < g.nb_vars; i++) {
            for (int j = 0; j < g.nb_vars; j++) {
                if (i == j) { continue; }
                if (g.adj_matrix[i][j]) {
                    GRBLinExpr expr = new GRBLinExpr();
                    expr.addTerm(1, this.vars[i]);
                    expr.addTerm(1, this.vars[j]);

                    this.model.addConstr(expr, GRB.LESS_EQUAL, Math.max(g.weights[i], g.weights[j]), "constraint "+i+" or "+ j);
                }
            }
        }
    }

    public void solve(int timeLimit, int threads) throws GRBException {
        if (timeLimit > 0) model.set(GRB.DoubleParam.TimeLimit, timeLimit);
        if (threads   > 0) model.set(GRB.IntParam.Threads, threads);
        model.optimize();
    }

    public double gap() throws GRBException {
        return model.get(GRB.DoubleAttr.MIPGap);
    }

    public double runTime() throws GRBException {
        return model.get(GRB.DoubleAttr.Runtime);
    }

    public double objVal() throws GRBException {
        return model.get(GRB.DoubleAttr.ObjVal);
    }

    public int[] solution() throws GRBException {
        int[] result = new int[graph.nb_vars];

        for (int i = 0; i < graph.nb_vars; i++) {
            result[i] = (int) Math.rint(vars[i].get(GRB.DoubleAttr.X));
        }

        return result;
    }
}
