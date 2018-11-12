/** 
 *
 * @File:      BuildSymTable
 * @Brief:     Visitor to build a symbol table
 * @Created:   Nov/11/2018
 * @Author:    Jiahao Cai
 *
 */
package ast_visitors;

import symtable.*;
import java.util.*;
import ast.visitor.*;
import ast.node.*;
import exceptions.InternalException;
import exceptions.SemanticException;

public class BuildSymTable extends DepthFirstVisitor {
  SymTable ST;

  public BuildSymTable() {
    ST = new SymTable();
  }

  public SymTable getSymTable() {
    return ST;
  }

  private Type getType(Node node) {
    return this.ST.getExpType(node);
  }

  private void setType(Node node, Type t) {
    this.ST.setExpType(node, t);
  }

  /* Literals */
  @Override
  public void outIntType(IntType node) {
    setType(node, Type.INT);
  }

  @Override
  public void outColorType(ColorType node) {
    setType(node, Type.COLOR);
  }

  @Override
  public void outButtonType(ButtonType node) {
    setType(node, Type.BUTTON);
  }

  @Override
  public void outBoolType(BoolType node) {
    setType(node, Type.BOOL);
  }

  @Override
  public void outByteType(ByteType node) {
    setType(node, Type.BOOL);
  }

  @Override
  public void outVoidType(VoidType node) {
    setType(node, Type.BOOL);
  }

  @Override
  public void inMainClass(MainClass node) {
    assert (ST.getCurrentScope() == ST.getGlobalScope());
    ClassSTE mSTE = new ClassSTE(node.getName(), true, null, new Scope());
    if (!ST.insert(mSTE)) {
      throw new SemanticException("Class " + mSTE.getName() + " already exists in current scope!", node.getLine(),
          node.getPos());
    }
    ST.pushScope(mSTE.getName());
  }

  @Override
  public void outMainClass(MainClass node) {
    defaultOut(node);
    ST.popScope();
  }

  @Override
  public void inTopClassDecl(TopClassDecl node) {
    assert (ST.getCurrentScope() == ST.getGlobalScope());
    ClassSTE mSTE = new ClassSTE(node.getName(), false, null, new Scope());
    if (!ST.insert(mSTE)) {
      throw new SemanticException("Class " + mSTE.getName() + " already exists in current scope!", node.getLine(),
          node.getPos());
    }
    ST.pushScope(mSTE.getName());
  }

  @Override
  public void outTopClassDecl(TopClassDecl node) {
    ST.popScope();
  }

  @Override
  public void inMethodDecl(MethodDecl node) {
    List<Type> mTypeList = new LinkedList<>();
    for (Formal e : node.getFormals()) {
      mTypeList.add(getType(e.getType()));
    }
    Type retType = getType(node.getType());
    MethodSTE mSTE = new MethodSTE(node.getName(), new Signature(mTypeList, retType), new Scope());
    if (!ST.insert(mSTE)) {
      throw new SemanticException("Method " + mSTE.getName() + " already exists in current scope!", node.getLine(),
          node.getPos());
    }
    ST.pushScope(mSTE.getName());
  }

  @Override
  public void outMethodDecl(MethodDecl node) {
    ST.popScope();
  }
}