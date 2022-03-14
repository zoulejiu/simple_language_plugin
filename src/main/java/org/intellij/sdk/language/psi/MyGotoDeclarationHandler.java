package org.intellij.sdk.language.psi;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlElement;
import org.jetbrains.annotations.Nullable;

import static org.intellij.sdk.language.SimpleAnnotator.SIMPLE_PREFIX_STR;
import static org.intellij.sdk.language.SimpleAnnotator.SIMPLE_SEPARATOR_STR;

public class MyGotoDeclarationHandler implements GotoDeclarationHandler {
    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        if(sourceElement instanceof XmlElement){
            PsiClass psiClass = JavaPsiFacade.getInstance(sourceElement.getProject()).findClass("com.example.Test", GlobalSearchScope.allScope(sourceElement.getProject()));
            assert psiClass != null;
            PsiMethod[] psiMethods = psiClass.getMethods();
            PsiCodeBlock psiCodeBlock = psiMethods[0].getBody();
            assert psiCodeBlock != null;
            PsiElement[] psiElements = psiCodeBlock.getChildren();
            PsiExpressionStatement psiExpressionStatement = (PsiExpressionStatement) psiElements[4];
            PsiElement[] cls = psiExpressionStatement.getExpression().getChildren();
            PsiExpressionList psiExpressionList = (PsiExpressionList) cls[1];
            PsiElement[] clss = psiExpressionList.getChildren();
            PsiLiteralExpression literalExpression = (PsiLiteralExpression) clss[1];
            String value = literalExpression.getValue() instanceof String ?
                    (String) literalExpression.getValue() : null;
            if ((value != null && value.startsWith(SIMPLE_PREFIX_STR + SIMPLE_SEPARATOR_STR))) {
                return new PsiLiteralExpression[]{literalExpression};
            }
        }

        return new PsiElement[0];
    }
}
