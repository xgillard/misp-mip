package com.github.xgillard.mispmip;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Graph {
    public final int         nb_vars;
    public final int[]       weights;
    public final boolean[][] adj_matrix;

    private Graph(final int n) {
        this.nb_vars    = n;
        this.weights    = new int[n];
        this.adj_matrix = new boolean[n][n];
    }

    private void setWeight(final int i, final int val) {
        this.weights[i] = val;
    }
    private void recordAdj(final int i, final int j) {
        this.adj_matrix[i][j] = true;
        this.adj_matrix[j][i] = true;
    }

    public Graph complement() {
        Graph g   = new Graph(nb_vars);
        System.arraycopy(weights, 0, g.weights, 0, nb_vars);
        for (int i=0; i<nb_vars; i++) {
            for(int j = 0; j < nb_vars; j++) {
                if (i==j) { continue;}
                g.adj_matrix[i][j] = !g.adj_matrix[i][j];
            }
        }
        return g;
    }

    public static Graph fromFile(final String fname) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(fname))) {
            Graph g = new Graph(0);
            Pattern comment = Pattern.compile("^c\\s.*$");
            Pattern pbDecl  = Pattern.compile("^p\\s+edge\\s+(?<vars>\\d+)\\s+(?<edges>\\d+)$");
            Pattern nodeDecl= Pattern.compile("^n\\s+(?<node>\\d+)\\s+(?<weight>-?\\d+)");
            Pattern edgeDecl= Pattern.compile("^e\\s+(?<src>\\d+)\\s+(?<dst>\\d+)");

            String line = null;
            while ((line = in.readLine())!=null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                
                Matcher capture = comment.matcher(line);
                if (capture.matches()) {
                    continue;
                }

                capture = pbDecl.matcher(line);
                if (capture.matches()) {
                    int n = Integer.parseInt(capture.group("vars"));
                    g = new Graph(n);

                    for (int i = 0 ; i < n ; i++) {
                        g.setWeight(i, 1);
                    }

                    continue;
                }
                capture = nodeDecl.matcher(line);
                if (capture.matches()) {
                    int n = Integer.parseInt(capture.group("node"));
                    int w = Integer.parseInt(capture.group("weight"));
                    g.weights[n-1] = w;
                    continue;
                }
                capture = edgeDecl.matcher(line);
                if (capture.matches()) {
                    int src = Integer.parseInt(capture.group("src")) - 1;
                    int dst = Integer.parseInt(capture.group("dst")) - 1;
                    g.recordAdj(src, dst);
                    
                    continue;
                }

                throw new RuntimeException("Ill formatted");
            }

            return g;
        }
    }
}