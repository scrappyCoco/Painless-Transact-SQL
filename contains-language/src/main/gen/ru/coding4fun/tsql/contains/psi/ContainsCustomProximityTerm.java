// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.contains.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ContainsCustomProximityTerm extends ContainsTermExpression {

  @NotNull
  List<ContainsLiteral> getLiteralList();

  @Nullable
  ContainsMatchOrder getMatchOrder();

  @Nullable
  ContainsMaximumDistance getMaximumDistance();

}
