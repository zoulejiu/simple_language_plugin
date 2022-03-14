package org.intellij.sdk.language;

import com.google.common.collect.Lists;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.XmlElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.xml.XmlTextImpl;
import com.intellij.psi.impl.source.xml.XmlTokenImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class SimpleXmlReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(XmlTokenImpl.class), new PsiReferenceProvider() {
            @Override
            public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                if(element.getText().equals("webUrl1")){
                    ArrayList<PsiReference> referenceList = Lists.newArrayList();
                    referenceList.add(new PsiReferenceBase<>(element){

                        @Override
                        public @Nullable PsiElement resolve() {
                            PsiClass psiClass =JavaPsiFacade.getInstance(element.getProject()).findClass("com.example.Test", GlobalSearchScope.allScope(element.getProject()));
                            assert psiClass != null;
                            PsiMethod[] psiMethods =psiClass.getMethods();
                            PsiCodeBlock psiCodeBlock =psiMethods[0].getBody();
                            assert psiCodeBlock != null;
                            PsiElement[] psiElements =psiCodeBlock.getChildren();
                            PsiExpressionStatement psiExpressionStatement = (PsiExpressionStatement) psiElements[4];
                            PsiElement[] cls =psiExpressionStatement.getExpression().getChildren();
                            PsiExpressionList psiExpressionList= (PsiExpressionList) cls[1];
                            PsiElement[] clss = psiExpressionList.getChildren();
                            PsiLiteralExpression psiLiteralExpression = (PsiLiteralExpression) clss[1];
                            PsiElement[] psiElements1 =psiLiteralExpression.getChildren();
                            return psiElements1[0];
                        }
                    });
                    return referenceList.toArray(new PsiReference[0]);
                }
                return PsiReferenceBase.EMPTY_ARRAY;
            }
        });
    }
}
