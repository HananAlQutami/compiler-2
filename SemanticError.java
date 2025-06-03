package SYMBOL_TABLE;
import java.util.ArrayList;
import java.util.List;

public abstract class SemanticError {
    protected SymbolTable symbolTable;
    protected String message;

    public SemanticError() {
        this.symbolTable = new SymbolTable();
    }

    public abstract void print();

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public String getMessage() {
        return message;
    }


}









/*
    public boolean error_in_import(SymbolTable Sympoltable )
    {


        for(int i =0 ; i<Symboltable.rows.size();i++)
        {
            if ( Symboltable.rows.get(i)!=null )
            {
for(int j=Symboltable.rows.size()-1 ;j>i;j--)
{
    if (Symboltable.rows.get(j).getType().equals(Symboltable.rows.get(i).getType())   &&  Symboltable.rows.get(j).getValue().equals(Symboltable.rows.get(i).getValue()) )
    {
        this.string =  Symboltable.rows.get(j).getValue();
        return false;


    }

}
            }

        }

        return true;
}


    public void print() {
        System.out.println("========= Semantic Errors Detected =========");

        for (int i = 0; i < AllErrors.size(); i++) {
            System.out.println("Error #" + (i + 1) + ":");
            AllErrors.get(i).printyy();
        }

        if (AllErrors.isEmpty()) {
            System.out.println("No semantic errors found.");
        }

        System.out.println("============================================");
    }




}

*/


















//String string;
//SymbolTable Symboltable;
//
//
//
//List<SymbolTable> AllErrors =new ArrayList<>();
//
//    public void setSymboltable(SymbolTable symboltable) {
//           this.Symboltable = symboltable;
//    }
//
//    public SymbolTable getSymboltable() {
//        return Symboltable;
//    }
//
//
//
//
//
//     public boolean check()
//{
//    if (!error_in_import(this.Symboltable))
//
//
//    {
//        SymbolTable  Symboltable1 =new SymbolTable();
//        Row r1 =new Row();
//        r1.setType("Dublicate_import_to_same_path");
//        r1.setValue(this.string);
//        Symboltable1.getRows().add(r1);
//        AllErrors.add( Symboltable1 );
//
//
//
//     //   System.out.println("--------Dublicate_import_to_same_path");
//
//        return false;
//    }
//
//
//
//
//
//
//
//
//    return true;