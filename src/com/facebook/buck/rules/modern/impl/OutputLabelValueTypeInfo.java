/*
 * Copyright 2019-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.facebook.buck.rules.modern.impl;

import com.facebook.buck.core.model.OutputLabel;
import com.facebook.buck.rules.modern.ValueCreator;
import com.facebook.buck.rules.modern.ValueTypeInfo;
import com.facebook.buck.rules.modern.ValueVisitor;

/** {@link ValueTypeInfo} for {@link OutputLabel} instances. */
public class OutputLabelValueTypeInfo implements ValueTypeInfo<OutputLabel> {
  public static final ValueTypeInfo<OutputLabel> INSTANCE = new OutputLabelValueTypeInfo();

  @Override
  public <E extends Exception> void visit(OutputLabel value, ValueVisitor<E> visitor) throws E {
    visitor.visitString(OutputLabel.internals().getLabel(value));
  }

  @Override
  public <E extends Exception> OutputLabel create(ValueCreator<E> creator) throws E {
    return OutputLabel.of(creator.createString());
  }
}