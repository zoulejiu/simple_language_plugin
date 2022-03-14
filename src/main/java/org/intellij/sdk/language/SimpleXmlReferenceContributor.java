package org.intellij.sdk.language;

import com.google.common.collect.Lists;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.XmlElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.xml.XmlTextImpl;
import com.intellij.psi.impl.source.xml.XmlTokenImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static org.intellij.sdk.language.SimpleAnnotator.SIMPLE_PREFIX_STR;
import static org.intellij.sdk.language.SimpleAnnotator.SIMPLE_SEPARATOR_STR;

public class SimpleXmlReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(XmlToken.class), new PsiReferenceProvider() {
            @Override
            public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                XmlToken token = (XmlToken) element;
                if(element.getText().equals("webUrl1")) {
                        ArrayList<PsiReference> referenceList = Lists.newArrayList();
                    TextRange property = new TextRange(0,
                            element.getText().length() + 1);
                        referenceList.add(new PsiReferenceBase<XmlElement>(token) {

                            @Override
                            public @Nullable PsiElement resolve() {
                                PsiClass psiClass = JavaPsiFacade.getInstance(element.getProject()).findClass("com.example.Test", GlobalSearchScope.allScope(element.getProject()));
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
                                   return literalExpression;
                                }
                                return null;
                            }
                        });
                        return referenceList.toArray(new PsiReference[0]);

                }
                return PsiReferenceBase.EMPTY_ARRAY;
            }
        });
    }
}
