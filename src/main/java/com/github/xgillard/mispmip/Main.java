package com.github.xgillard.mispmip;
import gurobi.GRBException;
/*
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
*/

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public final class Main {

    public static void main(String[] args) throws GRBException, IOException {
        //CommandLine cli = cli(args);

        if (args.length != 3) {
            System.err.println("Usage mispmip <INSTANCE> <duration> <threads>");
        }

        String inst     = args[0];
        String duration = args[1];
        String threads  = args[2];

        Graph g = Graph.fromFile(inst);
        Model m = new Model(g);
        m.solve(Integer.parseInt(duration), Integer.parseInt(threads));
        showResult(inst, m);
    }

    private static void showResult(String fname, Model mip) throws GRBException {
        String inst   = new File(fname).getName();
        String status = "Timeout";
        if (mip.gap() == 0.0) {
            status = "Proved";
        }

        double lb = mip.objVal();
        double ub = lb + (lb * mip.gap());
        double rt = mip.runTime();

        String sol= Arrays.toString(mip.solution());

        System.out.println(
                String.format("%-40s | %-10s | %-10d | %-10d | %10.3f | %s",
                        inst, status, (int) Math.rint(lb), (int) Math.rint(ub), rt, sol));

    }
/*
    private static CommandLine cli(String[] args) throws ParseException {
        final CommandLineParser parser = new DefaultParser();
        final CommandLine cli = parser.parse(opts(), args);

        return cli;
    }

    private static Options opts() {
        Option instance = Option.builder("inst")
            .argName("inst")
            .hasArg()
            .required()
            .build();

        Option threads = Option.builder("t")
                .argName("threads")
                .longOpt("threads")
                .hasArg()
                .required(false)
                .build();

        Option duration = Option.builder("d")
                .argName("duration")
                .longOpt("duration")
                .hasArg()
                .required(false)
                .build();

        Options opts = new Options();
        opts.addOption(instance);
        opts.addOption(threads);
        opts.addOption(duration);
        return opts;
    }
*/
}