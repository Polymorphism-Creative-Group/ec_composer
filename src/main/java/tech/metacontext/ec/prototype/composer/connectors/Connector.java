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
package tech.metacontext.ec.prototype.composer.connectors;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import tech.metacontext.ec.prototype.abs.Individual;
import tech.metacontext.ec.prototype.composer.SketchNode;
import tech.metacontext.ec.prototype.composer.SketchNodeFactory;
import tech.metacontext.ec.prototype.composer.materials.MusicMaterial;
import tech.metacontext.ec.prototype.composer.materials.enums.MaterialType;
import tech.metacontext.ec.prototype.composer.materials.enums.TransformType;

/**
 *
 * @author Jonathan Chang, Chun-yien <ccy@musicapoetica.org>
 */
public class Connector extends Individual {

    private final Map<MaterialType, TransformType> transformTypes;
    //@todo consider why need stylecheker in connector
    private Predicate<SketchNode> styleChecker;
    private SketchNode previous;
    private SketchNode next;

    public Connector() {

        this.transformTypes = new HashMap<>();
    }

    public Connector(String id) {

        super(id);
        this.transformTypes = new HashMap<>();
    }

    public void addTransformType(MaterialType mt, TransformType tt) {

        this.transformTypes.put(mt, tt);
    }
    private static final SketchNodeFactory sketchNodeFactory = SketchNodeFactory.getInstance();

    public SketchNode transform() {

        if (this.previous == null) {
            return null;
        }
        this.next = sketchNodeFactory.newInstance();
//        System.out.println("Connector: " + this.getTransformTypes());

        Map<MaterialType, ? extends MusicMaterial> mats
                = this.getTransformTypes().entrySet().stream()
                        .map(e -> new SimpleEntry<>(e.getKey(),
                        /*.............*/ this.previous.getMat(e.getKey()).transform(e.getValue())))
                        //                        .peek(e -> System.out.println("peek: " + e.getKey() + "=" + e.getValue()))
                        .collect(Collectors.toMap(SimpleEntry::getKey,
                                SimpleEntry::getValue));
        this.next.setMats(mats);
        return this.next;
    }

    @Override
    public String toString() {

        return super.toString() + getTransformTypes() + " "
                + ((previous == null) ? "N/A" : "\nfrom: " + previous)
                + ((next == null) ? "" : "\n => " + next);
    }

    /*
     * Default setters and getters
     */
    public SketchNode getPrevious() {
        return previous;
    }

    public void setPrevious(SketchNode previous) {
        this.previous = previous;
    }

    public SketchNode getNext() {
        return next;
    }

    public void setNext(SketchNode next) {
        this.next = next;
    }

    public Predicate<SketchNode> getStyleChecker() {
        return styleChecker;
    }

    public void setStyleChecker(Predicate<SketchNode> styleChecker) {
        this.styleChecker = styleChecker;
    }

    public Map<MaterialType, TransformType> getTransformTypes() {
        return transformTypes;
    }

}
