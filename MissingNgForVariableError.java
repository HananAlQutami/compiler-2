package SYMBOL_TABLE;

public class MissingNgForVariableError extends SemanticError {
    private String directive;
    private int lineNumber;

    public MissingNgForVariableError(String directive, int lineNumber) {
        super();

        this.directive = directive;
        this.lineNumber = lineNumber;
        this.message = "خطأ دلالي: توجيه " + directive +
                " مستخدم بدون تعريف متغير مرجعي. خطأ في السطر " + lineNumber + ".";



        Row row = new Row();
        row.setType("MissingNgForVariable");
        row.setValue(directive);
        this.symbolTable.getRows().add(row);
    }

    @Override
    public void print() {
        System.out.println(message);
        System.out.println("الجدول الرمزي الخاص بهذا الخطأ:");
        symbolTable.printyy();
    }

    public String getDirective() {
        return directive;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
