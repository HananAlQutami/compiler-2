import ANTLR.AngularLexer;
import ANTLR.AngularParser;
import AST.Program;
import VISITOR.BaseVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.IOException;

import static org.antlr.v4.runtime.CharStreams.fromFileName;

public class Main {
    public static void main(String[] args) throws IOException {

        String source = "C:\\Users\\refat\\Desktop\\o\\CompilerProject\\src\\TEST_FILES\\Test_File2.txt";
        //String source = "C:\\Users\\refat\\Desktop\\o\\CompilerProject\\src\\TEST_FILES\\Test_File1.txt";

        CharStream cs=fromFileName(source);
        AngularLexer lexer = new AngularLexer(cs);
        CommonTokenStream token= new CommonTokenStream(lexer);
        AngularParser parser = new AngularParser(token);
        ParseTree tree = parser.program();
        Program program = (Program) new BaseVisitor().visit(tree);
        System.out.println(program);


    }
}

/*
//        File folder = new File("D:CompilerProject\\CompilerProject\\src\\TEST_FILES");
//        File[] files = folder.listFiles();
//        for (int i = 0; i < files.length; i++) {
//            String source = files[i].getPath();
//            CharStream cs = fromFileName(source);
//            AngularLexer lexer = new AngularLexer(cs);
//            CommonTokenStream token = new CommonTokenStream(lexer);
//            AngularParser parser = new AngularParser(token);
//            ParseTree tree = parser.program();
//            Program program = (Program) new BaseVisitor().visit(tree);
//            System.out.println(program);
//        }*/
