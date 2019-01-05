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
package tech.metacontext.ec.prototype.composer.styles;

import tech.metacontext.ec.prototype.composer.model.*;
import tech.metacontext.ec.prototype.composer.enums.mats.*;
import tech.metacontext.ec.prototype.composer.enums.MaterialType;
import tech.metacontext.ec.prototype.composer.materials.MusicMaterial;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import tech.metacontext.ec.prototype.composer.enums.ComposerAim;
import tech.metacontext.ec.prototype.composer.materials.RhythmicPoints;
import tech.metacontext.ec.prototype.composer.materials.*;

/**
 *
 * @author Jonathan Chang, Chun-yien <ccy@musicapoetica.org>
 */
public class GoldenSectionClimax extends Style {

    public static void main(String[] args) throws Exception {

        var gsc = new GoldenSectionClimax(UnaccompaniedCello.getRange());
        var composer = new Composer(10, ComposerAim.Phrase, 2, new UnaccompaniedCello(), gsc);
        do {
            composer.compose().evolve();
        } while (composer.getPopulation().stream().anyMatch(c -> !composer.getAim().isCompleted(c)));
        composer.getPopulation().stream().map(gsc::rateComposition).forEach(System.out::println);
        
//        Stream.generate(() -> SketchNodeFactory.getInstance().newInstance(composer.styleChecker))
//                .limit(50)
//                .peek(System.out::println)
//                .map(gsc::climaxIndex)
//                .forEach(System.out::println);

    }

    public static final double RATIO = 1.6180339887498948482;

    public final SciRange lowest, highest;
    List<Double> climaxIndexes, standards;
    double peak, base;

    public GoldenSectionClimax(Collection<SciRange> ranges) {

        TreeSet<SciRange> sortedRanges = new TreeSet<>(ranges);
        this.lowest = sortedRanges.first();
        this.highest = sortedRanges.last();
    }

    /**
     * Golden Section Style is not about single SketchNodes.
     *
     * @param sketchNode
     * @return Always true.
     */
    @Override
    public boolean qualifySketchNode(SketchNode sketchNode) {

        return true;
    }

    @Override
    public double rateComposition(Composition composition) {

//        for (int i = 0; i < composition.getSize(); i++) {
//            double standard = getStandard(composition, i);
//            double score = standard
//                    * Math.abs(climaxIndexes.get(i) - standard);
//            scores.add(score);
//            base += standard * peak;
//        }
        this.updateClimaxIndexes(composition);
        double sum = IntStream.range(0, composition.getSize())
                .mapToDouble(i
                        -> Math.abs(climaxIndexes.get(i) - this.standards.get(i)) * this.standards.get(i))
                .sum();
        return (base - sum) / base;
    }

    public void updateClimaxIndexes(Composition composition) {

        this.climaxIndexes = composition
                .getRenderedChecked("GoldenSectionClimax::rateComposition")
                .stream()
                .map(this::climaxIndex)
                .collect(Collectors.toList());
        this.peak = climaxIndexes.stream()
                .mapToDouble(s -> s)
                .max().orElse(0.0);
        this.base = 0.0;
        this.standards = IntStream.range(0, composition.getSize())
                .mapToDouble(i -> this.getStandard(composition, i))
                .peek(s -> this.base += s * peak)
                .boxed()
                .collect(Collectors.toList());
    }

    public double getStandard(Composition composition, int i) {

        if (i < 0 || i > composition.getSize() - 1) {
            return 0.0;
        }
        long peakNodeIndex = Math.round((composition.getSize() - 1) / RATIO);
        return (i < peakNodeIndex)
                ? i * peak / peakNodeIndex
                : (composition.getSize() - i - 1) * peak
                / (composition.getSize() - peakNodeIndex - 1);
    }

    public double climaxIndex(SketchNode node) {

        DoubleAdder index = new DoubleAdder();
        node.getMats().forEach((MaterialType mt, MusicMaterial mm) -> {
            double mti = 0.0;
            switch (mt) {
                case Dynamics:
                    mti = ((Dynamics) mm).getAvgIntensityIndex(Intensity::getIntensityIndex);
                    break;
                case NoteRanges:
                    mti = ((NoteRanges) mm).getAvgIntensityIndex(mat -> SciRange.getIntensityIndex(mat, lowest, highest));
                    break;
                case PitchSets:
                    mti = ((PitchSets) mm).getAvgIntensityIndex(PitchSet::getIntensityIndex);
                    break;
                case RhythmicPoints:
                    mti = ((RhythmicPoints) mm).getAvgIntensityIndex(
                            mat -> 1.0 * mat / RhythmicPoints.DEFAULT_MAX_POINTS);
                    break;
                default:
            }
//            System.out.println(mt + ":" + mti);
            index.add(mti);
        });
        return index.doubleValue() / node.getMats().size();
    }

}
