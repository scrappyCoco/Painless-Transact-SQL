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
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import ru.coding4fun.tsql.lang.psi.ContainsVisitor;
import ru.coding4fun.tsql.lang.psi.ContainsWeightOption;
import ru.coding4fun.tsql.lang.psi.ContainsWeightedTerm;

import java.util.List;

import static ru.coding4fun.tsql.lang.psi.ContainsTypes.ISABOUT;

public class ContainsWeightedTermImpl extends ASTWrapperPsiElement implements ContainsWeightedTerm {

  public ContainsWeightedTermImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ContainsVisitor visitor) {
    visitor.visitWeightedTerm(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ContainsVisitor) accept((ContainsVisitor) visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ContainsWeightOption> getWeightOptionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ContainsWeightOption.class);
  }

  @Override
  @NotNull
  public PsiElement getIsabout() {
    return findNotNullChildByType(ISABOUT);
  }

}
