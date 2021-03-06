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
package tech.metacontext.ec.prototype.composer.draft.enums;

import tech.metacontext.ec.prototype.composer.draft.nodes.materials.MusicMaterial;
import tech.metacontext.ec.prototype.composer.draft.nodes.materials.NoteNumber;
import tech.metacontext.ec.prototype.composer.draft.nodes.materials.NoteRange;
import tech.metacontext.ec.prototype.composer.draft.nodes.materials.PitchSet;

/**
 *
 * @author Jonathan Chang, Chun-yien <ccy@musicapoetica.org>
 */
public enum MusicMaterialType {
  
  PitchSet(PitchSet.class),
  NoteNumber(NoteNumber.class),
  NoteRange(NoteRange.class);

  private Class<? extends MusicMaterial> clazz;

  private MusicMaterialType(Class<? extends MusicMaterial> clazz) {
    
    this.clazz = clazz;
  }

  public Class<? extends MusicMaterial> getClazz() {
    
    return clazz;
  }

  public void setClazz(Class<? extends MusicMaterial> clazz) {
    
    this.clazz = clazz;
  }

}
