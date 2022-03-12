package org.intellij.sdk.language.injector;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.impl.source.xml.XmlTextImpl;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlText;
import org.intellij.sdk.language.SimpleLanguage;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class MyMultiHostInjector implements MultiHostInjector {
    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement psiElement) {
        if (!(psiElement instanceof PsiLanguageInjectionHost)) {
            return;
        }
        if (!((PsiLanguageInjectionHost) psiElement).isValidHost()) {
            return;
        }
        PsiFile psiFile = psiElement.getContainingFile();
        if (psiFile == null) {
            return;
        }
        if (!(psiFile instanceof XmlFile)) {
            return;
        }
        PsiElement parentPsiElement = psiElement.getParent();
        if (parentPsiElement != null) {
            if (!(psiElement instanceof XmlTextImpl)) {
                return;
            }
            if (!psiElement.getText().contains("=")) {
                return;
            }

            TextRange textRang = new TextRange(0,psiElement.getTextLength());
            registrar.startInjecting(SimpleLanguage.INSTANCE)
                    .addPlace("","",(PsiLanguageInjectionHost)psiElement,textRang)
                    .doneInjecting();
        }
    }

    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Collections.singletonList(XmlText.class);
    }
}
