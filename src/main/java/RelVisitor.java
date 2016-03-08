import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

public class RelVisitor extends GenericVisitorAdapter<String, Object>{
	public String visit(final CompilationUnit n, final Object classString )
	{
		String res = "";
		if (n.getTypes() != null) {
			for (final TypeDeclaration typeDeclaration : n.getTypes()) {				
					res = res+ typeDeclaration.accept(this, classString); 
			}
		}
		return res;
	}

	public String visit(final ClassOrInterfaceDeclaration n, final Object classString)
	{
		String res = "";
		
		List<String> myInterfaceVarList = (List<String>) Utils.getInterfaces((String)classString);
		List<String> myClassVarList = (List<String>)Utils.getClasses((String)classString);
		
		List<ClassOrInterfaceType>	parentClasses = n.getExtends();
		String currentClassOrInterfaceName = n.getName();
		if(parentClasses!=null && !parentClasses.isEmpty())
		{
			for(ClassOrInterfaceType parentClass : parentClasses)
			{
				String parentClassName = parentClass.getName();
				for(String classVar : myClassVarList){
					String classNameFromUML = Utils.getClassName(classVar);
					if(classNameFromUML!= null && classNameFromUML.equals(parentClassName)){
						res += "[";
						res += currentClassOrInterfaceName;
						res += "]";
						res += Lines.EXTENDS_ARROW;
						res += "[";
						res += parentClassName;
						res += "]";
						res+="\n";
					}
				}
			}

		}

		List<ClassOrInterfaceType> parentInterfaces = n.getImplements();

		if(parentInterfaces!=null && !parentInterfaces.isEmpty())
		{
			for(ClassOrInterfaceType parentInterface: parentInterfaces)
			{
				String parentInterfaceName = parentInterface.getName();
				for(String interfaceVar : myInterfaceVarList){
					String interfaceNameFromUML = Utils.getInterfaceName(interfaceVar);
					if(interfaceNameFromUML!=null && interfaceNameFromUML.equals(parentInterfaceName))
					{	
						res += "[";
						res += currentClassOrInterfaceName;
						res += "]";
						res += Lines.IMPLEMENTS_ARROW;
						res = res+interfaceVar; break;
					}
				}
			}
		}		
		
		/* has a relationship */
		List<BodyDeclaration> bodies = n.getMembers();
		Set<String> usesList = new HashSet<String>();
		for(BodyDeclaration body: bodies )
		{
			if(body instanceof FieldDeclaration)
			{
				FieldDeclaration field = (FieldDeclaration)body;
				Type type = field.getType();
				
				String typeName = type.toString();
				typeName = Utils.getTypeForUMLString(typeName);
				Set<String> typeParams = new HashSet<String>();
				if(Utils.hasTypeParams(typeName)){
					typeParams.addAll(Utils.getTypeParamsFromType(typeName));
					typeParams.add(typeName.substring(0, typeName.indexOf("\uFF1C")));
				}
				else
				{
					typeParams.add(typeName);
				}
				String arrow = Lines.HAS_ARROW;
				for(String typeParam : typeParams){
					if(typeParam!= null && (
							typeParam.equals("Collection")
							|| typeParam.equals("Set")
							|| typeParam.equals("List")
							|| typeParam.contains("[]")
							))
						arrow = Lines.HAS_MANY_ARROW;
				}
				for(String typeParam : typeParams)
				{
					boolean flag = true;
					for(String classVar : myClassVarList)
					{
						String classNameFromUML = Utils.getClassName(classVar);
						if(typeParam!= null && typeParam.equals(classNameFromUML)){
							res = res + Utils.constructHasARelationShipClass(currentClassOrInterfaceName,classNameFromUML,arrow);
							flag = false; break;
						}
					}
					if(flag){
						for(String interfaceVar : myInterfaceVarList)
						{
							String interfaceNameFromUML = Utils.getInterfaceName(interfaceVar);
							if(typeParam!= null && typeParam.equals(interfaceNameFromUML)){								
								res = res + Utils.constructHasARelationShipInterface(currentClassOrInterfaceName,interfaceVar,arrow);break;
							}
						}
					}
				}
			}
			
			else if(body instanceof MethodDeclaration 
					&& !n.isInterface()) //remove this if prof asks to have "USES-A" relationship for interface to interface
			{
				//Set<String> usesList = new HashSet<String>();
				MethodDeclaration method = (MethodDeclaration)body;
				String returnType = method.getType().toString();
				returnType = Utils.getTypeForUMLString(returnType);
				if(Utils.hasTypeParams(returnType))
				{
					usesList.addAll(Utils.getTypeParamsFromType(returnType));
					usesList.add(returnType.substring(0, returnType.indexOf("\uFF1C")));
				}
				else
				{
					usesList.add(returnType);
				}
				
				List<Parameter> parameters = method.getParameters();
				
				for(Parameter parameter: parameters){
					String parameterType = parameter.getType().toString();
					parameterType = Utils.getTypeForUMLString(parameterType);
					if(Utils.hasTypeParams(parameterType))
					{
						usesList.addAll(Utils.getTypeParamsFromType(parameterType));
						usesList.add(parameterType.substring(0, parameterType.indexOf("\uFF1C")));
					}
					else
					{
						usesList.add(parameterType);
					}					
				}
				
				BlockStmt blockStatement = method.getBody();
				if(blockStatement!=null)
				{
					List<Statement> statements = blockStatement.getStmts();
					if(statements!=null)
					for(Statement statement : statements){
						if(statement instanceof ExpressionStmt){
							ExpressionStmt expressionStmt = ((ExpressionStmt) statement);
							Expression expression = expressionStmt.getExpression();
							if(expression!= null && expression instanceof VariableDeclarationExpr)
							{
								VariableDeclarationExpr variableDeclarationExpr = (VariableDeclarationExpr)expression;
								String varType = variableDeclarationExpr.getType().toString();
								usesList.add(varType);
							}
							//if(expression!= null && expression.get)
						}
					}
				}
			}
			
			else if(body instanceof ConstructorDeclaration 
					&& !n.isInterface()) //remove this if prof asks to have "USES-A" relationship for interface to interface and
										 // add code to have <<interface>> on the left \uFF1C\uFF1E
			{
				//Set<String> usesList = new HashSet<String>();
				ConstructorDeclaration method = (ConstructorDeclaration)body;				
				List<Parameter> parameters = method.getParameters();
				
				for(Parameter parameter: parameters){
					String parameterType = parameter.getType().toString();
					parameterType = Utils.getTypeForUMLString(parameterType);
					if(Utils.hasTypeParams(parameterType))
					{
						usesList.addAll(Utils.getTypeParamsFromType(parameterType));
						usesList.add(parameterType.substring(0, parameterType.indexOf("\uFF1C")));
					}
					else
					{
						usesList.add(parameterType);
					}					
				}
				
				BlockStmt blockStatement = method.getBlock();
				if(blockStatement!=null)
				{
					List<Statement> statements = blockStatement.getStmts();
					if(statements!=null)
					for(Statement statement : statements){
						if(statement instanceof ExpressionStmt){
							ExpressionStmt expressionStmt = ((ExpressionStmt) statement);
							Expression expression = expressionStmt.getExpression();
							if(expression!= null && expression instanceof VariableDeclarationExpr)
							{
								VariableDeclarationExpr variableDeclarationExpr = (VariableDeclarationExpr)expression;
								String varType = variableDeclarationExpr.getType().toString();
								usesList.add(varType);
							}
							//if(expression!= null && expression.get)
						}
					}
				}
			}
		}
		
		/* use the usesList to formulate relationship diagram*/
		for(String typeParam : usesList)
		{
			//boolean flag = true;
			/*   Commenting this part : Uses a relationship to be shown only when a class/interface uses a INTERFACE
			for(String classVar : myClassVarList)
			{
				String classNameFromUML = Utils.getClassName(classVar);
				if(typeParam!= null && typeParam.equals(classNameFromUML)){
					res = res + Utils.constructUsesARelationShipClass(currentClassOrInterfaceName,classNameFromUML);
					flag = false; break;
				}
			}
			*/
			//if(flag){
				for(String interfaceVar : myInterfaceVarList)
				{
					String interfaceNameFromUML = Utils.getInterfaceName(interfaceVar);
					if(typeParam!= null && typeParam.equals(interfaceNameFromUML)){								
						res = res + Utils.constructUsesARelationShipInterface(currentClassOrInterfaceName,interfaceVar);break;
					}
				}
			//}
		}
		return res;
	} 
	
	
}
