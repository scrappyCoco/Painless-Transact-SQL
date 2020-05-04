/*
 * Copyright [2020] Coding4fun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.coding4fun.tsql.lang.psi.ContainsMaximumDistance;
import ru.coding4fun.tsql.lang.psi.ContainsVisitor;

import static ru.coding4fun.tsql.lang.psi.ContainsTypes.INTEGER;
import static ru.coding4fun.tsql.lang.psi.ContainsTypes.MAX;

public class ContainsMaximumDistanceImpl extends ASTWrapperPsiElement implements ContainsMaximumDistance {

  public ContainsMaximumDistanceImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ContainsVisitor visitor) {
    visitor.visitMaximumDistance(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ContainsVisitor) accept((ContainsVisitor) visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getMax() {
    return findChildByType(MAX);
  }

  @Override
  @Nullable
  public PsiElement getInteger() {
    return findChildByType(INTEGER);
  }

}
