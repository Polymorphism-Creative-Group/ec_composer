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
package tech.metacontext.ec.prototype.composer.materials;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.io.Serializable;
import static tech.metacontext.ec.prototype.composer.Parameters.*;
import static tech.metacontext.ec.prototype.composer.Settings.getRandom;
import tech.metacontext.ec.prototype.composer.enums.TransformType;
import tech.metacontext.ec.prototype.composer.enums.mats.Pitch;
import tech.metacontext.ec.prototype.composer.factory.PitchSetFactory;

/**
 *
 * @author Jonathan Chang, Chun-yien <ccy@musicapoetica.org>
 */
public class PitchSets extends MusicMaterial<List<Pitch>> implements Serializable {

    public static final int SHARP_ALLOWED = 1, SHARP_NOT_ALLOWED = 0;
    public static final int DEFAULT_ENHARMONIC_ALLOWED = SHARP_NOT_ALLOWED;
    public static final int DEFAULT_SHARP_ALLOWED = SHARP_ALLOWED;

    private int commonTone = 0;
    public transient PitchSetFactory factory;

    public static void main(String[] args) {

        PitchSets pss = new PitchSets();
        pss.setCommonTone(2);
        for (int i = 0; i < 10; i++) {
            System.out.println(pss.random());
            pss.factory.randomize();
        }
    }

    public PitchSets() {
    }

    public PitchSets(PitchSets origin) {

        super(origin.getDivision(), origin.getMaterials());
        this.commonTone = origin.commonTone;
    }

    public PitchSets(Consumer<PitchSets> init) {

        super();
        init.accept(this);
    }

    @Override
    public PitchSets duplicate() {

        PitchSets dupe = new PitchSets();
        dupe.setDivision(this.getDivision());
        dupe.setCommonTone(this.getCommonTone());
        dupe.setMaterials(this.getMaterials().stream()
                .map(ArrayList::new)
                .collect(Collectors.toList()));
        return dupe;
    }

    @Override
    public PitchSets reset() {

        this.setDivision(DEFAULT_DIVISION.getInt());
        this.factory = new PitchSetFactory();
        return this;
    }

    @Override
    public PitchSets generate() {

        if (factory.getMinPitchNumber() < this.commonTone) {
            factory.setMinPitchNumber(this.commonTone);
        }
        this.setMaterials(Stream.generate(factory::generate)
                .peek(ps -> factory.setPresetPitches(selectPitch(ps, this.commonTone)))
                .limit(this.getDivision())
                .collect(Collectors.toList())
        );
        return this;
    }

    @Override
    public PitchSets random() {

        this.setDivision(getRandom().nextInt(
                MAX_DIVISION.getInt()
                - MIN_DIVISION.getInt() + 1)
                + MIN_DIVISION.getInt());
        return this.generate();
    }

    @Override
    public PitchSets transform(TransformType type) {

        return switch (type) {
            case Repetition->
                new PitchSets(this);
            case Retrograde->
                new PitchSets(this).retrograde();
            case MoveForward->
                new PitchSets(this).moveForward();
            case MoveBackward->
                new PitchSets(this).moveBackward();
            case Disconnected->
                new PitchSets();
        };
    }

    private Set<Pitch> selectPitch(List<Pitch> ps, int commonTone) {
        Set<Pitch> selected = new HashSet<>();
        while (selected.size() < commonTone) {
            selected.add(ps.get(getRandom().nextInt(ps.size())));
        }
        return selected;
    }

    private PitchSets retrograde() {

        this.setMaterials(IntStream.range(0, this.size())
                .mapToObj(i -> this.getMaterials().get(this.size() - i - 1))
                .collect(Collectors.toList()));
        return this;
    }

    private PitchSets moveForward() {

        IntStream.range(0, this.size())
                .forEach(i -> {
                    this.getMaterials().set(i,
                            this.getMaterials().get(i).stream()
                                    .map(Pitch::forward)
                                    .collect(Collectors.toList()));
                });
        return this;
    }

    private PitchSets moveBackward() {

        IntStream.range(0, this.size())
                .forEach(i -> {
                    this.getMaterials().set(i,
                            this.getMaterials().get(i).stream()
                                    .map(Pitch::backward)
                                    .collect(Collectors.toList()));
                });
        return this;
    }

    public double getIntensityIndex() {

        var counting = this.getMaterials().stream()
                .flatMap(List::stream)
                .map(Pitch::ordinalEnharmonic)
                .distinct()
                .collect(Collectors.counting());
        return 1.0 * counting / 12;
    }

    @Override
    public String toString() {

//        AtomicInteger i = new AtomicInteger(0);
//        return String.format("PitchSets[ Division=%2d ]\n%s",
//                this.getDivision(),
//                this.getMaterials().stream()
//                        .map(p -> "  " + i.incrementAndGet() + ". " + p.toString())
//                        .collect(Collectors.joining("\n")));
        return this.getMaterials().toString();
    }

    /*
     * default setters and getters
     */
    public int getCommonTone() {
        return commonTone;
    }

    public void setCommonTone(int commonTone) {
        this.commonTone = commonTone;
    }

}
