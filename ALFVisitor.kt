import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor
import domain.interfaces.AstObject
import domain.ast.Block
import domain.ast.Module
import domain.ast.Assignment
import domain.ast.Identifier
import domain.ast.VariableDefinition
import domain.ast.UnaryExpression
import domain.interfaces.Expression
import domain.interfaces.Assignable
import domain.ast.Value
import domain.ast.BinaryExpression
import domain.ast.Group
import domain.ast.Definition
import domain.interfaces.Type
import domain.ast.Return
import domain.types.Function
import domain.ast.Property
import domain.ast.ForLoop
import domain.ast.Loop
import domain.ast.Iteration
import domain.ast.FunctionCall
import domain.ast.IfClause

class AlfVisitor : AbstractParseTreeVisitor<AstObject>() , alfVisitor<AstObject>{

     override fun visitProgram(ctx: alfParser.ProgramContext) : AstObject{
        //cette méthode retournera une instance du Module qui contient une instance du Block avec toutes les instructions.
        var instructions : MutableList<AstObject> = mutableListOf()
        for(instruction in ctx.instruction()){
                instructions.add(visit(instruction))
           
        }
        //TODO: créez une instance de la classe Block avec les instructions obtenues après la visite
        val statements = Block(instructions, ctx.start.line)

        //TODO: créez une instance de la classe Module qui contient le Block avec les instructions
        return Module(statements, ctx.start.line)
    }

    override fun visitAsigRule(ctx: alfParser.AsigRuleContext): AstObject{
        return visit(ctx.asig())
    }
    override fun visitExpRule(ctx: alfParser.ExpRuleContext): AstObject{
        return visit(ctx.expression())
    }

    override fun visitExpValue(ctx: alfParser.ExpValueContext): AstObject{
        return visit(ctx.value())
    }
    override fun visitLoopRule(ctx: alfParser.LoopRuleContext): AstObject{
        return visit(ctx.loop())
    }
    override fun visitStructRule(ctx: alfParser.StructRuleContext): AstObject{
        return visit(ctx.struct())
    }
    override fun visitStructClass(ctx: alfParser.StructClassContext): AstObject{
        return Value(
            value="yes",
            line=99
        )
    }
    override fun visitForSimple(ctx: alfParser.ForSimpleContext): AstObject{
        var instructions : MutableList<AstObject> = mutableListOf()
        for(instruction in ctx.instruction()){
            instructions.add(visit(instruction))            
        } 
        return ForLoop(
            init=VariableDefinition(
                variable=Identifier(
                    title=ctx.ID().text,
                    line=ctx.start.line
                ),
                init=Value(
                    value=ctx.from.text,
                    typeName="I64",
                    line=ctx.start.line
                ),
                line=ctx.start.line
            ),
            loop=Loop(
                 condition = BinaryExpression(
                    left = Identifier(
                        title = ctx.ID().text,
                        line = ctx.start.line
                    ),
                    right = Value(
                        value = ctx.to.text,
                        typeName = "I64",
                        line = ctx.start.line
                    ),
                    op = BinaryExpression.BinaryOperator.LT,
                    line = ctx.start.line
                ),

                statement = Block(instructions, ctx.instruction(0).start.line),
                evaluation = Loop.Evaluation.BEFORE,
                line = ctx.start.line
                ),
                 steps = Assignment(
                to = Identifier(
                    title = ctx.ID().text,
                    line = ctx.start.line
                ),
                from =BinaryExpression(
                    left = Identifier(
                        title = ctx.ID().text,
                        line = ctx.start.line
                    ),
                    right = Value(
                        value = "1",
                        typeName = "I64",
                        line = ctx.start.line
                    ),
                    op = BinaryExpression.BinaryOperator.ADD,
                    line = ctx.start.line
                ),
                line = ctx.start.line
            ) ,
            line = ctx.start.line
            )
        
    }
    override fun visitWhile(ctx: alfParser.WhileContext): AstObject{
        var instructions : MutableList<AstObject> = mutableListOf()
        for(instruction in ctx.instruction()){
                instructions.add(visit(instruction))
        }
        return Loop(
            condition=(visit(ctx.expression()) as Expression),
            statement=Block(instructions,ctx.instruction(0).start.line),
            evaluation=Loop.Evaluation.BEFORE,
            line=ctx.start.line
        )

    }
    override fun visitForExp(ctx: alfParser.ForExpContext): AstObject{
        var instructions : MutableList<AstObject> = mutableListOf()
        for(instruction in ctx.instruction()){
                instructions.add(visit(instruction))
        }
        return Iteration(
            variable=ctx.variable.text,
            iterator=Identifier(
                title=ctx.iterator.text,
                line=ctx.start.line
            
            ),
            statement=Block(
                statements=instructions, 
                line=ctx.start.line+1
                //line=ctx.instruction(0).start.line
            ),
            line=ctx.start.line
        )
    }

    override fun visitRepeat(ctx: alfParser.RepeatContext): AstObject{
        var instructions : MutableList<AstObject> = mutableListOf()
        for(instruction in ctx.instruction()){
                instructions.add(visit(instruction))
        }
        return Loop(
            condition=visit(ctx.expression()) as Expression  ,
            statement=Block(
                statements=instructions,
                line=ctx.instruction(0).start.line
                )  ,
            evaluation=Loop.Evaluation.AFTER  ,
            line=ctx.start.line
        )
    }

    override fun visitAsig1(ctx: alfParser.Asig1Context): AstObject{
        return Assignment(
            to = Identifier(
                title = ctx.ID().text,
                line = ctx.start.line
            ),
            from = (visit(ctx.expression()) as Expression),
            line = ctx.start.line
        )     
    }
 //   override fun visitAsig2(ctx: alfParser.Asig2Context): AstObject{
//
//    }
/*
    override fun visitArray (ctx: alfParser.ArrayContext) : AstObject {
        return ArrayElement(
            array = Identifier(
                title = ctx.ID().text,
                line = ctx.start.line
            ),
            index = visit(ctx.expression()) as Expression,
            line = ctx.start.line
        )
    }

    override fun visitArrayExp(ctx: alfParser.ArrayExp): AstObject{

    } */

    override fun visitExpOP(ctx: alfParser.ExpOPContext): AstObject{
        val operator = when(ctx.OP().text){
            "+" -> BinaryExpression.BinaryOperator.ADD
            "-" -> BinaryExpression.BinaryOperator.SUB
            "*" -> BinaryExpression.BinaryOperator.MUL
            "/" -> BinaryExpression.BinaryOperator.DIV
            ">"->BinaryExpression.BinaryOperator.GT
            "<"->BinaryExpression.BinaryOperator.LT
            else -> BinaryExpression.BinaryOperator.CUSTOM
        }
        
        var e= BinaryExpression(
            left = (visit(ctx.left) as Expression),
            right = (visit(ctx.right) as Expression),
            op = operator,
            line = ctx.start.line
        )
        return e;
    }
    override fun visitExpOPValue(ctx: alfParser.ExpOPValueContext): AstObject{
        val operator = when(ctx.OP().text){
            "+" -> BinaryExpression.BinaryOperator.ADD
            "-" -> BinaryExpression.BinaryOperator.SUB
            "*" -> BinaryExpression.BinaryOperator.MUL
            "/" -> BinaryExpression.BinaryOperator.DIV
            else -> BinaryExpression.BinaryOperator.CUSTOM
        }
        
        var e= BinaryExpression(
            left = (visit(ctx.left) as Expression),
            right = (visit(ctx.right) as Expression),
            op = operator,
            line = ctx.start.line
        )
        return e;
    }
    override fun visitValueExpInt(ctx: alfParser.ValueExpIntContext): AstObject{
        return Value(
            value=ctx.INT().text,
            typeName="I64",
           line= ctx.start.line
        )
    }
    override fun visitValueExpFloat(ctx: alfParser.ValueExpFloatContext): AstObject{
        return Value(
             value=ctx.FLOAT().text,
            typeName="F64",
           line= ctx.start.line
        )
    }
    override fun visitIDExp(ctx: alfParser.IDExpContext): AstObject{
        return Identifier(
            title = ctx.ID().text,
            line = ctx.start.line
        )
    }
    override fun visitMinExp(ctx: alfParser.MinExpContext): AstObject{
        return UnaryExpression(
            expression = (visit(ctx.expression()) as Expression),
            op = UnaryExpression.UnaryOperator.NEGATIVE,
            line = ctx.start.line
        )
    }

    override fun visitInsSimpleRule(ctx: alfParser.InsSimpleRuleContext): AstObject{
        return visit(ctx.simple())
    }
    override fun visitInsDeclareRule(ctx: alfParser.InsDeclareRuleContext): AstObject{
        return visit(ctx.declare())
    }
    override fun visitDeclare(ctx: alfParser.DeclareContext):AstObject{
        var instructions : MutableList<AstObject> = mutableListOf()
        for(instruction in ctx.declarecontinue()){
           instructions.add(visit(instruction))            
        }
        return Group(instructions, ctx.start.line)
    }
    override fun visitDecAtrib(ctx: alfParser.DecAtribContext):AstObject{
        var variableI=Identifier( title=ctx.ID().text,line=ctx.start.line)
        return VariableDefinition(
            variable=variableI,
            init=(visit(ctx.expression()) as Expression),
            line=ctx.start.line
        )
    }
    override fun visitStringExp(ctx: alfParser.StringExpContext): AstObject{
        val x= ctx.STRING().text.replace("\"", "")
        return Value(
            value=x,
            typeName="String",
            line=ctx.start.line
        )
    }
    override fun visitBoolExp(ctx: alfParser.BoolExpContext): AstObject{
        return Value(
            value=ctx.BOOL().text,
            typeName="Boolean",
            line=ctx.start.line
        )
    }

    override fun visitDecSimple(ctx: alfParser.DecSimpleContext):AstObject{
        var variableI=Identifier( title=ctx.ID().text,typeName=ctx.TYPE().text,line=ctx.start.line)
        return VariableDefinition(
            variable=variableI,
          
            line=ctx.start.line
        )
    }
    override fun visitDecValue(ctx: alfParser.DecValueContext):AstObject{
        var variableI=Identifier( title=ctx.ID().text,typeName=ctx.TYPE().text,line=ctx.start.line)
        return VariableDefinition(
            variable=variableI,
            init=(visit(ctx.expression()) as Expression),
            line=ctx.start.line
        )
    }
    override fun visitSimpleEmpty(ctx: alfParser.SimpleEmptyContext):AstObject{
        var instructions : MutableList<AstObject> = mutableListOf()
        return Block(
            statements=instructions,
            line=ctx.start.line
        )
    }
    override fun visitSimpleSymbol(ctx: alfParser.SimpleSymbolContext): AstObject{
        val x= ctx.SYMBOL().text.replace("\"", "")
        return Value(
            value=x,
            typeName="Symbol",
            line=ctx.start.line
        )
    }
    override fun visitSimpleString(ctx: alfParser.SimpleStringContext): AstObject{
        val x= ctx.STRING().text.replace("\"", "")
        return Value(
            value=x,
            typeName="String",
            line=ctx.start.line
        )
    }
    override fun visitSimpleFloat(ctx: alfParser.SimpleFloatContext): AstObject{
        return Value(
            value=ctx.FLOAT().text,
            typeName="F64",
           line= ctx.start.line
        )
    }
    override fun visitSimpeChar(ctx: alfParser.SimpeCharContext): AstObject{
        return Value(
            value=ctx.CHAR().text,
            typeName="Char",
            line= ctx.start.line
        )
    }
    override fun visitSimpleInt(ctx: alfParser.SimpleIntContext): AstObject{
        return Value(
            value=ctx.INT().text,
            typeName="I64",
            line=ctx.start.line
        )
    }
    override fun visitSimpleBool(ctx: alfParser.SimpleBoolContext): AstObject{
        return Value(
            value=ctx.BOOL().text,
            typeName="Boolean",
            line=ctx.start.line
        )
    }

    override fun visitFunctionRule(ctx: alfParser.FunctionRuleContext): AstObject{
        return visit(ctx.function())
    }
    override fun visitExecFunction(ctx: alfParser.ExecFunctionContext): AstObject{
        var para :MutableList<Expression> = mutableListOf()
        for (param in ctx.expression()){
            para.add(visit(param) as Expression)
        }
        return FunctionCall(
            function=Identifier(
                title=ctx.ID().text,
                line=ctx.start.line
            ),
            parameters=para,
            line=ctx.start.line
        )
    }
   override fun visitFunctionNoDef(ctx: alfParser.FunctionNoDefContext): AstObject{
            var instructions : MutableList<AstObject> = mutableListOf()
            var para : MutableList<Property> = mutableListOf()
            val retn=Return(expression=visit(ctx.value()) as Expression,typeName="string",line=ctx.start.line)
            instructions.add(retn)
            val block=Block(instructions,ctx.start.line)
            val type=Function(title=ctx.ID().text,parameters=para,statements=block,returnTypeName="string")
            val def=Definition(
                definedType=type,
                line=ctx.start.line
            )
            return def
        }
        override fun visitFunctionStatements(ctx: alfParser.FunctionStatementsContext): AstObject{
            var para : MutableList<Property> = mutableListOf()
            var instructions : MutableList<AstObject> = mutableListOf()
            for(param in ctx.property()){
                para.add(visit(param)as Property)
            }
            for(statement in ctx.instruction()){
                instructions.add(visit(statement))
            }
            return Definition(
                definedType=Function(
                    title=ctx.ID().text,
                    parameters=para,
                    statements=Block(instructions,ctx.start.line+1),
                    returnTypeName=when (ctx.TYPE().text) {
                            "integer" -> "I64"
                            "float" -> "F64"
                            "boolean" -> "boolean"
                            "string" -> "string"
                            else -> "unsupported"
                    }
                ),
                line=ctx.start.line
            )
        }
    override fun visitFunctionDef(ctx: alfParser.FunctionDefContext): AstObject{
            var instructions : MutableList<AstObject> = mutableListOf()
             var para : MutableList<Property> = mutableListOf()
            for(instruction in ctx.instruction()){
            instructions.add(visit(instruction))
        }  
            val block=Block(instructions,ctx.start.line+1)
            val type=Function(title=ctx.ID().text,parameters=para,statements=block,returnTypeName="string")
            val def=Definition(
                definedType=type,
                line=ctx.start.line
            )
            return def
        }
    override fun visitFunctionDefEx(ctx: alfParser.FunctionDefExContext): AstObject{
            var para : MutableList<Property> = mutableListOf()
            var state : MutableList<AstObject> = mutableListOf()
            for(param in ctx.property()){
                para.add(visit(param)as Property)
            }
            return Definition(
                definedType=Function(
                    title=ctx.ID().text,
                    parameters=para,
                    statements=Block(state,ctx.start.line),
                    returnTypeName=when (ctx.TYPE().text) {
                            "integer" -> "I64"
                            "float" -> "F64"
                            "boolean" -> "boolean"
                            "string" -> "string"
                            else -> "unsupported"
                    }
                ),
                line=ctx.start.line
            )
            
    }
    override fun visitPropertySimple(ctx: alfParser.PropertySimpleContext): AstObject{
        return Property(
            title=ctx.ID().text,
            typeName = when (ctx.TYPE().text) {
                "integer" -> "I64"
                "float" -> "F64"
                "boolean" -> "boolean"
                "string" -> "string"
                else -> "unsupported"
            },
            line = ctx.start.line

        )
    }
    override fun visitPropertyExp(ctx: alfParser.PropertyExpContext): AstObject{
        return Property(
            title=ctx.ID().text,
            typeName = when (ctx.TYPE().text) {
                "integer" -> "I64"
                "float" -> "F64"
                "boolean" -> "boolean"
                "string" -> "string"
                else -> "unsupported"
            },
            defaultValue=visit(ctx.expression()) as Expression,
            line = ctx.start.line

        )
    }

    override fun visitIf (ctx: alfParser.IfContext): AstObject{
        var if_instructions : MutableList<AstObject> = mutableListOf()  
        var  else_instructions : MutableList<AstObject> = mutableListOf()
        for(ins in ctx.ify()){
            if_instructions.add(visit(ins))            
        }  
        for(ins in ctx.elsey()){
            else_instructions.add(visit(ins))            
        } 
        var liney=ctx.elsey(0)?.start?.line?: ctx.start.line
         return IfClause(
            condition=visit(ctx.expression()) as Expression,
            then=Block(if_instructions,ctx.ify(0).start.line),
            els=Block(else_instructions,
            line=liney      
            ),
            line=ctx.start.line
        )
       
    }
override fun visitIfy(ctx: alfParser.IfyContext): AstObject{
    return visit(ctx.instruction())
}
override fun visitElsey(ctx: alfParser.ElseyContext): AstObject{
    return visit(ctx.instruction())
}
    override fun visitParaListSimple(ctx: alfParser.ParaListSimpleContext): Property{
        return Property(
            title=ctx.ID().text,
            typeName=when(ctx.TYPE().text){
                "integer"->"I64"
                "float"->"F64"
                "char"->"Char"
                "boolean"->"Boolean"
                "string"->"String"
                else->"foult"
            },
            line=ctx.start.line
        )
    }
    override fun visitParaListMul(ctx: alfParser.ParaListMulContext): AstObject{
        var para : MutableList<Property> = mutableListOf()
        return Value(
            value="test",
            typeName="Boolean",
            line=ctx.start.line
            )
    }
    
   
}