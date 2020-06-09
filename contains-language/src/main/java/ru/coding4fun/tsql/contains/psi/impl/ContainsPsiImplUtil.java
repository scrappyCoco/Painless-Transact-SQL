package ru.coding4fun.tsql.contains.psi.impl;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import ru.coding4fun.tsql.contains.psi.*;

public class ContainsPsiImplUtil {
    @Nullable
    public static IElementType getOperatorElementType(ContainsBoolOperator operatorExpression) {
        return PsiTreeUtil.firstChild(operatorExpression).getNode().getElementType();
    }

    public static boolean isTrue(ContainsBoolLiteral matchOrder) {
        var singleElement = PsiTreeUtil.firstChild(matchOrder).getNode().getElementType();
        return ContainsTypes.TRUE.equals(singleElement);
    }

    @Nullable
    public static Float getWeightValue(ContainsWeightOption weightOption) {
        ContainsElement weightLiteral = weightOption.getWeightLiteral();
        if (weightLiteral == null) return null;
        return Float.parseFloat(weightLiteral.getText());
    }
}
