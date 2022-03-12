// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.language;

import com.google.common.collect.Lists;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import org.intellij.sdk.language.psi.SimpleFile;
import org.intellij.sdk.language.psi.SimpleProperty;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleUtil {

  /**
   * Searches the entire project for Simple language files with instances of the Simple property with the given key.
   *
   * @param project current project
   * @param key     to check
   * @return matching properties
   */
  public static List<SimpleProperty> findProperties(Project project, String key) {
    List<SimpleProperty> result = new ArrayList<>();
    Collection<VirtualFile> virtualFiles =
            FileTypeIndex.getFiles(SimpleFileType.INSTANCE, GlobalSearchScope.allScope(project));
    for (VirtualFile virtualFile : virtualFiles) {
      SimpleFile simpleFile = (SimpleFile) PsiManager.getInstance(project).findFile(virtualFile);
      if (simpleFile != null) {
        SimpleProperty[] properties = PsiTreeUtil.getChildrenOfType(simpleFile, SimpleProperty.class);
        if (properties != null) {
          for (SimpleProperty property : properties) {
            if (key.equals(property.getKey())) {
              result.add(property);
            }
          }
        }
      }
    }
    //Search for injections in XML files
    Collection<VirtualFile> xmlVirtualFiles = FileTypeIndex.getFiles(XmlFileType.INSTANCE,GlobalSearchScope.allScope(project));

    List<VirtualFile> v=xmlVirtualFiles.stream().filter(virtualFile -> virtualFile.getName().equals("test.xml")).collect(Collectors.toList());
    if(v.isEmpty()){
      return result;
    }
    VirtualFile virtualFile = v.get(0);
    PsiFile psiFile =  PsiManager.getInstance(project).findFile(virtualFile);
    if(psiFile==null){
      return result;
    }
    if(!(psiFile instanceof XmlFile)){
      return result;
    }
    XmlFile xmlFile = (XmlFile) psiFile;
    Collection<PsiLanguageInjectionHost> psiLanguageInjectionHosts = PsiTreeUtil.collectElementsOfType(xmlFile.getOriginalElement(),PsiLanguageInjectionHost.class);
    if(psiLanguageInjectionHosts.isEmpty()){
      return result;
    }
    for(PsiLanguageInjectionHost psiLanguageInjectionHost:psiLanguageInjectionHosts){
      List<Pair<PsiElement, TextRange>> pairList = InjectedLanguageManager.getInstance(project)
              .getInjectedPsiFiles(psiLanguageInjectionHost);
      if(pairList!=null && !pairList.isEmpty()){
        List<Pair<PsiElement,TextRange>> list= pairList.stream().filter(it->it.first.getLanguage().equals(SimpleLanguage.INSTANCE)).collect(Collectors.toList());
        if(!list.isEmpty()){
          for(Pair<PsiElement,TextRange> pair:list){
            if(pair.first instanceof SimpleFile){
              SimpleFile mybatisParamFile = (SimpleFile) pair.first;
              PsiElement[] psiElements1= mybatisParamFile.getChildren();
              if(psiElements1.length>0){
                for(PsiElement psi:psiElements1){
                  if(psi instanceof SimpleProperty){
                    SimpleProperty simpleProperty = (SimpleProperty) psi;
                    if (key.equals(simpleProperty.getKey())) {
                      result.add(simpleProperty);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return result;
  }

  public static List<SimpleProperty> findProperties(Project project) {
    List<SimpleProperty> result = new ArrayList<>();
    Collection<VirtualFile> virtualFiles =
            FileTypeIndex.getFiles(SimpleFileType.INSTANCE, GlobalSearchScope.allScope(project));
    for (VirtualFile virtualFile : virtualFiles) {
      SimpleFile simpleFile = (SimpleFile) PsiManager.getInstance(project).findFile(virtualFile);
      if (simpleFile != null) {
        SimpleProperty[] properties = PsiTreeUtil.getChildrenOfType(simpleFile, SimpleProperty.class);
        if (properties != null) {
          Collections.addAll(result, properties);
        }
      }
    }
    return result;
  }

  /**
   * Attempts to collect any comment elements above the Simple key/value pair.
   */
  public static @NotNull String findDocumentationComment(SimpleProperty property) {
    List<String> result = new LinkedList<>();
    PsiElement element = property.getPrevSibling();
    while (element instanceof PsiComment || element instanceof PsiWhiteSpace) {
      if (element instanceof PsiComment) {
        String commentText = element.getText().replaceFirst("[!# ]+", "");
        result.add(commentText);
      }
      element = element.getPrevSibling();
    }
    return StringUtil.join(Lists.reverse(result),"\n ");
  }

}
