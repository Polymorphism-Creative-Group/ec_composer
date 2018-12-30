/*
 * Copyright 2018 Jonathan Chang, Chun-yien <ccy@musicapoetica.org>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.metacontext.ec.prototype.composer;

import tech.metacontext.ec.prototype.composer.model.Composer;
import java.util.stream.IntStream;
import tech.metacontext.ec.prototype.composer.enums.ComposerAim;
import tech.metacontext.ec.prototype.composer.styles.*;

/**
 *
 * @author Jonathan Chang, Chun-yien <ccy@musicapoetica.org>
 */
public class Main {

    // 決定作品數量及演進世代
    private Composer composer;

    /**
     * Entry point of main.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        int POP_SIZE = 50;
        int SELECTED_SIZE = 10;
        int GENERATION = 1500;
        Main main = new Main(POP_SIZE, SELECTED_SIZE, GENERATION, Settings.DEFAULT);

        main.composer.render(Composer.RENDERTYPE_AVERAGELINECHART);
        main.composer.render(Composer.RENDERTYPE_SCATTERPLOT);
        System.out.println(header("Persisting Conservatory"));
        main.composer.persistAll();
    }

    /**
     * Main constructor.
     *
     * @param popSize
     * @param goalSize
     * @param generation
     * @param logState
     * @throws Exception
     */
    public Main(int popSize,
            int goalSize,
            int generation,
            int logState) throws Exception {

        this.composer = new Composer(popSize, ComposerAim.Phrase,
                logState,
                new UnaccompaniedCello(),
                new GoldenSectionClimax(UnaccompaniedCello.RANGE.keySet())
        );
        System.out.println(header("Evolutionary Computation"));
        System.out.printf("Composer = [%s]\n", composer.getId());
        System.out.println("Population size = " + popSize);
        System.out.println("Expected selected size = " + goalSize);
        System.out.println(header("Evolution"));
        int conserved = 0;
        do {
            if (composer.getGenCount() > 0) {
                if (composer.getGenCount() % 100 == 0) {
                    System.out.println(" (" + composer.getGenCount() + ")");
                } else if (composer.getGenCount() % 50 == 0) {
                    System.out.print("|");
                }
            }
            composer.compose().evolve();
            if (composer.getConservetory().size() > conserved) {
                System.out.print(composer.getConservetory().size() - conserved);
                conserved = composer.getConservetory().size();
            } else {
                System.out.print(".");
            }
        } while (composer.getConservetory().size() < goalSize
                && composer.getGenCount() < generation);
        System.out.println(" (" + composer.getGenCount() + ")");

        System.out.println(header("Dumping Archive"));
        IntStream.range(0, composer.getGenCount())
                .mapToObj(i -> String.format("%3d >> ", i)
                + Composer.getSummary(composer.getArchive().get(i)))
                .forEach(System.out::println);

        composer.getConservetory().keySet().stream()
                .map(Composer::output)
                .forEach(System.out::println);
    }

    static String header(String text) {
        return "\n---------- " + text + " ----------";
    }

    public Composer getComposer() {
        return composer;
    }

}
