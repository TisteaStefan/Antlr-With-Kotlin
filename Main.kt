import java.io.File
import com.fasterxml.jackson.databind.ObjectMapper
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.gui.TreeViewer

fun readFromFile(filename: String): String = File(filename).readText(Charsets.UTF_8)

fun writeToFile(filename: String, contents: String) = File(filename).writeText(contents)

fun main(args: Array<String>){
    val fileName1=args[0]
    val fileName2=args[1]
    var x=readFromFile(fileName1)
    val regex = Regex("/\\*.*?\\*/")
    x=x.replace(regex, "")
   // println(x)
    val lexer=alfLexer(CharStreams.fromString(x))
    val parser=alfParser(CommonTokenStream(lexer))
    val tree=parser.program()
    val mapper=ObjectMapper().writerWithDefaultPrettyPrinter()
    val visitor=AlfVisitor()
    val visit=visitor.visit(tree)
    val json=mapper.writeValueAsString(visit)
    writeToFile(fileName2,json)
}