/*
 * SonarQube Java
 * Copyright (C) 2012-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.java.checks.spring;

import java.util.List;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.semantic.SymbolMetadata;
import org.sonar.plugins.java.api.semantic.SymbolMetadata.AnnotationValue;
import org.sonar.plugins.java.api.tree.AnnotationTree;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.LiteralTree;

@Rule(key = "S3750")
public class S3750Check extends BaseTreeVisitor implements JavaFileScanner {

  private static final String SCOPE_ANNOTATION_FQN = "org.springframework.context.annotation.Scope";
  private JavaFileScannerContext context;

  @Override
  public void scanFile(JavaFileScannerContext context) {
    this.context = context;
    scan(context.getTree());
  }

  @Override
  public void visitClass(ClassTree tree) {
    SymbolMetadata clazzMeta = tree.symbol().metadata();

    if ((clazzMeta.isAnnotatedWith("org.springframework.stereotype.Controller")
      || clazzMeta.isAnnotatedWith("org.springframework.stereotype.Service")
      || clazzMeta.isAnnotatedWith("org.springframework.stereotype.Repository"))
      && clazzMeta.isAnnotatedWith(SCOPE_ANNOTATION_FQN)) {

      if (!isScopeSingleton(clazzMeta)) {
        AnnotationTree scopeAnnotation = findScopeAnnotation(tree);
        if (scopeAnnotation != null) {
          context.reportIssue(this, scopeAnnotation, "Remove this \"@Scope\" annotation.");
        }
      }
    }
  }

  private boolean isScopeSingleton(SymbolMetadata clazzMeta) {
    List<AnnotationValue> values = clazzMeta.valuesForAnnotation(SCOPE_ANNOTATION_FQN);
    for (AnnotationValue annotationValue : values) {
      if ("value".equals(annotationValue.name()) || "scopeName".equals(annotationValue.name())) {
        String scopeValue = ((LiteralTree) annotationValue.value()).value();
        if (!"\"singleton\"".equals(scopeValue)) {
          return Boolean.FALSE;
        }
      }
    }
    return Boolean.TRUE;
  }

  private AnnotationTree findScopeAnnotation(ClassTree tree) {
    List<AnnotationTree> annotations = tree.modifiers().annotations();
    for (AnnotationTree annotationTree : annotations) {
      if (annotationTree.annotationType().symbolType().fullyQualifiedName().equals(SCOPE_ANNOTATION_FQN)) {
        return annotationTree;
      }
    }
    return null;
  }

}
